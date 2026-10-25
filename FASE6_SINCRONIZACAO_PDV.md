# Fase 6 — Sincronização ERP ↔ cactus-pdv

Documento de arquitetura + checklist de implementação/teste para a Fase 6. Vive separado
do `CLAUDE.md` porque é grande o suficiente pra merecer o próprio arquivo (mesmo padrão
já usado para `BANCO_PDV.md`) — o `CLAUDE.md` mantém só o resumo executivo e aponta pra
cá. **Antes de escrever qualquer código desta fase, ler este arquivo inteiro.**

Responde às 6 perguntas feitas pelo usuário, nesta ordem: (1) inventário do que
sincronizar, (2) tecnologia, (3) auditoria de sucesso/erro, (4) dead letter, (5) balanças,
(6) cenários. As seções abaixo estão organizadas por assunto, não por pergunta, mas cada
uma referencia a pergunta que responde.

---

## 1. Princípios que guiam todo o desenho

1. **O PDV manda quando o assunto é operação de loja; o ERP manda quando o assunto é
   cadastro.** Não existe edição bidirecional do mesmo campo — isso elimina toda a classe
   de problema de "conflito de merge" que sincronização bidirecional ingênua sofre.
2. **O PDV é intermitente por design** (o próprio `LICENCA_CACHE` do `BANCO_PDV.md` já
   assume isso). Qualquer canal de comunicação PDV↔ERP precisa sobreviver a: internet
   caindo no meio de um envio, o PDV ficando dias offline, o PDV sendo religado com
   relógio dessincronizado. Conexão persistente (socket de fila aberto 24/7 num PC de loja
   com internet ruim) é o desenho errado para esse ponto — ver seção 3.
3. **Nunca perder dado operacional gerado no PDV.** Venda, sangria, movimento de estoque e
   fiado são dinheiro e produto de verdade — o desenho tem que garantir "at-least-once" com
   trilha de auditoria, nunca "fire and forget".
4. **ID local do PDV nunca vira chave primária no ERP** (já registrado no `CLAUDE.md`) —
   toda tabela de ingestão usa uma chave de idempotência composta
   `lojaId + dispositivoId + tipoRegistro + idOrigem`.
5. **Cada novo mecanismo entra como extensão do que já existe**, não como exceção nova:
   Client Credentials para serviço-a-serviço (Fase 3/5), padrão nominal de aprovação acima
   de limiar (Estoque/Compras) e `PaginatedFetchSupport`/hidratação para N+1 (varredura
   2026-08-31) todos se aplicam aqui sem redesenho.

---

## 2. Inventário — o que precisa ser sincronizado (pergunta 1)

Baseado no "Padrão de sincronização observado no DDL" e no "Inventário completo — as 33
tabelas" já registrados no `CLAUDE.md`. Tabela única, com o serviço dono no ERP e o
status real de prontidão dos dados de origem (não do canal de sync em si, que é o que
esta fase constrói).

### 2.1 ERP → PDV (pull, iniciado pelo PDV)

| Domínio | Dono no ERP | Dado de origem pronto? | Observação |
|---|---|---|---|
| Produto (+ tributação resolvida) | `cadastro-base-service` | ✅ Sim (`Produto` + campos "resolvido padrão" cacheados) | Payload achatado — CST/CSOSN/taxas já calculados, PDV não recalcula nada |
| Produto — grupo/subgrupo/tipo/unidade/marca | `cadastro-base-service` | ✅ Sim | Lookups, mudam raramente — cabe em pull incremental normal |
| Produto — faixa de preço (quantidade/promoção) | `cadastro-base-service` | ✅ Sim (`ProdutoFaixaPreco`) | Vigência de data já modelada |
| Produto — imagem | `cadastro-base-service` | ⚠️ Parcial | Só URL hoje, upload binário via GED ainda não migrado — decisão: **não sincronizar imagem nesta fase**, PDV não exibe foto de produto hoje mesmo |
| Produto — saldo de estoque agregado | `controle-estoque-service` | ✅ Sim (`SaldoEstoque`) | **Não é campo do Produto** — o pull de cadastro precisa agregar (somar saldo de todos os depósitos da filial) na hora de montar o payload |
| Cliente | `cadastro-base-service` | ✅ Sim | Inclui fidelidade, `limiteCredito`, endereço/contato |
| Tabela de preço | `cadastro-base-service` | ✅ Sim (`TabelaPreco`/`TabelaPrecoProduto`) | |
| Forma de pagamento | `cadastro-base-service` | ✅ Sim (`FormaPagamento`, Fase 1c) | Fecha o gap que o próprio DDL do PDV já apontava |
| Operador (`PDV_OPERADOR`) | `seguranca-service` (+ `Colaborador` no cadastro-base) | ✅ Sim | Precisa de endpoint de composição — ver 2.3 |
| Configuração operacional da loja | `cadastro-base-service` | ✅ Sim (`ConfiguracaoOperacionalLoja`) | Mesas/comandas/delivery + política comercial de desconto |
| Licenciamento (`LICENCA_CACHE`) | — | ❌ Não existe | Fora de escopo desta fase — módulo de licenciamento não foi construído; registrado, não esquecido |

### 2.2 PDV → ERP (push, iniciado pelo PDV)

| Domínio | Dono no ERP | Dado de destino pronto? | Observação |
|---|---|---|---|
| `ESTOQUE_MOVIMENTO` (venda baixando estoque) | `controle-estoque-service` | ✅ Sim (`EstoqueMovimento`, tipo `SAIDA`) | Precisa de `OrigemMovimentoEstoque` novo, ex. `VENDA_PDV` |
| `PDV_VENDA_CABECALHO`/`_DETALHE` | **novo** — nenhum serviço de Vendas/Faturamento existe ainda | ❌ Não existe destino | Maior gap da Fase 6 — ver seção 9 (decisão pendente) |
| `PDV_MOVIMENTO` (abertura/fechamento de caixa) | **novo** — módulo de Caixa/Financeiro não existe | ❌ Não existe destino | Mesmo gap |
| `PDV_SANGRIA`/`PDV_SUPRIMENTO` | **novo** — idem | ❌ Não existe destino | Mesmo gap |
| `PDV_CONFERENCIA_CAIXA` | **novo** — idem | ❌ Não existe destino | Mesmo gap |
| `PDV_TOTAL_TIPO_PAGAMENTO` | **novo** — idem | ❌ Não existe destino | Mesmo gap |
| `PDV_DEVOLUCAO` | **novo** — idem | ❌ Não existe destino | Mesmo gap |
| `CLIENTE_FIADO_LANCAMENTO` | **novo** — módulo financeiro não escopado | ❌ Não existe destino | Já registrado como gap no `CLAUDE.md` |

### 2.3 Gap novo encontrado nesta análise: composição de `PDV_OPERADOR`

Não existe hoje nenhum endpoint que junte `Usuario` (seguranca-service) +
`Colaborador` (cadastro-base-service) + resolução `Papel`→`NIVEL_AUTORIZACAO` (G/S/O/E,
mapa já registrado como pendência na Fase 1a) num único payload pronto pro PDV. Isso é
trabalho de composição cross-serviço — natural responsabilidade do novo
`sincronizacao-service` (seção 4), não de um dos dois serviços de origem.

### 2.3-bis Reavaliação (2026-09-27) — os 7 gaps de push, agora que outros módulos existem

Quando a Fase 6 foi construída (2026-08-31), só `controle-estoque-service` existia como
destino possível. Desde então o monorepo ganhou `vendas-service` (Fase 7) e
`financeiro-service` (com Contas a Pagar/Receber, Caixa e Bancos, Tesouraria) — o usuário
pediu pra reavaliar se algum dos 7 gaps da tabela 2.2 já tem destino real hoje. Verificação
feita lendo o código de verdade (entidades/campos/enums), não por suposição — resultado é
**misto**: alguns encaixam bem, dois têm atrito real de modelo que não dá pra fingir que
não existe.

| Domínio PDV | Destino candidato | Encaixe | Observação |
|---|---|---|---|
| `PDV_SANGRIA` | `financeiro-service.MovimentoCaixaBanco` (`ContaBancaria.tipo=CAIXA_INTERNO`, `tipoMovimento=SAIDA`) | ✅ **Bom, baixo esforço** | `CAIXA_INTERNO` existe especificamente pra "dinheiro em espécie, sem banco associado" (`bancoId` nulo) — desenhado sem o PDV em mente, mas serve. Falta só: novo valor no enum fechado `OrigemMovimentoCaixa` (hoje só tem `BAIXA_CONTA_RECEBER/PAGAR`, `ESTORNO_*`, `TRANSFERENCIA`, `APLICACAO_FINANCEIRA`, `RESGATE_APLICACAO`, `CHEQUE_*`, `AJUSTE`, `CONCILIACAO` — nenhum de PDV ainda), e decidir o mapeamento "qual `ContaBancaria` recebe" (1 por loja? 1 por terminal/PDV?). |
| `PDV_SUPRIMENTO` | idem, `tipoMovimento=ENTRADA` | ✅ **Bom, baixo esforço** | Mesmo caminho da Sangria, sentido oposto. |
| `CLIENTE_FIADO_LANCAMENTO` | `financeiro-service.ContaReceber`/`BaixaContaReceber` | ✅ **Bom, baixo esforço** | `ContaReceber.clienteId` já é referência solta (`Long`, sem FK), exatamente como o PDV precisa. Tipo `C` (crédito concedido) → gera `ContaReceber` nova; tipo `P` (pagamento) → gera `BaixaContaReceber` no título correspondente. Falta só: novo valor no `origemTipo` da `ContaReceber` (hoje cobre `VENDA`/`COMPRA`/`MANUAL`/`RENEGOCIACAO` — não confirmado se tem mais, mas certamente não tem `FIADO_PDV` ainda). |
| `PDV_MOVIMENTO` (abertura/fechamento de caixa) | `financeiro-service`, mesma base de Caixa/Bancos | ⚠️ **Parcial, esforço médio** | Tecnicamente dá pra registrar o saldo de abertura/fechamento como um `MovimentoCaixaBanco`, mas falta decidir a granularidade da `ContaBancaria` (loja inteira vs. por terminal/PDV individual — cada PDV físico tem seu próprio caixa de verdade) antes de implementar, senão o saldo de dois terminais da mesma loja se mistura numa única conta. |
| `PDV_CONFERENCIA_CAIXA` (+`_DETALHE`) | idem, via `OrigemMovimentoCaixa.AJUSTE` só quando há divergência | ⚠️ **Parcial, esforço médio** | Contagem física (por cédula/moeda) não tem entidade equivalente em `financeiro-service` — não é um "movimento", é uma auditoria de conferência. Proposta mínima: se o valor contado bater com o esperado, só fica registrado na auditoria do `sincronizacao-service` (payload já é guardado ali); se divergir, gera um `AJUSTE` (enum já existe) no valor da diferença. Não cria entidade nova em `financeiro-service` — decisão pendente de confirmar com o usuário antes de codar. |
| `PDV_VENDA_CABECALHO`/`_DETALHE` (+`_HISTORICO`) | `vendas-service.VendaCabecalho`/`VendaItem` | ❌ **Não fecha ainda, esforço real** | `VendaCabecalho.vendedor` é FK **obrigatória** (`nullable=false`) pra um `Vendedor`, que por sua vez exige `colaboradorId` **único** (`unique=true`) — ou seja, toda venda do PDV precisa de um `Vendedor` real pré-cadastrado pro operador do caixa, mesmo que ele seja só um caixa sem comissão nenhuma. `condicoesPagamento` (FK obrigatória) também é **obrigatória** — pensada pra parcelamento B2B, não pra "pago à vista no balcão". `notaFiscalTipo` (FK obrigatória) também precisa resolver pra NFC-e. Nenhum desses 3 tem um "modo simplificado"/valor genérico hoje. **Não é um bloqueio definitivo, mas não é plug-and-play** — exige ou (a) sintetizar registros padrão (`Vendedor`/`VendaCondicoesPagamento`/`NotaFiscalTipo` "genéricos de balcão") automaticamente na hora do import, ou (b) relaxar essas 3 colunas pra `nullable`, o que é uma mudança de schema em produção-relevante do `vendas-service`, não trivial. |
| `PDV_TOTAL_TIPO_PAGAMENTO` | depende de #1 acima (venda) | ❌ **Não fecha, achado novo** | `VendaCabecalho.formaPagamentoId` é um campo **único** (`Long`, não uma coleção) — só suporta 1 forma de pagamento por venda. O PDV já suporta pagamento **fracionado** (`PDV_TOTAL_TIPO_PAGAMENTO` é N linhas por venda — parte dinheiro, parte cartão, etc.), então mesmo resolvendo o gap de Venda acima, esse detalhe específico exigiria uma entidade nova em `vendas-service` (`VendaTotalPagamento` ou similar) — hoje não existe. |
| `PDV_DEVOLUCAO` | nenhum | ❌ **Gap real, sem destino** | `vendas-service.VendaService#cancelar` só permite cancelar em `RASCUNHO` — devolução pós-venda confirmada (com estoque/comissão já lançados) é explicitamente "fora de escopo, fluxo de devolução fica pra quando a Fase 8/NF-e existir" (registrado desde a Fase 7). Continua sem nenhum destino no ERP hoje. |

**Conclusão da reavaliação**: dos 7 gaps originais, **3 têm caminho claro e barato** (Sangria/Suprimento/Fiado — só extensão de enum + endpoint), **2 são viáveis mas precisam de uma decisão de modelagem antes de codar** (abertura/fechamento de caixa por terminal; conferência de caixa como ajuste), e **2 continuam sem solução real** (Venda do PDV em si, por atrito de modelo com o `vendas-service` que foi desenhado pra B2B/atacado, não pra balcão; e Devolução, que nunca foi construída em lugar nenhum). Ver item de `BACKLOG.md` ("Fase 6-B") para o roadmap de fechamento, em ordem de esforço crescente.

### 2.4 Conclusão do inventário

**A Fase 6 tem duas metades de tamanho muito diferente**: exportar cadastro (2.1) é
"só" compor e paginar dado que já existe — trabalho real, mas conhecido. Ingerir operação
de caixa (2.2) esbarra em **7 das 8 linhas não terem serviço de destino no ERP ainda**
(só `ESTOQUE_MOVIMENTO` tem). Ou seja: antes de "sincronizar vendas", o sistema precisa
de um módulo de Vendas/Faturamento e, separadamente, um de Caixa/Financeiro — nenhum dos
dois está no roadmap ainda. Isso está formalizado como decisão pendente na seção 9 e
**bloqueia o início do trabalho de push** até ser resolvido — ver seção 11 (ordem de
implementação) para como isso é sequenciado sem travar o resto da fase.

---

## 3. Tecnologia de sincronização (pergunta 2)

### 3.1 Decisão: canal híbrido, não uma tecnologia única

| Canal | Tecnologia | Por quê |
|---|---|---|
| **Borda PDV ↔ ERP** (os dois sentidos) | **HTTP/REST**, PDV sempre inicia a chamada (pull para 2.1, push para 2.2) | PDV é desktop de loja com internet intermitente. Uma conexão de fila persistente (consumer AMQP/Kafka rodando 24/7 num PC de comércio, atrás de rede doméstica/4G, sem ninguém de TI local) é operacionalmente frágil e caro de suportar. HTTP com retry no próprio PDV (ele já faz isso hoje — é como fala com `LICENCA_CACHE`) é o padrão de mercado para integração de terminal de loja com retaguarda central (é assim que a maioria das redes de varejo brasileiras já opera). |
| **Núcleo do ERP** (entre `sincronizacao-service` e os serviços de domínio) | **RabbitMQ** | Aqui sim faz sentido: infraestrutura própria, rede estável, precisa de desacoplamento (ingestão não pode esperar o Estoque processar pra responder ao PDV) + fan-out (mesmo evento pode interessar a Estoque e a BI) + retry/DLQ nativo. |

### 3.2 Por que não Kafka

Kafka resolve um problema de **volume/streaming** (milhões de eventos/segundo, múltiplos
consumidores replayando o mesmo log) que o Cactus ERP não tem — o volume aqui é "algumas
centenas de lojas, cada uma gerando algumas centenas de eventos por dia". RabbitMQ cobre
isso com menos operação (não precisa de Zookeeper/KRaft, cluster de partição, retenção de
log) e já resolve nativamente o requisito de DLQ (pergunta 4) via
Dead Letter Exchange, que no Kafka exigiria reimplementar por cima. Fica registrado como
alternativa a revisitar **apenas se** o volume real após produção crescer a um patamar
que justifique (milhares de lojas simultâneas) — não uma decisão definitiva para sempre,
mas o ponto de partida correto para o tamanho atual do problema.

### 3.3 Por que não script/arquivo

Um script de importação em lote (CSV/arquivo trocado por FTP, por exemplo) é o padrão
mais antigo e mais frágil: não dá granularidade de erro por registro, não tem contrapressão
nem confirmação por item, e não serve para o requisito de **estoque/fiado em quase tempo
real** que um ERP competitivo precisa (uma venda no PDV deveria refletir no saldo do
`controle-estoque-service` em minutos, não da próxima vez que alguém rodar um job noturno).
Fica descartado para o fluxo principal. **Não descartado** para um caso específico: carga
inicial de um volume muito grande de histórico ao migrar uma loja existente para o Cactus
ERP pela primeira vez — isso pode justificar uma ferramenta de importação em lote pontual,
mas é operação de implantação, não o mecanismo de sincronização contínua.

### 3.4 Infraestrutura nova necessária

**Atualizado (2026-08-31)**: RabbitMQ roda via Docker (imagem `rabbitmq:3.13-management`,
container `cactus-rabbitmq`, vhost `cactus`, painel em `http://localhost:15672`), mas o
`docker-compose.yml` **não vive neste repositório** — o usuário mantém e sobe a partir da
própria pasta pessoal onde já centraliza os `docker-compose` de outros projetos (o arquivo
que chegou a ser criado em `docker/rabbitmq/docker-compose.yml` foi removido daqui de
propósito). É exatamente o mesmo padrão já usado para o MySQL (`cactus-mysql`, "banco local
já existente, fora do Docker deste repo") — infraestrutura local compartilhada entre
projetos do usuário fica fora do monorepo, só o container/vhost/porta precisam bater com o
que este documento assume: container `cactus-rabbitmq`, vhost `cactus`, porta `5672`
(AMQP)/`15672` (management). Nenhum código deste repo depende do arquivo compose em si, só
do broker estar no ar nesses parâmetros — o `sincronizacao-service` (seção 4) aponta pra lá
via configuração de conexão comum (`application.yml`/variável de ambiente), igual ao que já
é feito com o `cactus-mysql`.

---

## 4. Novo módulo: `sincronizacao-service`

Porta **8087**, schema **`cactus_sincronizacao`**, package
`br.com.cactusdigital.erp.sincronizacao`. Resource Server puro, mesmo scaffold dos demais.

### 4.1 Por que um módulo novo, e não endpoints dentro de cada serviço dono

- **Camada anticorrupção**: o formato de payload do PDV (flags `char(1)`, IDs locais por
  dispositivo, envelope de lote) é um dialeto próprio do PDV — não deveria vazar para
  dentro de `cadastro-base-service`/`controle-estoque-service`, que hoje têm APIs limpas,
  RESTful, pensadas para o domínio, não para o formato de outro sistema.
- **Composição cross-serviço é responsabilidade de ninguém hoje** — o payload de produto
  para o PDV precisa juntar `cadastro-base-service` (dados do produto) +
  `controle-estoque-service` (saldo agregado); o de operador precisa juntar
  `seguranca-service` + `cadastro-base-service` (seção 2.3). Isso é trabalho de
  orquestração, SRP pede um dono próprio.
- **Auditoria de sincronização (pergunta 3) é um domínio em si** — `LoteSincronizacao`/
  `RegistroSincronizacao` não pertencem a nenhum serviço de negócio existente.
- Mesmo padrão arquitetural já usado no monorepo: cada preocupação cross-cutting nova
  ganhou o próprio serviço (RBAC saiu para `seguranca-service`, BI é o próprio
  `business-intelligence-service` lendo de outros). Sincronização segue a mesma régua.

### 4.2 Responsabilidades

1. **Expor os endpoints de pull** que o PDV chama para baixar cadastro (seção 2.1),
   compondo chamadas internas (Client Credentials, mesmo padrão da Fase 3/5) aos serviços
   donos.
2. **Expor os endpoints de push** que o PDV chama para subir operação de caixa (seção 2.2),
   validar o envelope, checar idempotência, persistir o registro de auditoria e publicar
   no RabbitMQ para processamento assíncrono pelos serviços donos.
3. **Manter o ledger de auditoria** (`LoteSincronizacao`/`RegistroSincronizacao`) —
   consultável por loja/dispositivo/período/status, inclusive pela equipe de suporte.
4. **Relay do outbox** (seção 5.3) — job agendado que garante publicação no RabbitMQ mesmo
   se o broker estava fora do ar no momento exato do push.
5. **Reprocessamento de DLQ** (seção 6) — endpoint manual + (futuro) automático.

### 4.3 O que este módulo explicitamente NÃO faz

- Não guarda cópia duradoura do cadastro (não é cache — cada pull de fato consulta os
  serviços donos na hora, dado que o volume por chamada é per-loja, não um full-scan tipo
  BI). Se isso se provar lento na prática, cache com invalidação por evento é evolução
  futura, não desenho inicial.
- Não decide regra de negócio nenhuma (não recalcula tributação, não valida limite de
  crédito) — só traduz formato e garante entrega. Regra de negócio mora nos serviços donos.

---

## 5. Contratos de sincronização

### 5.1 Pull (ERP → PDV) — cursor de alteração, não full dump

`GET /api/v1/sync/produtos?lojaId=&desde={timestampCursor}&pagina=&tamanho=`
(mesma forma para clientes, tabelas de preço, formas de pagamento, operadores).

- `desde` é um cursor **carimbado pelo servidor** (`dataAtualizacao` das entidades, já
  existente via `BaseEntity`), nunca o relógio do PC da loja — evita todo problema de
  clock skew (cenário da seção 8).
- Resposta inclui `proximoCursor` — o PDV só avança seu cursor local depois de persistir
  a página inteira com sucesso (se cair no meio, repete a mesma página, idempotente por
  natureza porque é leitura).
- Primeira sincronização de um terminal novo: `desde` ausente/nulo = carga completa
  paginada (mesmo endpoint, sem cursor).

### 5.2 Push (PDV → ERP) — envelope de lote com resultado por item

`POST /api/v1/sync/lotes` — corpo:
```json
{
  "lojaId": 1, "dispositivoId": "CAIXA-01", "tipoRegistro": "ESTOQUE_MOVIMENTO",
  "registros": [
    { "idOrigem": "10023", "payload": { ... } },
    { "idOrigem": "10024", "payload": { ... } }
  ]
}
```
Resposta **síncrona, imediata** (o PDV não espera o processamento downstream terminar):
```json
{
  "loteId": "uuid-do-lote",
  "resultados": [
    { "idOrigem": "10023", "chaveIdempotencia": "1:CAIXA-01:ESTOQUE_MOVIMENTO:10023", "situacao": "ACEITO" },
    { "idOrigem": "10024", "situacao": "REJEITADO", "motivo": "payload inválido: quantidade ausente" }
  ]
}
```
`ACEITO` = passou validação de envelope e foi persistido/publicado — **não** significa que
o Estoque já aplicou o movimento, só que não será perdido. `REJEITADO` = erro de forma
(payload malformado), o PDV precisa corrigir e reenviar — não entra na fila.

### 5.3 Idempotência e o padrão Outbox

Chave de idempotência: `lojaId:dispositivoId:tipoRegistro:idOrigem` (`UNIQUE` no banco do
`sincronizacao-service`). Reenvio do mesmo lote (ex.: PDV não recebeu a resposta por queda
de rede e reenvia por segurança) é detectado e devolve o resultado já registrado, sem
duplicar processamento — resolve o cenário de reenvio duplicado (seção 8).

**Outbox**: `RegistroSincronizacao` é gravado em transação local (`status=RECEBIDO`)
**antes** de tentar publicar no RabbitMQ. Um job (`@Scheduled`, curto intervalo) varre
registros `RECEBIDO` não publicados e publica — se o broker estiver fora do ar no exato
momento do `POST`, o dado já está durável no MySQL e será publicado assim que o RabbitMQ
voltar, sem que o PDV precise saber ou reenviar. Isso fecha a garantia de "nunca perder
dado operacional" (princípio 3) mesmo num cenário de indisponibilidade do broker.

---

## 6. RabbitMQ — topologia e Dead Letter (pergunta 4)

### 6.1 Topologia

- **Exchange** `cactus.sincronizacao` (`topic`), durável.
- **Routing key** = `tipoRegistro` em minúsculas (`estoque.movimento`, `vendas.cabecalho`,
  `caixa.movimento`, `caixa.sangria`, `caixa.suprimento`, `fiado.lancamento`, ...).
- **Uma fila por consumidor de domínio**, vinculada pela routing key relevante:
  `estoque.movimento.q` (consumida por `controle-estoque-service`), e as filas dos módulos
  ainda não construídos (seção 9) entram quando esses módulos existirem — a topologia já
  nasce pronta para múltiplos consumidores sem redesenho.
- Cada fila principal declara `x-dead-letter-exchange` apontando para
  `cactus.sincronizacao.dlx` (`topic` também), que roteia para
  `<nome-da-fila>.dlq` correspondente.

### 6.2 Política de retry

- Retry **com backoff**, não imediato: mensagem rejeitada (`nack`, `requeue=false`) cai na
  DLQ de retry intermediária com TTL crescente (ex.: 30s → 2min → 10min), que ao expirar
  reencaminha (dead-letter novamente) de volta à fila principal — padrão "delayed retry via
  TTL + DLX" (não precisa de plugin extra, é composição de exchanges padrão do RabbitMQ).
- **Máximo 5 tentativas** (contador no header `x-death` da própria mensagem, o RabbitMQ já
  mantém isso). Na 5ª falha, vai para a DLQ **final** (`<fila>.dlq`), não mais retentada
  automaticamente.
- Falha = exceção no consumidor (erro de negócio real, ex. produto referenciado não existe
  mais) — timeout de rede transitório já é coberto pelas 5 tentativas antes de desistir.

### 6.3 Nunca depender só da fila para auditoria

A DLQ do RabbitMQ é **complementar**, não a fonte de verdade — `RegistroSincronizacao` no
`sincronizacao-service` é atualizado para `status=ERRO` (com a mensagem de erro) por um
listener da fila DLQ final, então o registro fica consultável mesmo que a fila do RabbitMQ
seja um dia purgada/expirada. Suporte consulta erro por `GET /api/v1/sync/registros?status=ERRO&lojaId=`,
não precisa abrir o painel do RabbitMQ.

### 6.4 Reprocessamento

`POST /api/v1/sync/registros/{id}/reprocessar` (`SINCRONIZACAO_GERENCIAR`) — republica a
mensagem original (payload já está salvo no `RegistroSincronizacao`) na fila principal,
zerando o contador de tentativas. Ação nominal (registra `usuarioId` de quem reprocessou),
mesmo padrão de rastreabilidade nominal já usado em aprovação de ajuste/pedido.

### 6.5 Estados de `RegistroSincronizacao`

`RECEBIDO` (persistido, aguardando publicação) → `PUBLICADO` (na fila, outbox concluído) →
`PROCESSADO` (consumidor aplicou com sucesso) | `ERRO` (esgotou tentativas, foi para DLQ
final — ação manual necessária).

---

## 7. Sincronização de produtos para balanças (pergunta 5)

**Decidido (2026-08-31)**: os dois cenários existem e precisam de tratamento — balança
mediada pelo PDV **e** balança de rede autônoma (ex. autoatendimento de hortifruti que fala
TCP/IP direto, sem passar pelo terminal de caixa). São dois caminhos, não um só:

### 7.1 Caminho A — balança mediada pelo PDV (sem trabalho novo)

Já está coberto pelo desenho existente: `Produto.codigoBalanca` (cadastro-base, Fase 1b) é
só mais um campo do payload de pull de produto (seção 2.1). `PDV_CONFIGURACAO_BALANCA` já
é "config técnica de hardware, só PDV" — é o **PDV** quem fala o protocolo proprietário
(serial/TCP, SDK do fabricante) e empurra o PLU pro equipamento, usando o `codigoBalanca`
que já recebeu do ERP no cadastro normal de produto. Nenhuma peça nova de sincronização.

### 7.2 Caminho B — balança de rede autônoma (peça nova, contrato pronto nesta fase)

Uma balança de rede não é endereçável pela internet — ela vive na LAN da loja, atrás do
mesmo NAT/firewall que hoje só o PDV atravessa (é exatamente por isso que o PDV existe como
processo local, não como cliente direto de nuvem). Uma balança autônoma tem o mesmo
problema: alguém precisa estar *na rede da loja* pra falar o protocolo do fabricante com
ela. Duas peças, escopos diferentes:

- **Contrato do lado ERP (nesta fase)**: `GET /sync/produtos/balanca?lojaId=&desde=` no
  `sincronizacao-service` — mesmo mecanismo de cursor/paginação de `GET /sync/produtos`
  (seção 5.1), mas com payload **reduzido ao que uma balança de PLU precisa**:
  `codigoBalanca`, descrição truncada (a maioria dos equipamentos limita a ~20-24
  caracteres), preço por kg/unidade, indicador peso-variável×unidade, validade em dias
  (etiqueta de peso variável costuma imprimir data de validade). Baixo custo de construir
  já que é o mesmo dado do produto, só filtrado/achatado diferente — entra no checklist
  desta fase (seção 10.6).
- **Bridge/agente local na loja (fora do escopo desta fase, registrado como próxima peça)**:
  um processo rodando na LAN da loja que consome `GET /sync/produtos/balanca` (autenticado
  com o mesmo client Client Credentials **por loja**, seção 9 — decidido) e fala o protocolo
  específico do fabricante da balança (Toledo/Filizola/Urano/outro — cada um com SDK e
  protocolo próprios, precisa saber qual(is) equipamento(s) o cliente-alvo usa antes de
  implementar). Esse bridge **não é o cactus-pdv** (por definição do cenário — não passa por
  ele) nem faz parte deste monorepo de serviços de nuvem (roda localmente, como o próprio
  PDV) — é um componente novo e pequeno, fora do escopo de código desta fase. Fica marcado
  como a peça a retomar quando houver um cliente/hardware real pra validar contra (mesma
  régua já usada no projeto: não construir integração sem hardware/protocolo real pra
  testar).

---

## 8. Cenários e casos de borda a prever (pergunta 6)

| # | Cenário | Tratamento |
|---|---|---|
| 1 | Primeira sincronização de um terminal novo | Pull sem cursor = carga completa paginada (seção 5.1) |
| 2 | PDV fica dias/semanas offline, acumula milhares de registros de push | Envelope de lote com tamanho máximo configurável; PDV pagina o próprio envio; `sincronizacao-service` não impõe limite artificial de fila (RabbitMQ aguenta backlog) |
| 3 | Reenvio duplicado (PDV não recebeu o ACK por queda de rede) | Chave de idempotência (seção 5.3) — reenvio devolve o resultado já registrado, sem duplicar |
| 4 | Relógio do PC da loja desincronizado | Cursor de pull é carimbo do servidor, nunca do dispositivo (seção 5.1); registros de push carimbam `dataRecebimento` no servidor, o timestamp do PDV vai só como campo informativo no payload, nunca usado para lógica de sincronização |
| 5 | Falha parcial dentro de um lote (1 de 50 registros é inválido) | Resultado por item (seção 5.2) — os outros 49 seguem normalmente, só o item inválido é rejeitado |
| 6 | RabbitMQ fora do ar no momento do push | Outbox (seção 5.3) — dado já está durável no MySQL do `sincronizacao-service`, publicado quando o broker voltar |
| 7 | Consumidor de domínio (ex. Estoque) fora do ar | Mensagem fica na fila até o consumidor voltar (RabbitMQ é durável) — não é responsabilidade do PDV nem do `sincronizacao-service` |
| 8 | Erro de negócio real (produto referenciado não existe mais no ERP) | Esgota retries → DLQ final → `status=ERRO` auditável → correção manual/reprocessamento (seção 6.4) — nunca falha silenciosa |
| 9 | Produto alterado no ERP no meio de uma venda em andamento no PDV | Fora do escopo de sincronização — a venda já usa o preço que o PDV tinha localmente no momento da venda (o PDV é quem decide o preço praticado, o ERP não sincroniza "no meio" de uma venda); mudança de preço só vale a partir do próximo pull |
| 10 | Loja/terminal bloqueado (inadimplência, licença) | Fora de escopo desta fase — depende do módulo de licenciamento (`LICENCA_CACHE`), não construído; quando existir, o pull/push simplesmente passa a responder 403 e o PDV já sabe tratar isso (mesmo padrão de erro HTTP de qualquer outro endpoint) |
| 11 | Volume de lojas crescendo (10 → 500) | RabbitMQ com múltiplas filas por domínio escala horizontalmente por consumidor (mais instâncias do `controle-estoque-service` consumindo a mesma fila); reavaliar Kafka só se isso um dia não bastar (seção 3.2) |
| 12 | Cliente/produto excluído no ERP mas ainda referenciado numa venda antiga sincronizando depois | Exclusão no cadastro-base já é lógica (`situacao`), nunca física — a referência sempre resolve, mesmo que "inativo" |
| 13 | Segurança do client de sincronização | Client Credentials por loja (não por terminal individual — nível de granularidade discutido na seção 11) via `seguranca-service`, mesmo padrão Fase 3/5; sem necessidade de mTLS adicional neste estágio |

---

## 9. Decisões confirmadas com o usuário (2026-08-31)

1. **Ordem de construção dado o gap de destino (seção 2.2)**: confirmado — a Fase 6
   constrói a metade viável agora: pull de cadastro completo (2.1) + push de
   `ESTOQUE_MOVIMENTO` (única linha pronta em 2.2) + toda a infraestrutura de
   auditoria/outbox/DLQ (já reaproveitável quando os próximos módulos existirem).
   Vendas/Caixa/Financeiro ficam como **fases novas subsequentes** (a nomear, ex.
   `vendas-service`/`financeiro-service`), fora do escopo de código desta fase.
2. **Granularidade do client OAuth2 de sincronização**: confirmado — **um client por
   loja**, com `dispositivoId` no envelope garantindo a rastreabilidade fina por caixa sem
   precisar de um client próprio por terminal.
3. **Infraestrutura RabbitMQ local**: confirmado — Docker Compose,
   `docker/rabbitmq/docker-compose.yml` (seção 3.4), usuário sobe manualmente antes do
   início do código desta fase.
4. **Balança autônoma fora do PDV**: confirmado que **os dois cenários existem** — desenho
   de dois caminhos registrado na seção 7 (caminho A via PDV, sem trabalho novo; caminho B
   via bridge local + endpoint `GET /sync/produtos/balanca` novo, este último dentro do
   escopo de código desta fase — o bridge em si fica para quando houver hardware real
   pra validar contra).

---

## 10. Checklist de implementação

### 10.0 Pré-requisitos de infraestrutura
- [x] Decisões 9.1–9.4 confirmadas com o usuário (2026-08-31)
- [x] RabbitMQ rodando (`cactus-rabbitmq`, vhost `cactus`) — gerenciado pelo usuário fora
      deste repo, mesmo padrão do `cactus-mysql` (ver seção 3.4 atualizada)
- [x] Painel/broker confirmado no ar pelo usuário (2026-08-31)

### 10.1 Scaffold do `sincronizacao-service`
- [x] Novo módulo Maven (porta 8087, schema `cactus_sincronizacao`), Resource Server puro
- [x] Client de serviço novo em `OAuth2ClientSeeder`/`AuthorizationServerConfig`
      (`sincronizacao-service`, authorities compostas do que precisar chamar em
      cadastro-base/estoque/seguranca)
- [x] Dependência RabbitMQ (`spring-boot-starter-amqp`) + config de conexão
- [x] `RabbitConfig`: exchange principal + exchange DLX + filas + bindings (seção 6.1)

### 10.2 Auditoria (base de tudo, construir antes do resto)
- [x] Entidades `LoteSincronizacao`/`RegistroSincronizacao` (estados da seção 6.5)
- [x] `UNIQUE` de idempotência (`lojaId, dispositivoId, tipoRegistro, idOrigem`)
- [x] Endpoints de consulta (`GET /sync/lotes`, `GET /sync/registros?status=&lojaId=`)
- [x] RBAC: `SINCRONIZACAO_CONSULTAR`/`SINCRONIZACAO_GERENCIAR`, grants explícitos na
      própria migration (lição fixada desde a Fase 3)

### 10.3 Outbox + publicação
- [x] Job `@Scheduled` de relay do outbox (registros `RECEBIDO` → publica → `PUBLICADO`)
- [x] Listener da DLQ final → atualiza `RegistroSincronizacao` para `ERRO`
- [x] Endpoint de reprocessamento manual (seção 6.4)

### 10.4 Pull — ERP → PDV
- [x] `GET /sync/produtos` (compõe cadastro-base + saldo agregado do controle-estoque)
- [x] `GET /sync/clientes`
- [x] `GET /sync/tabelas-preco`
- [x] `GET /sync/formas-pagamento`
- [x] `GET /sync/operadores` (composição seguranca-service + cadastro-base, seção 2.3)
- [x] `GET /sync/configuracao-loja`
- [x] Cursor por `dataAtualizacao` + paginação em todos

### 10.5 Push — PDV → ERP (escopo desta fase: só o que tem destino pronto)
- [x] `POST /sync/lotes` (envelope genérico, seção 5.2)
- [x] Novo `OrigemMovimentoEstoque.VENDA_PDV` no `controle-estoque-service`
- [x] Consumidor `estoque.movimento.q` no `controle-estoque-service`
- [x] Confirmação de processamento de volta ao `sincronizacao-service`

### 10.6 Balança (seção 7 — dois caminhos)
- [x] Confirmar `codigoBalanca` presente no payload de `GET /sync/produtos` (caminho A, via
      PDV — validado ao vivo, campo presente mesmo quando `null`)
- [x] `GET /sync/produtos/balanca?lojaId=&desde=` (caminho B — payload reduzido: código,
      descrição truncada, preço/kg ou unidade, indicador peso-variável, validade em dias)
- [ ] Bridge/agente local de balança autônoma: **fora do escopo de código desta fase**,
      registrado para quando houver hardware/protocolo real do fabricante pra validar contra

### 10.7 Documentação e Swagger
- [x] Todos os controllers com `@Tag`/`@Operation`/`@ApiResponse(s)` desde a criação
- [x] Atualizar `CLAUDE.md` com o resumo executivo da fase + link para este arquivo

---

## 11. Checklist de testes

### 11.1 Unitário/integração (por módulo)
- [x] Idempotência: reenviar o mesmo `idOrigem` duas vezes não duplica processamento —
      validado ao vivo (reenvio do lote 9001 devolveu o mesmo resultado, saldo não mexeu)
- [x] Envelope com item inválido não derruba os demais itens do lote — **gap real
      encontrado e corrigido na mesma sessão (2026-08-31)**: `LoteSincronizacaoPushService
      .receber()` não fazia nenhuma validação estrutural por item — todo `payload` que
      era JSON válido virava `ACEITO` (o branch `REJEITADO` do contrato da seção 5.2 era
      código morto). Corrigido com `RegistroPayloadValidator` (novo, por `tipoRegistro` —
      hoje só `ESTOQUE_MOVIMENTO`: exige `produtoId`/`depositoId`/`tipoMovimento`/
      `quantidade`), chamado antes de checar idempotência/persistir. Item que falha vira
      `REJEITADO` na resposta síncrona, sem gerar `RegistroSincronizacao` nem consumir a
      chave de idempotência (o PDV pode corrigir e reenviar com o mesmo `idOrigem`).
      Validado ao vivo: lote com 1 item válido + 2 inválidos (um sem `produtoId`, outro
      sem `quantidade`) devolveu `totalAceitos=1`/`totalRejeitados=2` com motivo
      específico por item, e só o item válido virou registro persistido/processado —
      os outros dois nunca tocaram o banco.
- [x] Outbox: simular RabbitMQ indisponível no momento do `POST`, confirmar publicação
      posterior pelo job de relay sem intervenção manual — validado ao vivo (`docker stop
      cactus-rabbitmq`, push ficou `RECEBIDO`, `docker start`, relay publicou sozinho
      assim que a conexão AMQP estabilizou — levou ~1 min após o container reportar
      "healthy" pras reconexões pararem de falhar, não instantâneo, mas sem intervenção)
- [x] Retry: forçar falha no consumidor, confirmar backoff e DLQ final — validado ao vivo
      (produtoId inexistente, 1 tentativa reportada — ver nota abaixo — DLQ final recebeu
      a mensagem, `RegistroSincronizacao` virou `ERRO` via `DlqListener` automaticamente,
      motivo consultável via API). Nota: o desenho original desta seção (3 filas TTL em
      cascata, 5 tentativas) foi conscientemente simplificado no código pra
      `StatelessRetryOperationsInterceptor` (retry em memória, bloqueando a thread do
      consumidor, backoff 2s×3.0^n até 30s, `RepublishMessageRecoverer` manda pra DLQ final
      direto) — decisão já registrada no Javadoc de `controle-estoque-service.RabbitConfig`,
      não uma descoberta desta sessão, só confirmada funcionando ao vivo agora
- [x] Reprocessamento manual: registro em `ERRO` volta pra fila (`RECEBIDO`, outbox
      republica) — validado ao vivo, incluindo RBAC (`SINCRONIZACAO_GERENCIAR` exigido,
      403 confirmado sem a authority)
- [x] Cursor de pull: validado indiretamente — `GET /sync/produtos` devolveu
      `proximoCursor` = `dataAtualizacao` do produto, mecanismo íntegro
- [ ] N+1: não reverificado nesta sessão (nenhuma listagem nova testada com múltiplos
      registros — só 1 produto/1 registro de cada vez nos testes ao vivo)

### 11.2 Resiliência (ponta a ponta, ambiente local)
- [x] Derrubar RabbitMQ, disparar push, subir RabbitMQ de novo, confirmar entrega — ok
- [x] Derrubar `controle-estoque-service`, disparar push de `ESTOQUE_MOVIMENTO`, subir de
      novo, confirmar consumo da fila acumulada — ok, saldo refletiu corretamente após o
      restart consumir a mensagem que ficou em `PUBLICADO`/`messages_unacknowledged`
- [ ] Simular lote grande (milhares de registros) representando um PDV que ficou dias
      offline, confirmar que a paginação do envio e o processamento não travam — não
      testado nesta sessão (custo/benefício de tempo, sem indício de problema)

### 11.3 Segurança
- [x] Client de sincronização de uma loja não consegue enviar/ler dado de outra loja —
      **gap real encontrado e corrigido na mesma sessão (2026-08-31)**: construída a
      infraestrutura de client "um por loja" completa. `seguranca-service` ganhou
      `ClienteLojaService`/`ClienteLojaController` (`POST`/`GET
      /api/v1/sincronizacao/clientes-loja`, `SEGURANCA_GERENCIAR`) — provisiona um
      `RegisteredClient` Client Credentials com `clientId="loja-{empresaId}"` e
      `lojaId`/`descricao` gravados como `String` no `ClientSettings` customizado
      (`String`, não `Long`, pra não esbarrar no mesmo `PolymorphicTypeValidator`
      restrito já encontrado em `JdbcAuthorizationServerConfig`). `secret` só aparece na
      resposta da criação (32 bytes aleatórios, nunca recuperável depois — só o hash
      bcrypt persiste). `AuthorizationServerConfig.jwtTokenCustomizer` passou a
      reconhecer esse `ClientSettings` e injeta a claim `lojaId` no token (sem
      authorities específicas — isolamento é só por `lojaId`). No `sincronizacao-service`,
      novo `LojaAutorizacaoService` (`validarAcesso(jwt, lojaIdSolicitado)`: token sem a
      claim = cliente de confiança, passa sem restrição; token com a claim tem que bater
      com o `lojaId` da chamada, senão `AccessDeniedException`/403) aplicado nos 4
      endpoints de pull com `lojaId` e no push. Validado ao vivo: client `loja-1`
      acessando `lojaId=1` (200, dado real), o mesmo client tentando `lojaId=2` no pull
      e no push (403 nos dois), token de serviço/admin sem claim continuando sem
      restrição (retrocompatibilidade preservada), provisionamento duplicado rejeitado
      (409)
- [x] Endpoints de reprocessamento/gerenciamento exigem `SINCRONIZACAO_GERENCIAR`, não
      acessíveis por `SINCRONIZACAO_CONSULTAR`/token sem a authority — validado ao vivo
      (403 com token de serviço sem a authority, 200 com token ADMIN)

### 11.4 Regressão do monorepo
- [x] Reactor completo (8 módulos) compila limpo
- [x] Todos os serviços sobem juntos, JWKS aceito em todos — validado com
      `seguranca-service`/`cadastro-base-service`/`controle-estoque-service`/
      `gestao-compras-service`/`gestao-documentos-service`/`business-intelligence-service`/
      `sincronizacao-service` de pé simultaneamente
- [ ] `/v3/api-docs` de todos os serviços com zero operação sem tag — não reverificado
      nesta sessão (só confirmado que os controllers têm `@Tag` no código-fonte)

**Validação ao vivo concluída em 2026-08-31** (não só compilação — mesma régua de todas
as fases anteriores). Dois gaps reais encontrados nesta sessão, ambos corrigidos e
revalidados ao vivo na mesma sessão: (1) `REJEITADO` síncrono da seção 5.2 era código
morto — fechado com `RegistroPayloadValidator`; (2) isolamento por loja no push/pull não
existia — fechado com a infraestrutura completa de client "um por loja"
(`ClienteLojaService` no seguranca-service + `LojaAutorizacaoService` no
sincronizacao-service). Fora esses dois, nenhum bug de execução encontrado — outbox,
retry, DLQ, idempotência, RBAC e resiliência a queda de broker/consumidor funcionaram
exatamente como desenhado.

## 12. Fase 6-B — caixa e fiado do PDV no financeiro (2026-10-24) ✅ itens 1-4 da reavaliação 2.3-bis

### Decisões confirmadas com o usuário
1. **Uma conta `CAIXA_INTERNO` por terminal** (`PDV_CAIXA`), não uma por loja. Criada automaticamente no primeiro envio daquele terminal (`conta_bancaria.pdv_caixa_id`, `UNIQUE(empresa_id, pdv_caixa_id)`), com nome "Caixa PDV <nome do terminal>". O saldo de cada caixa bate com a conferência do operador, sem misturar terminais.
2. **Conferência vira ajuste da diferença**: usa a divergência (contado − esperado) **da forma DINHEIRO**, que o PDV grava em `PDV_CONFERENCIA_CAIXA_DETALHE` (ver correção abaixo). Sobra vira `ENTRADA`, falta vira `SAIDA`, origem `CONFERENCIA_CAIXA_PDV`. Isso fica correto mesmo sem as vendas do PDV no ERP (item 5): o ajuste corrige o caixa pela diferença real apurada no balcão, não por um "esperado" que o ERP ainda não sabe calcular.

### Contrato de payload (o adaptador do PDV manda ids do ERP, mesmo critério do `ESTOQUE_MOVIMENTO`)
Um lote por tipo (`LoteSincronizacaoRequest.tipoRegistro` é único). Datas em `yyyy-MM-dd`.

| `tipoRegistro` | Origem no PDV | Campos obrigatórios | Opcionais | Efeito no financeiro |
|---|---|---|---|---|
| `SANGRIA` | `PDV_SANGRIA` | `pdvCaixaId`, `valor`, `dataMovimento` | `pdvCaixaNome`, `observacao` | `SAIDA` / `SANGRIA_PDV` |
| `SUPRIMENTO` | `PDV_SUPRIMENTO` | `pdvCaixaId`, `valor`, `dataMovimento` | `pdvCaixaNome`, `observacao` | `ENTRADA` / `SUPRIMENTO_PDV` |
| `ABERTURA_CAIXA` | `PDV_MOVIMENTO` (abertura) | `pdvCaixaId`, `valorFundoInicial`, `dataAbertura` | `pdvCaixaNome` | `ENTRADA` / `ABERTURA_CAIXA_PDV` (fundo zero: só confirma) |
| `CONFERENCIA_CAIXA` | `PDV_CONFERENCIA_CAIXA` + `_DETALHE` da forma dinheiro | `pdvCaixaId`, `valorDivergenciaDinheiro` (com sinal), `dataConferencia` | `observacao` | ajuste da diferença (zero: só confirma) |
| `FIADO_LANCAMENTO` | `CLIENTE_FIADO_LANCAMENTO` | `clienteId`, `tipoLancamento` (C/P), `valor`, `dataLancamento`; `pdvCaixaId` quando P | `observacao` | C: título `FIADO_PDV`; P: baixas |

`origemId` de todo movimento e título gerado = `RegistroSincronizacao.id`, então qualquer lançamento é rastreável até o envio do PDV.

### Regras deliberadas
- **Fechamento de caixa** não movimenta dinheiro além da conferência: fica só na auditoria, sem tipo de registro próprio.
- **Fiado C**: título com vencimento em data do lançamento + `cactus.financeiro.pdv.fiado-prazo-vencimento-dias` (default 30), porque o PDV não tem vencimento de fiado.
- **Fiado P**: abate os títulos `FIADO_PDV` em aberto do cliente do mais antigo pro mais novo, uma baixa por título (via `ContaReceberService#registrarBaixa`, o mesmo caminho da API), na conta do terminal onde o dinheiro entrou. Pagamento maior que a dívida em aberto no ERP (ex: fiado anterior à sincronização) faz a transação inteira falhar e o registro ir pra `ERRO`. Nada é aplicado pela metade, e o excedente nunca vira entrada "solta" de caixa.
- **Idempotência no consumidor**: `sincronizacao_pdv_processado(registro_id)`, inserida na mesma transação do lançamento. Reentrega do RabbitMQ (que entrega "pelo menos uma vez") só reconfirma. Isso complementa a deduplicação por `idOrigem` que já existia na ingestão.
- **Topologia**: fila única `financeiro.pdv.q` (routing key `financeiro.pdv`), despachada pelo `tipoRegistro` que o outbox passou a incluir na mensagem junto com `lojaId` (= `empresaId`). Mesmo retry (5 tentativas com backoff) e DLQ (`financeiro.pdv.q.dlq`) do consumidor de estoque. O `DlqListener` do sincronizacao-service passou a ouvir as duas DLQs. Confirmação em `confirmacao.financeiro.pdv`, já coberta pelo binding `confirmacao.#`.

### Validação ao vivo (RabbitMQ e MySQL reais)
- Abertura nos terminais 901 (R$ 100) e 902 (R$ 80) criou 2 contas separadas.
- No 901: suprimento de R$ 50, sangria de R$ 30 e conferência com falta de R$ 5.
- Fiado: compras de R$ 40 e R$ 60, e um pagamento de R$ 70 no 901, que quitou o título de R$ 40 e deixou o de R$ 60 com R$ 30 em aberto.
- Resultado: o caixa 901 fechou em exatamente R$ 185 e o 902 em R$ 80. A conferência sem divergência foi confirmada sem gerar movimento.
- **Rejeições estruturais síncronas, com mensagem clara**: sangria sem valor, fiado P sem `pdvCaixaId`, `tipoLancamento` inválido.
- **Robustez**:
  - O reenvio do mesmo `idOrigem` não duplicou nada.
  - Uma reentrega publicada direto no broker foi só reconfirmada: continua 1 sangria, saldo intacto.
  - Um pagamento de R$ 1.000 contra R$ 30 de dívida esgotou as tentativas e foi pra `ERRO`, com o título intacto.
- Tudo `PROCESSADO` na auditoria. No navegador: tipos novos com rótulo na tela de auditoria e contas de terminal listadas em Contas Bancárias, sem erro de console. Dados de teste removidos.

### Pendências pequenas — fechadas no mesmo dia
- **Erro de negócio não é mais retentado** (consumidores de estoque e financeiro): a política de retry
  (`configureRetryPolicy`, `predicate`) exclui `BusinessException`/`ConflictException`/`NotFoundException`,
  procurando na cadeia de causas (o container embrulha a exceção em `ListenerExecutionFailedException`).
  Repetir uma regra de negócio dá sempre o mesmo resultado; erro técnico continua com as 5 tentativas.
- **`motivoErro` real**: o `DlqListener` lê o header `x-exception-message` que o `RepublishMessageRecoverer`
  grava, em vez do texto genérico "esgotou tentativas".
- Validado ao vivo: um pagamento de fiado maior que a dívida foi pra `ERRO` em 4s com o motivo "Pagamento de
  fiado de R$ 500 do cliente #1 excede a dívida de fiado em aberto no ERP em R$ 500". Um valor não numérico
  (erro técnico) continuou retentando e só foi pra `ERRO` depois de ~90s, também com o motivo real. Os
  acentos estão corretos no banco (conferido por hex).

### Restam da reavaliação 2.3-bis
Os itens 5 (venda do PDV), 6 (total por forma de pagamento) e 7 (devolução) dependem de decisões de modelagem em `vendas-service`.

## 13. Fase 6-B — venda, pagamento fracionado e devolução do PDV (2026-10-24) ✅ itens 5-7 da reavaliação 2.3-bis

Com isto, **os 7 gaps de envio PDV→ERP da reavaliação 2.3-bis estão fechados**.

### Bug real corrigido na conferência (seção 12)
O `VALOR_DIVERGENCIA` do cabeçalho de `PDV_CONFERENCIA_CAIXA` é a divergência **total, somando todas as formas de pagamento** (confirmado lendo o `ConferenciaCaixaService` do PDV). A 1ª versão lançava esse total como ajuste no caixa de DINHEIRO, então uma diferença de cartão virava sobra/falta de dinheiro na gaveta. Corrigido: o contrato passou a exigir `valorDivergenciaDinheiro` (a linha da forma dinheiro em `PDV_CONFERENCIA_CAIXA_DETALHE`). O campo foi renomeado de propósito: um payload no formato antigo é rejeitado na hora com mensagem clara, em vez de lançar o valor errado em silêncio.

### Decisões
- **Entidade própria `VendaPdv`** no `vendas-service` (decisão do usuário), não `VendaCabecalho`: a venda B2B exige vendedor cadastrado, condição de pagamento e tipo de nota; o balcão tem operador, terminal, pagamento fracionado e NFC-e/SAT já emitida pelo PDV. Agregado: `VendaPdv` → `VendaPdvItem` / `VendaPdvPagamento` (N formas por venda) / `VendaPdvDevolucao`. Só leitura no ERP: `GET /api/v1/vendas-pdv` (período + terminal) e `GET /api/v1/vendas-pdv/{id}`, `VENDAS_CONSULTAR`.
- **Escopo escolhido por mim, a pedido do usuário ("como os grandes ERPs"), no padrão de ERP de varejo:**
  - **Estoque: a venda NÃO baixa.** Confirmado no código do PDV: `EstoqueMovimentoService#registrarSaidaPorVenda` grava a saída (S) e `DevolucaoService` grava a reentrada (D), que chegam pelo `ESTOQUE_MOVIMENTO`. O ledger tem uma fonte só; baixar pelos dois caminhos contaria cada item duas vezes.
  - **Caixa: só o dinheiro líquido** (pagamentos tPag `01` − troco) entra na conta do terminal, via `POST /api/v1/pdv/vendas/dinheiro` no financeiro (idempotente pela mesma tabela `sincronizacao_pdv_processado`, origem `VENDA_PDV`). É exatamente o que o PDV soma como "esperado" de dinheiro na conferência (fundo + suprimentos + vendas em dinheiro − sangrias − troco), então o saldo do terminal no ERP bate com a gaveta.
  - **Cartão/PIX/outros**: ficam registrados nos pagamentos da venda (NSU, rede, crédito/débito), mas ainda **não viram título a receber da operadora**. Isso exige cadastro de adquirente, taxa e prazo de repasse, que não existe, e gerar prazo e taxa "no chute" seria pior que não gerar (no BACKLOG).
  - **Fiado (tPag `05`)**: nada na venda, porque a dívida já chega pelo `FIADO_LANCAMENTO` tipo C.
  - **Devolução**: só registra o fato contra o item (com o gerente autorizador obrigatório do PDV) e limita a quantidade ao que ainda resta do item. Não mexe em caixa: o próprio PDV não decide como o dinheiro volta (sangria, crédito em loja, estorno de cartão); se for em dinheiro, a saída já chega como `SANGRIA`.

### Contrato de payload
| `tipoRegistro` | Obrigatórios | Opcionais |
|---|---|---|
| `VENDA_PDV` | `pdvCaixaId`, `dataHoraVenda` (`yyyy-MM-ddTHH:mm:ss`, hora local da loja), `situacao` (F/C), `valorFinal`, `valorRecebido`, `itens[]` (`numeroItem`, `produtoId`, `quantidade`, `valorUnitario`, `valorTotal`); se F, `pagamentos[]` (`codigoPagamentoNfce` = tPag, `valor`) | `pdvCaixaNome`, `pdvOperadorId`, `clienteId`, `colaboradorId`, `nomeCliente`, `cpfCnpjCliente`, `origemVenda` (B/M/C/D/E), `valorTotalProdutos`, `valorDesconto`, `valorAcrescimo`, `valorTroco`, tributos, `tipoOperacaoFiscal`, `statusFiscal`, `chaveAcessoFiscal`, `protocoloAutorizacao`, `coo`, `ccf`; por item `gtin`, `cfop`, `cst`, alíquota/valores, `cancelado`; por pagamento `formaPagamentoId`, `descricao`, `nsu`, `rede`, `cartaoDebitoCredito`, `estornado` |
| `DEVOLUCAO_PDV` | `vendaIdOrigem` (o `idOrigem` com que a venda foi enviada), `numeroItem`, `quantidadeDevolvida`, `valorDevolvido`, `pdvGerenteAutorizadorId`, `dataHoraDevolucao` | `motivo`, `pdvOperadorId` |

Fila `vendas.pdv.q` (routing `vendas.pdv`, DLQ `vendas.pdv.q.dlq`). O outbox passou a mandar também o `idOrigem` na mensagem (para a devolução achar a venda). O fuso da loja é configurável: `cactus.vendas.pdv.fuso-horario`, default `America/Sao_Paulo`.

### Consistência e robustez
- Venda F só é gravada se a soma dos pagamentos não estornados bater com o recebido, e recebido − troco bater com o final (tolerância de 1 centavo). Inconsistente vai pra `ERRO` com o motivo, sem entrar torta no ERP.
- **Achado no teste ao vivo, corrigido**: devolução que chega antes da sua venda (lotes fora de ordem) é erro técnico, então retenta. Mas com **um consumidor só**, o retry em memória segurava a thread, e a venda ficava presa na fila atrás da devolução que esperava por ela até esgotar as tentativas. Corrigido com **3 consumidores** na fila de vendas. No financeiro, ficou explicitamente **1 consumidor**: lá a ordem importa (um pagamento de fiado não pode passar na frente da compra).
- **Achado secundário, corrigido**: um registro reprocessado com sucesso continuava exibindo o motivo do erro antigo. O `ConfirmacaoListener` agora limpa o `motivoErro` ao marcar `PROCESSADO`.
- A chamada ao financeiro roda dentro da transação do vendas e também é idempotente: se ela passar e o commit do vendas falhar, o retry reaplica e o financeiro reconhece o registro. Falha de comunicação vira erro técnico (retentado), não `BusinessException` (que agora vai direto pra DLQ).

### Validação ao vivo (RabbitMQ, MySQL e navegador reais)
- Abertura do terminal com R$ 100.
- Venda de R$ 50 paga com R$ 40 em dinheiro + R$ 20 no crédito, troco de R$ 10: o caixa foi para exatamente R$ 130 (dinheiro líquido R$ 30).
- Venda em PIX e venda em débito não mexeram no caixa. A venda cancelada foi gravada sem dinheiro.
- Venda inconsistente foi pra `ERRO` com "Pagamentos somam R$ 30, recebido R$ 50".
- Rejeições estruturais: venda fechada sem pagamento, item sem produto, situação inválida.
- Devoluções:
  - 1 unidade aceita;
  - 2 unidades com só 1 restando, item inexistente e venda cancelada foram pra `ERRO` com o motivo real;
  - devolução enviada antes da venda entrou 3s depois da venda chegar (pós-correção);
  - o reprocessamento manual de uma devolução que estava em `ERRO` funcionou.
- Reentrega direta da venda no broker: não lançou dinheiro de novo.
- Conferência: o campo antigo foi rejeitado; uma falta de R$ 2 no dinheiro levou o caixa a R$ 128.
- Nenhum movimento de estoque gerado pelas vendas.
- ENTREGADOR recebe 403 na consulta.
- Tela "Vendas do PDV" (menu Vendas) conferida no navegador: lista, linha expandida com itens, pagamento fracionado e devoluções. Dados de teste removidos.

### Fora de escopo, registrado no BACKLOG
- Recebíveis de cartão/PIX (exige adquirente/taxa/prazo).
- Somar `VendaPdv` nos relatórios de vendas e no BI (hoje só leem `VendaCabecalho`).
- Cancelamento de uma venda já sincronizada (o PDV reenviaria o mesmo `idOrigem`, que a deduplicação descartaria — precisa de um tipo de registro de cancelamento próprio).

