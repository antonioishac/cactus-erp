package br.com.cactuserp.pdv.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;

/**
 * Transporte TCP genérico para falar com o ACBr Monitor (NFC-e/SAT/MFE).
 *
 * <p>O ACBr Monitor expõe um servidor TCP que aceita comandos em texto no
 * formato {@code Objeto.Comando(param1,param2,...)}. Endereço/porta vêm de
 * {@code PDV_CONFIGURACAO.ACBR_MONITOR_ENDERECO}/{@code _PORTA}.
 *
 * <h3>Protocolo — validado contra instância real (2026-08-22)</h3>
 * <p>Testado manualmente via socket cru contra um ACBrMonitorPLUS demo
 * (v1.4.0.482) rodando localmente — não só documentação:
 * <ul>
 *   <li><b>Banner de boas-vindas ao conectar</b>: assim que o socket
 *       conecta, o ACBr Monitor <b>envia um banner</b> (versão, data/hora,
 *       "Esperando por comandos.") terminado em ETX, <b>antes</b> de
 *       qualquer comando ser enviado. Um client que não lê e descarta esse
 *       banner primeiro vai ler o banner como se fosse a resposta do seu
 *       comando — bug real que existiu numa versão anterior desta classe,
 *       encontrado só ao testar contra a instância de verdade.</li>
 *   <li><b>Terminador de comando enviado</b>: {@code CR LF '.' CR LF}
 *       ({@code #13#10#46#13#10}) — confirmado, não é só um {@code \n}.</li>
 *   <li><b>Terminador de resposta</b>: byte ETX ({@code CHR(3)}, valor 3) —
 *       confirmado tanto no banner quanto na resposta de comando.</li>
 *   <li><b>Charset</b>: Windows-1252 — acentuação (ex: "não") veio
 *       corrompida com ISO-8859-1, confirmando que precisa ser 1252.</li>
 *   <li><b>Formato da resposta</b>: {@code STATUS: CONTEUDO}, STATUS
 *       {@code OK} ou {@code ERRO} — confirmado
 *       ({@code "ERRO: Objeto inválido: MONITOR"} etc.).</li>
 * </ul>
 * Fontes: teste manual + documentação oficial do projeto ACBr
 * (acbr.sourceforge.io/ACBrMonitor/ComoemitirumaNFeouNFCe.html) + cliente
 * Java de referência da comunidade
 * (github.com/edsonmoretti/JConnACBRMonitorPlus).
 *
 * <p>Esta classe cuida <b>só do transporte</b> — a montagem dos comandos
 * específicos de cada módulo fiscal fica em {@link AcbrComandoFiscalBuilder},
 * e a orquestração de negócio fica em {@link EmissaoFiscalService}.
 */
@Service
public class AcbrMonitorClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(AcbrMonitorClient.class);

    private static final Charset CODEPAGE = Charset.forName("windows-1252");
    private static final String TERMINADOR_COMANDO = "\r\n.\r\n";
    private static final int ETX = 3;

    /** Timeout de conexão e leitura. ACBr/SEFAZ pode demorar em horário de pico. */
    private static final int TIMEOUT_MS_PADRAO = 30_000;

    /**
     * Envia um único comando e retorna a resposta bruta do ACBr Monitor
     * (sem o byte ETX final).
     *
     * @param host    endereço do ACBr Monitor (ex: "127.0.0.1")
     * @param porta   porta TCP configurada no ACBr Monitor
     * @param comando comando completo, ex: {@code "NFe.CriarEnviarNFe(C:\...\nfce.ini,1,0,1)"}
     * @throws IOException se não conseguir conectar, enviar ou ler dentro do timeout
     */
    public String enviarComando(String host, int porta, String comando) throws IOException {
        if (host == null || host.isBlank()) {
            throw new IllegalArgumentException("Endereço do ACBr Monitor não configurado.");
        }
        if (porta <= 0) {
            throw new IllegalArgumentException("Porta do ACBr Monitor não configurada.");
        }

        LOGGER.debug("ACBr Monitor [{}:{}] >> {}", host, porta, comando);

        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, porta), TIMEOUT_MS_PADRAO);
            socket.setSoTimeout(TIMEOUT_MS_PADRAO);

            // Descarta o banner de boas-vindas que o ACBr manda ao conectar
            // (ver javadoc da classe) — senão o comando lê o banner como resposta.
            String banner = lerAteEtx(socket.getInputStream());
            LOGGER.debug("ACBr Monitor [{}:{}] banner: {}", host, porta, banner);

            OutputStream out = socket.getOutputStream();
            out.write((comando + TERMINADOR_COMANDO).getBytes(CODEPAGE));
            out.flush();

            String resposta = lerAteEtx(socket.getInputStream());
            LOGGER.debug("ACBr Monitor [{}:{}] << {}", host, porta, resposta);
            return resposta;
        }
    }

    /**
     * Lê bytes do socket até encontrar o byte ETX (3) ou o stream fechar.
     */
    private String lerAteEtx(InputStream in) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int b;
        while ((b = in.read()) != -1) {
            if (b == ETX) break;
            buffer.write(b);
        }
        return new String(buffer.toByteArray(), CODEPAGE).trim();
    }

    /**
     * Indica se a resposta do ACBr Monitor sinaliza sucesso.
     * Formato confirmado: {@code "OK: ..."} ou {@code "ERRO: ..."}.
     */
    public boolean respostaComErro(String resposta) {
        return resposta == null
                || resposta.isBlank()
                || resposta.toUpperCase().startsWith("ERRO");
    }
}
