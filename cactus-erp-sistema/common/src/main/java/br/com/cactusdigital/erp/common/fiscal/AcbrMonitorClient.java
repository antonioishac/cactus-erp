package br.com.cactusdigital.erp.common.fiscal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Transporte TCP genérico para falar com o ACBr Monitor (NFe/NFCe/NFSe/CTe/SAT) — portado
 * de {@code cactus-pdv.AcbrMonitorClient} (mesmo protocolo, já validado contra uma
 * instância real do ACBrMonitorPLUS — ver o Javadoc de lá e a seção "Integração fiscal"
 * do CLAUDE.md deste monorepo). Vive em {@code common} porque {@code fiscal-service}
 * (Fase 8, NF-e) não é o único consumidor esperado — NFS-e (Fase 9) e CT-e (Fase 11)
 * também vão precisar do mesmo transporte, só o payload/comando muda por documento.
 *
 * <p>O ACBr Monitor expõe um servidor TCP que aceita comandos em texto no formato
 * {@code Objeto.Comando(param1,param2,...)}.
 *
 * <h3>Protocolo — herdado do cliente já validado do cactus-pdv (2026-08-22)</h3>
 * <ul>
 *   <li><b>Banner de boas-vindas ao conectar</b>: assim que o socket conecta, o ACBr
 *       Monitor envia um banner (versão, data/hora, "Esperando por comandos."),
 *       terminado em ETX, <b>antes</b> de qualquer comando ser enviado — precisa ser
 *       descartado antes de ler a resposta do comando de verdade.</li>
 *   <li><b>Terminador de comando enviado</b>: {@code CR LF '.' CR LF}.</li>
 *   <li><b>Terminador de resposta</b>: byte ETX ({@code CHR(3)}, valor 3).</li>
 *   <li><b>Charset</b>: Windows-1252.</li>
 *   <li><b>Formato da resposta</b>: {@code STATUS: CONTEUDO}, STATUS {@code OK} ou {@code ERRO}.</li>
 * </ul>
 *
 * <p><b>Escopo desta portagem (Fase 8, 2026-09-03)</b>: reaproveita o transporte
 * inteiro, idêntico — a montagem do comando/payload específico de cada documento fiscal
 * (NFe/NFCe/NFSe/CTe) fica em builders próprios de cada serviço consumidor (ex:
 * {@code fiscal-service.NfeIniBuilder}), nunca aqui.
 */
@Component
public class AcbrMonitorClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(AcbrMonitorClient.class);

    private static final Charset CODEPAGE = Charset.forName("windows-1252");
    private static final String TERMINADOR_COMANDO = "\r\n.\r\n";
    private static final int ETX = 3;

    /** Timeout de conexão e leitura. ACBr/SEFAZ pode demorar em horário de pico. */
    private static final int TIMEOUT_MS_PADRAO = 30_000;

    /**
     * Envia um único comando e retorna a resposta bruta do ACBr Monitor (sem o byte ETX final).
     *
     * @param host    endereço do ACBr Monitor (ex: "127.0.0.1")
     * @param porta   porta TCP configurada no ACBr Monitor
     * @param comando comando completo, ex: {@code "NFE.CriarEnviarNFe(C:\...\nfe.ini,1,0,1)"}
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

            // Descarta o banner de boas-vindas que o ACBr manda ao conectar (ver Javadoc
            // da classe) — senão o comando lê o banner como se fosse a resposta dele.
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

    /** Lê bytes do socket até encontrar o byte ETX (3) ou o stream fechar. */
    private String lerAteEtx(InputStream in) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int b;
        while ((b = in.read()) != -1) {
            if (b == ETX) {
                break;
            }
            buffer.write(b);
        }
        return new String(buffer.toByteArray(), CODEPAGE).trim();
    }

    /** Indica se a resposta do ACBr Monitor sinaliza sucesso. Formato: {@code "OK: ..."} ou {@code "ERRO: ..."}. */
    public boolean respostaComErro(String resposta) {
        return resposta == null
                || resposta.isBlank()
                || resposta.toUpperCase().startsWith("ERRO");
    }
}
