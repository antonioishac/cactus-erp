package br.com.cactusdigital.erp.common.report;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.RecordComponent;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import org.springframework.stereotype.Component;

/**
 * Compila/preenche/exporta relatórios a partir de um {@code .jrxml} escrito à mão (sem
 * iReport/Jaspersoft Studio disponível neste ambiente — ver ANÁLISE: MÓDULO DE RELATÓRIOS
 * no CLAUDE.md raiz; o XML é padrão-aberto, então continua editável depois numa
 * ferramenta visual se o usuário instalar uma). Reutilizável por qualquer serviço: cada
 * um mantém seus próprios {@code .jrxml} em {@code src/main/resources/reports/} e chama
 * {@link #gerar} passando o {@code List<record>} já calculado pela própria regra de
 * negócio do módulo — o engine nunca busca dado sozinho, só formata o que já foi
 * calculado.
 * <p>
 * Os records de dados são convertidos pra {@code Map<String, Object>} via reflexão sobre
 * {@link RecordComponent} (uma chave por componente, mesmo nome) antes de virar
 * {@link JRMapCollectionDataSource} — <b>não</b> {@code JRBeanCollectionDataSource}
 * (que usa Apache Commons BeanUtils por baixo, não {@code java.beans.Introspector}; Commons
 * BeanUtils não reconhece accessors de record, só o padrão clássico {@code getXxx()}/
 * {@code isXxx()} — descoberto testando ao vivo, não documentado). Um campo do template
 * ({@code $F{produtoId}}) resolve contra a chave {@code "produtoId"} do mapa.
 * <p>
 * {@link JasperReport}s compilados são cacheados em memória por caminho de classpath —
 * compilar (via {@code JRJdtCompiler}, ver {@code jasperreports.properties}) a cada
 * requisição seria trabalho repetido sem necessidade, o template não muda em runtime.
 */
@Component
public class ReportEngine {

    private final Map<String, JasperReport> cacheDeTemplatesCompilados = new ConcurrentHashMap<>();

    /**
     * @param caminhoClasspathJrxml ex: {@code "reports/curva-abc.jrxml"} — resolvido via
     *                              {@link ClassLoader#getResourceAsStream(String)} contra
     *                              o classloader do serviço chamador (nunca o do
     *                              {@code common}), pra cada serviço manter seus próprios
     *                              templates em {@code src/main/resources/reports/}.
     * @param dados                a coleção já pronta (filtrada/calculada) a formatar —
     *                              tipicamente o mesmo {@code List<record>} já devolvido
     *                              pelo endpoint JSON irmão deste relatório.
     * @param parametros            valores extras que o template referencia via
     *                              {@code $P{...}} (ex: título, período consultado,
     *                              filtros aplicados) — nunca dado tabular, isso vai em
     *                              {@code dados}.
     */
    public byte[] gerar(ClassLoader classLoaderDoServico, String caminhoClasspathJrxml, Collection<?> dados,
            Map<String, Object> parametros, ReportFormat formato) {
        try {
            JasperReport template = compilar(classLoaderDoServico, caminhoClasspathJrxml);
            // JasperFillManager injeta o próprio datasource DENTRO do mapa de parâmetros
            // recebido (via put) — um Map.of(...) imutável (o jeito natural de montar
            // parâmetros do lado de quem chama) estoura UnsupportedOperationException em
            // runtime; por isso sempre copiamos pra um HashMap mutável aqui, nunca no
            // lado do chamador.
            JasperPrint impresso = JasperFillManager.fillReport(template, new HashMap<>(parametros),
                    new JRMapCollectionDataSource(paraLinhasDeMapa(dados)));
            return exportar(impresso, formato);
        } catch (JRException | RuntimeException e) {
            throw new ReportGenerationException(
                    "Falha ao gerar relatório '" + caminhoClasspathJrxml + "' em formato " + formato, e);
        }
    }

    private List<Map<String, ?>> paraLinhasDeMapa(Collection<?> dados) {
        List<Map<String, ?>> linhas = new ArrayList<>(dados.size());
        for (Object registro : dados) {
            linhas.add(paraMapa(registro));
        }
        return linhas;
    }

    private Map<String, Object> paraMapa(Object registro) {
        if (!registro.getClass().isRecord()) {
            throw new ReportGenerationException(
                    "ReportEngine só aceita records como linha de dado — recebido " + registro.getClass(), null);
        }
        Map<String, Object> mapa = new LinkedHashMap<>();
        for (RecordComponent componente : registro.getClass().getRecordComponents()) {
            try {
                mapa.put(componente.getName(), paraValorDeTemplate(componente.getAccessor().invoke(registro)));
            } catch (ReflectiveOperationException e) {
                throw new ReportGenerationException(
                        "Falha ao ler campo '" + componente.getName() + "' do record " + registro.getClass(), e);
            }
        }
        return mapa;
    }

    /**
     * {@code java.time.LocalDate}/{@code LocalDateTime} não respeitam o atributo
     * {@code pattern} do {@code <textField>} de forma confiável na série 6.x (achado
     * testando ao vivo: campo exibido em ISO — "2026-09-03" — em vez do formato pedido no
     * template) — convertidos aqui pra {@code java.sql.Date}/{@code Timestamp}
     * ({@code java.util.Date} por baixo, cujo {@code pattern} sempre usou
     * {@code SimpleDateFormat}, caminho maduro/estável desde a origem do JasperReports).
     * Declarar o campo como {@code java.util.Date} no {@code .jrxml} (não mais
     * {@code java.time.LocalDate}) é o que torna essa conversão efetiva.
     */
    private Object paraValorDeTemplate(Object valor) {
        if (valor instanceof LocalDate data) {
            return java.sql.Date.valueOf(data);
        }
        if (valor instanceof LocalDateTime dataHora) {
            return Timestamp.valueOf(dataHora);
        }
        return valor;
    }

    private JasperReport compilar(ClassLoader classLoaderDoServico, String caminhoClasspathJrxml) {
        return cacheDeTemplatesCompilados.computeIfAbsent(caminhoClasspathJrxml, caminho -> {
            try (InputStream jrxml = classLoaderDoServico.getResourceAsStream(caminho)) {
                if (jrxml == null) {
                    throw new ReportGenerationException(
                            "Template de relatório não encontrado no classpath: " + caminho, null);
                }
                return JasperCompileManager.compileReport(jrxml);
            } catch (JRException e) {
                throw new ReportGenerationException("Falha ao compilar template de relatório: " + caminho, e);
            } catch (java.io.IOException e) {
                throw new ReportGenerationException("Falha ao ler template de relatório: " + caminho, e);
            }
        });
    }

    private byte[] exportar(JasperPrint impresso, ReportFormat formato) throws JRException {
        ByteArrayOutputStream saida = new ByteArrayOutputStream();
        switch (formato) {
            case PDF -> {
                JRPdfExporter exporter = new JRPdfExporter();
                exporter.setExporterInput(new SimpleExporterInput(impresso));
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(saida));
                exporter.exportReport();
            }
            case XLSX -> {
                JRXlsxExporter exporter = new JRXlsxExporter();
                exporter.setExporterInput(new SimpleExporterInput(impresso));
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(saida));
                SimpleXlsxReportConfiguration configuracao = new SimpleXlsxReportConfiguration();
                configuracao.setOnePagePerSheet(false);
                configuracao.setDetectCellType(true);
                configuracao.setCollapseRowSpan(false);
                exporter.setConfiguration(configuracao);
                exporter.exportReport();
            }
        }
        return saida.toByteArray();
    }
}
