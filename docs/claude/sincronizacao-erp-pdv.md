<!-- Conteúdo movido do CLAUDE.md original em 2026-10-31, sem nenhuma alteração. Linhas originais: 104-191, 1039-1051 -->

# MAPA DE SINCRONIZAÇÃO CAMPO A CAMPO (ERP ↔ PDV)
Princípio: **toda entidade do cadastro-base que tem equivalente no cactus-pdv nasce com pelo menos a mesma riqueza de campos que o PDV já tem** — porque na Fase 6 esses dados são exportados pra lá. A tabela abaixo é o resultado de cruzar `PRODUTO`/`CLIENTE`/`LOJA_CONFIGURACAO` (e tabelas satélite) do `BANCO_PDV.md` campo a campo com o que existe hoje no cadastro-base. ✅ = coberto | ❌ = faltando, entra na Fase 1b/1c | 🆕 = enriquecimento do ERP sem equivalente no PDV (mantém, só não tem campo de sync).

## `PRODUTO`
Rework completo feito (entidade/DTO/mapper/service/controller/migration `V6__produto.sql`, compilando limpo) — tabela abaixo atualizada, ✅ = feito nesta passada:

| Campo no PDV | Situação no ERP | Ação |
|---|---|---|
| `ID_PRODUTO_TIPO` (+ tabela `PRODUTO_TIPO`: codigo char(2)+descricao) | ✅ | `ProdutoTipo` (lookup) criado — **distinta** de Grupo/Subgrupo, CRUD próprio em `/api/v1/produtos-tipos` |
| `TIPO_ITEM_SPED` char(2) | ✅ | Enum `TipoItemSped` (tabela oficial do Registro 0200 do SPED, códigos 00-99 reais) no `Produto`, **não confundido com `ProdutoTipo`** |
| `IAT` (A=Arredondamento/T=Truncamento) | ✅ | Enum `IndicadorArredondamentoTruncamento` + converter (mesmo padrão de `Situacao`) |
| `IPPT` (P=Própria/T=Terceiro) | ✅ | Enum `IndicadorProprioTerceiro` + converter |
| `VALOR_COMPRA` | ✅ | Campo `valorCompra`, distinto de `valorCusto` |
| `TOTALIZADOR_PARCIAL` varchar(10) | ✅ | Campo `totalizadorParcial` |
| `CODIGO_BALANCA` int | ✅ | Campo `codigoBalanca` |
| `PAF_P_ST` char(1) | ✅ | Campo `pafPSt` (string crua — não tenho certeza do domínio de valores P/S/T, não inventei enum sem confirmar) |
| `HASH_REGISTRO` varchar(32) | Decidido: não adicionar | É de **movimento** (PAF-NFC-e), não de cadastro — ver seção `HASH_REGISTRO — decidido` acima. Fica pro futuro módulo de emissão fiscal, não pro `Produto` |
| `CODIGO_NCM`, `CODIGO_CEST` | ✅ | Viraram `ncmId`(FK, obrigatório)/`cestId`(FK, opcional — só quando sujeito a ST), mais `grupoTributarioId`(FK, obrigatório) |
| `TAXA_IPI/ISSQN/PIS/COFINS/ICMS`, `CST`, `CSOSN` | ✅ | Motor tributário resolve via `POST /api/v1/tributacao/resolver`; "resolvido padrão" cacheado no `Produto` (`cstIcmsPadrao`/`csosnPadrao`/`aliquotaXxxPadrao`/`dataResolucaoPadrao`) via `POST /api/v1/produtos/{id}/recalcular-tributacao-padrao` |
| `GTIN`, `CODIGO_INTERNO`, `NOME`, `DESCRICAO`, `DESCRICAO_PDV`, `VALOR_VENDA`, `VALOR_CUSTO`, `ESTOQUE_MINIMO/MAXIMO`, `SITUACAO`, FK Unidade/Subgrupo | ✅ | — |
| `QUANTIDADE_ESTOQUE` | Não vive no cadastro-base | Fica no futuro `controle-estoque-service`; sync = saldo agregado calculado lá, não duplicado aqui |
| `PRODUTO_FAIXA_PRECO` (tabela inteira — preço por faixa de quantidade, atacarejo) | ✅ | `ProdutoFaixaPreco` — **um conceito só** (quantidade mínima + vigência de data), gerenciado como agregado do `Produto` (lista, `orphanRemoval`) |
| `PRODUTO_IMAGEM` (tabela inteira — galeria, binário/URL, hash dedup) | ✅ (parcial) | `ProdutoImagem` modelada (URL + hash + principal + ordem), gerenciada como agregado do `Produto`. Upload direto de arquivo **ainda não existe** — hoje só aceita imagem já hospedada por URL; upload binário fica pra quando o GED (Fase 4) existir |
| `TabelaPreco`/`TabelaPrecoProduto` (do `fenix.sql`, não do PDV) | ✅ | Precificação por canal/perfil (Varejo/Atacado/VIP), mecanismo **complementar** a `ProdutoFaixaPreco` — CRUD em `/api/v1/tabelas-precos` |
| `ProdutoMarca` | 🆕 | Vem do `fenix.sql`, sem equivalente no PDV — mantém como enriquecimento, sem campo de sync |

**Pendências que sobraram do rework**: seed de demonstração de um `Produto` completo (não fiz — exigiria inventar também seed de `produto_unidade`/`produto_subgrupo`, que nenhuma migration anterior semeou).

## `CLIENTE`
Todos os gaps abaixo foram fechados (✅ = feito e verificado no código atual):

| Campo no PDV | Situação no ERP | Ação |
|---|---|---|
| `URL` (site) | ✅ | Campo `url` no `Cliente` |
| `FIDELIDADE_AVISO`/`FIDELIDADE_QUANTIDADE`/`FIDELIDADE_VALOR` (programa de fidelidade) | ✅ | `fidelidadeAviso`/`fidelidadeQuantidade`/`fidelidadeValor` no `Cliente` e `ClienteRequest` |
| `CODIGO_IBGE_UF` | ✅ | `codigoIbgeUf` no `Endereco` (junto de `codigoIbgeMunicipio`) |
| `FIADO_VALOR_TETO` | ✅ = `limiteCredito` | **Decidido**: mesmo conceito ("teto de crédito concedido ao cliente") — semântica equivalente confirmada, sem necessidade de campo/renomeação extra |
| `CONTATO` (nome do responsável) | ✅ (via `Contato` tipo RESPONSAVEL) | Confirmar mapeamento na hora do export (Fase 6) |
| Nome, fantasia, email, documentos, endereço, telefone/celular | ✅ | — |

## `EMPRESA` / `LOJA_CONFIGURACAO`
Todos os gaps abaixo foram fechados (✅ = feito e verificado no código atual):

| Campo no PDV | Situação no ERP | Ação |
|---|---|---|
| `TIPO_ESTABELECIMENTO` (varejo/restaurante...) | ✅ | Enum `TipoEstabelecimento` no `Empresa` |
| `CODIGO_IBGE_UF` | ✅ | Campo `codigoIbgeUf` no `Empresa` |
| `PERMITE_MESAS`/`PERMITE_COMANDAS`/`PERMITE_DELIVERY`/`MODO_COMANDA`/`PERMITE_MESA_AVULSA` (config operacional do terminal) | ✅ | `ConfiguracaoOperacionalLoja` (1:1 com `Empresa`, entidade própria — SRP), sub-recurso `GET/PUT /api/v1/empresas/{id}/configuracao-operacional`. O ERP provisiona a config; o PDV consome no primeiro sync do terminal (Fase 6) |
| Razão social, fantasia, CNPJ, IE, CRT, endereço, telefone | ✅ | — |

## `PRODUTO_FAIXA_PRECO` é UM conceito só — não confundir com dois
Faixa de preço por quantidade (atacarejo) e produto promocional (preço temporário com vigência) são **a mesma tabela**, não duas features separadas:
```
QUANTIDADE_MINIMA decimal   -- >1 = desconto por quantidade | =1 = preço vale pra qualquer quantidade
VALOR_UNITARIO    decimal   -- preço nesta faixa
ROTULO            varchar   -- "ATACADO", "CX FECHADA", "PROMOÇÃO"...
DATA_INICIO/FIM   date      -- vigência (NULL = sempre vale) — É ISSO que faz uma faixa virar "promoção"
ATIVO             char(1)
```
Um produto promocional é uma faixa com `QUANTIDADE_MINIMA=1` + `DATA_INICIO`/`DATA_FIM` preenchidos. Modelar como `ProdutoFaixaPreco` único (Fase 1b/produto), com quantidade mínima E vigência de data desde o desenho inicial — não como "faixa de quantidade" simples e depois tentar encaixar promoção por cima.

## Inventário completo — as 33 tabelas do `BANCO_PDV.md`, uma a uma
Motivo de existir esta lista: em passadas anteriores eu já deixei passar `PRODUTO_TIPO`/`PRODUTO_FAIXA_PRECO`/`PRODUTO_IMAGEM` e o alcance promocional de `PRODUTO_FAIXA_PRECO` por analisar em amostras, não a lista inteira. Esta tabela existe pra isso não voltar a acontecer — toda tabela do PDV tem uma linha, sem exceção.

| Tabela PDV | Destino no ERP |
|---|---|
| `CLIENTE` | cadastro-base `Cliente`/`Pessoa` (Fase 1a, gaps listados acima) |
| `COLABORADOR` | cadastro-base, especialização de `Pessoa` como `Cliente` — ✅ construído (Fase 1a), junto com `Fornecedor` (✅, mesmo padrão) |
| `PRODUTO_GRUPO`, `PRODUTO_SUBGRUPO`, `PRODUTO_TIPO`, `PRODUTO_UNIDADE` | cadastro-base, lookups de `Produto` (Fase 1a/1b) |
| `PRODUTO`, `PRODUTO_FAIXA_PRECO`, `PRODUTO_IMAGEM` | cadastro-base `Produto` (Fase 1b, gaps listados acima) |
| `TRIBUT_GRUPO_TRIBUTARIO`, `TRIBUT_OPERACAO_FISCAL` | cadastro-base, redesenhados como motor tributário completo (Fase 1b) |
| `LOJA_CONFIGURACAO` | cadastro-base `Empresa` + `ConfiguracaoOperacionalLoja` (Fase 1c, gaps listados acima) |
| `PDV_TIPO_PAGAMENTO` | cadastro-base — **gap novo, não modelado ainda**: comentário explícito no DDL "dados devem ser importados da retaguarda". Precisa de `FormaPagamento` no cadastro-base (Fase 1a/1c) — hoje não existe. |
| `PDV_CONFIGURACAO` | **misto**: campos de política comercial (`LIMITE_DESCONTO_ITEM_PERCENTUAL`, `LIMITE_DESCONTO_VENDA_PERCENTUAL`, `LIMITE_DESCONTO_VENDA_VALOR`, `LIMITE_ACRESCIMO_*`, `PERMITE_DESCONTO_ACIMA_LIMITE` — o comentário do próprio DDL diz "política da matriz") deveriam nascer no ERP (`ConfiguracaoOperacionalLoja`, Fase 1c) e ser empurrados pro terminal, não digitados um por um. O resto (impressora, TEF, gaveta, ACBr) é config técnica local do terminal, fica só no PDV. |
| `PDV_CONFIGURACAO_BALANCA` | Config técnica de hardware, só PDV — não entra no cadastro-base |
| `PDV_CAIXA`, `DISPOSITIVO_CAIXA` | Identidade operacional do terminal, só PDV |
| `PDV_OPERADOR` | Login/senha local do PDV — mapeia conceitualmente pro `Usuario` do cadastro-base (Fase 6, adaptar autenticação do PDV pro OAuth2, já registrado no roadmap) |
| `MESA` | Operacional de atendimento, só PDV — sem decisão ainda se número de mesas deve ser provisionado pelo ERP |
| `LICENCA_CACHE` | Cache local do último contato com o endpoint de licenciamento do ERP — endpoint em si é módulo futuro, fora do escopo atual |
| `PDV_MOVIMENTO`, `PDV_SANGRIA`, `PDV_SUPRIMENTO`, `PDV_CONFERENCIA_CAIXA`(+`_DETALHE`) | Operação de caixa gerada no PDV, **exportada pra retaguarda** (Fase 6) |
| `CONTA_ABERTA`(+`_ITEM`) | Atendimento (mesa/comanda/delivery), gerado no PDV, exportado (Fase 6) |
| `PDV_VENDA_CABECALHO`, `PDV_VENDA_DETALHE`(+`_HISTORICO`), `PDV_TOTAL_TIPO_PAGAMENTO`, `PDV_DEVOLUCAO` | Vendas/fiscal, gerado no PDV, exportado (Fase 6) — referencia `PDV_TIPO_PAGAMENTO`, que por sua vez vem do ERP |
| `ESTOQUE_MOVIMENTO` | Ledger de estoque gerado no PDV, exportado — mapeia pro `EstoqueMovimento` do futuro `controle-estoque-service` (Fase 2/6) |
| `CLIENTE_FIADO_LANCAMENTO` | Ledger de fiado gerado no PDV, exportado (Fase 6, módulo financeiro ainda não escopado) |

## `HASH_REGISTRO` — decidido (2026-08-23)
`HASH_REGISTRO` no PDV é gerado pelo `VendaService` antes de persistir, pra auditoria PAF-NFC-e de **movimento** (venda, sangria, suprimento) — não de cadastro. **Decisão do usuário**: não adicionar em `Produto`/`Cliente` agora; o cadastro-base já tem auditoria via `criadoPor`/`atualizadoPor`+`dataCriacao`/`dataAtualizacao` (`BaseEntity`). Fica registrado pro roadmap: quando um módulo de emissão fiscal completo (NFe/NFCe) entrar em escopo, o hash nasce lá, no movimento, no mesmo padrão do PDV — não retroativamente no cadastro.

# REFERÊNCIA: BANCO_PDV.md (DDL do cactus-pdv — frente de caixa)
Mapa das tabelas do PDV por área, para orientar o desenho do banco do ERP e da sincronização:
- **Cadastros base hoje residentes no PDV** (candidatos a nascer no ERP e ser replicados para o PDV): `CLIENTE`, `COLABORADOR`, `PRODUTO` (+ `PRODUTO_GRUPO`/`PRODUTO_SUBGRUPO`/`PRODUTO_TIPO`/`PRODUTO_UNIDADE`/`PRODUTO_FAIXA_PRECO`/`PRODUTO_IMAGEM`), `TRIBUT_GRUPO_TRIBUTARIO`, `TRIBUT_OPERACAO_FISCAL`, `PDV_TIPO_PAGAMENTO` — este último já tem o comentário explícito no DDL: "Dados devem ser importados da retaguarda".
- **Operação de caixa/loja** (gerada no PDV): `PDV_CAIXA`, `DISPOSITIVO_CAIXA`, `PDV_CONFIGURACAO` (+ `_BALANCA`), `PDV_OPERADOR`, `PDV_MOVIMENTO` (abertura/fechamento de turno de caixa), `PDV_SANGRIA`, `PDV_SUPRIMENTO`, `PDV_CONFERENCIA_CAIXA` (+ `_DETALHE`). `PDV_SANGRIA` e `PDV_SUPRIMENTO` têm comentário explícito "Dados devem ser exportados para a retaguarda".
- **Vendas e fiscal**: `PDV_VENDA_CABECALHO`, `PDV_VENDA_DETALHE` (+ `_HISTORICO`, versionado), `PDV_TOTAL_TIPO_PAGAMENTO` (também "exportado para a retaguarda"), `PDV_DEVOLUCAO`. Campos fiscais cobrem NFC-e/SAT/MFE (`STATUS_FISCAL`, `CHAVE_ACESSO_FISCAL`, `PROTOCOLO_AUTORIZACAO`) e há `HASH_REGISTRO` (SHA-256) em várias tabelas para auditoria PAF-NFC-e.
- **Atendimento (mesa/comanda/delivery)**: `MESA`, `CONTA_ABERTA` (+ `_ITEM`) — controla contas abertas antes de virarem venda fechada.
- **Ledgers append-only**: `ESTOQUE_MOVIMENTO` (toda alteração de `PRODUTO.QUANTIDADE_ESTOQUE` gera um registro; tipos E/S/A/P/D) e `CLIENTE_FIADO_LANCAMENTO` (saldo devedor do cliente, tipos C/P) — ambos guardam saldo anterior/novo a cada lançamento.
- **Licenciamento**: `LICENCA_CACHE` — cache local no PDV do último contato bem-sucedido com o endpoint de licenciamento do ERP; o comentário do DDL referencia um `LICENCIAMENTO.md` que não existe neste repositório (provavelmente vive no repo do cactus-pdv).

## Padrão de sincronização ERP ↔ PDV observado no DDL
- **ERP → PDV** (o PDV importa/consome): cadastros base — produto, cliente, tabelas fiscais, formas de pagamento, configuração de licenciamento.
- **PDV → ERP** (o PDV exporta/gera): tudo que é operação de caixa — sangria, suprimento, movimento de caixa, vendas, totais por forma de pagamento, movimentação de estoque, lançamentos de fiado. É esse fluxo que o ERP precisa consolidar para BI, estoque e financeiro.
- **IDs locais por dispositivo**: a maioria das FKs no PDV é `int unsigned AUTO_INCREMENT` local ao banco daquele caixa/loja; várias tabelas de movimento já carregam `ID_DISPOSITIVO`/`ID_GERADO_DISPOSITIVO` para rastrear a origem. Ao desenhar a sincronização, o ID do PDV não deve ser reaproveitado como chave primária no ERP — é preciso uma chave de sincronismo própria (ex.: UUID ou identificador composto loja+dispositivo+ID local).
