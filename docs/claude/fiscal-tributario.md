<!-- Conteúdo movido do CLAUDE.md original em 2026-10-31, sem nenhuma alteração. Linhas originais: 51-103, 760-891 -->

# ANÁLISE TRIBUTÁRIA — Produto e regras fiscais (revisão crítica)

A primeira versão do `Produto` do cadastro-base tinha um campo solto `codigoNcm` (string, sem FK) e nenhum modelo de alíquota. Isso não serve para um ERP vendido comercialmente que precisa atender ao âmbito fiscal completo — o `PRODUTO` do cactus-pdv (que é o sistema de frente de caixa **em produção**, não um legado a ser descartado) já é bem mais completo do que isso, e o `fenix.sql` mostra o desenho correto do motor por trás. Análise das duas fontes:

## `PRODUTO` no cactus-pdv (o que ele espera receber já pronto)
Além dos campos comerciais (GTIN, código interno, valor de compra/venda/custo — estoque ali é um contador flat único, sem depósito, o que também não é o modelo certo, ver seção de Estoque), `PRODUTO` carrega um conjunto fiscal **pré-resolvido**, pronto para imprimir o cupom sem calcular nada em tempo de venda: `CODIGO_NCM`, `CODIGO_CEST`, `CST`, `CSOSN`, `TAXA_ICMS`, `TAXA_IPI`, `TAXA_PIS`, `TAXA_COFINS`, `TAXA_ISSQN`, `IAT` (A=Arredondamento/T=Truncamento), `IPPT` (P=Própria/T=Terceiro — mercadoria em consignação), `TIPO_ITEM_SPED`, `PAF_P_ST`, `HASH_REGISTRO` (auditoria PAF-NFC-e). O PDV também já tem `TRIBUT_GRUPO_TRIBUTARIO` e `TRIBUT_OPERACAO_FISCAL`, mas como tabelas simples (descrição + CFOP) — sem o motor de cálculo por trás; lá elas só existem pra dar contexto ao produto, quem resolve a alíquota de fato é o ERP.

Isso confirma o padrão de sincronização: **o PDV precisa de um resultado tributário já achatado por produto** (rápido, sem join, pronto pro cupom) — mas quem **calcula** esse resultado, considerando todos os cenários (venda interestadual, substituição tributária, etc.), tem que ser o ERP.

## Motor tributário do fenix.sql (o desenho certo a seguir)
fenix.sql não guarda alíquota solta no produto. A cadeia é:

```
Produto ──> TRIBUT_GRUPO_TRIBUTARIO (perfil tributário do produto: "mercadoria própria
             sujeita a ICMS-ST", "revenda sem ST" etc. + origem da mercadoria 0-9)
                    │
                    × TRIBUT_OPERACAO_FISCAL (tipo de venda: consumidor final, revenda,
                      interestadual... cada um com seu CFOP)
                    │
                    ▼
             TRIBUT_CONFIGURA_OF_GT  ── a célula da matriz Grupo Tributário × Operação Fiscal
                    │
                    ├──> TRIBUT_ICMS_UF  (1 linha POR UF DE DESTINO dentro da célula — é aqui
                    │     que mora a complexidade real do ICMS: alíquota interna, alíquota
                    │     interestadual, MVA, base de cálculo ST, tudo variando por estado)
                    ├──> TRIBUT_PIS      (1:1 com a célula — CST + modalidade de cálculo
                    ├──> TRIBUT_COFINS      percentual/unidade + alíquota)
                    └──> TRIBUT_IPI

TRIBUT_OPERACAO_FISCAL ──> TRIBUT_ISS  (1:1 direto com a operação, não com a célula — serviço)
```

Esse é o desenho usado por sistemas de mercado (TOTVS, SAP Business One, Sankhya) porque o ICMS **varia por UF de destino** — não dá pra ter uma "taxa_icms" fixa gravada no produto se a empresa vende pra fora do estado, e substituição tributária (MVA, base de cálculo ST) só existe combinando produto × operação × UF.

## Modelo tributário do Cactus ERP (proposta, substitui o `codigoNcm` solto)
O cadastro-base passa a ser dono do **motor completo**, e resolve/achata o resultado na hora de exportar pro PDV (Fase de sincronização) — não o contrário.

Catálogos de referência (dado público, carga inicial via Flyway/importação da tabela oficial da Receita/SEFAZ, só leitura via API):
- `Ncm` (código 8 dígitos, descrição), `Cfop` (código, descrição, aplicação), `Cest` (código 7 dígitos, descrição, NCM associado).
- `CstIcms`, `CstIpi`, `CstPis`, `CstCofins`, `Csosn` — tabelas de código/descrição da legislação (não mudam por empresa).

Modelo de negócio (por empresa, dono real da regra):
- `GrupoTributario` (nome, origemMercadoria 0-9) — perfil tributário do produto.
- `OperacaoFiscal` (nome, descricaoNaNf, cfop) — tipo de venda/operação.
- `ConfiguracaoTributaria` — célula `GrupoTributario × OperacaoFiscal` (chave composta única).
- `IcmsPorUf` — N:1 com `ConfiguracaoTributaria`, 1 linha por UF destino: cstIcms/csosn, modalidadeBaseCalculo, aliquota, mva, aliquotaInternaSt, aliquotaInterestadualSt, baseCalculoSt.
- `PisConfiguracao`, `CofinsConfiguracao`, `IpiConfiguracao` — 1:1 com `ConfiguracaoTributaria`: cst, modalidadeBaseCalculo (percentual/unidade), aliquotaPercentual, aliquotaUnidade.
- `IssConfiguracao` — 1:1 com `OperacaoFiscal`: modalidadeBaseCalculo, aliquota, itemListaServico, codigoTributacao.
- `Produto` ganha `grupoTributarioId` (FK, obrigatório) + `ncmId`/`cestId` (FK, não mais string solta) — a alíquota de fato só é resolvida na hora de vender/faturar (`ConfiguracaoTributaria` pela combinação grupo do produto × operação da venda × UF do destinatário), exatamente como no fenix.sql.
- Endpoint de resolução (`POST /api/v1/tributacao/resolver` ou equivalente) recebendo produto+operação+UF destino e devolvendo o resultado achatado — é esse resultado que alimenta `TAXA_ICMS/TAXA_IPI/TAXA_PIS/TAXA_COFINS/TAXA_ISSQN/CST/CSOSN` do PDV na sincronização.

Isso é bem mais trabalho que o `codigoNcm` solto que existia — é o preço de ser "sistema de ponta" no âmbito fiscal. Entra como item próprio no roadmap revisado (não é um adendo pequeno ao Cadastro Base).

# ANÁLISE: MÓDULO CONTÁBIL E FISCAL (2026-09-13, pré-planejamento das Fases 14-17)

Pedido explícito do usuário: desenvolver o módulo Contábil e Fiscal cobrindo Tributação (validação/melhoria do que já existe), SPED Contábil e Fiscal, e MDF-e — com varredura do `fenix.sql` igual às análises anteriores (Loja Virtual, Módulo Comercial), validação contra ERPs de mercado, atualização dos `.md`, e exigência explícita de rigor ("não podemos admitir erros"). Mesmo padrão já estabelecido: pensar a arquitetura primeiro, registrar em `.md`, confirmar decisões-chave com o usuário, só codar depois.

## Varredura `fenix.sql` — resultado: módulo Contábil é greenfield puro

Grep exaustivo (`CREATE TABLE`, e depois palavras-chave soltas fora de nome de tabela) por `plano_conta`/`lancamento_contabil`/`partida_dobrada`/`centro_custo`/`balancete`/`razao`/`diario`/`conta_contabil`/`mdfe`/`mdf_e`/`ecd`/`efd`: **zero tabelas reais de contabilidade e zero de MDF-e** — o único acerto de `"balancete"` é um comentário de campo (`DATA_BALANCETE`) solto em outra tabela, sem tabela de lançamento contábil nenhuma por trás. `CONTADOR`/`CNAE` já têm equivalente construído desde a Fase 1c (`Contador`/`Empresa.codigoCnaePrincipal`), sem gap.

O que o `fenix.sql` TEM, relacionado a SPED, são só **6 `VIEW`s de leitura** (`view_sped_c190`, `view_sped_nfe_cabecalho/detalhe/emitente/destinatario/item`) — não registros do arquivo digital, são consultas que **achatam dados de `NFE_CABECALHO`/`NFE_DETALHE`** num formato próximo ao Bloco C da EFD ICMS/IPI (registro C190 aparece por nome). Isso é um dado importante pro desenho: confirma que **SPED Fiscal (EFD ICMS/IPI e EFD Contribuições) é essencialmente um export/agregação sobre dados que o `fiscal-service` já captura** (`NotaFiscalEletronica`/`NotaFiscalEletronicaItem`, com ICMS/IPI/PIS/COFINS por item desde a Fase 8) — não exige modelagem de domínio nova, só a camada de layout posicional (mesma classe de trabalho já feita pra CNAB 240/OFX/ACBr .ini). **SPED Contábil (ECD)**, ao contrário, depende de um livro-caixa de partida dobrada que **não existe em lugar nenhum do monorepo** — é greenfield igual o CRM foi, só que aqui nem o `fenix.sql` tem uma referência pra cruzar campo a campo (diferente de CRM, que ao menos tinha `Cliente`/`Vendedor` prontos pra se ligar).

## Camada de conhecimento geral (não `fenix.sql`) — SPED, pesquisada via WebSearch, honestidade sobre confiança

Como o `fenix.sql` não modela nada disso, o desenho abaixo vem de pesquisa geral sobre os leiautes oficiais da Receita Federal — mesmo padrão de honestidade já usado pra ACBr/OFX/CNAB 240 (estrutura de blocos/registros confirmada por múltiplas fontes secundárias — Tecnospeed, e-Auditoria, VRI Consulting, Taxshape — e pelo próprio Guia Prático oficial linkado nos resultados —, mas o preenchimento campo-a-campo de cada registro nunca foi testado contra um validador real da Receita nesta sessão):

- **SPED Contábil (ECD)** — Bloco I é o núcleo: `I050` (plano de contas da empresa) → `I051` (vínculo com o plano de contas referencial da RFB, só contas analíticas) → `I100` (centro de custo) → `I150`/`I155` (saldos periódicos, cabeçalho+detalhe, só contas com movimento) → `I200`+`I250` (lançamento contábil + suas partidas — débito/crédito) → `I157` (transferência de saldo quando o plano de contas mudou no período). [Registros da ECD — VRI Consulting](https://www.vriconsulting.com.br/guias/guiasIndex.php?idGuia=669), [Bloco I — Taxshape](https://taxshape.com/especial-sped/manual-de-orientacao-da-ecd-layout-7-sped-online/capitulo-3-e-bloco-i-manual-de-orientacao-layout-7-ecd/).
- **SPED Fiscal (EFD ICMS/IPI)** — Bloco 0 (cadastro/abertura) / Bloco C (documentos fiscais de mercadoria — NF-e, é daqui que vem o `view_sped_c190` do fenix) / Bloco D (transporte/comunicação — CT-e) / Bloco E (apuração de ICMS/IPI) / Bloco G (CIAP — crédito do ativo permanente) / Bloco H (inventário físico) / Bloco K (controle de produção/estoque). [Guia Prático oficial RFB/SPED](http://sped.rfb.gov.br/estatico/D3/E8079AF711D6FCC64E5890ECD756C90A2E9B74/GUIA_PRATICO_EFD_ICMS_IPI_Versao2.0.14.pdf), [Tecnospeed](https://blog.tecnospeed.com.br/sped-fiscal-efd-icms-ipi/).
- **EFD Contribuições (PIS/COFINS/CPRB)** — Bloco A (NFS-e/ISS) / Bloco C (mercadoria, ICMS) / Bloco D (transporte/comunicação) / Bloco F (demais operações, quando não cabem em A/C/D) / Bloco M (apuração de PIS/COFINS) / Bloco P (CPRB, apurada separada). [Jornal Contábil](https://jornalcontabil.com.br/noticia/quais-os-blocos-da-efd-contribuicoes-e-suas-respectivas-funcoes/).
- **MDF-e** — comando ACBr **de alta confiança, mesma família de `NFE.CriarEnviarNFe`/`CTE.CriarEnviarCTe`** (tiro único: gera XML + envia + autoriza): `MDFE.CriarEnviarMDFe(cIniMDFe,[nLote],[nImprimir],[nImpressora],[bAssincrono],[bPreview],[nCopias],[bGerarPDF],[bEncerrado])`. [ACBr — MDFe.CriarEnviarMDFe](https://acbr.sourceforge.io/ACBrMonitor/MDFeCriarEnviarMDFe.html). Confirma a decisão já registrada em `ANALISE_SWCONSULTORIA_VS_ACBR.md` ("se/quando MDF-e entrar em escopo, ir de ACBr, não `java-mdfe`" — abandonado desde 2019).

## Achado crítico de Tributação — Reforma Tributária (IBS/CBS/Imposto Seletivo) já é exigência VENCIDA, não mais risco futuro

O `CLAUDE.md` já registrava esse risco desde a Fase 8 ("acompanhar quando a emissão real for validada de verdade") como algo distante. **Pesquisa nesta sessão (2026-09-13) mostra que não é mais distante**: a Nota Técnica 2025.002 (NF-e/NFC-e) tornou os campos de IBS/CBS **obrigatórios em produção desde 03/ago/2026** pro Regime Normal (Lucro Presumido/Real) — já passado, na data desta sessão —, com Simples Nacional/MEI entrando em 04/jan/2027. [Legisweb](https://www.legisweb.com.br/noticia/?id=31719), [Tecnospeed](https://blog.tecnospeed.com.br/nota-tecnica-reforma-tributaria-nfe-nfce/).

O modelo novo é **bem mais rico** que o `IcmsPorUf`/`Pis`/`Cofins`/`Ipi` atuais (Fase 1b) — não é um enum novo, é um subsistema: novo Grupo `UB` no XML com `CST`(código de situação tributária **próprio** do IBS/CBS, distinto de `CstIcms`/`Csosn`)/`cClassTrib`(classificação tributária) + subgrupos `IBSUF`(alíquota estadual, diferimento, redução, devolução)/`IBSMun`(mesmos campos, municipal)/`CBS`(federal, + `indDoacao`) + `gIBSCBSMono`(tributação monofásica — combustíveis/cigarros)/`gALCZFMCBS`(Zona Franca de Manaus/Área de Livre Comércio)/`gTransfCred`(transferência de créditos)/`gCredPresOper`(crédito presumido)/`gAjusteCompet`/`gEstornoCred`, mais um novo grupo de totais (`W03`) por documento. **Transparência**: o detalhamento campo-a-campo de CADA subgrupo (nomes exatos de todo campo dentro de `IBSUF`/`CBS`/`gIBSCBSMono`, os valores válidos de `cClassTrib`, as regras de cálculo dual durante a transição 2026-2033) exigiria uma pesquisa própria e dedicada, bem mais profunda que a rodada desta análise — **não modelado ainda, decisão de escopo pendente com o usuário abaixo**, pra não modelar campo fiscal real "no chute" (violaria diretamente "não podemos admitir erros").

## Arquitetura proposta

```
contabilidade-service (NOVO)                    fiscal-service (EXTENSÃO)
  PlanoConta (referencial RFB + da empresa)        MdfeCabecalho + MdfeDocumentoVinculado
  CentroCusto                                        (agrupa N CteCabecalho já emitidos —
  LancamentoContabil + PartidaContabil                mesma família ACBr de alta confiança)
    (débito/crédito, partida dobrada validada)
  SaldoContaPeriodo (snapshot por período)         SpedFiscalExportService
  SpedContabilExportService (ECD, Bloco I)           (EFD ICMS/IPI Bloco 0/C/D/E/G/H/K +
                                                       EFD Contribuições Bloco A/C/D/F/M/P —
                                                       ambos agregando NotaFiscalEletronica/
                                                       CteCabecalho já existentes, sem
                                                       domínio novo, só camada de layout)

cadastro-base-service (EXTENSÃO, se aprovado)
  Motor tributário ganha o subsistema IBS/CBS/IS
  (fase própria, pesquisa dedicada antes de codar)
```

**Por que `contabilidade-service` novo, não dentro de `financeiro-service`**: Contas a Pagar/Receber (regime de caixa, já construído) e Livro Contábil (regime de competência, partida dobrada) são dois livros com regras, leiautes de exportação (CNAB/OFX vs. SPED/ECD) e ciclos de vida totalmente diferentes — misturar no mesmo serviço violaria a responsabilidade única já respeitada em todo o resto do monorepo (mesmo critério que separou `seguranca-service` de `cadastro-base-service`). O `financeiro-service` fica como um CONSUMIDOR eventual do `contabilidade-service` (baixa de título → lançamento contábil automático), não o dono do livro.

**Por que MDF-e entra no `fiscal-service`, não um serviço novo**: mesma família de documento fiscal eletrônico (XML + ACBr + numeração por empresa) que NF-e/NFC-e/CT-e/NFS-e já vivem lá — replicar a infraestrutura de transporte ACBr (`common.fiscal.AcbrMonitorClient`, já genérico) e o padrão de `EmpresaConfiguracaoFiscal` (série/número/certificado) num serviço novo seria duplicação sem ganho.

## Decisões que precisam de confirmação do usuário antes de codar

Registradas abaixo como perguntas — ver `AskUserQuestion` desta mesma resposta.

1. **Reforma Tributária (IBS/CBS/IS)**: entra nesta mesma leva de trabalho (exigindo uma rodada de pesquisa própria, mais profunda, antes de qualquer código) ou fica registrada como achado crítico e vira uma fase própria futura, com a leva atual focando só no que o usuário pediu explicitamente (Tributação-validação-geral/SPED/MDF-e)?
2. **Contabilização automática** (lançamento contábil gerado sozinho a partir de Venda/Compra/Baixa financeira, padrão SAP FI/TOTVS Protheus de "regra de contabilização por natureza de evento") — entra já na primeira fatia do módulo Contábil, ou o MVP é só o livro manual (Plano de Contas + Lançamento com partida dobrada validada + Balancete/Razão + export ECD) com a automação ficando pra uma sub-fase 2?

**Decisões confirmadas com o usuário (2026-09-13)**: (1) IBS/CBS/IS fica registrado como achado crítico (ver acima) e vira fase própria futura — não entra nesta leva; a leva atual foca em Tributação-validação-geral (sem IBS/CBS)/SPED/MDF-e. (2) Contábil nasce manual (Plano de Contas + Centro de Custo + Lançamento com partida dobrada validada + Balancete/Razão + export ECD) — contabilização automática por evento de negócio fica pra uma sub-fase 2 futura, registrada no BACKLOG.

## Roadmap desta leva (Fases 14-16, numeração sequencial ao Módulo Comercial)

- **Fase 14 — `contabilidade-service` (novo)** ✅ **feita por completo, validada ao vivo (2026-09-13)**: Plano de Contas (hierárquico, sintética/analítica) → Centro de Custo → Lançamento Contábil + Partida (débito/crédito, partida dobrada validada no servidor) → Balancete/Razão (sob demanda, sem snapshot) → export SPED Contábil (ECD, Bloco I). Módulo novo (porta 8094, schema `cactus_contabilidade`, package `br.com.cactusdigital.erp.contabilidade`), Resource Server puro, **sem cliente OAuth2 de serviço nesta fase** (lançamento é sempre manual — contabilização automática por evento de negócio é sub-fase 2 futura, registrada no BACKLOG), 4 entidades, 5 controllers, 1 migration de schema + 1 de seed demo.
  - **Domínio**: `PlanoConta` (auto-referência via `contaPai`, mesmo padrão de `PastaDocumento` — validação de ciclo ao trocar o pai, subindo a cadeia de ancestrais; só conta **analítica** pode receber lançamento, só conta **sintética** pode ser pai de outra) → `CentroCusto` (catálogo flat) → `LancamentoContabil` (cabeçalho, imutável depois de lançado — sem `PUT`, só `POST .../estornar`, mesmo critério de `EstoqueMovimento`/`BaixaContaReceber`) → `PartidaContabil` (débito/crédito, `valor` sempre positivo, direção pelo enum).
  - **A validação mais crítica do módulo, no servidor, nunca só no cliente**: soma dos débitos tem que ser EXATAMENTE igual à soma dos créditos (`BigDecimal.compareTo`, sem tolerância de arredondamento) — "não podemos admitir erros" é literal aqui, um lançamento desbalanceado corrompe todo Balancete/Razão/SPED gerado a partir dele.
  - **Estorno, não edição**: gera um lançamento NOVO com débito↔crédito invertidos (`switch` expression), vínculo bidirecional (`estornoDeId`/`estornadoPorId`) — o original nunca é apagado/alterado, só marcado `ESTORNADO`.
  - **Relatórios sob demanda** (`RelatorioContabilService`): Balancete (Streams — `groupingBy`/`reduce` implícito via agregação SQL + `Comparator`) mostra saldo já ajustado pela `NaturezaConta` da conta (convenção de exibição — "positivo = a favor da natureza esperada"); Razão usa saldo corrido calculado num loop sequencial simples (mais claro que forçar um `reduce` com estado externo pra uma acumulação inerentemente sequencial).
  - **SPED ECD (Bloco I)**: `SpedLineBuilder` (formato pipe-delimited, diferente do posicional fixo do CNAB 240) + `SpedContabilExportService` monta `0000`/`I050`/`I100`/`I150`/`I155`/`I200`/`I250`/`9999`. **Transparência, mesmo padrão de honestidade já usado pra ACBr/OFX/CNAB 240**: estrutura de blocos confirmada por pesquisa (WebSearch, múltiplas fontes secundárias + Guia Prático oficial linkado), layout campo-a-campo NUNCA validado contra o PVA da Receita; `0000` não resolve dados cadastrais da empresa (sem client cross-serviço pro cadastro-base-service nesta fase); `COD_NAT` do `I050` usa o nome do enum (`"ATIVO"`), não o código numérico oficial (01-09) do Ato COTEPE — não confirmado nesta sessão, inventá-lo seria a "alucinação" fiscal que o projeto se recusa a fazer.
  - **2 BUGS REAIS encontrados e corrigidos na validação ao vivo** (nenhum em `mvn compile`):
    1. **Indicador D/C do registro `I155` invertido pra toda conta `CREDORA`**: a primeira versão calculava o indicador a partir do saldo já ajustado pela natureza (útil pro Balancete/Razão), mas o campo `IND_DC` da ECD precisa da comparação CRUA débito−crédito, independente da natureza. Pra conta `DEVEDORA` os dois cálculos coincidem (mascarou o bug nos primeiros testes — Caixa/Despesas saíram "certos" por acaso); só apareceu de verdade testando uma conta `CREDORA` real ("Fornecedores a Pagar", saldo credor de R$250,50 saindo com indicador `D`, errado). Corrigido calculando o indicador a partir do débito−crédito cru, sem envolver `NaturezaConta`.
    2. **Mesma classe de bug já documentada em `ProdutoImagem.ordem`/`FormaPagamento.tefTipoGp` (cadastro-base-service), agora em `situacao`**: `ContabilidadeMapper.toEntity(PlanoContaRequest)`/`toEntity(CentroCustoRequest)` copiavam o `null` do request (campo opcional) por cima do `@Builder.Default` da entidade — que só se aplica via `.builder()`, não via o construtor sem argumentos que o MapStruct usa — estourando `DataIntegrityViolationException` (`situacao NOT NULL`) ao criar uma conta/centro de custo sem informar `situacao` explicitamente. Corrigido com `@Mapping(defaultValue = "ATIVO")` no `toEntity`; nos métodos `atualizar` (update, `@MappingTarget`) o fix é `NullValuePropertyMappingStrategy.IGNORE` em vez de `defaultValue` — omitir o campo num `PUT` precisa preservar a situação atual, nunca reativar silenciosamente um registro inativo.
  - **RBAC, deliberadamente diferente de `FINANCEIRO_*`**: `CONTABIL_CONSULTAR`/`CONTABIL_LANCAR`/`CONTABIL_GERENCIAR` (`V17__seed_contabilidade_funcionalidades.sql`, seguranca-service) — ADMIN/GERENTE tudo, SUPERVISOR consulta+lança, **OPERADOR só consulta** (diferente de `FINANCEIRO_LANCAR`, onde OPERADOR também lança — registrar uma baixa é rotina de qualquer operador, lançamento contábil exige conhecimento contábil especializado).
  - Validado ao vivo fim a fim, com usuários de teste reais criados via SQL direto (removidos ao final): seed demo (plano de contas 5 grupos/13 contas + 2 centros de custo pra empresa 1) → lançamento de venda a vista (débito Caixa/crédito Receita, R$100) → 3 validações de erro (partida divergente 422, só 1 partida 422, conta sintética usada em partida 422) → Balancete/Razão corretos → estorno (partidas invertidas, original `ESTORNADO`, `estornadoPorId` preenchido) → estornar de novo rejeitado (409) → estornar um estorno rejeitado (422) → segundo lançamento (compra a prazo, débito Despesas/crédito Fornecedores) → export SPED ECD com os 2 lançamentos + saldos periódicos corretos (incl. os 2 bugs acima, achados e corrigidos nesta mesma rodada) → guards de `PlanoConta` (código duplicado 409, pai não-sintética 422, ciclo de hierarquia 422, exclusão bloqueada por movimento/subconta 409) → guards de `CentroCusto` (código duplicado 409) → RBAC OPERADOR real (200 consulta, 403 real em lançar e em gerenciar plano de contas). Reactor de 15 módulos compila limpo.
  - **Fora de escopo desta fase, registrado**: contabilização automática por evento de negócio (Venda/Compra/Baixa financeira — sub-fase 2 futura); resolução de dados cadastrais da empresa no registro `0000` do SPED; código numérico oficial `COD_NAT` (01-09); plano de contas referencial completo da RFB pra vínculo em `I051`.
- **Fase 15 — SPED Fiscal + EFD Contribuições (extensão do `fiscal-service`)** ✅ **feita por completo, validada ao vivo (2026-09-20)**: export EFD ICMS/IPI (Blocos 0/C/D/E) e EFD Contribuições (Blocos 0/A/C/M), agregando `NotaFiscalEletronica`/`NotaFiscalEletronicaItem`/`CteCabecalho`/`NotaFiscalServico` já existentes — sem entidade de domínio nova, só a camada de layout (`SpedFiscalIcmsIpiExportService`/`SpedContribuicoesExportService`, novo pacote `sped/` no `fiscal-service`) + 1 controller novo (`SpedFiscalController`, 2 operações). Nenhuma migration nova (export é leitura pura sobre schema já existente).
  - **`common.sped.SpedLineBuilder` promovido de `contabilidade-service` pra `common`** (mesmo mecanismo pipe-delimited da Fase 14, `.campo()`/`.data()`/`.valor()`) — segundo consumidor real, confirma que o formato generaliza sem ajuste. `contabilidade-service` continua usando a mesma classe, agora via `common`.
  - **EFD ICMS/IPI** (`SpedFiscalIcmsIpiExportService.gerarEfdIcmsIpi`): Bloco 0 (`0000`/`0001`) / Bloco C — NF-e saídas (`C001`/`C100`/`C170`/`C190`/`C990`, analítico agrupado por CST-CFOP-alíquota via `LinhaAnaliticaIcms`, record tipado) / Bloco D — CT-e saídas (`D001`/`D100`/`D190`/`D990`) / Bloco E — apuração de débitos (`E001`/`E100`/`E110`/`E200`/`E210`/`E990`, sem créditos de entrada) / `9999`. **Recorte de escopo, registrado**: só documentos `AUTORIZADA`, só saídas (sem NF-e de entrada importada), sem Blocos G/H/K (CIAP/inventário/produção — sem fonte de dado), apuração sem créditos.
  - **EFD Contribuições** (`SpedContribuicoesExportService.gerarEfdContribuicoes`): Bloco 0 / Bloco A — NFS-e (`A001`/`A100`/`A170`/`A990`, PIS/COFINS de serviço ficam zerados — `NotaFiscalServicoItem` só captura ISS) / Bloco C — NF-e mercadoria com campos PIS/COFINS (`C001`/`C100`/`C170`/`C990`) / Bloco M — apuração PIS (`M001`/`M200`/`M210`, agrupado por CST via `LinhaApuracaoContribuicao`) + COFINS (`M600`/`M610`) fechado por um único `M990` (Bloco M inteiro, PIS e COFINS são seções dele, não blocos próprios) / `9999`. **Recorte de escopo, registrado**: regime não-cumulativo assumido (sem rastreio cumulativo×não-cumulativo por empresa), sem Bloco D/F (CT-e/demais operações — sem PIS/COFINS capturado em `CteCabecalho`), sem créditos de entrada nem Bloco P (CPRB).
  - **2 bugs reais, autocorrigidos ANTES de qualquer compilação** (mesma disciplina de autorrevisão já demonstrada na Fase 14, "não podemos admitir erros" levada à prática, não só ao discurso):
    1. **`C190`/`D190` com ordem de campo trocada e alíquota sem formatação decimal**: primeira versão agrupava os itens num `List<String>` frágil e remontava a linha com `VL_OPR` antes de `ALIQ_ICMS` (ordem errada) e a alíquota passada por `.campo()` em vez de `.valor()` (perdendo a formatação com vírgula). Corrigido introduzindo `LinhaAnaliticaIcms`/`LinhaApuracaoContribuicao` (records tipados) antes de qualquer compilação — elimina a classe de erro pela raiz (acesso nomeado, não posicional).
    2. **Bloco M do EFD Contribuições com registro de abertura duplicado e `M990` fechando só a seção PIS**: a primeira versão emitia um `M600` extra como se fosse um "abertura" da seção COFINS (espelhando `M001`→`M200` do PIS), quando na realidade `M001` abre o bloco M INTEIRO uma única vez — PIS e COFINS são seções dele, não blocos próprios — e fechava com um `M990` por seção em vez de um só pro bloco inteiro. Corrigido reescrevendo `adicionarBlocoM` com um contador de linhas único, `M001` uma vez só e um único `M990` contando todas as linhas M emitidas (M001+M200+N×M210+M600+N×M610+M990).
  - **Achado real, corrigido durante a escrita (compile error genuíno)**: `NotaFiscalEletronicaItem` não tem campo `cstIpi` (só `aliquotaIpi`/`valorIpi`) — assumido erroneamente ao montar o `C170`; corrigido removendo a chamada e deixando `CST_IPI` em branco com comentário explícito, em vez de inventar um valor.
  - **RBAC**: reaproveita `FISCAL_CONSULTAR` já seedado na Fase 8 — sem migration de RBAC nova (export é consulta, mesmo critério de `SpedContabilController`).
  - Validado ao vivo fim a fim, contra o banco real, com 2 usuários de teste criados via SQL direto (removidos ao final): reactor de 15 módulos compila limpo; `seguranca-service`/`cadastro-base-service`/`fiscal-service` subidos (Docker Desktop e os containers `cactus-mysql`/`cactus-rabbitmq` também precisaram ser religados, mesmo achado operacional já registrado em fases anteriores) → 1 NF-e + 1 CT-e + 1 NFS-e (já seedados como `RASCUNHO` de fases de teste anteriores) flipados via SQL direto pra `AUTORIZADA` com `data_emissao`/CFOP/CST preenchidos (nenhum documento deste ambiente jamais chegou a `AUTORIZADA` pelo fluxo real de emissão — mesma limitação já registrada nas Fases 8/9/11, contornada com a mesma técnica) → `GET /sped/efd-icms-ipi` devolveu Blocos 0/C/D/E corretos com os valores reais da nota/CT-e (ICMS 8,09/IPI branco/ICMS do frete 4,20) → `GET /sped/efd-contribuicoes` devolveu Blocos 0/A/C/M corretos (PIS 0,74/COFINS 3,42, `M990` contando as 6 linhas certas do bloco M) → RBAC confirmado (200 com `FISCAL_CONSULTAR`, 403 real nos dois endpoints com usuário `ENTREGADOR`, que não tem nenhuma `FISCAL_*`) → flips de teste revertidos pra `RASCUNHO` ao final, usuários de teste removidos.
  - **Fora de escopo, registrado**: cancelamento/complementar/substituto de qualquer documento; Blocos G/H/K (ICMS/IPI) e D/F/P (Contribuições); código numérico oficial de alguns campos ainda não confirmados (`COD_VER`, `IND_NAT_PJ`, `IND_ATIV`); frontend (`cactus-erp-web`) de disparo do export.
- **Fase 16 — MDF-e (extensão do `fiscal-service`)**: `MdfeCabecalho`+`MdfeDocumentoVinculado`, agrupando N `CteCabecalho` já emitidos numa viagem — mesma família ACBr de alta confiança de NF-e/CT-e (`MDFE.CriarEnviarMDFe`, tiro único), nunca invocado contra o ACBr Monitor real (mesma decisão de segurança já em vigor pras Fases 8/9/11). ✅ **Backend feito, validado ao vivo (2026-10-24)**.
  - **O que já existia de uma sessão anterior (nunca registrado aqui)**: `V7__mdfe.sql` (fiscal-service) + entidades `MdfeCabecalho`/`MdfeDocumentoVinculado`, `V12__mdfe_numeracao.sql` (cadastro-base-service, `serieMdfe`/`proximoNumeroMdfe` + `POST .../reservar-numero-mdfe`) e `CadastroClient#reservarProximoNumeroMdfe`. Construído nesta rodada: `MdfeCabecalhoRepository`, DTOs (`GerarMdfeRequest`/`MdfeCabecalhoResponse`/`MdfeDocumentoVinculadoResponse`), mapper, `MdfeIniBuilder`, `MdfeCabecalhoService`, `MdfeCabecalhoController` (`/api/v1/mdfes`: listar/buscar/gerar/emitir, 4 operações, 39 no serviço, zero sem tag) e o primeiro teste unitário do `fiscal-service` (`MdfeIniBuilderTest`).
  - **`POST /mdfes`**: recebe `empresaId` + `cteIds` + veículo/condutor; carga (soma de `valorTotalCarga`/`pesoBruto` dos CT-e), município de carregamento (= emitente) e municípios de descarga (snapshot do 1º endereço do destinatário de cada CT-e) são derivados. Validações: CT-e duplicado no request (422), inexistente (404), não `AUTORIZADA` (422), de outra empresa (422), já em outro MDF-e (409, checado na aplicação e reforçado por `uk_mdfe_documento_cte`), emitente sem UF/IBGE (422), destinatário sem IBGE (422), destino em UF diferente do emitente (422, só intra-UF).
  - **Sem saga, de propósito**: a reserva do número é a última chamada remota (todas as validações rodam antes, e depois dela só vem o `save` local), então só se perde número se o próprio commit falhar. Diferente da NF-e, que faz chamadas remotas depois de reservar.
  - **`MdfeIniBuilder`**: comando `MDFE.CriarEnviarMDFe(ini,lote,0)`; um `[DESCnnn]` por município de descarga distinto com seus `[infCTennnmmm]`. `tpRod=06`/`tpCar=02` fixos. Layout pelo vocabulário oficial do leiaute 3.00, **não conferido contra um INI de referência nem contra o ACBr real**. `emitir` nunca foi chamado nesta rodada.
  - **RBAC**: reaproveita `FISCAL_CONSULTAR`/`FISCAL_EMITIR`.
  - Validado ao vivo contra o banco real (CT-e #1/#2 trocados via SQL pra `AUTORIZADA` com chave sintética; empresa 1 e endereço do cliente 1 receberam UF/IBGE temporários; usuários ADMIN/ENTREGADOR de teste; tudo revertido ao final): os 11 cenários de validação acima, mais sucesso com 2 CT-e (carga 109,90 = 89,90+20,00, peso 45,5, série/número reservados) e um 2º MDF-e com número 2 (contador do cadastro-base em 3). Listagem e busca sem N+1 (fetch de `documentos` e `cteCabecalho` na mesma query), 404 em id inexistente, ENTREGADOR com 403 real ao gerar e ao listar. **2 achados corrigidos durante a validação**: empresa sem UF gerava a mensagem enganosa "emitente em null" (agora tem erro próprio, `MDFE_EMITENTE_SEM_ENDERECO`), e o erro de "outra empresa" não dizia qual empresa o MDF-e pedia.
  - **Fora de escopo, registrado no BACKLOG**: viagem interestadual, reboque, múltiplos condutores, seguro, frontend.
  - **Sub-fase 2: Encerramento e Cancelamento ✅ (2026-10-24), validado ao vivo.** `POST /mdfes/{id}/encerrar` (evento 110112, `FISCAL_EMITIR`; corpo opcional, sem data usa hoje e sem município usa o de descarga do último CT-e) e `POST /mdfes/{id}/cancelar` (evento 110111, `FISCAL_GERENCIAR`, mesmo nível de cancelamento do financeiro; justificativa de 15 a 255 caracteres). Situação nova `ENCERRADA` em `SituacaoNotaFiscal`. `V8__mdfe_encerramento_cancelamento.sql` adiciona data/município/protocolo do encerramento e data/justificativa/protocolo do cancelamento.
    - **Assinaturas confirmadas na doc oficial** (WebFetch, acbr.sourceforge.io `MDFeEncerrarMDFe.html`/`MDFeCancelarMDFe.html`): `MDFE.EncerrarMDFe(chave,dtEnc dd/MM/yyyy,cMun,[CNPJ],[protocolo])` e `MDFE.CancelarMDFe(chave,justificativa,[CNPJ])`. Todo parâmetro vai entre aspas (justificativa com vírgula quebraria a lista), com aspas internas removidas.
    - **Regras**: evento só em MDF-e `AUTORIZADA` (409). Data de encerramento nem futura nem anterior ao início da viagem (422). Cancelamento só até 24h após a autorização (422; depois disso o caminho é encerrar). Sucesso exige `CStat=135`: o ACBr responde `OK:` quando a comunicação funcionou, não quando a SEFAZ aceitou o evento, então um `OK:` com outro CStat é tratado como rejeição.
    - **Falha num evento nunca muda a situação** (diferente do `emitir`, que vai para `ERRO`/`REJEITADA`): o documento continua `AUTORIZADA` e válido. Rejeição da SEFAZ volta como 422 com o `XMotivo`, falha de comunicação como 502.
    - **Cancelar libera os CT-e**: o `UNIQUE` de banco em `mdfe_documento_vinculado.cte_cabecalho_id` saiu (a V8 cria antes um índice próprio para a FK). A regra virou "CT-e em no máximo um MDF-e não cancelado", checada em `buscarCteIdsJaVinculados`. O vínculo do manifesto cancelado fica como histórico. Um MDF-e encerrado continua prendendo seus CT-e.
    - **Validado ponta a ponta contra um ACBr Monitor FALSO** (script Python local na porta 3535 que fala o mesmo protocolo: banner+ETX, `CRLF.CRLF`, resposta+ETX, cp1252). O fiscal-service subiu com `--cactus.acbr.porta=3535`, então nada foi para o ACBr real (3434) nem para a SEFAZ. Foi a primeira vez que `emitir` rodou de ponta a ponta neste projeto, ainda que contra um ACBr simulado. 20 cenários: emitir→AUTORIZADA; encerrar em RASCUNHO (409), data futura e anterior à viagem (422), rejeição da SEFAZ (422, situação intacta), ENTREGADOR (403), sucesso (ENCERRADA, protocolo gravado), encerrar/cancelar de novo (409), CT-e do encerrado continua preso (409); cancelamento com justificativa curta (400), rejeição (422), sucesso (CANCELADA), CT-e liberados geram um novo MDF-e (201), prazo de 24h expirado (422). Os comandos que chegaram ao ACBr falso batem com as assinaturas da doc. +2 testes unitários dos comandos em `MdfeIniBuilderTest` (3 no total). Dados de teste revertidos.
    - **Ainda não validado**: contra um ACBr/SEFAZ real (mesmo bloqueio de certificado); formato real da resposta de evento além do exemplo da doc.
  - **`GET /mdfes/ctes-disponiveis?empresaId=` (2026-10-24)**: devolve os CT-e que `POST /mdfes` aceitaria (`AUTORIZADA`, da empresa, fora de qualquer MDF-e não cancelado), com a mesma regra aplicada no banco (`CteCabecalhoRepository#buscarDisponiveisParaMdfe`, `NOT EXISTS` + projeção `SELECT new CteDisponivelMdfeResponse`). Sem paginação, porque é a fila de CT-e ainda não manifestados. `FISCAL_CONSULTAR`. Substituiu o filtro no cliente do `GerarMdfeModal`, que dependia de páginas de 200 registros. Validado ao vivo: CT-e de outra empresa e CT-e em RASCUNHO ficam de fora; um MDF-e em RASCUNHO já tira seus CT-e da lista; cancelar devolve os CT-e; ENTREGADOR recebe 403; sem `empresaId` retorna 400. No navegador, o seletor faz uma chamada só e mostra exatamente os CT-e esperados.

# REFORMA TRIBUTÁRIA — IBS/CBS na NF-e, fase 1 (2026-10-25) ✅ validado ao vivo

Fecha o "achado crítico" registrado na ANÁLISE: MÓDULO CONTÁBIL E FISCAL: o IBS/CBS deixou de ser risco futuro e é **obrigatório em produção para CRT 3 (Regime Normal) desde 03/08/2026**. Simples Nacional/MEI/monofásicos: "orientações serão publicadas em NT futura".

## Fontes primárias (nada modelado "no chute")
- **NT 2025.002-RTC v1.50 (junho/2026)**, lida no texto integral (PDF extraído): cronograma por CRT; base `UB16-10` (`vBC = vProd + vServ + vFrete + vSeg + vOutro + vII − vDesc − vPIS − vCOFINS − vICMS − vICMSUFDest − vFCP − vFCPUFDest − vICMSMono − vISSQN + vIS`); alíquotas 2025/2026 — pIBSUF 0,1% (`UB18-10`), pIBSMun 0% (`UB37-10`), pCBS 0,9% (`UB56-10`), zeradas quando o cClassTrib indica tributação regular; valores `vX = vBC × p/100 − vDif − vDevTrib` com tolerância 0,01 (`UB35-10`/`UB54-10`/`UB67-10`); `vIBS = vIBSUF + vIBSMun` (`UB54a-10`); alíquota efetiva com 4 casas; grupo W03 de totais; `vNFTot`/`vItem` com regras ainda "implementação futura" (não preenchidos).
- **Tabela oficial de CST/cClassTrib da SVRS** (Conformidade Fácil): 18 CST e 173 cClassTrib com todos os indicadores, publicação mais recente 2026-10-01, extraída da página pública (o endpoint de API exige certificado — 403). Gerador versionado em `cadastro-base-service/ferramentas/gerar_catalogo_ibs_cbs.py`.
- **Modelo oficial de INI do ACBr para a reforma** (`ModeloNFeINICompletoReformaTribu.html`): seções `[IBSCBSnnn]`/`[gIBSCBSnnn]`/`[gIBSUFnnn]`/`[gIBSMunnnn]`/`[gCBSnnn]` e totais `[IBSCBSTot]`/`[gIBS]`/`[gIBSUFTot]`/`[gIBSMunTot]`/`[gCBSTot]`.

## cadastro-base-service (dono da regra) — `V13__ibs_cbs_catalogos.sql`
- Catálogos oficiais só leitura (`cst_ibs_cbs`, `classificacao_tributaria_ibs_cbs`), alíquotas por ano (`aliquota_ibs_cbs_vigencia`, só 2025/2026 — ano sem linha = 422, nunca alíquota inventada), `ibs_cbs_configuracao` 1:1 com a célula grupo × operação (só o cClassTrib), e o "resolvido padrão" do produto ganhou `cst_ibs_cbs_padrao`/`classificacao_ibs_cbs_padrao`.
- API `GET /api/v1/tributacao/ibs-cbs/{cst, classificacoes, classificacoes/{codigo}, aliquotas?data=}` (`CADASTRO_TRIBUTACAO_CONSULTAR`).
- `RegrasClassificacaoIbsCbs`: o ERP emite os CST **000, 010, 011, 200 (com redução oficial), 400 e 410**. Diferimento (510/515), suspensão (550), monofásica (620), alíquota fixa/base reduzida (220/221/222), transferência de crédito (800), ajustes (810/811), 820, 830 e qualquer cClassTrib com tributação regular são **recusados na configuração com o motivo** — melhor que gerar NF-e que a SEFAZ rejeitaria. Classificação fora de vigência também é recusada.
- `seguranca-service`: client `fiscal-service` ganhou `CADASTRO_TRIBUTACAO_CONSULTAR`.

## fiscal-service — `V9__ibs_cbs_nfe.sql`
- `CalculadoraIbsCbs` (componente puro): a base parte dos valores **arredondados a 2 casas, como vão no XML** — o INI manda 6 casas e o ACBr arredonda; a SEFAZ valida a base com os valores do XML (tolerância 0,01, rejeição 1104).
- Só emitente **CRT 3** recebe o grupo. Em CRT 3, produto sem classificação ou com classificação não suportada é recusado **antes** de reservar o número da NF-e (não queima numeração). Na emissão o cálculo é refeito com as alíquotas do ano da data de emissão (rascunho de dezembro emitido em janeiro).
- `NfeIniBuilder`: grupos do item e totais no formato exato do modelo do ACBr; isenção/imunidade leva só CST + cClassTrib; redução leva `pRedAliq`/`pAliqEfet`.

## Bugs reais encontrados no caminho
1. **(pré-existente) Todo PUT de configuração tributária com ICMS por UF falhava com 409**: o serviço limpava a lista e recriava; o Hibernate executa INSERT antes do DELETE dos órfãos no flush e a nova linha "SP" colidia em `uk_icms_config_uf`. Corrigido atualizando as linhas no lugar, casando pela UF.
2. **Validação de schema** recusou `Integer`/`int` para colunas `SMALLINT`/`TINYINT` da V13 — corrigido na entidade (`Short`/`Byte`), sem editar migration aplicada.
3. **Frontend apagaria a classificação**: a tela de configuração tributária manda o PUT completo; sem conhecer `ibsCbs`, qualquer edição removeria o cClassTrib em silêncio. A tela ganhou o seletor (catálogo oficial, só classificações suportadas, redução visível) e sempre reenvia o valor.

## Validação
- Unitários (`CalculadoraIbsCbsTest`, valores feitos à mão): integral 100 − ICMS 18 − PIS 1,65 − COFINS 7,60 = base 72,75 → IBS 0,07, CBS 0,65; arredondamento como no XML; redução de 60% (alíquota efetiva 0,04%/0,36%); imunidade sem valores; totais; INI no formato do ACBr. 9 testes do fiscal, suítes de cadastro/seguranca sem regressão.
- Ao vivo: 18 CST/173 cClassTrib pela API; 2027 → 422; 510001, 550001, 220001, 200022 (tributação regular) e 999999 recusados com o motivo; 000001 aceito; produto recalculado com o padrão; **NF-e real da venda #6 composta com base 72,75 / IBS 0,07 / CBS 0,65** e emitida contra um ACBr Monitor FALSO local (nunca o real): INI capturado idêntico ao modelo oficial. Sem classificação a NF-e é recusada sem consumir número (contador conferido). Tela conferida no navegador. Dados de teste removidos, numeração restaurada; a classificação 000001 ficou na configuração/produto de demonstração (configuração correta — sem ela a empresa demo, CRT 3, não geraria NF-e).

## Fora desta fase (BACKLOG)
NFC-e (fiscal-service da Loja Virtual e cactus-pdv), Simples/MEI (aguardam NT), CST com diferimento/suspensão/monofásica/tributação regular/crédito presumido, Imposto Seletivo, `vNFTot`/`vItem`, alíquotas de 2027 em diante (inserir quando definidas), SPED com IBS/CBS, emissão contra o ACBr/SEFAZ reais.

# EMISSÃO DIRETA À SEFAZ — NF-e sem ACBr Monitor (2026-10-31)

Primeira fatia da migração descrita em `analise-migracao-fiscal-acbr-java.md` (análise de 26/10/2026, feita fora do repositório): eliminar a dependência paga do ACBr Pro trocando o middleware por bibliotecas Java encapsuladas atrás de uma camada própria do ERP. **Só NF-e (modelo 55) migrou nesta fatia**; NFC-e, NFS-e, CT-e e MDF-e continuam no ACBr (`cactus.acbr.*` segue no `application.yml`).

## Decisão de biblioteca: `com.github.wmixvideo:nfe` 5.1.11, não `swconsultoria`
A análise recomendava o ecossistema swconsultoria (`java-nfe`/`java-cte`) e citava a wmixvideo como "alternativa unificada". Escolhida a wmixvideo porque:
- um único projeto cobre NF-e/NFC-e 4.00, CT-e 4.00 e MDF-e 3.00 (o `java-mdfe` da swconsultoria está abandonado desde 2019, ver `ANALISE_SWCONSULTORIA_VS_ACBR.md`, e o ERP já emite MDF-e);
- o jar 5.1.11 traz o grupo UB de IBS/CBS completo (item e total W03), conferido nas classes antes de codar;
- traz os XSDs oficiais empacotados (`DFXMLValidador`), o que permite validar o leiaute em teste unitário, sem rede.

Decisão tomada na sessão de 2026-10-27/30 (perdida com a troca de máquina, reconstruída e confirmada em 2026-10-31). Fecha o item de backlog "avaliar swconsultoria".

## Arquitetura (`fiscal-service`, pacote `sefaz/` — adaptador de saída, como `client/`)
- `EmissorNfe` (porta) + `SefazEmissorNfe` (única classe do fluxo que conhece a biblioteca). `NotaFiscalEletronicaService` só fala com a porta, com `RetornoSefazNfe` e com `FalhaComunicacaoSefazException`.
- `NfeDocumentoBuilder`: domínio → `NFNota`. Componente puro, testável. Arredonda tudo explicitamente: os setters da biblioteca **lançam exceção** com casas decimais além do leiaute (não arredondam).
- `NfeConfigFactory` + `CactusNFeConfig`: UF/ambiente da empresa + certificado + cadeia; aplica a trava `cactus.fiscal.permitir-producao` (padrão `false`: nota de empresa em PRODUCAO é **recusada** com 422, não desviada para homologação, porque chave e `tpAmb` nascem com o ambiente).
- `CertificadoDigitalProvider`: A1 por alias. O banco (`EmpresaConfiguracaoFiscal.certificadoAlias`) guarda só o nome; arquivo e senha vêm do ambiente (`CACTUS_FISCAL_CERTIFICADOS_<ALIAS>_ARQUIVO`/`_SENHA`). A3 recusado (exige PKCS#11 no servidor).
- `CadeiaCertificadosProvider` + `GeradorCadeiaCertificados` (ferramenta de operador com `main`). **A cadeia TLS da SEFAZ é obrigatória e nunca baixada em tempo de execução**: o download da biblioteca é trust-on-first-use (aceita qualquer certificado devolvido pelo host), e a própria biblioteca proíbe esse uso fora de uma execução manual conferida. O rascunho da sessão anterior previa download automático; foi revertido.

## Fluxo de `POST /notas-fiscais/{id}/emitir`
1. Aceita `RASCUNHO`, `REJEITADA` (reenvio com o mesmo número após corrigir o cadastro: rejeição não consome numeração) e `ERRO`.
2. Monta → assina (chave/DV pela biblioteca) → **valida no XSD antes de qualquer rede**. Erro de montagem, tributação não suportada, certificado/cadeia ausentes ou produção desligada = 422 e rollback: nada enviado, nota intacta.
3. Transmite em modo síncrono (`indSinc=1`, um documento por lote). cStat 100/150 → `AUTORIZADA`; 110/301/302/303 → `DENEGADA` (situação nova, terminal, número consumido); outros → `REJEITADA`. cStat e motivo gravados (`codigo_status_sefaz`, coluna nova).
4. Falha de comunicação → `ERRO` com a chave gravada. **Retransmissão idempotente**: o `cNF` é determinístico por nota e a nota em `ERRO` mantém a data de emissão, então o documento sai idêntico. Se a primeira transmissão tinha sido autorizada, a SEFAZ responde 204 (duplicidade) e o emissor consulta a chave e monta o `nfeProc` com o protocolo original.
5. `nfeProc` (NF-e assinada + protocolo) guardado em `nota_fiscal_eletronica_xml` (tabela 1:1 separada, `MEDIUMTEXT`, para a listagem paginada nunca carregá-lo) e servido em `GET /notas-fiscais/{id}/xml`.

Migration `V10__nfe_emissao_direta_sefaz.sql`. `NfeIniBuilder` removido (NF-e não usa mais INI).

## O que o XML cobre, e o que é recusado em vez de enviado errado
- ICMS: CST 00/40/41/50/60 e CSOSN 102/103/300/400/500. CST 10/20/30/51/70/90 e CSOSN 101/201/202/203/900 = 422 (`TRIBUTACAO_ITEM_NAO_SUPORTADA`): o motor tributário não entrega ST, redução de base nem crédito do Simples.
- PIS/COFINS: 01/02 por alíquota, 04-09 não tributado, 49-99 outras operações; 03 (por quantidade) recusado.
- IPI: `cEnq=999`, CST 50 com alíquota > 0, 99 com alíquota zero.
- IBS/CBS: grupo UB com `gRed` quando há redução; total W03 com diferimento, devolução e crédito presumido zerados.
- Operação interestadual recusada (CFOP 6xxx, DIFAL e partilha não implementados). Destinatário sem endereço com IBGE recusado.
- Homologação: `xNome` do destinatário = texto exigido pela SEFAZ (rejeição 598).
- `indIEDest` pela IE do cliente; não contribuinte sempre `indFinal=1` (rejeição 696); contribuinte com IE `indFinal=0`. `indPres=9` + `indIntermed=0`.
- `vProd = qCom × vUnCom` (rejeição 629; o `valorTotal` do item da venda já vem líquido do desconto); `vNF` somado do que vai no XML, não copiado da venda.
- **Simplificação**: pagamento único `tPag=99` ("Outros") no valor da nota — a forma de pagamento da venda ainda não é traduzida para o código da SEFAZ (BACKLOG).
- `infRespTec` a partir de `cactus.fiscal.responsavel-tecnico.*` (opcional; várias UFs exigem, rejeição 972).

## Validação feita
- `NfeDocumentoBuilderTest` (5 testes): gera um certificado autoassinado com o `keytool` do JDK, assina o lote como a biblioteca faz e **valida contra o XSD oficial** dois cenários (Regime Normal com IBS/CBS integral, redução de 60% e imunidade; Simples com CSOSN 102). Confere grupos UB, totais (vProd 1150, vDesc 10, vNF 1140) e pagamento. Cobre também a recusa de operação interestadual, a recusa de CST 10 e o `cNF` determinístico.
- Achado no teste: a biblioteca formata percentuais com 2 a 4 casas (`0.10`, não `0.1000`), o que é válido no tipo `TDec_0302a04` do XSD. Não é bug.
- Suíte do fiscal-service: 13/13. Frontend (`DENEGADA`, reenvio de `REJEITADA`, cStat no alerta, botão "Baixar XML"): `tsc`, `oxlint` e 13 testes limpos.

## Ainda NÃO validado
- **Transmissão real**: nenhuma chamada à SEFAZ foi feita (falta certificado A1 de homologação). Autorização, rejeições reais, duplicidade 204 + consulta e TLS com a cadeia gerada só serão exercitados com ele.
- Emissão de ponta a ponta pela API (com seguranca/cadastro/vendas de pé) até a recusa por certificado não configurado.

Feito em 2026-10-31: o fiscal-service subiu com a V10 aplicada (`cactus_fiscal` V9 → V10, `ddl-auto=validate` passou, `cactus.fiscal.*` carregou). Achado de ambiente: a máquina tem uma `DB_PASSWORD` global (do cactus-pdv) que sobrescreve o padrão de todos os serviços do ERP, que usam a mesma variável; subir com `DB_PASSWORD=<senha do serviço>` no próprio processo (ver BACKLOG, Operacional).

## Próximas fatias
NFC-e no fiscal-service (QR Code e CSC já suportados pela biblioteca), cancelamento (evento 110111) e carta de correção, inutilização de numeração (fecharia o `NumeroFiscalPulado`), DANF-E em PDF, CT-e e MDF-e pela mesma biblioteca. O `cactus-pdv` continua no ACBr (NFC-e lá é produção validada), conforme a recomendação de `ANALISE_SWCONSULTORIA_VS_ACBR.md`.

# NF-e: CANCELAMENTO E INUTILIZAÇÃO DIRETOS À SEFAZ (2026-11-01)

Segunda fatia da emissão direta. Mesma biblioteca, porta separada da autorização: `EventosNfe` (cancelar/inutilizar), implementada por `SefazEventosNfe`. `EmissorNfe` continua só com a autorização (responsabilidades e retornos diferentes).

## Cancelamento — `POST /notas-fiscais/{id}/cancelar` (`FISCAL_GERENCIAR`)
- Só `AUTORIZADA` (409 nos demais). Justificativa 15-255 (`CancelarNotaFiscalRequest`, e o validador da biblioteca também exige).
- **Prazo não é checado localmente**: 24h é a regra geral, mas há UFs com cancelamento extemporâneo (cStat 155); a SEFAZ decide e a rejeição 501 volta com o motivo. (O MDF-e checa 24h localmente porque lá o prazo é fixo.)
- Lote do evento precisa voltar 128; evento 135/155 = homologado. Homologado: `CANCELADA`, `protocolo_cancelamento`/`data_cancelamento`/`justificativa_cancelamento`, cStat gravado, e o `procEventoNFe` vai para `nota_fiscal_eletronica_xml.xml_cancelamento` (o `nfeProc` original fica intocado — os dois são documentos fiscais).
- Rejeição = 422 com cStat e motivo; falha de rede = 502. **Nos dois casos a nota continua `AUTORIZADA` e válida** (mesmo critério dos eventos do MDF-e).

## Inutilização — `POST /inutilizacoes-nfe` (`FISCAL_GERENCIAR`), `GET /inutilizacoes-nfe?empresaId=`, `GET /inutilizacoes-nfe/{id}/xml`
Recusado localmente, antes de qualquer rede (`InutilizacaoNumeracaoNfeService`):
- faixa invertida, série fora de 0-999 (422);
- **número ainda não reservado** (`numeroFinal >= proximoNumeroNfe` da série configurada, 422): inutilizar à frente do contador faria a NF-e ser rejeitada quando ele chegasse lá;
- número que pertence a qualquer NF-e do ERP, em qualquer situação (409) — RASCUNHO/REJEITADA/ERRO ainda podem ser emitidas, AUTORIZADA/CANCELADA já são documento;
- sobreposição com faixa já homologada (409).

Ano com 2 dígitos do horário de Brasília. Toda resposta da SEFAZ vira registro em `inutilizacao_numeracao_nfe` (HOMOLOGADA = cStat 102, ou REJEITADA), com o `retInutNFe` assinado pela SEFAZ (a biblioteca não tem classe de `procInutNFe`; o retorno assinado com o protocolo é a prova). Falha de rede = 502 e nada registrado.

**Fecha o ciclo do `NumeroFiscalPulado`**: homologada, o fiscal-service chama `POST /empresas/{id}/configuracao-fiscal/numeros-fiscais-pulados/regularizar` no cadastro-base-service (V14: `protocolo_inutilizacao`, `data_regularizacao`; bulk update só dos pendentes, idempotente). Best-effort: a inutilização já vale na SEFAZ e não se desfaz, então uma falha ali só é logada. `GET .../numeros-fiscais-pulados?somentePendentes=true` lista o que ainda falta inutilizar.

## Migrations
fiscal-service `V11__nfe_cancelamento_inutilizacao.sql` (+ índice `empresa_id, serie, numero` para a checagem de faixa); cadastro-base-service `V14__numero_fiscal_pulado_regularizacao.sql`. **Aplicadas no MySQL local em 2026-11-01**, os dois serviços subiram com `ddl-auto=validate`.

## Frontend
`NotaFiscalDetailPage`: botão "Cancelar NF-e" (só AUTORIZADA, `FISCAL_GERENCIAR`), dados do cancelamento, XML também para CANCELADA. Tela nova `InutilizacoesNfeListPage` (`/fiscal/inutilizacoes-nfe`, menu Fiscal) com `InutilizarNumeracaoModal`.

## Validação
- `InutilizacaoNumeracaoNfeServiceTest` (8): faixa invertida, número não reservado, faixa com NF-e, faixa já inutilizada (nenhum toca a SEFAZ); homologada registra e regulariza; falha no cadastro não desfaz; rejeitada registra sem regularizar; falha de rede não registra nada.
- `CancelamentoNotaFiscalEletronicaTest` (4): situação inválida não chama a SEFAZ; rejeição 501 e falha de rede mantêm AUTORIZADA; homologado cancela, preserva o protocolo de autorização e anexa o evento sem tocar o nfeProc.
- fiscal-service 25/25; frontend `tsc`/`oxlint`/13 testes.
- **Não validado**: nenhum evento foi enviado à SEFAZ (mesmo bloqueio de certificado A1 de homologação). Casos 573 e 155, e a trava de nova NF-e para venda com nota cancelada: ver BACKLOG.

