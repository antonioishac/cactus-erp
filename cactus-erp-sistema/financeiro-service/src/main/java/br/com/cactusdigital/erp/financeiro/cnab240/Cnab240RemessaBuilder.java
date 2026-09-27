package br.com.cactusdigital.erp.financeiro.cnab240;

import br.com.cactusdigital.erp.financeiro.entity.Banco;
import br.com.cactusdigital.erp.financeiro.entity.ContaBancaria;
import br.com.cactusdigital.erp.financeiro.entity.RemessaBancaria;
import br.com.cactusdigital.erp.financeiro.entity.RemessaBancariaItem;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Monta um arquivo de remessa CNAB 240 de cobrança (registro de título em carteira) — 1
 * lote, segmentos {@code P}+{@code Q} por título. Header de Arquivo/Header de
 * Lote/Segmento P/Segmento Q/Trailer de Lote/Trailer de Arquivo.
 * <p>
 * <b>Transparência sobre a confiança deste layout</b>: as posições exatas de cada campo
 * foram obtidas de uma especificação real, versionada e usada por bibliotecas CNAB de
 * mercado (repositório {@code banco-br/cnab_yaml}, que documenta explicitamente sua fonte
 * como o manual FEBRABAN/CAIXA de cobrança CNAB240 SICOB) — não é uma reconstrução por
 * memória geral, é uma consulta a uma fonte real, verificável, no momento da construção
 * (2026-09-07). A estrutura (header arquivo/header lote/segmentos P e Q/trailers) e o
 * conjunto de campos obrigatórios são o padrão FEBRABAN estável entre bancos. O que NÃO
 * foi validado: (1) nenhum comando foi disparado contra nenhum banco real ou ambiente de
 * homologação — mesmo padrão de honestidade já usado em {@code NfeIniBuilder}/
 * {@code CteIniBuilder}/{@code OfxParser}; (2) alguns campos (código de convênio, dígitos
 * verificadores calculados por módulo 11 de agência/cedente) variam ligeiramente de banco
 * pra banco — aqui ficam com valor default/zero quando não há como calcular com certeza
 * (dígito verificador de agência/cedente por módulo 11 NÃO é calculado, fica '0' — exigiria
 * confirmar o algoritmo exato do banco específico antes de qualquer envio real).
 * {@link Cnab240LineBuilder#build()} garante que nenhuma linha sai com tamanho errado
 * (240 é verificado programaticamente a cada registro, não só na revisão do código).
 */
public final class Cnab240RemessaBuilder {

    private static final DateTimeFormatter HORA_CNAB = DateTimeFormatter.ofPattern("HHmmss");

    private Cnab240RemessaBuilder() {
    }

    public static String gerar(ContaBancaria conta, Banco banco, RemessaBancaria remessa, List<RemessaBancariaItem> itens) {
        String codigoBanco = banco.getCodigo();

        List<String> linhas = new ArrayList<>();
        linhas.add(headerArquivo(codigoBanco, conta, banco, remessa));
        linhas.add(headerLote(codigoBanco, conta, remessa));

        int numeroSequencialLote = 1;
        for (RemessaBancariaItem item : itens) {
            linhas.add(segmentoP(codigoBanco, conta, item, numeroSequencialLote++));
            linhas.add(segmentoQ(codigoBanco, item, numeroSequencialLote++));
        }

        linhas.add(trailerLote(codigoBanco, itens));
        // qtde_registros do trailer de arquivo conta TODOS os registros, incluindo a
        // própria linha de trailer de arquivo que ainda vai ser adicionada.
        linhas.add(trailerArquivo(codigoBanco, linhas.size() + 1));

        return String.join("\r\n", linhas) + "\r\n";
    }

    private static String headerArquivo(String codigoBanco, ContaBancaria conta, Banco banco, RemessaBancaria remessa) {
        String documento = soDigitos(conta.getCedenteDocumento());
        boolean cnpj = documento.length() > 11;
        LocalTime agora = LocalTime.now();

        return new Cnab240LineBuilder()
                .texto(codigoBanco, 3)
                .texto("0000", 4)
                .texto("0", 1)
                .brancos(9)
                .numero(cnpj ? 2 : 1, 1)
                .numero(paraLong(documento), 14)
                .numero(paraLong(soDigitos(conta.getCodigoConvenioCobranca())), 16)
                .brancos(4)
                .numero(paraLong(soDigitos(conta.getAgencia())), 5)
                .numero(paraLong(soDigitos(conta.getAgenciaDigito())), 1)
                .numero(paraLong(soDigitos(conta.getCodigoCedenteCobranca())), 12)
                .zeros(1)
                .zeros(1)
                .texto(conta.getCedenteNome(), 30)
                .texto(banco.getNome(), 30)
                .brancos(10)
                .texto("1", 1)
                .data(remessa.getDataGeracao(), 8)
                .texto(agora.format(HORA_CNAB), 6)
                .numero(remessa.getNumeroSequencialArquivo(), 6)
                .texto("030", 3)
                .zeros(5)
                .brancos(20)
                .brancos(20)
                .brancos(29)
                .build();
    }

    private static String headerLote(String codigoBanco, ContaBancaria conta, RemessaBancaria remessa) {
        String documento = soDigitos(conta.getCedenteDocumento());
        boolean cnpj = documento.length() > 11;

        return new Cnab240LineBuilder()
                .texto(codigoBanco, 3)
                .texto("0001", 4)
                .texto("1", 1)
                .texto("R", 1)
                .texto("01", 2)
                .texto("00", 2)
                .texto("030", 3)
                .brancos(1)
                .numero(cnpj ? 2 : 1, 1)
                .numero(paraLong(documento), 15)
                .texto(soDigitos(conta.getCodigoConvenioCobranca()), 16)
                .brancos(4)
                .numero(paraLong(soDigitos(conta.getAgencia())), 5)
                .numero(paraLong(soDigitos(conta.getAgenciaDigito())), 1)
                .numero(paraLong(soDigitos(conta.getCodigoCedenteCobranca())), 12)
                .zeros(1)
                .zeros(1)
                .texto(conta.getCedenteNome(), 30)
                .brancos(40)
                .brancos(40)
                .numero(remessa.getNumeroSequencialArquivo(), 8)
                .data(remessa.getDataGeracao(), 8)
                .zeros(8)
                .brancos(33)
                .build();
    }

    private static String segmentoP(String codigoBanco, ContaBancaria conta, RemessaBancariaItem item, int numeroSequencialLote) {
        String carteira = soDigitos(conta.getCarteiraCobranca());

        return new Cnab240LineBuilder()
                .texto(codigoBanco, 3)
                .texto("0001", 4)
                .texto("3", 1)
                .numero(numeroSequencialLote, 5)
                .texto("P", 1)
                .brancos(1)
                .texto("01", 2) // código de ocorrência: 01 = entrada de título
                .numero(paraLong(soDigitos(conta.getAgencia())), 5)
                .numero(paraLong(soDigitos(conta.getAgenciaDigito())), 1)
                .numero(paraLong(soDigitos(conta.getCodigoCedenteCobranca())), 12)
                .zeros(1)
                .zeros(1)
                .brancos(9)
                .numero(paraLong(soDigitos(item.getNossoNumero())), 11) // 9(11) — numérico, zero à esquerda (achado real: estava como texto)
                .numero(carteira.isBlank() ? 1 : paraLong(carteira), 1)
                .texto("1", 1) // forma de cadastramento: 1 = registrada
                .texto("2", 1) // tipo de documento: 2 = escritural
                .texto("2", 1) // emissão do boleto: 2 = cliente emite
                .texto("2", 1) // distribuição: 2 = cedente distribui
                .texto(item.getNumeroDocumento(), 11)
                .brancos(4)
                .data(item.getDataVencimento(), 8)
                .valorMonetario(item.getValorTitulo(), 15)
                .zeros(5)
                .zeros(1)
                .texto("02", 2) // espécie do título: 02 = duplicata mercantil (default genérico)
                .texto("N", 1) // aceite: N (default seguro)
                .data(LocalDate.now(), 8) // data de emissão
                .zeros(1)
                .zeros(8)
                .zeros(15)
                .zeros(1)
                .zeros(8)
                .zeros(15)
                .zeros(15)
                .zeros(15)
                .brancos(25)
                .zeros(1)
                .zeros(2)
                .zeros(1)
                .zeros(3)
                .texto("09", 2) // código da moeda: 09 = Real
                .zeros(10)
                .brancos(1)
                .build();
    }

    private static String segmentoQ(String codigoBanco, RemessaBancariaItem item, int numeroSequencialLote) {
        String documento = soDigitos(item.getSacadoNumeroInscricao());
        boolean cnpj = documento.length() > 11;

        return new Cnab240LineBuilder()
                .texto(codigoBanco, 3)
                .texto("0001", 4)
                .texto("3", 1)
                .numero(numeroSequencialLote, 5)
                .texto("Q", 1)
                .brancos(1)
                .texto("01", 2)
                .numero(cnpj ? 2 : 1, 1)
                .numero(paraLong(documento), 15)
                .texto(item.getSacadoNome(), 40)
                .texto(item.getSacadoLogradouro(), 40)
                .texto(item.getSacadoBairro(), 15)
                .numero(paraLong(soDigitos(item.getSacadoCep())), 8)
                .texto(item.getSacadoCidade(), 15)
                .texto(item.getSacadoUf(), 2)
                .zeros(1)
                .zeros(15)
                .brancos(40)
                .brancos(3)
                .brancos(20)
                .brancos(8)
                .build();
    }

    private static String trailerLote(String codigoBanco, List<RemessaBancariaItem> itens) {
        BigDecimal valorTotal = itens.stream()
                .map(RemessaBancariaItem::getValorTitulo)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        // 2 registros de detalhe (P+Q) por título + header/trailer de lote = itens*2 + 2
        int quantidadeRegistrosLote = itens.size() * 2 + 2;

        return new Cnab240LineBuilder()
                .texto(codigoBanco, 3)
                .texto("0001", 4)
                .texto("5", 1)
                .brancos(9)
                .numero(quantidadeRegistrosLote, 6)
                .numero(itens.size(), 6)
                .valorMonetario(valorTotal, 17)
                .brancos(6)
                .zeros(17)
                .zeros(6)
                .zeros(17)
                .zeros(6)
                .zeros(17)
                .brancos(8)
                .brancos(117)
                .build();
    }

    private static String trailerArquivo(String codigoBanco, int quantidadeRegistrosArquivo) {
        return new Cnab240LineBuilder()
                .texto(codigoBanco, 3)
                .texto("9999", 4)
                .texto("9", 1)
                .brancos(9)
                .numero(1, 6) // quantidade de lotes — sempre 1 nesta implementação
                .numero(quantidadeRegistrosArquivo, 6)
                .brancos(6)
                .brancos(205)
                .build();
    }

    private static String soDigitos(String valor) {
        return valor == null ? "" : valor.replaceAll("\\D", "");
    }

    private static long paraLong(String digitos) {
        return digitos == null || digitos.isBlank() ? 0L : Long.parseLong(digitos);
    }
}
