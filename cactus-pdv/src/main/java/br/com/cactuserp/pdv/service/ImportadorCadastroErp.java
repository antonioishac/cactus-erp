package br.com.cactuserp.pdv.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Grava no banco local um cadastro vindo do ERP, casando pelo {@code ID_ERP}
 * (upsert): atualiza se o vínculo já existe, insere se não existe.
 *
 * <p>JDBC direto, não JPA, de propósito: é uma tradução campo a campo de um
 * formato externo para colunas, sem regra de negócio do PDV no meio, e
 * várias colunas obrigatórias do PDV não têm equivalente no ERP (ver
 * "padrões" abaixo). Cada método roda dentro da transação de quem chama
 * ({@link SincronizacaoPullService}, uma por página).
 *
 * <h3>O que o ERP não manda e como é resolvido</h3>
 * <ul>
 *   <li><b>Produto</b>: subgrupo, tipo e grupo tributário são obrigatórios no
 *       PDV e não existem no payload. Produto NOVO recebe os padrões
 *       configurados ({@code cactus.pdv.sync.produto.*-padrao-id});
 *       produto já vinculado mantém os valores locais. A unidade é resolvida
 *       pela sigla (criada se não existir).</li>
 *   <li><b>Operador</b>: o ERP não manda o nome do colaborador; o colaborador
 *       novo nasce com o login como nome. Operador inativo ou bloqueado no
 *       ERP tem a senha local invalidada (prefixo {@code !}, que nunca casa
 *       no BCrypt), porque {@code PDV_OPERADOR} não tem coluna de situação.</li>
 *   <li><b>Cliente / forma de pagamento</b>: o PDV não tem coluna de
 *       situação; inativos no ERP continuam no PDV (venda antiga ainda os
 *       referencia), só não são criados se ainda não existirem.</li>
 * </ul>
 */
@Service
public class ImportadorCadastroErp {

    /** {@code TipoItemSped} do ERP chega pelo nome; o PDV guarda o código do Registro 0200. */
    private static final Map<String, String> TIPO_ITEM_SPED = Map.ofEntries(
            Map.entry("MERCADORIA_PARA_REVENDA", "00"), Map.entry("MATERIA_PRIMA", "01"), Map.entry("EMBALAGEM", "02"),
            Map.entry("PRODUTO_EM_PROCESSO", "03"), Map.entry("PRODUTO_ACABADO", "04"), Map.entry("SUBPRODUTO", "05"),
            Map.entry("PRODUTO_INTERMEDIARIO", "06"), Map.entry("MATERIAL_USO_CONSUMO", "07"),
            Map.entry("ATIVO_IMOBILIZADO", "08"), Map.entry("SERVICOS", "09"), Map.entry("OUTROS_INSUMOS", "10"),
            Map.entry("OUTRAS", "99"));
    /** {@code TefTipoGp} do ERP → domínio de {@code PDV_TIPO_PAGAMENTO.TEF_TIPO_GP} (1/2/3/N). */
    private static final Map<String, String> TEF_TIPO_GP = Map.of(
            "TEF_DIAL", "1", "TECBAN", "2", "TEF_HIPERLINK", "3", "NENHUM", "N");
    private static final Set<String> UNIDADES_FRACIONAVEIS = Set.of("KG", "LT", "GR", "ML");

    private final JdbcTemplate jdbc;
    private final Long subgrupoPadraoId;
    private final Long tipoPadraoId;
    private final Long grupoTributarioPadraoId;

    public ImportadorCadastroErp(JdbcTemplate jdbc,
                                 @Value("${cactus.pdv.sync.produto.subgrupo-padrao-id:}") Long subgrupoPadraoId,
                                 @Value("${cactus.pdv.sync.produto.tipo-padrao-id:}") Long tipoPadraoId,
                                 @Value("${cactus.pdv.sync.produto.grupo-tributario-padrao-id:}") Long grupoTributarioPadraoId) {
        this.jdbc = jdbc;
        this.subgrupoPadraoId = subgrupoPadraoId;
        this.tipoPadraoId = tipoPadraoId;
        this.grupoTributarioPadraoId = grupoTributarioPadraoId;
    }

    // ================================================================ produto

    public void importarProduto(JsonNode p) {
        long idErp = p.path("id").asLong();
        Long idLocal = idLocal("PRODUTO", idErp);
        Long unidadeId = unidadePorSigla(texto(p, "produtoUnidadeSigla"));
        Object[] valores = {
                unidadeId, limitar(texto(p, "gtin"), 14), limitar(texto(p, "codigoInterno"), 50), limitar(texto(p, "nome"), 100),
                limitar(texto(p, "descricao"), 250), limitar(texto(p, "descricaoPdv"), 30),
                decimal(p, "valorCompra"), decimal(p, "valorVenda"), decimal(p, "valorCusto"),
                decimal(p, "estoqueMinimo"), decimal(p, "estoqueMaximo"),
                limitar(texto(p, "ncmCodigo"), 8), limitar(texto(p, "cestCodigo"), 7),
                primeiraLetra(texto(p, "iat")), primeiraLetra(texto(p, "ippt")),
                traduzir(TIPO_ITEM_SPED, texto(p, "tipoItemSped"), null), limitar(texto(p, "totalizadorParcial"), 10),
                p.hasNonNull("codigoBalanca") ? p.get("codigoBalanca").asInt() : null, primeiraLetra(texto(p, "pafPSt")),
                semPrefixo(texto(p, "cstIcmsPadrao"), "CST_"), semPrefixo(texto(p, "csosnPadrao"), "CSOSN_"),
                decimal(p, "aliquotaIcmsPadrao"), decimal(p, "aliquotaIpiPadrao"), decimal(p, "aliquotaPisPadrao"),
                decimal(p, "aliquotaCofinsPadrao"), decimal(p, "aliquotaIssqnPadrao"),
                primeiraLetra(texto(p, "situacao"))
        };
        String colunas = "ID_PRODUTO_UNIDADE, GTIN, CODIGO_INTERNO, NOME, DESCRICAO, DESCRICAO_PDV, VALOR_COMPRA, VALOR_VENDA, "
                + "VALOR_CUSTO, ESTOQUE_MINIMO, ESTOQUE_MAXIMO, CODIGO_NCM, CODIGO_CEST, IAT, IPPT, TIPO_ITEM_SPED, "
                + "TOTALIZADOR_PARCIAL, CODIGO_BALANCA, PAF_P_ST, CST, CSOSN, TAXA_ICMS, TAXA_IPI, TAXA_PIS, TAXA_COFINS, "
                + "TAXA_ISSQN, SITUACAO";

        if (idLocal != null) {
            jdbc.update("UPDATE PRODUTO SET " + colunas.replace(", ", " = ?, ") + " = ? WHERE ID = ?", concat(valores, idLocal));
        } else {
            if (subgrupoPadraoId == null || tipoPadraoId == null || grupoTributarioPadraoId == null) {
                throw new IllegalStateException("Produto #" + idErp + " do ERP não existe no PDV e os padrões de subgrupo/tipo/"
                        + "grupo tributário (cactus.pdv.sync.produto.*-padrao-id) não estão configurados.");
            }
            idLocal = inserir("INSERT INTO PRODUTO (" + colunas + ", ID_PRODUTO_SUBGRUPO, ID_PRODUTO_TIPO, "
                            + "ID_TRIBUT_GRUPO_TRIBUTARIO, QUANTIDADE_ESTOQUE, ID_ERP) VALUES (" + marcadores(valores.length + 5) + ")",
                    concat(valores, subgrupoPadraoId, tipoPadraoId, grupoTributarioPadraoId,
                            decimal(p, "quantidadeEstoque") != null ? decimal(p, "quantidadeEstoque") : BigDecimal.ZERO, idErp));
        }
        // Saldo: só no produto NOVO (carga inicial, soma dos depósitos da loja no ERP). Depois disso o
        // saldo do PDV muda só pelo próprio ledger (EstoqueMovimentoService), nunca sobrescrito pelo pull.
        substituirFaixasPreco(idLocal, p.path("faixasPreco"));
    }

    /**
     * Faixas de preço (atacado/promoção) vêm inteiras a cada alteração do
     * produto: as locais são trocadas pelas do ERP. Imagens NÃO são importadas
     * (no ERP são URL; o PDV guarda o arquivo), ficam como pendência.
     */
    private void substituirFaixasPreco(long produtoId, JsonNode faixas) {
        jdbc.update("DELETE FROM PRODUTO_FAIXA_PRECO WHERE ID_PRODUTO = ?", produtoId);
        if (!faixas.isArray()) return;
        for (JsonNode f : faixas) {
            jdbc.update("INSERT INTO PRODUTO_FAIXA_PRECO (ID_PRODUTO, QUANTIDADE_MINIMA, VALOR_UNITARIO, ROTULO, DATA_INICIO, "
                            + "DATA_FIM, ATIVO) VALUES (?,?,?,?,?,?,?)",
                    produtoId, decimal(f, "quantidadeMinima"), decimal(f, "valorUnitario"), limitar(texto(f, "rotulo"), 30),
                    data(f, "dataInicio"), data(f, "dataFim"), "INATIVO".equals(texto(f, "situacao")) ? "N" : "S");
        }
    }

    private Long unidadePorSigla(String sigla) {
        String chave = sigla == null || sigla.isBlank() ? "UN" : sigla.trim().toUpperCase(Locale.ROOT);
        List<Long> ids = jdbc.queryForList("SELECT ID FROM PRODUTO_UNIDADE WHERE UPPER(SIGLA) = ? ORDER BY ID LIMIT 1", Long.class, chave);
        if (!ids.isEmpty()) return ids.get(0);
        return inserir("INSERT INTO PRODUTO_UNIDADE (SIGLA, DESCRICAO, PODE_FRACIONAR) VALUES (?,?,?)",
                new Object[]{limitar(chave, 10), "Importada do ERP", UNIDADES_FRACIONAVEIS.contains(chave) ? "S" : "N"});
    }

    // ================================================================ cliente

    public void importarCliente(JsonNode c) {
        long idErp = c.path("id").asLong();
        Long idLocal = idLocal("CLIENTE", idErp);
        if (idLocal == null && "INATIVO".equals(texto(c, "situacao"))) return;

        JsonNode endereco = principal(c.path("enderecos"));
        String telefone = null;
        String celular = null;
        for (JsonNode t : c.path("telefones")) {
            if ("CELULAR".equals(texto(t, "tipoTelefone"))) {
                if (celular == null) celular = limitar(texto(t, "numero"), 15);
            } else if (telefone == null) {
                telefone = limitar(texto(t, "numero"), 15);
            }
        }
        Object[] valores = {
                limitar(texto(c, "nome"), 150), limitar(texto(c, "email"), 250), limitar(texto(c, "url"), 250),
                limitar(texto(c, "documento"), 14), primeiraLetra(texto(c, "tipoPessoa")),
                limitar(texto(endereco, "logradouro"), 250), limitar(texto(endereco, "numero"), 10),
                limitar(texto(endereco, "complemento"), 100), limitar(texto(endereco, "cep"), 8),
                limitar(texto(endereco, "bairro"), 100), limitar(texto(endereco, "municipio"), 100), limitar(texto(endereco, "uf"), 2),
                endereco.hasNonNull("codigoIbgeMunicipio") ? endereco.get("codigoIbgeMunicipio").asInt() : null,
                endereco.hasNonNull("codigoIbgeUf") ? endereco.get("codigoIbgeUf").asInt() : null,
                telefone, celular, primeiraLetra(texto(c, "fidelidadeAviso")),
                c.hasNonNull("fidelidadeQuantidade") ? c.get("fidelidadeQuantidade").asInt() : null,
                decimal(c, "fidelidadeValor"), decimal(c, "limiteCredito")
        };
        String colunas = "NOME, EMAIL, URL, CPF_CNPJ, TIPO_PESSOA, LOGRADOURO, NUMERO, COMPLEMENTO, CEP, BAIRRO, CIDADE, UF, "
                + "CODIGO_IBGE_CIDADE, CODIGO_IBGE_UF, TELEFONE, CELULAR, FIDELIDADE_AVISO, FIDELIDADE_QUANTIDADE, "
                + "FIDELIDADE_VALOR, FIADO_VALOR_TETO";
        if (idLocal != null) {
            jdbc.update("UPDATE CLIENTE SET " + colunas.replace(", ", " = ?, ") + " = ? WHERE ID = ?", concat(valores, idLocal));
        } else {
            inserir("INSERT INTO CLIENTE (" + colunas + ", DATA_CADASTRO, ID_ERP) VALUES (" + marcadores(valores.length + 2) + ")",
                    concat(valores, Date.valueOf(LocalDate.now()), idErp));
        }
    }

    // ================================================================ forma de pagamento

    public void importarFormaPagamento(JsonNode f) {
        long idErp = f.path("id").asLong();
        Long idLocal = idLocal("PDV_TIPO_PAGAMENTO", idErp);
        if (idLocal == null && "INATIVO".equals(texto(f, "situacao"))) return;
        Object[] valores = {
                limitar(texto(f, "codigo"), 3), limitar(texto(f, "descricao"), 20), limitar(texto(f, "icone"), 50),
                simNao(f, "tef"), simNao(f, "imprimeVinculado"), simNao(f, "permiteTroco"),
                traduzir(TEF_TIPO_GP, texto(f, "tefTipoGp"), "N"), simNao(f, "geraParcelas"),
                limitar(texto(f, "codigoPagamentoNfce"), 2)
        };
        String colunas = "CODIGO, DESCRICAO, ICONE, TEF, IMPRIME_VINCULADO, PERMITE_TROCO, TEF_TIPO_GP, GERA_PARCELAS, "
                + "CODIGO_PAGAMENTO_NFCE";
        if (idLocal != null) {
            jdbc.update("UPDATE PDV_TIPO_PAGAMENTO SET " + colunas.replace(", ", " = ?, ") + " = ? WHERE ID = ?", concat(valores, idLocal));
        } else {
            inserir("INSERT INTO PDV_TIPO_PAGAMENTO (" + colunas + ", ID_ERP) VALUES (" + marcadores(valores.length + 1) + ")",
                    concat(valores, idErp));
        }
    }

    // ================================================================ operador

    /** {@code ID_ERP} do operador = {@code usuarioId} do ERP; do colaborador = {@code colaboradorId} (quando houver). */
    public void importarOperador(JsonNode o) {
        long usuarioId = o.path("usuarioId").asLong();
        String login = limitar(texto(o, "login"), 20);
        String nivel = primeiraLetra(texto(o, "nivelAutorizacao"));
        boolean podeLogar = o.path("ativo").asBoolean(false) && !o.path("bloqueado").asBoolean(false);
        String senha = texto(o, "senhaHash");
        if (senha == null || !podeLogar) {
            senha = "!" + (senha == null ? "" : senha); // nunca casa no BCrypt: bloqueia o login local
        }

        Long operadorLocal = idLocal("PDV_OPERADOR", usuarioId);
        Long colaboradorErp = o.hasNonNull("colaboradorId") ? o.get("colaboradorId").asLong() : null;
        Long colaboradorLocal = colaboradorErp != null ? idLocal("COLABORADOR", colaboradorErp) : null;
        if (colaboradorLocal == null && operadorLocal != null) {
            colaboradorLocal = jdbc.queryForObject("SELECT ID_COLABORADOR FROM PDV_OPERADOR WHERE ID = ?", Long.class, operadorLocal);
        }
        if (colaboradorLocal == null) {
            colaboradorLocal = inserir("INSERT INTO COLABORADOR (NOME, NIVEL_AUTORIZACAO, ID_ERP) VALUES (?,?,?)",
                    new Object[]{login, nivel, colaboradorErp});
        } else {
            jdbc.update("UPDATE COLABORADOR SET NIVEL_AUTORIZACAO = ? WHERE ID = ?", nivel, colaboradorLocal);
        }

        if (operadorLocal != null) {
            jdbc.update("UPDATE PDV_OPERADOR SET ID_COLABORADOR = ?, LOGIN = ?, SENHA = ? WHERE ID = ?",
                    colaboradorLocal, login, limitar(senha, 100), operadorLocal);
        } else {
            inserir("INSERT INTO PDV_OPERADOR (ID_COLABORADOR, LOGIN, SENHA, ID_ERP) VALUES (?,?,?,?)",
                    new Object[]{colaboradorLocal, login, limitar(senha, 100), usuarioId});
        }
    }

    // ================================================================ configuração da loja

    /**
     * Mesas/comandas/delivery na {@code LOJA_CONFIGURACAO} (linha única) e a
     * política comercial de desconto/acréscimo em todo {@code PDV_CONFIGURACAO}
     * do terminal — política da matriz, não digitada caixa a caixa.
     */
    public void importarConfiguracaoLoja(JsonNode c) {
        jdbc.update("UPDATE LOJA_CONFIGURACAO SET PERMITE_MESAS = ?, PERMITE_COMANDAS = ?, PERMITE_DELIVERY = ?, "
                        + "MODO_COMANDA = COALESCE(?, MODO_COMANDA), PERMITE_MESA_AVULSA = ?",
                simNao(c, "permiteMesas"), simNao(c, "permiteComandas"), simNao(c, "permiteDelivery"),
                primeiraLetra(texto(c, "modoComanda")), simNao(c, "permiteMesaAvulsa"));
        jdbc.update("UPDATE PDV_CONFIGURACAO SET LIMITE_DESCONTO_ITEM_PERCENTUAL = ?, LIMITE_DESCONTO_VENDA_PERCENTUAL = ?, "
                        + "LIMITE_DESCONTO_VENDA_VALOR = ?, LIMITE_ACRESCIMO_ITEM_PERCENTUAL = ?, "
                        + "LIMITE_ACRESCIMO_VENDA_PERCENTUAL = ?, PERMITE_DESCONTO_ACIMA_LIMITE = ?",
                decimal(c, "limiteDescontoItemPercentual"), decimal(c, "limiteDescontoVendaPercentual"),
                decimal(c, "limiteDescontoVendaValor"), decimal(c, "limiteAcrescimoItemPercentual"),
                decimal(c, "limiteAcrescimoVendaPercentual"), simNao(c, "permiteDescontoAcimaLimite"));
    }

    // ================================================================ utilitários

    private Long idLocal(String tabela, long idErp) {
        List<Long> ids = jdbc.queryForList("SELECT ID FROM " + tabela + " WHERE ID_ERP = ?", Long.class, idErp);
        return ids.isEmpty() ? null : ids.get(0);
    }

    private long inserir(String sql, Object[] valores) {
        KeyHolder chave = new GeneratedKeyHolder();
        jdbc.update(conexao -> {
            PreparedStatement ps = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            for (int i = 0; i < valores.length; i++) ps.setObject(i + 1, valores[i]);
            return ps;
        }, chave);
        return chave.getKey().longValue();
    }

    private static JsonNode principal(JsonNode lista) {
        if (!lista.isArray() || lista.isEmpty()) return com.fasterxml.jackson.databind.node.MissingNode.getInstance();
        for (JsonNode item : lista) {
            if (item.path("principal").asBoolean(false)) return item;
        }
        return lista.get(0);
    }

    private static Object[] concat(Object[] base, Object... extra) {
        Object[] r = new Object[base.length + extra.length];
        System.arraycopy(base, 0, r, 0, base.length);
        System.arraycopy(extra, 0, r, base.length, extra.length);
        return r;
    }

    private static String marcadores(int n) {
        return String.join(",", java.util.Collections.nCopies(n, "?"));
    }

    /** {@code Map.of} lança NPE em {@code get(null)}: campo ausente no ERP cai no padrão. */
    private static String traduzir(Map<String, String> mapa, String chave, String padrao) {
        return chave == null ? padrao : mapa.getOrDefault(chave, padrao);
    }

    static String texto(JsonNode n, String campo) {
        JsonNode v = n.path(campo);
        return v.isMissingNode() || v.isNull() || v.asText().isBlank() ? null : v.asText();
    }

    private static BigDecimal decimal(JsonNode n, String campo) {
        JsonNode v = n.path(campo);
        return v.isMissingNode() || v.isNull() ? null : v.decimalValue();
    }

    private static Date data(JsonNode n, String campo) {
        String v = texto(n, campo);
        return v == null ? null : Date.valueOf(LocalDate.parse(v));
    }

    private static String simNao(JsonNode n, String campo) {
        return n.path(campo).asBoolean(false) ? "S" : "N";
    }

    static String primeiraLetra(String v) {
        return v == null ? null : v.substring(0, 1).toUpperCase(Locale.ROOT);
    }

    static String semPrefixo(String v, String prefixo) {
        return v == null ? null : v.startsWith(prefixo) ? v.substring(prefixo.length()) : v;
    }

    private static String limitar(String v, int max) {
        return v == null ? null : v.length() <= max ? v : v.substring(0, max);
    }
}
