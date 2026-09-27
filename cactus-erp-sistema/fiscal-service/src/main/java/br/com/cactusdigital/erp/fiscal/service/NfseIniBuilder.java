package br.com.cactusdigital.erp.fiscal.service;

import br.com.cactusdigital.erp.fiscal.client.dto.ClienteFiscalDTO;
import br.com.cactusdigital.erp.fiscal.client.dto.EmpresaFiscalDTO;
import br.com.cactusdigital.erp.fiscal.entity.NotaFiscalServico;
import br.com.cactusdigital.erp.fiscal.entity.NotaFiscalServicoItem;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;

/**
 * Monta o payload {@code NFSe.INI} (Padrão Nacional/ADN) e o comando ACBr Monitor
 * correspondente.
 *
 * <p><b>Confiança menor que {@link NfeIniBuilder} — registrado explicitamente, não
 * escondido.</b> Diferente do NF-e/NFC-e (Fase 7/8, que reaproveitaram o transporte E o
 * formato de comando já validados contra uma instância real pelo {@code cactus-pdv}),
 * NFS-e via ACBr Monitor não tem nenhuma referência já testada neste ecossistema:
 * <ul>
 *   <li><b>Sequência de comando CONFIRMADA por documentação oficial</b>
 *   (acbr.sourceforge.io/ACBrMonitor, 2026-09-05 — resolvido o que antes estava
 *   registrado como incerto): emissão de NFS-e é um fluxo de <b>2 comandos</b>, diferente
 *   do {@code NFE.CriarEnviarNFe} de tiro único —
 *   {@code NFSe.AdicionarRPS("cArqIni","cNumLote")} adiciona o RPS a um lote, depois
 *   {@code NFSe.EnviarLoteRPS("cNumLote",nModoEnvio)} transmite o lote de fato (página
 *   "Como emitir uma NFSe" do próprio ACBr documenta esse exato fluxo:
 *   AdicionarRPS → EnviarLoteRPS → ConsultarSituacaoLote/ConsultarLote (modo assíncrono) →
 *   ImprimirNFSe). {@code nModoEnvio} usa {@code 2} (Lote Síncrono) — devolve o resultado
 *   na mesma chamada, sem precisar consultar o lote depois (mais simples, e mais parecido
 *   com o tiro único do NF-e/NFC-e); {@code 0}(Automático)/{@code 1}(Lote Assíncrono)/
 *   {@code 3}(Unitário) são as outras opções documentadas, não usadas aqui.</li>
 *   <li><b>Seções do INI</b> ({@code [IdentificacaoRps]}, {@code [Prestador]},
 *   {@code [Tomador]}, seção de itens de serviço) — nomes confirmados existirem no
 *   layout oficial "Padrão Nacional", mas o conjunto completo de chaves usado abaixo é
 *   inferência a partir do padrão já visto em {@code NfeIniBuilder}/{@code cactus-pdv},
 *   não uma transcrição de um modelo `.ini` de referência lido na íntegra — essa parte
 *   AINDA não está confirmada, só o comando em si.</li>
 *   <li><b>Layout NÃO-Padrão-Nacional</b> (município com provedor próprio, ex: Ábaco,
 *   Betha, Ginfes) usa um `.ini` completamente diferente por provedor — fora de escopo
 *   aqui, só o Padrão Nacional (ADN) é coberto.</li>
 *   <li><b>`ACBrMonitor.ini` deste ambiente não tem a seção {@code [NFSe]} configurada</b>
 *   (município/provedor zerados) — nem daria pra testar mesmo se a decisão de segurança
 *   da Fase 8 permitisse.</li>
 * </ul>
 * <b>Não usar em produção sem validar contra uma instância real primeiro</b> — mesma
 * disciplina que o {@code EmissaoFiscalService} do cactus-pdv seguiu antes de ser
 * validado (a lacuna que resta aqui é só o detalhamento de chaves do INI, não mais a
 * sequência de comando).
 */
@Component
public class NfseIniBuilder {

    private static final DateTimeFormatter FORMATO_DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    /** Modo de envio do lote — 2 = Lote Síncrono, o usado por {@link #construirComandoEnviarLote}. */
    private static final int MODO_ENVIO_LOTE_SINCRONO = 2;

    /** Monta o comando {@code NFSe.AdicionarRPS} (1º dos 2 passos — ver Javadoc da classe). */
    public String construirComandoAdicionarRps(NotaFiscalServico nota, EmpresaFiscalDTO prestador, ClienteFiscalDTO tomador, String numeroLote) {
        String ini = construirIniRps(nota, prestador, tomador);
        return "NFSe.AdicionarRPS(" + ini + "," + numeroLote + ")";
    }

    /**
     * Monta o comando {@code NFSe.EnviarLoteRPS} (2º dos 2 passos — ver Javadoc da classe).
     * Usa modo Síncrono ({@value #MODO_ENVIO_LOTE_SINCRONO}) — devolve o resultado da
     * autorização na própria resposta do comando, sem precisar de um passo extra de
     * consulta de lote (mais simples e mais parecido com o tiro único de NF-e/CT-e).
     */
    public String construirComandoEnviarLote(String numeroLote) {
        return "NFSe.EnviarLoteRPS(" + numeroLote + "," + MODO_ENVIO_LOTE_SINCRONO + ")";
    }

    public String construirIniRps(NotaFiscalServico nota, EmpresaFiscalDTO prestador, ClienteFiscalDTO tomador) {
        StringBuilder ini = new StringBuilder();

        secaoIdentificacaoRps(ini, nota);
        secaoPrestador(ini, prestador);
        secaoTomador(ini, tomador);

        int numeroItem = 1;
        for (NotaFiscalServicoItem item : nota.getItens()) {
            secaoServico(ini, numeroItem++, item);
        }

        return ini.toString();
    }

    private void secaoIdentificacaoRps(StringBuilder ini, NotaFiscalServico nota) {
        secao(ini, "IdentificacaoRps");
        chave(ini, "Numero", nota.getNumeroRps());
        chave(ini, "Serie", nota.getSerieRps());
        chave(ini, "Tipo", "1"); // 1 = RPS comum
        chave(ini, "DataEmissao", nota.getDataEmissao() != null
                ? LocalDateTime.ofInstant(nota.getDataEmissao(), java.time.ZoneId.systemDefault()).format(FORMATO_DATA_HORA)
                : LocalDateTime.now().format(FORMATO_DATA_HORA));
        chave(ini, "Competencia", nota.getCompetencia());
        chave(ini, "OptanteSimplesNacional", "2"); // 2 = Não optante — simplificação, ver CLAUDE.md
        chave(ini, "IncentivadorCultural", "2");   // 2 = Não incentivador — simplificação
        chave(ini, "Status", "1");                 // 1 = Normal
    }

    private void secaoPrestador(StringBuilder ini, EmpresaFiscalDTO prestador) {
        secao(ini, "Prestador");
        chave(ini, "CNPJ", prestador.cnpj());
        chave(ini, "InscricaoMunicipal", prestador.inscricaoEstadual()); // EmpresaFiscalDTO não carrega IM separada nesta fase — ver limitação no CLAUDE.md
        chave(ini, "RazaoSocial", prestador.razaoSocial());
    }

    private void secaoTomador(StringBuilder ini, ClienteFiscalDTO tomador) {
        secao(ini, "Tomador");
        String documento = tomador.documento();
        if (documento != null && !documento.isBlank()) {
            chave(ini, documento.length() > 11 ? "CNPJ" : "CPF", documento);
        }
        chave(ini, "RazaoSocial", tomador.nome());
        chave(ini, "Email", tomador.email());
        if (!tomador.enderecos().isEmpty()) {
            var endereco = tomador.enderecos().get(0);
            chave(ini, "Logradouro", endereco.logradouro());
            chave(ini, "Numero", endereco.numero());
            chave(ini, "Bairro", endereco.bairro());
            chave(ini, "CEP", endereco.cep());
            chave(ini, "CodigoMunicipio", str(endereco.codigoIbgeMunicipio()));
            chave(ini, "UF", endereco.uf());
        }
    }

    private void secaoServico(StringBuilder ini, int numero, NotaFiscalServicoItem item) {
        secao(ini, "Servico" + pad(numero));
        chave(ini, "ItemListaServico", item.getListaServico() != null ? item.getListaServico().getCodigo() : null);
        chave(ini, "CodigoTributacaoMunicipio", item.getCodigoTributacaoMunicipio());
        chave(ini, "Discriminacao", item.getDiscriminacao());
        chave(ini, "ValorServicos", str(item.getValorServicos()));
        chave(ini, "Aliquota", str(item.getAliquotaIss()));
        chave(ini, "ValorIss", str(item.getValorIss()));
        chave(ini, "ISSRetido", "2"); // 2 = Não retido — simplificação
    }

    // ====================================================================
    //  Helpers — mesmo padrão exato de NfeIniBuilder/cactus-pdv.AcbrComandoFiscalBuilder
    // ====================================================================

    private void secao(StringBuilder ini, String nome) {
        ini.append('[').append(nome).append(']').append("\r\n");
    }

    private void chave(StringBuilder ini, String nome, String valor) {
        ini.append(nome).append('=').append(valor != null ? valor : "").append("\r\n");
    }

    private String pad(int numero) {
        return String.format("%03d", numero);
    }

    private String str(Object valor) {
        return valor == null ? "" : valor.toString();
    }

    private String str(BigDecimal valor) {
        return valor == null ? "0" : valor.toPlainString();
    }
}
