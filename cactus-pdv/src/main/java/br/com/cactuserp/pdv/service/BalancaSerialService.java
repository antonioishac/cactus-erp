package br.com.cactuserp.pdv.service;

import br.com.cactuserp.pdv.entity.ModeloBalanca;
import br.com.cactuserp.pdv.entity.PdvConfiguracaoBalanca;
import com.fazecast.jSerialComm.SerialPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;

/**
 * Leitura de peso EM TEMPO REAL de uma balança de checkout conectada por
 * serial (RS-232) — diferente de {@link BalancaCodigoDecoder}, que decodifica
 * uma etiqueta JÁ IMPRESSA (peso pré-pesado, sem comunicação com hardware).
 *
 * <p>Usa <a href="https://fazecast.github.io/jSerialComm/">jSerialComm</a>
 * (dependência nova, ver {@code pom.xml}) — biblioteca Java multiplataforma
 * pra portas seriais, sem exigir driver nativo separado.
 *
 * <p><b>⚠️ NÃO TESTADO contra hardware real.</b> Diferente do decodificador
 * de código de barras (lógica pura, verificável por dígito verificador
 * EAN-13), o protocolo de solicitação de peso ao vivo VARIA por modelo e
 * até por firmware da balança — o comando ENQ (0x05) usado abaixo é o mais
 * comum na família Toledo ("protocolo de 1 via"/"impressora"), mas
 * precisa ser validado contra a balança física antes de ir pra produção.
 * Ver {@link #solicitarPeso(PdvConfiguracaoBalanca)} pro ponto exato a
 * ajustar por modelo.
 */
@Service
public class BalancaSerialService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BalancaSerialService.class);

    /** Byte ENQ (0x05) — solicitação de peso no protocolo clássico Toledo. */
    private static final byte[] COMANDO_TOLEDO_ENQ = { 0x05 };

    /**
     * Solicita e lê o peso atual da balança configurada.
     *
     * @return peso em kg, ou {@code null} se a leitura falhar (porta
     *         indisponível, timeout, resposta ilegível) — nunca lança
     *         exceção pra não travar a tela de venda por causa de balança
     *         desligada/desconectada.
     */
    public BigDecimal lerPesoAtual(PdvConfiguracaoBalanca config) {
        if (config == null || config.getPorta() == null || config.getPorta().isBlank()) {
            LOGGER.debug("Balança não configurada — leitura de peso ignorada.");
            return null;
        }

        SerialPort porta = abrirPorta(config);
        if (porta == null) return null;

        try {
            byte[] comando = solicitarPeso(config);
            porta.getOutputStream().write(comando);
            porta.getOutputStream().flush();

            byte[] buffer = new byte[64];
            int lidos = porta.readBytes(buffer, buffer.length);
            if (lidos <= 0) {
                LOGGER.warn("Balança na porta {} não respondeu dentro do timeout.", config.getPorta());
                return null;
            }

            String resposta = new String(buffer, 0, lidos, StandardCharsets.US_ASCII).trim();
            return parsearPeso(resposta);

        } catch (Exception ex) {
            LOGGER.error("Falha ao ler peso da balança na porta {}: {}", config.getPorta(), ex.getMessage(), ex);
            return null;
        } finally {
            porta.closePort();
        }
    }

    private SerialPort abrirPorta(PdvConfiguracaoBalanca config) {
        try {
            SerialPort porta = SerialPort.getCommPort(config.getPorta());
            porta.setBaudRate(config.getBaudRate() != null ? config.getBaudRate() : 9600);
            porta.setNumDataBits(config.getDataBits() != null ? config.getDataBits() : 8);
            porta.setNumStopBits(traduzirStopBits(config.getStopBits()));
            porta.setParity(traduzirParity(config.getParity()));
            porta.setFlowControl(traduzirHandShake(config.getHandShake()));

            int timeoutMs = config.getTimeout() != null ? config.getTimeout() : 2000;
            porta.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, timeoutMs, 0);

            if (!porta.openPort()) {
                LOGGER.error("Não foi possível abrir a porta serial {}.", config.getPorta());
                return null;
            }
            return porta;
        } catch (Exception ex) {
            LOGGER.error("Falha ao configurar porta serial {}: {}", config.getPorta(), ex.getMessage(), ex);
            return null;
        }
    }

    /**
     * ⚠️ Ponto de ajuste por modelo/protocolo — hoje só implementa o
     * comando ENQ genérico (Toledo clássico). Balanças Filizola/Urano/etc
     * têm protocolos próprios que precisam ser adicionados aqui conforme
     * forem homologadas.
     */
    private byte[] solicitarPeso(PdvConfiguracaoBalanca config) {
        ModeloBalanca modelo = config.getModelo();
        if (modelo == ModeloBalanca.TOLEDO || modelo == ModeloBalanca.TOLEDO2180 || modelo == null) {
            return COMANDO_TOLEDO_ENQ;
        }
        LOGGER.warn("Modelo de balança {} sem protocolo de solicitação de peso implementado — "
                + "usando comando genérico Toledo (pode não funcionar).", modelo);
        return COMANDO_TOLEDO_ENQ;
    }

    /**
     * ⚠️ Parsing best-effort — assume resposta ASCII com dígitos
     * representando gramas (ex: "  00750" = 0,750 kg), formato mais comum
     * na família Toledo. Ajustar se a balança real responder em formato
     * diferente (ponto decimal explícito, prefixo de status, etc).
     */
    private BigDecimal parsearPeso(String resposta) {
        String digitos = resposta.replaceAll("[^0-9]", "");
        if (digitos.isEmpty()) {
            LOGGER.warn("Resposta da balança sem dígitos reconhecíveis: '{}'", resposta);
            return null;
        }
        try {
            return new BigDecimal(digitos).divide(BigDecimal.valueOf(1000), 3, RoundingMode.HALF_UP);
        } catch (NumberFormatException ex) {
            LOGGER.warn("Não foi possível interpretar peso da resposta '{}'", resposta);
            return null;
        }
    }

    private int traduzirStopBits(Integer stopBits) {
        if (stopBits == null) return SerialPort.ONE_STOP_BIT;
        return switch (stopBits) {
            case 1 -> SerialPort.ONE_POINT_FIVE_STOP_BITS;
            case 2 -> SerialPort.TWO_STOP_BITS;
            default -> SerialPort.ONE_STOP_BIT;
        };
    }

    /**
     * Traduz {@code PDV_CONFIGURACAO_BALANCA.HAND_SHAKE}
     * (0=Nenhum, 1=XON_XOFF, 2=RTS_CTS, 3=DTR_DSR — ver
     * {@link PdvConfiguracaoBalanca#getHandShake()}) pra flag de flow
     * control do jSerialComm. Bug corrigido (2026-08-23): esse campo era
     * lido do banco mas nunca aplicado na porta — {@code abrirPorta}
     * configurava baud/dataBits/stopBits/parity e ignorava o handshake,
     * o que faria a leitura travar/estourar timeout em qualquer balança
     * configurada com controle de fluxo por hardware (RTS/CTS) ou
     * software (XON/XOFF).
     */
    private int traduzirHandShake(Integer handShake) {
        if (handShake == null) return SerialPort.FLOW_CONTROL_DISABLED;
        return switch (handShake) {
            case 1 -> SerialPort.FLOW_CONTROL_XONXOFF_IN_ENABLED | SerialPort.FLOW_CONTROL_XONXOFF_OUT_ENABLED;
            case 2 -> SerialPort.FLOW_CONTROL_RTS_ENABLED | SerialPort.FLOW_CONTROL_CTS_ENABLED;
            case 3 -> SerialPort.FLOW_CONTROL_DTR_ENABLED | SerialPort.FLOW_CONTROL_DSR_ENABLED;
            default -> SerialPort.FLOW_CONTROL_DISABLED;
        };
    }

    private int traduzirParity(Integer parity) {
        if (parity == null) return SerialPort.NO_PARITY;
        return switch (parity) {
            case 1 -> SerialPort.ODD_PARITY;
            case 2 -> SerialPort.EVEN_PARITY;
            case 3 -> SerialPort.MARK_PARITY;
            case 4 -> SerialPort.SPACE_PARITY;
            default -> SerialPort.NO_PARITY;
        };
    }
}
