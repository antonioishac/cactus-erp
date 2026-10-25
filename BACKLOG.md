# Backlog — Cactus ERP

Lista de itens pendentes, não bloqueantes, levantados durante o desenvolvimento.
Nenhum item aqui impede o uso normal do sistema — são melhorias, débitos técnicos
pequenos ou lembretes operacionais. Detalhamento completo de cada achado (quando
existe) está registrado no `CLAUDE.md` correspondente; este arquivo é só o índice
acionável, pra não precisar vasculhar o histórico de entregas pra saber "o que
falta fazer".

Convenção: `[ ]` aberto, `[x]` feito (mantido riscado por um tempo pra rastro,
removido quando não agregar mais valor manter). Datas são de quando o item foi
identificado, não de quando foi resolvido.

## 🔴 CRÍTICO — Reforma Tributária (IBS/CBS/Imposto Seletivo)

Diferente de todo o resto deste arquivo, **este item não é um débito técnico
nem uma melhoria — é obrigação legal já VENCIDA**: a Nota Técnica 2025.002
tornou os campos de IBS/CBS obrigatórios em produção na NF-e/NFC-e desde
03/ago/2026 pro Regime Normal (Simples Nacional/MEI entra em 04/jan/2027). O
motor tributário atual (`cadastro-base-service`, Fase 1b) não modela nada
disso — só cobre ICMS/IPI/PIS/COFINS/ISS no formato pré-reforma. Qualquer
emissão fiscal real feita por este sistema a partir dessas datas, sem o
subsistema IBS/CBS, está **fora de conformidade com a SEFAZ**. Mitigante
atual: nenhuma emissão real foi feita ainda (bloqueio de certificado de
homologação, ver seção Fiscal abaixo) — mas isso deixa de proteger o sistema
no momento em que o certificado existir e a emissão real for liberada.
Detalhamento completo (estrutura do Grupo `UB`, subgrupos `IBSUF`/`IBSMun`/
`CBS`, decisão confirmada com o usuário de tratar como fase própria) na seção
"Backend" abaixo e no root `CLAUDE.md`, ANÁLISE: MÓDULO CONTÁBIL E FISCAL.
**Ação recomendada**: priorizar uma rodada de pesquisa dedicada (campo-a-campo
de cada subgrupo, antes de qualquer código — não modelar tributo real "no
chute") assim que a capacidade de trabalho permitir, independente da ordem dos
outros itens deste backlog.

## Frontend (cactus-erp-web)

### Funcionais — valem revisitar

- [x] ~~**`RegistrarMovimentoModal` (Estoque)** — `aprovadorId` cru~~ —
      **esclarecido em 2026-09-13, já estava feito**: o campo já é um
      `AsyncSelect` (`useBuscaUsuariosParaSelecao`/`buscarUsuariosParaSelecao`,
      `GET /api/v1/usuarios?busca=X&size=20`), não um `InputNumber` — a
      entrada original do backlog estava desatualizada. Verificado ao vivo
      via `curl` contra o `seguranca-service` real.
- [x] ~~**`ComponenteKitModal` (Estoque)** — nunca validado com submit real**~~
      — **fechado em 2026-09-13**: contrato validado ao vivo via `curl`
      contra o `controle-estoque-service` real — componente adicionado
      (201), componente circular rejeitado (422
      `ESTOQUE_KIT_COMPONENTE_CIRCULAR`), baixa de kit sem saldo rejeitada
      (422 `ESTOQUE_SALDO_INSUFICIENTE`), entrada de estoque + baixa de kit
      com sucesso (204), movimento `SAIDA`/`KIT` cascateado conferido
      (2 unidades = 1 kit × 2 do componente). Sem clique real em navegador
      (sem ferramenta de automação disponível nesta sessão), mas o contrato
      request/response que a UI usa foi exercitado de ponta a ponta.
- [x] ~~**Motor Tributário — sem tela de gerenciamento própria**~~ —
      **feito em 2026-09-13**: `GruposTributariosListPage`/
      `OperacoesFiscaisListPage` (CRUD simples) +
      `ConfiguracaoTributariaFormPage`/`ConfiguracoesTributariasListPage`
      (a célula Grupo×Operação, com `icmsPorUf` gerenciado como lista local
      + modal por UF, PIS/COFINS/IPI como seções do mesmo form) — 3 itens
      novos em `NavConfig.tsx` sob "Cadastro Base". **BUG REAL encontrado e
      corrigido no backend**: `ConfiguracaoTributariaService.listar()`
      ignorava os filtros `grupoTributarioId`/`operacaoFiscalId` e sempre
      chamava `findAll` — nunca detectado antes porque nenhuma UI tinha
      exercitado esses parâmetros. Corrigido com 3 métodos de repositório
      derivados + branch no service; validado ao vivo (filtro por grupo, por
      operação, sem filtro — resultados corretamente escopados nos 3 casos)
      contra o `cadastro-base-service` recompilado/reiniciado.
- [x] ~~**`NavConfig.tsx` — mapeamento módulo→authority nunca conferido**~~ —
      **auditado em 2026-09-13**: toda authority referenciada em
      `NavConfig.tsx` comparada contra o catálogo real (`GET
      /api/v1/funcionalidades?size=200`, ~68 códigos) — nenhum typo/string
      obsoleta encontrado. `LOJA_*`/`SINCRONIZACAO_*` existem no catálogo sem
      entrada no menu — esperado, `loja-virtual-service`/`sincronizacao-service`
      não têm frontend nenhum ainda, não é um gap deste item.
- [x] ~~**GED: integração `entidadeTipo`/`entidadeId` só teórica**~~ —
      **feito em 2026-09-13**: `shared/ui/documentos/DocumentosEntidadeSection.tsx`
      (+ `DocumentoEntidadeUploadModal.tsx`) — seção reaproveitável de
      "Documentos anexados", em `shared/` de propósito (mesmo critério de
      `ProdutoResumoCell`), reaproveitando `useDocumentos`/`useCriarDocumento`/
      `useBaixarUltimaVersao` de `features/ged`. Embutida como nova aba
      "Documentos" em `ProdutoFormPage`/`FornecedorFormPage` — gate de
      "salve o cadastro primeiro" quando `entidadeId` ainda não existe, mesmo
      padrão das sub-abas de Empresa/OsAbertura. `tsc -b`/`oxlint`/`vitest`/
      `vite build` confirmados limpos.
- [x] ~~**Financeiro: desconciliar item já conciliado sem botão na tela**~~ —
      **esclarecido em 2026-10-12, já estava feito**: `ConciliacaoBancariaPage`
      já tem o botão "Desconciliar" por item, dentro da linha expandida de
      cada importação (`ItensImportacaoExpandidos`, só nos itens já
      `conciliado=true`, gate por `CAIXA_MOVIMENTAR`) — a entrada original do
      backlog estava desatualizada, não reconferida depois de implementada.
      (originalmente 2026-09-07, `cactus-erp-web/CLAUDE.md`, Entrega 21)
- [x] ~~**Financeiro: `AplicacoesFinanceirasListPage` sem filtro por conta
      bancária**~~ — **esclarecido em 2026-10-12, já estava feito**: a tela
      já tem o `Select` "Todas as contas" no cabeçalho, igual às outras 3
      listagens do módulo, alimentando `useAplicacoesFinanceiras(contaBancariaId, ...)`
      — a entrada original do backlog estava desatualizada. (originalmente
      2026-09-07, `cactus-erp-web/CLAUDE.md`, Entrega 21)
- [x] ~~**`sincronizacao-service` sem nenhuma tela no `cactus-erp-web`**~~ —
      **fechado em 2026-10-12**: módulo novo `features/sincronizacao/`
      (porta 8087) — `RegistrosSincronizacaoListPage` (filtro status/loja,
      botão "Reprocessar" só em registros `ERRO`, `SINCRONIZACAO_GERENCIAR`),
      `LotesSincronizacaoListPage` (resumo aceito/rejeitado por lote),
      `ClientesLojaListPage` (provisiona o client OAuth2 Client Credentials
      por loja — endpoint vive no **`seguranca-service`**, não no
      `sincronizacao-service`, confirmado por agente de exploração antes de
      codar — `clientSecret` só aparece na resposta de criação, alerta
      explícito na UI de que não será mostrado de novo, com botão copiar).
      Novo item de menu "Sincronização PDV". Validado ao vivo (sem navegador
      disponível — contrato de API via curl, `seguranca-service`+
      `sincronizacao-service` reais, usuário de teste criado via SQL direto e
      removido ao final): listagem de registros/lotes reais, filtro por
      status, reprocessar um registro `ERRO` real virando `RECEBIDO`,
      provisionar/listar client de loja, RBAC confirmado real (`OPERADOR` sem
      `SINCRONIZACAO_GERENCIAR` — 403 real em reprocessar; sem
      `SEGURANCA_GERENCIAR` — 403 real em provisionar client). (2026-09-13,
      achado durante a auditoria de `NavConfig.tsx`; fechado 2026-10-12)
- [x] ~~**Segurança: fluxo de criação de usuário + papel**~~ — **esclarecido
      em 2026-10-12, já estava feito**: `UsuarioFormPage` já tem
      `papelInicialId`/`empresaInicialId` (campos só na criação, fora de
      `UsuarioRequest`, mostrados em `DadosGeraisUsuarioTab`) — ao criar, o
      form chama `atribuirPapelEmpresa` numa chamada avulsa logo depois do
      `POST /usuarios`, antes de navegar pro usuário recém-criado (falha
      nessa chamada avulsa não impede a navegação — o papel pode ser
      atribuído depois pela aba "Papéis por Empresa"). A entrada original do
      backlog estava desatualizada. (originalmente 2026-09-02,
      `cactus-erp-web/CLAUDE.md` item 14)
- [x] ~~Vendas — telas do frontend pendentes~~ — **fechado em 2026-09-06**
      (Entrega 14, `cactus-erp-web/CLAUDE.md`): Vendedores/Perfis de Comissão,
      Condições de Pagamento, Tipos de Nota Fiscal (+Modelo), Orçamentos
      (form+detail com itens+máquina de estados+gerar venda), Vendas
      (form+detail com itens+frete+confirmar/cancelar+comissão), Comissões
      (consulta). Gap real fechado no caminho: `Transportadora` (backend desde
      a Fase 7) também nunca tinha ganhado tela — construída como
      pré-requisito. Achado de arquitetura corrigido antes de testar: item de
      venda copiado de um orçamento nasce sem `depositoId` e só um `PUT`
      dedicado consegue completá-lo — sem isso a venda ficaria travada sem
      conseguir confirmar; `AdicionarItemVendaModal` generalizada pra também
      editar. Validado ao vivo (Playwright, navegador real) fim a fim contra o
      backend real, com verificação cruzada via API (comissão de 5% batendo
      exatamente). **AFV** (`VendedorRota`/`VendedorMeta`) e
      `ComissaoObjetivo` ficam pra uma Fase B (backend já pronto, só falta
      tela — mesmo padrão de faseamento já usado em Estoque), registrados
      abaixo. (2026-09-03, root `CLAUDE.md`, Fase 7; fechado 2026-09-06)
- [x] ~~**Vendas — Fase B do frontend (AFV + Comissão por Objetivo)**~~ —
      **feito (Entrega 18, `cactus-erp-web/CLAUDE.md`, 2026-09-06)**: Rota
      (AFV)/Metas por cliente-período no `VendedorFormPage` (só em modo
      edição); `ComissaoPerfil` ganhou página própria
      (`/vendas/comissao-perfis`, gap real descoberto — só tinha modal
      rápido) com seção de Objetivos de Comissão por produto. Módulo Vendas
      **completo em todas as fases**.
- [x] ~~**Fiscal — telas do frontend pendentes**~~ — **feito (Entrega 15,
      `cactus-erp-web/CLAUDE.md`, 2026-09-06)**: NF-e completa (listagem,
      detalhe, botão "Gerar NF-e" na `VendaDetailPage`, ação "Emitir"), a
      partir de uma venda `CONFIRMADA`.
- [x] ~~**Ordem de Serviço — telas do frontend pendentes**~~ — **feito
      (Entrega 16, `cactus-erp-web/CLAUDE.md`, 2026-09-06)**: Status de OS/
      Tipos de Equipamento (catálogos com CRUD), abrir OS, adicionar
      equipamento/item(peça ou mão de obra)/evolução, aprovar orçamento,
      trocar status (catálogo livre, sem máquina de estados), encerrar.
      Desbloqueia NFS-e (item abaixo).
- [x] ~~**NFS-e — telas do frontend pendentes**~~ — **feito (Entrega 17,
      `cactus-erp-web/CLAUDE.md`, 2026-09-06)**: catálogo de Lista de Serviço
      Municipal (CRUD), botão "Gerar NFS-e" na `OsAberturaDetailPage`
      (desabilitado com tooltip até orçamento aprovado + item de serviço
      existir), listagem/detalhe com ação "Emitir". Validado ao vivo: só o
      item `SERVICO` entra na nota (peças ficam de fora), ISS calculado
      corretamente pela alíquota padrão da lista. Com este item, **todo o
      quarteto NF-e/CT-e/OS/NFS-e do Módulo Comercial está completo no
      frontend**.
- [x] ~~**CT-e — telas do frontend pendentes**~~ — **feito (Entrega 15,
      `cactus-erp-web/CLAUDE.md`, 2026-09-06)**: CT-e completo (listagem,
      detalhe, botão "Gerar CT-e" na `VendaDetailPage` — desabilitado com
      tooltip até a venda ter frete com transportadora definida —, ação
      "Emitir").
- [x] ~~**CRM — telas do frontend pendentes**~~ — **feito (Entrega 19,
      `cactus-erp-web/CLAUDE.md`, 2026-09-06)**: Leads (com
      qualificar/desqualificar/converter), Oportunidades (form + Kanban de
      funil), Atividades/Interações (seções compartilhadas), catálogo de
      Estágios do Funil. Kanban implementado **sem drag-and-drop** (decisão
      deliberada — botão "Mover" + modal, ver detalhe no CLAUDE.md) — item
      novo abaixo registra essa melhoria futura possível. Com este item, o
      **Módulo Comercial inteiro (Vendas/Fiscal/OS/CRM) está completo no
      frontend**.
- [x] ~~**CRM: drag-and-drop real no Kanban de Oportunidades**~~ — **feito em
      2026-09-13**: reverte a decisão original — instalado `@dnd-kit/core`,
      `OportunidadesKanbanPage` ganhou `DndContext`/`useDraggable`/
      `useDroppable`. Arrastar pra uma coluna `ABERTO`/`GANHO` move direto
      (`useMoverEstagioOportunidadeGenerico`, nova mutação com `id` como
      variável em vez de fixado na criação do hook); arrastar pra uma coluna
      `PERDIDO` abre `MoverEstagioModal` já pré-selecionado no estágio
      destino (`motivoPerda` é obrigatório, só o modal coleta esse dado — o
      drop nunca pula essa validação). Botão "Mover" mantido como fallback
      explícito. `PointerSensor` com `activationConstraint.distance: 8` pra
      não conflitar com o clique de navegar pro detalhe do card.
- [x] ~~**CRM: resolução de nome pra `vendedorId`/`responsavelId`/`usuarioId`**~~
      — **esclarecido em 2026-09-13, já estava feito**: `ResumoCells.tsx`
      já tinha `VendedorResumoCell` (resolução em 2 hops, vendas-service→
      cadastro-base-service) e `UsuarioResumoCell` (reaproveita `GET
      /usuarios/{id}`, já existente desde a Entrega 10 — nenhum endpoint
      novo foi necessário no `seguranca-service`), usadas em
      `AtividadesSection`/`InteracoesSection`; `LeadFormPage`/
      `OportunidadeFormPage` já resolvem `vendedorId` via `Select` com nome
      (`useVendedoresComNome`); `AdicionarAtividadeModal` já usa
      `AsyncSelect` pra `responsavelId`. A entrada original do backlog
      estava desatualizada. Único gap real encontrado: `VendedorResumoCell`
      não era usado em nenhuma listagem — adicionada coluna "Vendedor" em
      `LeadsListPage`/`OportunidadesListPage`.
- [x] ~~**Financeiro — telas do frontend, Fase A**~~ — **feito em 2026-09-07**
      (Entrega 20, `cactus-erp-web/CLAUDE.md`): Contas a Receber/Contas a
      Pagar (título avulso/parcelado via Segmented, baixa, estorno,
      cancelar, renegociar, atualizar classificação), catálogos (Natureza
      Financeira/Tipo de Documento) e Configuração Financeira (multa/juros
      por atraso). Faseamento confirmado com o usuário antes de codar
      (`AskUserQuestion`) — módulo grande demais (16 controllers) pra uma
      entrega só. **BUG REAL corrigido**: `getValueProps` de `DatePicker`
      aplicado por engano num `<Input type="date">` nativo (`dataBaixa`/
      `descontoAte`) deixava o campo silenciosamente vazio ao pré-preencher.
      Validado ao vivo fim a fim (avulso+parcelado, baixa parcial+total,
      estorno, cancelamento, renegociação, RBAC OPERADOR real — `LANCAR`
      sim/`GERENCIAR` não, 403 real). Zero erro de console.
- [x] ~~**Financeiro — telas do frontend, Fase B**~~ — **feito em 2026-09-07**
      (Entrega 21, `cactus-erp-web/CLAUDE.md`): Caixa e Bancos (CRUD completo
      de `ContaBancaria`), Cheques emitidos/recebidos (compensar/descontar/
      depositar/devolver), Tesouraria (transferência entre contas, aplicação/
      resgate, posição de caixa consolidada), Relatório de Fluxo de Caixa
      (projetado, dia/mês), Conciliação Bancária (upload de extrato OFX +
      conciliação automática/manual + gerar ajuste), EDI Bancário (gerar
      remessa CNAB 240 + baixar `.rem`, importar retorno CNAB 240 com
      resultado item a item). 10 telas + 12 modais novos, submenu
      "Financeiro" com 14 itens. Validado ao vivo em 3 sessões de navegador
      independentes — saldo de conta bancária conferido exato via API após
      uma cadeia real de 7 operações (R$1.045,00 batendo com o cálculo
      manual). Duas investigações de "bug" concluídas como problema do
      script de teste, não do produto (detalhado no `CLAUDE.md`). Com isso,
      **o módulo Financeiro está completo em todas as fases no frontend** —
      não resta módulo de negócio do backend sem tela no `cactus-erp-web`.
      Pendências pequenas registradas: desconciliar item já conciliado sem
      botão na tela (API pronta); Aplicações Financeiras sem filtro por
      conta bancária (as outras 3 telas do módulo têm). (root `CLAUDE.md`,
      seção `financeiro-service`)
- [x] ~~**Financeiro — Tesouraria** (transferência entre contas próprias +
      aplicação/resgate financeiro + posição de caixa consolidada)~~ —
      **feito em 2026-09-07**: `TransferenciaBancaria` (débito/crédito
      atômico entre 2 contas, sem estado "em trânsito" — simplificação
      deliberada, sem liquidação bancária real por trás), `AplicacaoFinanceira`
      (CDB/POUPANCA/FUNDO/TESOURO_DIRETO/OUTRO, rendimento pode ser negativo),
      `GET /tesouraria/posicao-caixa` (saldo consolidado por tipo de conta).
      Validado ao vivo fim a fim (transferência, resgate com ganho e com
      perda, posição de caixa batendo com soma manual, RBAC OPERADOR real).
      Reaproveita `CAIXA_CONSULTAR`/`CAIXA_MOVIMENTAR` já seedados, sem RBAC
      novo. (root `CLAUDE.md`, seção "financeiro-service — Tesouraria")
- [x] ~~**Financeiro — Conciliação Bancária** (import de extrato OFX +
      matching automático/manual contra `MovimentoCaixaBanco`)~~ — **feito em
      2026-09-07**: `OfxParser` (OFX 1.x/SGML, upload manual — decidido com o
      usuário, sem integração com API/Open Finance de banco parceiro que não
      temos), dedup por `fitId`, `conciliarAutomatico` (match por
      direção+valor exato+tolerância de 3 dias, só quando há exatamente 1
      candidato), `conciliarManual`/`desconciliar` (override do operador),
      `gerarAjuste` (cria `MovimentoCaixaBanco` real a partir de linha do
      extrato sem lançamento correspondente — tarifa/rendimento não
      lançado). Validado ao vivo fim a fim com arquivo OFX sintético — saldo
      da conta bateu exatamente com o `LEDGERBAL` do próprio arquivo de
      teste. Reaproveita `CAIXA_CONSULTAR`/`CAIXA_MOVIMENTAR`, sem RBAC novo.
      **Transparência registrada**: parser nunca testado contra arquivo real
      de nenhum banco brasileiro específico, só contra OFX sintético com o
      layout oficial da especificação. (root `CLAUDE.md`, seção
      "financeiro-service — Conciliação Bancária")
- [x] ~~**Financeiro — EDI Bancário (última sub-fase do backend do módulo
      financeiro)**~~ — feito e validado ao vivo em 2026-09-07: geração de
      remessa CNAB 240 (registro de boleto em carteira de cobrança) e leitura
      de retorno CNAB 240 (baixa automática por nosso número), só CNAB 240
      (CNAB 400 fora de escopo, decidido com o usuário). Fecha os 7 itens
      originais do módulo financeiro pedidos pelo usuário. **BUG REAL
      encontrado e corrigido**: toda baixa automática via retorno travava
      50s e falhava com "Lock wait timeout exceeded" — `importarRetorno`
      envolvia o loop inteiro numa `@Transactional`, e o `INSERT` de
      `retorno_bancario` (FK pra `conta_bancaria`) tomava um lock
      compartilhado na linha pai que colidia com o lock exclusivo que a
      baixa (transação `REQUIRES_NEW` aninhada) precisava na mesma linha —
      autotravamento que o MySQL não detecta como deadlock. Corrigido
      removendo o `@Transactional` do método externo. (root `CLAUDE.md`,
      seção "financeiro-service — EDI Bancário")
- [x] ~~**Loja Virtual: reserva de estoque real (dois carrinhos concorrentes
      disputando o mesmo estoque)**~~ — **fechado em 2026-09-12**:
      `VarianteProdutoLoja.estoqueReservado` (+ `estoqueDisponivel` derivado),
      lock pessimista (`buscarParaAtualizarEstoque`, mesmo critério de
      `ContaBancariaRepository#buscarParaAtualizarSaldo`), `CarrinhoService`
      reserva/ajusta/libera de verdade em adicionar/atualizar/remover item,
      `PedidoService#confirmarPagamento` debita físico e libera reserva na
      mesma chamada, `cancelar` decide pela situação anterior (libera reserva
      se `AGUARDANDO_PAGAMENTO`, devolve estoque físico se já `PAGO`/
      `EM_SEPARACAO`). Novo `POST /carrinhos/{token}/abandonar` (sinal
      explícito) + `CarrinhoExpiracaoService` (`@Scheduled`, libera carrinho
      `ABERTO` inativo há mais de 30min — rede de segurança pra aba fechada
      sem aviso). Validado ao vivo contra o banco real: 2 carrinhos
      concorrentes (1º reserva 3, 2º rejeitado em 7/aceito em 6 — exatamente
      o disponível), ajuste de quantidade, remoção, abandono idempotente,
      pagamento debitando físico+liberando reserva, cancelamento pós-pagamento
      devolvendo estoque, cancelamento pré-pagamento só liberando reserva,
      guard contra reduzir estoque manualmente abaixo do reservado. O job de
      expiração liberou de verdade um carrinho leftover de sessão anterior —
      confirma a rede de segurança contra dado real, não só cenário fabricado.
      (2026-09-04, root `CLAUDE.md`, Fase 13; fechado 2026-09-12, seção
      "Fase 13, sub-fase 4")
- [ ] **Loja Virtual — próximas sub-fases (Fase 13, seguintes à sub-fase 4)**:
      `loja-virtual-service` tem hoje catálogo (Loja/Categoria/Atributo/
      Produto/Variante) + carrinho/checkout/pedido (público via token de
      carrinho, pedido atrás de RBAC admin-only, reserva de estoque real) +
      emissão de NFC-e (Simples Nacional, via `fiscal-service`). Faltam:
      integração com gateway de pagamento real (nenhum avaliado ainda —
      Mercado Pago/PagSeguro/Stripe/Cielo são candidatos — hoje
      `confirmar-pagamento` é ação manual/webhook-ready sem gateway plugado),
      cálculo de frete automático, fluxo de devolução/reembolso, seção de
      pagamento (`pag001`) no INI da NFC-e (`Pedido.formaPagamento` ainda não
      mapeia pro código `tPag` da SEFAZ), regime tributário normal na NFC-e
      (só Simples Nacional/CSOSN hoje), notificação de mudança de situação do
      pedido/expiração de carrinho (e-mail/SMS), portal de acompanhamento pro
      cliente final, admin self-service por lojista (login do próprio lojista
      na própria conta — admin da PLATAFORMA, staff Cactus, já tem frontend
      completo desde 2026-10-11/12, ver `cactus-erp-web/CLAUDE.md` Entrega 22:
      Lojas/Categorias/Atributos/Produtos/Variantes/Pedidos), modo canal
      (sincronização de catálogo com um `cadastro-base-service` de cliente que
      já tem o Cactus ERP completo). (2026-09-04, root `CLAUDE.md`, Fase 13;
      atualizado 2026-10-12)

### Cosméticos / baixa prioridade

- [ ] Favicon da aba do navegador ainda usa a marca em SVG, não a foto real
      usada na sidebar — inconsistência visual conhecida. (2026-09-01,
      `cactus-erp-web/CLAUDE.md` item 3)
- [ ] Dark mode completo — adiado, não esquecido. (`cactus-erp-web/CLAUDE.md` item 4)
- [ ] Code-splitting por rota — bundle de produção já passa de 1.8MB
      (majoritariamente antd); revisitar quando os módulos de negócio
      adicionarem peso real. (`cactus-erp-web/CLAUDE.md` item 6)
- [ ] Geração de client tipado via OpenAPI (`openapi-typescript`/`orval`) —
      ideia futura, nunca gerada; já existe API de negócio real (Clientes,
      Produtos, etc.) pra gerar contra. (`cactus-erp-web/CLAUDE.md` item 5)

## Backend (cactus-erp-sistema)

- [ ] **Fase 6-B — Fechar os 7 gaps de push PDV→ERP (Sangria/Suprimento/
      Fiado/Caixa/Conferência/Venda/Devolução), agora que vendas-service e
      financeiro-service existem** — reavaliação completa feita em
      2026-09-27 (pedido do usuário: "entender e deixar no backlog como
      vamos realizar a sincronização"), verificada lendo o código real
      (entidades/campos/enums), não por suposição — resultado detalhado em
      `FASE6_SINCRONIZACAO_PDV.md`, seção "2.3-bis Reavaliação (2026-09-27)".
      Resumo, em ordem de esforço crescente:
      1. **Sangria/Suprimento** (fácil) → `financeiro-service.MovimentoCaixaBanco`
         com `ContaBancaria.tipo=CAIXA_INTERNO` (já existe, feito sem o PDV em
         mente mas serve) — falta só adicionar valores novos no enum fechado
         `OrigemMovimentoCaixa` e decidir a granularidade da conta (ver item 4).
      2. **Fiado** (fácil) → `financeiro-service.ContaReceber`/`BaixaContaReceber`
         — `clienteId` já é referência solta, encaixe natural (tipo C=crédito
         gera título novo, tipo P=pagamento gera baixa) — falta só um valor
         novo no `origemTipo` da `ContaReceber`.
      3. **Conferência de Caixa** (médio, decisão pendente) — não vira
         entidade nova em `financeiro-service`; proposta é só gerar um
         `AJUSTE` (enum já existe) quando o valor contado divergir do
         esperado, mantendo o resto só na auditoria do
         `sincronizacao-service` — precisa confirmar com o usuário antes de
         codar.
      4. **Abertura/fechamento de caixa (`PDV_MOVIMENTO`)** (médio, decisão
         pendente) — tecnicamente cabe na mesma base de Caixa/Bancos, mas
         precisa decidir granularidade: 1 `ContaBancaria` por loja (mistura
         saldo de terminais diferentes) ou 1 por terminal físico (mais
         correto, mais registros pra manter).
      5. **Venda do PDV (`PDV_VENDA_CABECALHO`/`_DETALHE`)** (difícil, achado
         real) — `vendas-service.VendaCabecalho` foi desenhado pra venda
         B2B/atacado, não pra balcão: `vendedor` é FK obrigatória exigindo
         um `Vendedor` cadastrado por operador de caixa (com `colaboradorId`
         único), `condicoesPagamento` obrigatória não faz sentido pra "pago
         à vista", `notaFiscalTipo` obrigatória precisa resolver pra NFC-e.
         Não é bloqueio definitivo, mas exige ou sintetizar registros
         "genéricos de balcão" no import ou relaxar essas 3 colunas pra
         nullable (mudança de schema não trivial).
      6. **Total por tipo de pagamento (`PDV_TOTAL_TIPO_PAGAMENTO`)** (difícil,
         achado novo) — depende do item 5 primeiro, e mesmo assim
         `VendaCabecalho.formaPagamentoId` só aceita 1 forma de pagamento
         por venda (`Long` único, não coleção) — o PDV já suporta pagamento
         fracionado (várias formas na mesma venda), então precisaria de uma
         entidade nova (`VendaTotalPagamento` ou similar) que não existe
         hoje.
      7. **Devolução (`PDV_DEVOLUCAO`)** (sem destino ainda) — `VendaService
         #cancelar` só permite cancelar em `RASCUNHO`; devolução pós-venda
         confirmada continua "fora de escopo" desde a Fase 7, sem nenhum
         destino no ERP.
      **Atualização (2026-10-24): itens 1, 2, 3 e 4 feitos**, validados ao
      vivo. Sangria, suprimento, abertura de caixa, conferência de caixa e
      fiado chegam ao `financeiro-service` (ver `FASE6_SINCRONIZACAO_PDV.md`,
      seção "12. Fase 6-B"). Decisões confirmadas com o usuário: uma conta
      `CAIXA_INTERNO` por terminal e conferência vira ajuste da diferença.
      **Itens 5, 6 e 7 também feitos (2026-10-24)** — `VendaPdv` própria,
      pagamento fracionado e devolução (seção 13 do FASE6). Os 7 gaps estão
      fechados. Pendências novas que sobraram:
      - [ ] Recebíveis de cartão/PIX das vendas do PDV: hoje só registrados na
            venda. Exige cadastro de adquirente, taxa e prazo de repasse.
      - [ ] Relatórios de vendas e BI ainda só leem `VendaCabecalho` (B2B):
            somar `VendaPdv`.
      - [ ] Cancelamento de venda do PDV já sincronizada: precisa de um tipo de
            registro próprio (reenviar o mesmo `idOrigem` é descartado pela
            deduplicação).
      Pendências pequenas que sobraram:
      - [x] ~~Motivo de `ERRO` vindo da DLQ é genérico~~ — feito (2026-10-24): o `DlqListener` lê o header
            `x-exception-message`. Texto original:
            o motivo real (ex: pagamento de fiado maior que a dívida) fica só no
            log do financeiro. Ler o header `x-exception-message` que o
            `RepublishMessageRecoverer` já grava e gravar no `motivoErro`.
      - [x] ~~Erro de negócio determinístico passa pelas 5 tentativas~~ — feito (2026-10-24), estoque e
            financeiro. Texto original: Erro de negócio determinístico (como o pagamento maior que a
            dívida) passa pelas 5 tentativas antes de ir pra DLQ, sem ganho
            nenhum. Não retentar `BusinessException`.
      **Próximo passo sugerido (original)**: atacar os itens 1-2 primeiro (baixo
      esforço, alto valor — fecham 3 dos 7 gaps originais), depois voltar
      pro usuário com as decisões de modelagem pendentes (itens 3-4) antes
      de encarar os dois mais difíceis (5-7). (2026-09-27, `FASE6_SINCRONIZACAO_PDV.md`)

- [x] **Fase 15 — SPED Fiscal (EFD ICMS/IPI) + EFD Contribuições (extensão do
      `fiscal-service`)** — feita por completo e validada ao vivo em
      2026-09-20. Blocos 0/C/D/E (EFD ICMS/IPI) e 0/A/C/M (EFD Contribuições),
      sem entidade de domínio nova, só camada de layout
      (`SpedFiscalIcmsIpiExportService`/`SpedContribuicoesExportService` +
      `SpedFiscalController`, `GET /api/v1/sped/efd-icms-ipi`/`efd-contribuicoes`,
      `FISCAL_CONSULTAR`). 2 bugs reais autocorrigidos antes de compilar
      (C190/D190 com ordem de campo trocada via `List<String>` frágil — fix:
      records tipados `LinhaAnaliticaIcms`/`LinhaApuracaoContribuicao`; Bloco M
      com `M600` de abertura duplicado e `M990` por seção em vez de único pro
      bloco inteiro). Ver detalhamento completo no root `CLAUDE.md`, ROADMAP
      REVISADO, entrada da Fase 15. **Gap identificado, não fechado, registrado
      como próximo item**: Blocos G/H/K (ICMS/IPI) e D/F/P (Contribuições) fora
      de escopo por falta de fonte de dado; `MOTIVO_DESONERACAO_ICMS`/
      `CODIGO_BENEFICIO_FISCAL` (presentes no `view_sped_nfe_detalhe` do fenix)
      não capturados em `NotaFiscalEletronicaItem` — auditoria de campo contra
      o layout oficial completo da EFD ainda não feita, só o recorte prático
      usado nesta fase.
- [x] ~~**Fase 16 — MDF-e (extensão do `fiscal-service`)**~~ — **backend feito
      (2026-10-24)**, ver root `CLAUDE.md`. Pendências que sobraram:
      - [x] ~~Encerramento e cancelamento~~ — feito (2026-10-24), ver root
            `CLAUDE.md`. Sobrou: encerramento automático (job) de MDF-e
            esquecido aberto, e um alerta de "MDF-e autorizado há mais de X
            dias sem encerrar".
      - [ ] Viagem interestadual (percurso por UF), reboque, múltiplos
            condutores, seguro de carga, `tpRod`/`tpCar` configuráveis (hoje
            fixos 06/02).
      - [ ] Emissão real via ACBr nunca exercitada (mesmo bloqueio de
            certificado das Fases 8/9/11); layout `MDFe.INI` não conferido
            contra um INI de referência nem contra ACBr real.
      - [x] ~~Frontend (`cactus-erp-web`): telas de MDF-e~~ — feito (Entrega
            24, 2026-10-24).
      - [x] ~~Endpoint de "CT-e disponíveis para manifesto"~~ — feito
            (2026-10-24): `GET /mdfes/ctes-disponiveis?empresaId=`, o seletor
            deixou de filtrar no cliente.
- [ ] 🔴 **CRÍTICO — Reforma Tributária (IBS/CBS/Imposto Seletivo) — motor
      tributário desatualizado, já é exigência legal VENCIDA, não mais risco
      futuro** — ver resumo no topo deste arquivo, seção "🔴 CRÍTICO". —
      achado crítico pesquisado em 2026-09-13: Nota Técnica 2025.002 tornou os
      campos de IBS/CBS obrigatórios em produção na NF-e/NFC-e desde
      03/ago/2026 pro Regime Normal (Simples Nacional/MEI entra em
      04/jan/2027). Modelo bem mais rico que o `IcmsPorUf`/`Pis`/`Cofins`/`Ipi`
      atuais — novo Grupo `UB` no XML (`CST` próprio do IBS/CBS, `cClassTrib`,
      subgrupos `IBSUF`/`IBSMun`/`CBS`, tributação monofásica, ZFM/ALC,
      transferência de crédito). **Decisão confirmada com o usuário**: não
      entra na leva de Contábil/SPED/MDF-e (Fases 14-16) — fica registrada
      aqui como fase própria futura, exigindo uma rodada de pesquisa dedicada
      e mais profunda (campo-a-campo de cada subgrupo) antes de qualquer
      código, pra não modelar tributo real "no chute". (2026-09-13, root
      `CLAUDE.md`, ANÁLISE: MÓDULO CONTÁBIL E FISCAL)
- [ ] **Contabilização automática (motor de regras por evento de negócio —
      Venda/Compra/Baixa financeira → Lançamento Contábil)** — sub-fase 2 do
      `contabilidade-service` (Fase 14), deliberadamente fora do MVP. Padrão
      SAP FI/TOTVS Protheus: uma tabela de regra de contabilização (por
      natureza/tipo de evento → conta+centro de custo débito/crédito) que
      geraria `LancamentoContabil` automaticamente a partir de hooks em
      `vendas-service`/`gestao-compras-service`/`financeiro-service` —
      `OrigemLancamentoContabil.VENDA/COMPRA/FINANCEIRO` já existem no enum
      (sem migration necessária quando essa fase começar), só nunca usadas
      ainda (todo lançamento hoje nasce `MANUAL`). (2026-09-13, root
      `CLAUDE.md`, ANÁLISE: MÓDULO CONTÁBIL E FISCAL)
- [x] ~~`mvn compile -am` falha no reactor inteiro~~ — reverificado em 2026-09-03
      (`mvn -q compile` na raiz do monorepo, 9 módulos incluindo o novo
      `vendas-service`): reactor inteiro compila limpo, exit 0. Já não
      reproduz — o BOM do `spring-retry` deve ter sido resolvido em alguma
      passada anterior sem nota explícita, ou o achado original era pontual
      a uma combinação de módulos específica. (originalmente 2026-08-31, root
      `CLAUDE.md`, seção CORS)
- [x] ~~`PedidoCompraService.adicionarItem` (gestao-compras-service) — mesmo bug
      de "id null na resposta do POST" já corrigido em `vendas-service`~~ —
      confirmado e corrigido em 2026-09-03: `PedidoCompraService.adicionarItem`,
      `CotacaoCompraService.adicionarItem` e `RecebimentoCompraService.processarItem`
      tinham o mesmo padrão (`pai.getItens().add(item)` sem `itemRepository.save(item)`
      explícito — `GenerationType.IDENTITY` só atribui o id no `INSERT` de
      verdade, não no `add()` a uma coleção de um pai já gerenciado). Corrigido
      nos 3 lugares (criados `FornecedorCotacaoItemRepository`/
      `RecebimentoCompraItemRepository`, que não existiam), validado ao vivo
      nos 3 endpoints (`POST /pedidos/{id}/itens`, `POST /cotacoes/{id}/fornecedores/{id}/itens`,
      `POST /recebimentos`) — todos voltam com `id` real agora.
      `RequisicaoCompraService.criar` foi checado e **não** tem o bug (pai novo
      + save único do agregado, não pai-já-gerenciado + add posterior — mesmo
      padrão que `VendaService.gerarAPartirDeOrcamento` no vendas-service, que
      também está correto). (2026-09-03, root `CLAUDE.md`, Fase 7)
- [x] ~~Financeiro / Contas a Receber — módulo inexistente~~ — **fechado em
      2026-09-05**: novo `financeiro-service` (Contas a Receber/Contas a
      Pagar + Baixa, ledger append-only), com integração REAL —
      `VendaService#confirmar` (vendas-service) gera 1 `ContaReceber` por
      parcela via `FinanceiroClient`, idempotente por `vendaId`. Validado ao
      vivo fim a fim (venda de R$100 em 3 parcelas → contas geradas com
      vencimentos e valores corretos → baixa parcial/total → cancelamento →
      RBAC). `ContaPagar` só `MANUAL` nesta fase — geração automática a
      partir do `gestao-compras-service` **continua em aberto**, registrada
      abaixo. (2026-09-03, root `CLAUDE.md`, ANÁLISE: MÓDULO COMERCIAL;
      fechado 2026-09-05, seção `financeiro-service`)
- [x] ~~`financeiro-service`: geração automática de `ContaPagar` a partir de
      compras confirmadas no `gestao-compras-service`~~ — **fechado em
      2026-09-05**: `PedidoCompraService.atualizarSituacaoAposRecebimento`
      agora chama `POST /contas-pagar/gerar-de-compra` (novo
      `FinanceiroClient`, mesmo padrão de `EstoqueClient`) na primeira vez que
      o pedido vira `RECEBIDO_TOTAL` — 1 `ContaPagar` por parcela, usando
      `quantidadeParcelas`/`diaPrimeiroVencimento`/`intervaloEntreParcelas`/
      `diaFixoParcela` do próprio pedido quando informados, ou 1 parcela única
      com vencimento em `dataPrevisaoPagamento` (ou `dataPedido+30d`) quando
      não. Idempotente por `pedidoCompraId` (409 numa segunda chamada).
      **2 bugs reais encontrados e corrigidos na validação ao vivo**: (1) o
      mesmo bug de builder `RestClient.Builder` singleton compartilhado já
      corrigido no `vendas-service` ao integrar com o `financeiro-service`
      (`FinanceiroClient`, ver `CLAUDE.md`, seção `financeiro-service`) existia
      também em `gestao-compras-service.EstoqueClientConfig` — só não tinha se
      manifestado ainda por só ter 1 destino até agora; corrigido ao adicionar
      o segundo destino (`financeiro-service`), builder fresco por bean; (2)
      `ContaPagarService.gerarDeCompra` montava `numeroDocumento` como
      "Pedido de compra #14 - Parcela 1" (33 caracteres) contra uma coluna
      `VARCHAR(30)` — `DataIntegrityViolationException` real (`Data too long`)
      na primeira tentativa ao vivo. Corrigido encurtando pro mesmo formato
      compacto que `ContaReceberService` já usa pro lado de venda
      ("Compra #14 - Parcela 1"). Validado ao vivo fim a fim (2 cenários):
      pedido de R$50 em 2 parcelas (dia 15 + intervalo 30) → recebido total →
      2 `ContaPagar` de R$25 geradas com vencimento 20/09 e 20/10 corretos →
      segunda chamada pro mesmo pedido rejeitada (409); pedido de R$12 sem
      parcelamento com `dataPrevisaoPagamento=25/09` → recebido total → 1
      `ContaPagar` de R$12 com vencimento 25/09 — confirma o fallback de
      parcela única. Transação confirmada atômica: a 1ª tentativa (antes do
      fix do `numeroDocumento`) falhou dentro do `financeiro-service`, e o
      `RecebimentoCompra`/mudança de situação do pedido no
      `gestao-compras-service` fizeram rollback completo (pedido continuou
      `ENVIADO`, sem recebimento nenhum persistido) — sem estado inconsistente
      entre os dois serviços. (2026-09-05, root `CLAUDE.md`, seção
      `financeiro-service`; fechado 2026-09-05)
- [x] ~~`financeiro-service`: estorno de baixa (cancelamento de título com
      baixa já registrada é bloqueado, exigiria esse fluxo)~~ — **fechado em
      2026-09-05**: `BaixaContaReceber`/`BaixaContaPagar` ganharam
      `estornado`/`dataEstorno`/`motivoEstorno` (`V2__estorno_baixa.sql`,
      linha original nunca apagada/alterada — mesma imutabilidade de
      `EstoqueMovimento`/`DocumentoVersao`); `POST .../baixas/{baixaId}/estornar`
      (`FINANCEIRO_LANCAR`) recalcula `valorPago`/`valorSaldo`/`situacao` a
      partir das baixas NÃO estornadas; `cancelar` mudou de "rejeita se tem
      baixa" pra "rejeita se tem baixa ATIVA" — com tudo estornado, o
      cancelamento passa a ser aceito de verdade. Validado ao vivo nos 2 lados
      (`ContaReceber`/`ContaPagar`): título com 2 baixas quitando → cancelar
      rejeitado (409) → estornar as 2 → recomputado pra `ABERTA`/saldo cheio →
      cancelar aceito → segundo estorno da mesma baixa rejeitado (409,
      idempotência) → RBAC OPERADOR real (200 registrar/estornar via
      `FINANCEIRO_LANCAR`, 403 real em cancelar via `FINANCEIRO_GERENCIAR`).
      (2026-09-05, root `CLAUDE.md`, seção `financeiro-service`; fechado 2026-09-05)
- [x] ~~`financeiro-service`: juros/multa por atraso calculados automaticamente
      (hoje só `vencido` é sinalizado, sem cobrança de juros)~~ — **fechado em
      2026-09-05**: `ConfiguracaoFinanceiroEmpresa` (get-or-default por
      empresa, `GET/PUT /configuracao-financeiro/empresa/{id}`) +
      `diasAtraso`/`valorMulta`/`valorJuros`/`valorAtualizado` calculados a
      cada leitura em `ContaReceberResponse`/`ContaPagarResponse` (nunca
      persistidos, mesmo espírito de `vencido`). Simplificação deliberada:
      `registrarBaixa` continua validando contra `valorSaldo` (o principal),
      não `valorAtualizado` — cobrar a penalidade de verdade fica pra quando
      um fluxo de renegociação existir; hoje é só informativo. Validado ao
      vivo nos 2 lados: título vencido há 10 dias sem config (zerado) →
      configurada 2%+0,10%/dia → recalculado corretamente (R$20 multa + R$10
      juros sobre R$1.000) → título futuro com tudo zerado → baixa pelo saldo
      original aceita → RBAC OPERADOR real (200 consulta, 403 real
      gerenciar). (2026-09-05, root `CLAUDE.md`, seção `financeiro-service`;
      fechado 2026-09-05)
- [x] ~~`financeiro-service`: relatório de fluxo de caixa~~ — **fechado em
      2026-09-05**: `GET /api/v1/relatorios/fluxo-caixa` — soma `valorSaldo`
      de títulos `ABERTA`/`PARCIAL` (fluxo projetado, não realizado) por
      dia/mês, com `saldoAcumulado` corrido. Sem snapshot persistido,
      calculado sob demanda (mesmo critério de `CurvaAbcService` no
      controle-estoque-service). Validado ao vivo: agrupamento `MES` com dado
      de teste real batendo exatamente com o cálculo manual (3 períodos,
      saldo acumulado conferido período a período); agrupamento `DIA`
      confirmado numa janela mais estreita; RBAC OPERADOR real (200,
      `FINANCEIRO_CONSULTAR`). (2026-09-05, root `CLAUDE.md`, seção
      `financeiro-service`; fechado 2026-09-05)
- [x] ~~**`financeiro-service`: conciliação bancária**~~ — superado, feito de
      fato em 2026-09-07 via upload manual de arquivo OFX (decisão do
      usuário) em vez de integração com API/Open Finance de banco parceiro —
      ver item já fechado na seção Frontend acima ("Financeiro — Conciliação
      Bancária"). Com **EDI Bancário** também fechado na mesma data, não
      resta nenhum item de backend nesta lista — só o frontend do módulo
      financeiro inteiro (ver item "Financeiro — telas do frontend
      pendentes" acima). (2026-09-05, root `CLAUDE.md`, seção
      `financeiro-service`; superado 2026-09-07)
- [ ] **`fiscal-service`: emissão real via ACBr Monitor nunca foi testada
      (NF-e, NFS-e, CT-e e agora NFC-e)** — `POST /notas-fiscais/{id}/emitir`,
      `POST /notas-servico/{id}/emitir`, `POST /ctes/{id}/emitir` e
      `POST /notas-consumidor/{id}/emitir` compilam e estão com o protocolo
      TCP portado de um cliente já validado (`cactus-pdv.AcbrMonitorClient`),
      mas nenhum comando (`NFE.CriarEnviarNFe`/`NFSe.AdicionarRPS`+
      `NFSe.EnviarLoteRPS`/`CTE.CriarEnviarCTe`) foi disparado de verdade contra o
      `ACBrMonitor.exe` local em nenhuma sessão — decisão do usuário, porque
      o `ACBrMonitor.ini` deste ambiente tem um certificado que parece ser de
      produção (`Ambiente=1`). Reconfirmado em 2026-09-04 (Fases 11 e 13):
      usuário ainda não tem o certificado de homologação/teste. **NFC-e é a
      exceção de confiança** — `NfceIniBuilder` é portado do layout NFCe.INI
      que o próprio `cactus-pdv` já testou manualmente contra um
      ACBrMonitorPLUS real (2026-08-22), então quando o certificado existir
      é o candidato mais seguro pra validar primeiro. Antes de testar de
      verdade: confirmar com o usuário se o ambiente pode ser trocado pra
      Homologação (`Ambiente=2`) com segurança, ou usar um certificado de
      teste à parte. (2026-09-03, root `CLAUDE.md`, Fase 8; reconfirmado
      2026-09-04, Fases 11 e 13)
- [x] ~~**`fiscal-service`/`cadastro-base-service`: numeração de NF-e pode
      "pular" em caso de falha após reservar**~~ — **feito em 2026-09-13**:
      terceira aplicação do `common.saga.SagaOrchestrator`. Compensação
      DELIBERADAMENTE diferente das duas aplicações anteriores — não tenta
      reverter/decrementar o número reservado (risco de colisão com reserva
      concorrente + a legislação já exige inutilização formal, não reuso
      silencioso); em vez disso registra o gap como consultável, novo
      `NumeroFiscalPulado` (ledger append-only) no cadastro-base-service.
      Validado ao vivo: caminho feliz (saga `CONCLUIDA`, número reservado e
      usado) e caminho de falha+compensação (produto inexistente → saga
      `COMPENSADA` → linha real criada em `NumeroFiscalPulado` → contador
      permanece incrementado, número intencionalmente pulado, nenhuma NF-e
      persistida). Mesmo gap existe em CT-e/NFC-e, não aplicado nesta
      rodada (fora do escopo original do item). Com isso, os 3 gaps
      originais de "chamada síncrona sem saga" (vendas/compras/fiscal) estão
      todos fechados. (2026-09-03, root `CLAUDE.md`, Fase 8; candidato de
      saga registrado 2026-09-12; fechado 2026-09-13, seção "Padrão Saga
      orquestrada — terceira aplicação")
- [x] ~~**Padrão Saga orquestrada — mecanismo genérico + piloto**~~ — **feito
      em 2026-09-12**: `common.saga` (`SagaStep`/`SagaAuditor`/
      `SagaOrchestrator`, orquestração — não coreografia, sem
      `saga-orchestrator-service` novo) aplicado como piloto em
      `vendas-service.VendaService#confirmar` (débito de estoque por item +
      geração de contas a receber, com compensação real via
      `EstoqueClient#registrarEntrada`/`FinanceiroClient#estornarContasDeVenda`
      em caso de falha no meio). Trilha de auditoria `SagaExecucao`/
      `SagaExecucaoPasso` consultável em `GET /api/v1/sagas`. **BUG REAL
      corrigido**: a auditoria inicialmente vivia na mesma transação de
      `confirmar` — quando a saga falhava e a exceção original era relançada,
      o rollback do Spring apagava também os registros de auditoria (exatamente
      o caso que mais precisa deles); corrigido com `SagaAuditoriaService`
      (`REQUIRES_NEW` por método). Validado ao vivo: caminho feliz (saga
      `CONCLUIDA`) e caminho de falha+compensação (saga `COMPENSADA`, estoque
      debitado e depois revertido líquido zero, venda permaneceu `RASCUNHO`).
      **Próximos candidatos, não aplicados nesta rodada**: numeração de NF-e
      (item acima) e `gestao-compras-service.RecebimentoCompraService`
      (mesma classe de gap — débito de estoque por item sem compensação).
      (2026-09-12, root `CLAUDE.md`, seção "Padrão Saga orquestrada")
- [x] ~~**`gestao-compras-service.RecebimentoCompraService`: aplicar
      `common.saga.SagaOrchestrator`**~~ — **feito em 2026-09-13**: mesmo
      mecanismo do `common.saga`, segundo consumidor real (confirma que o
      desenho generaliza sem ajuste). N passos "dar-entrada-estoque-item-X"
      + 1 passo condicional "gerar-contas-a-pagar" (só quando o recebimento
      completa o pedido), com compensação real (`EstoqueClient#registrarSaida`/
      `FinanceiroClient#estornarContasDeCompra`, este espelhando
      `ContaReceberService#estornarPorVenda` do lado financeiro). Exigiu
      dividir `PedidoCompraService.atualizarSituacaoAposRecebimento` (mutação
      local + chamada cross-serviço acopladas) em duas responsabilidades
      separadas. Nenhum bug novo (diferente da 1ª aplicação, que achou o bug
      do `REQUIRES_NEW`) — replicado 1:1 do `vendas-service`. Validado ao vivo:
      caminho feliz (saga `CONCLUIDA`, pedido completa, `ContaPagar` gerada) e
      caminho de falha+compensação (409 determinístico forçado, saga
      `COMPENSADA`, estoque líquido zero, pedido permaneceu `ENVIADO` sem
      corromper nem a mutação local nem a remota). (2026-09-12, root
      `CLAUDE.md`, seção "Padrão Saga orquestrada"; fechado 2026-09-13, seção
      "segunda aplicação em gestao-compras-service")
- [x] **`fiscal-service`: sequência de comando ACBr pra NFS-e confirmada por
      completo** — `NotaFiscalServicoService#emitir` agora chama os 2
      comandos do fluxo (`NFSe.AdicionarRPS` adiciona o RPS ao lote,
      `NFSe.EnviarLoteRPS("cNumLote",2)` transmite em modo Lote Síncrono e
      devolve o resultado da autorização na mesma chamada) — o segundo
      comando, que antes não tinha assinatura confirmada, foi confirmado via
      pesquisa direta em `acbr.sourceforge.io/ACBrMonitor/NFSeEnviarLoteRPS.html`
      (2026-09-05). `emitir` agora marca `AUTORIZADA`/`REJEITADA` a partir do
      retorno real do segundo comando, não mais `ENVIADA` sem aguardar
      autorização. **O que continua incerto, registrado à parte**: o
      detalhamento campo-a-campo do `NFSe.INI` (seções `[IdentificacaoRps]`/
      `[Prestador]`/`[Tomador]`/serviço) ainda é inferência a partir do padrão
      de `NfeIniBuilder`/`cactus-pdv`, não uma transcrição de um `.ini` de
      referência lido na íntegra — ver Javadoc de `NfseIniBuilder`. Nunca
      chamado ao vivo contra o `ACBrMonitor.exe` real (mesma decisão de
      segurança da Fase 8, e a seção `[NFSe]` do `.ini` local nem está
      configurada) — esse bloqueio permanece até o certificado de
      homologação existir. (2026-09-03, root `CLAUDE.md`, Fase 9; comando
      confirmado e implementado 2026-09-05)
- [ ] **`fiscal-service`: layout campo-a-campo do `CTe.INI` não confirmado
      contra instância real** — diferente do comando em si (`CTE.CriarEnviarCTe`,
      confirmado por pesquisa direta na doc do ACBr, alta confiança, mesma
      família do `NFE.CriarEnviarNFe`), o detalhamento de cada seção do INI
      (nomes exatos de subseção pra `toma`/`ICMS` conforme o regime) foi
      levantado por pesquisa geral, sem confirmação real — ver Javadoc de
      `CteIniBuilder`. Simples Nacional (`ICMSSN`) não implementado, só regime
      normal. Resolver junto do item de emissão real acima quando o
      certificado existir. (2026-09-04, root `CLAUDE.md`, Fase 11)
- [ ] **`fiscal-service`: avaliar trocar NFe/CT-e de ACBr pra biblioteca Java
      nativa `swconsultoria` (`java-nfe`/`java-cte`)** — levantamento completo
      já feito e registrado em `ANALISE_SWCONSULTORIA_VS_ACBR.md` (raiz do
      repo), com fontes reais (GitHub/Maven Central), não decidido/implementado
      ainda — usuário pediu pra deixar em backlog pra avaliar depois. Resumo:
      `java-nfe` (NFe/NFCe/CCe) e `java-cte` (CT-e) são bibliotecas Java puras
      (falam direto com a SEFAZ via SOAP, sem precisar do `ACBrMonitor.exe`
      externo), maduras e ativamente mantidas (CT-e atualizado em mar/2026,
      já cobrem IBS/CBS da Reforma Tributária — gap que hoje é risco aberto no
      motor tributário) — troca de NFe/CT-e no `fiscal-service` seria **sem
      custo de regressão** (nenhum dos dois nunca emitiu de verdade, bloqueio
      de certificado). `java-mdfe` está abandonado desde 2019, não usar. Não
      existe equivalente da família pra NFS-e/SAT — continuam só ACBr.
      **Recomendação registrada no documento**: não mexer no `cactus-pdv`
      (NFCe via ACBr lá já é produção validada) — só avaliar pro
      `fiscal-service`. 3 perguntas em aberto no documento (seção 8): migração
      futura do `cactus-pdv`, tratamento de MDF-e, e se a NFC-e do
      `fiscal-service`/`loja-virtual-service` também migra ou fica no builder
      ACBr atual. (2026-09-06, `ANALISE_SWCONSULTORIA_VS_ACBR.md`)
- [ ] **Fase 17 — Módulo de Relatórios: rollout pros demais serviços +
      integração com o frontend** —
      fundação (`common.report`, JasperReports 6.21.4 + `JRMapCollectionDataSource`),
      piloto (`controle-estoque-service`, export PDF/Excel da Curva ABC) e
      `vendas-service` (vendas por período/vendedor/cliente, produtos mais
      vendidos, comissões por vendedor) já feitos e validados ao vivo (ver
      ANÁLISE: MÓDULO DE RELATÓRIOS no root `CLAUDE.md`). **`financeiro-service`
      feito no backend em 2026-10-01** (export do Fluxo de Caixa + relatório
      novo de Títulos em Aberto a Receber/Pagar, validado ao vivo e conferido
      visualmente). **Integração com o frontend feita (2026-10-01)** para os
      8 relatórios existentes (Curva ABC, Fluxo de Caixa, Títulos em Aberto, 3
      de Vendas), com telas novas `/financeiro/titulos-em-aberto` e
      `/vendas/relatorios`, validadas em navegador real. Os 6 templates foram
      renderizados e conferidos visualmente; corrigidos PDF vazio sem página,
      "TOTAL null", filtro cortado no cabeçalho e números colados na borda (ver
      root `CLAUDE.md`). **`contabilidade-service` feito no backend em
      2026-10-10** (export de Balancete em paisagem + Razão em retrato,
      validado ao vivo e conferido visualmente nos 2 templates — sem bug
      encontrado nesta rodada, já nascendo com todas as convenções fixadas
      nas fases anteriores). Achado técnico generalizado em
      `common.report.ReportEngine#paraValorDeTemplate`: conversão automática
      de qualquer campo `Enum` pra `.name()` String, reaproveitável por todo
      relatório futuro com campo enum (`BalanceteItemResponse.tipo`/
      `natureza`, `RazaoItemResponse.tipoPartida`). Frontend deste módulo
      fechado em 2026-10-11/12 — ver nota consolidada abaixo. **`gestao-compras-
      service` feito no backend em 2026-10-10** (pedidos de compra por
      período, filtro opcional de fornecedor/situação, validado ao vivo e
      conferido visualmente — sem bug encontrado, mesmo padrão já maduro).
      Frontend deste módulo também fechado em 2026-10-11/12. O usuário autorizou
      seguir direto pro próximo módulo sem pausa pra revisão de layout desta
      vez ("manda ver", 2026-10-10) — a pausa de 2026-09-27 fica registrada
      como prática padrão, mas não é mais bloqueante por padrão: seguir
      construindo e perguntar se a pausa deve voltar a valer.
      **`business-intelligence-service` feito no backend em 2026-10-10**
      (export dos 3 indicadores já existentes — Valor de Estoque/Curva ABC/
      Compras por Fornecedor —, validado ao vivo e conferido visualmente, com
      o fallback `#id` pra produto/depósito/fornecedor órfão herdado de graça
      do próprio DTO, que já denormaliza nome — único módulo do monorepo que
      já fazia isso antes desta rodada). Frontend deste módulo também fechado
      em 2026-10-11/12. **`controle-estoque-service` fechado por completo em
      2026-10-11** (saldo de estoque por depósito + ficha de movimentação por
      produto — os 2 relatórios que faltavam além da Curva ABC/piloto —,
      validado ao vivo e conferido visualmente, incl. um segundo achado
      técnico generalizado em `ReportEngine` pra campos `java.time.Instant`,
      mesma técnica já usada pra `Enum`). Frontend deste módulo também fechado
      em 2026-10-11/12. **`cadastro-base-service` fechado em 2026-10-11**
      (listagem cadastral de clientes/fornecedores/produtos, o relatório mais
      simples do lote — validado ao vivo e conferido visualmente; achado real
      corrigido ANTES de validar com volume: a 1ª versão pulava a hidratação
      das coleções de `Pessoa`/`Produto` no pressuposto errado de que isso
      evitaria N+1 — na real o mapper MapStruct monta essas coleções de
      qualquer forma, hidratar ou não, então pular a hidratação CAUSA o N+1
      em vez de evitar; corrigido reaproveitando as mesmas hidratações já
      usadas pelo `GET` paginado de cada recurso). Com isso, os 6 módulos
      originalmente escopados (financeiro/contabilidade/compras/BI/estoque/
      cadastro-base) estão todos fechados.
      **Os 5 módulos restantes (desenho em aberto) fechados em 2026-10-11**:
      `gestao-documentos-service` (documentos por pasta/tipo), `os-service`
      (ordens de serviço por período — achado real: `OsAbertura` tem 3
      coleções irmãs, mesmo padrão "hidratar por lado" já usado em
      `OsAberturaService`), `crm-service` (funil de oportunidades — relatório
      plano, diferente da visão Kanban já existente, que tem
      `List<OportunidadeResponse>` aninhada; situação default `ABERTA`),
      `fiscal-service` (notas emitidas por período — **o mais arquiteturalmente
      distinto do lote**: unifica NF-e/NFC-e/NFS-e/CT-e, 4 tipos sem shape
      comum, numa grade só via `SELECT new` com constructor expression JPQL
      por repositório, validado ao vivo com um documento real de cada tipo
      flipado via SQL pra `AUTORIZADA` — funcionou de primeira, nenhum bug),
      `loja-virtual-service` (pedidos por período — achado real: `PedidoResponse`
      não expunha `dataCriacao`, diferente de quase todo outro DTO do
      monorepo, corrigido adicionando o campo ao record). Todos os 5
      validados ao vivo com dado real, PDFs conferidos visualmente, RBAC
      positivo/negativo confirmado. **Com isso, os 11 módulos de negócio do
      monorepo têm export PDF/Excel no backend — escopo original da Fase 17
      fechado por completo.** **Frontend dos 15 relatórios restantes fechado
      em 2026-10-11/12** (a pedido do usuário, "Não deixe lacunas, implemente
      tudo"): botões reaproveitando filtro já existente da tela em
      Clientes/Fornecedores/Produtos (cadastro)/Saldos (estoque)/Documentos
      (GED, ganhou 3 selects novos só pro relatório)/Oportunidades (CRM, ganhou
      1 select novo); páginas novas sem view equivalente — Ficha de
      Movimentação (estoque), Pedidos de Compra por Período (compras), OS por
      Período, Notas por Período (fiscal, unifica NF-e/NFC-e/NFS-e/CT-e numa
      grade só); os 3 indicadores do BI e Pedidos da Loja Virtual também
      ganharam os botões. **Requisito confirmado pelo usuário (2026-09-27):
      todo relatório precisa ganhar integração com o frontend
      (`cactus-erp-web`), não só a API** — atendido por completo, nenhum dos
      23 relatórios do monorepo (8 originais + 15 desta rodada) ficou sem
      botão de exportar na tela. Validado sem navegador (indisponível nesta
      sessão) via contrato de API real contra o backend local — ver
      `cactus-erp-web/CLAUDE.md`, Entrega 22.
- [ ] **PDF/Excel dos relatórios mostram `#id` em vez de nome** (vendedor,
      cliente, produto, fornecedor). A tela resolve os nomes via lookup
      cross-serviço; o arquivo exportado não, porque cada serviço só guarda a
      referência solta. Pra um relatório impresso/arquivado isso é uma
      limitação real. Decidir a abordagem antes do próximo módulo de
      relatórios: (a) o serviço que gera o relatório busca os nomes em lote no
      dono do dado (ex: `cadastro-base-service`) na hora de gerar; ou (b) nome
      denormalizado no momento da gravação (como o BI já faz). (2026-10-01)

## Operacional / ambiente

- [ ] **Variável de ambiente GLOBAL `DB_PASSWORD` derruba todo serviço do
      ERP no boot** (`Access denied`, erro 1045). Ela provavelmente veio da
      mudança do `cactus-pdv` pra `${DB_PASSWORD:changeme}`. Todo serviço do ERP
      lê `${DB_PASSWORD:<senha_local>}`, então a variável global sobrescreve a
      senha de cada `*_app`. Contorno atual: `env -u DB_PASSWORD mvn -pl
      <servico> spring-boot:run`. Decidir a correção definitiva: renomear a
      variável do PDV (ex: `PDV_DB_PASSWORD`) ou usar um nome por serviço no
      ERP. (2026-10-01)

- [ ] **Processos Java em background não sobrevivem a reinício de sessão do
      harness — e o Docker Desktop também pode cair junto** — `mvn
      spring-boot:run` de cada serviço precisa ser resubido manualmente no
      início de toda sessão nova, antes de qualquer teste ao vivo. Reconfirmado
      em 2026-09-04 (Fase 11→13): desta vez o **Docker Desktop em si** também
      tinha caído entre sessões (não só os processos Java), derrubando junto
      `cactus-mysql`/`cactus-rabbitmq` — checklist de religamento completo:
      (1) `Start-Process "Docker Desktop.exe"` + aguardar o daemon responder
      (`docker ps`), (2) `docker start cactus-mysql cactus-rabbitmq` +
      aguardar `mysqladmin ping`, (3) subir os serviços Java na ordem
      (seguranca-service primeiro, os demais em paralelo depois), cada um
      com `CACTUS_RABBITMQ_USER`/`PASSWORD` quando aplicável (`controle-estoque-service`).
      Dados sobrevivem ao ciclo (volumes Docker persistem via `docker start`,
      não `docker run`), só o processo cai. Não é bug de código, só lembrete
      operacional recorrente — mas agora mais completo que a versão anterior
      desta nota. (`cactus-erp-web/CLAUDE.md` item 8; reconfirmado 2026-09-04)
- [x] ~~**`mvn install` em `common` corrompe o classloading de qualquer
      serviço já rodando**~~ — **achado e corrigido em 2026-09-12**, durante a
      validação do piloto de Saga (ver root `CLAUDE.md`, seção "Padrão Saga
      orquestrada"): sobrescrever o `.jar` de `common` em disco enquanto um
      JVM já tem esse classpath aberto corrompe a resolução de classes ainda
      não carregadas por aquele processo (Windows) —
      `NoClassDefFoundError`/`ClassNotFoundException` só aparece no primeiro
      request que precisar de uma classe nova de `common`, então "o serviço
      não deu erro" não é prova de que ele está saudável depois de um
      `mvn install common` feito com ele já no ar. **Regra prática**: depois
      de `mvn install` em `common` (ou qualquer módulo do qual outros
      dependam), reiniciar TODO serviço já em execução, mesmo os que não
      erraram na hora — não só o que estava sendo testado. Confirmado
      corrigido de verdade (não só reiniciado por precaução): os 5 serviços
      da sessão foram reiniciados e revalidados com smoke test funcional real
      (login + 1 endpoint de cada serviço com dado real, não só
      `/v3/api-docs`) — todos 200.
- [ ] **`vendas-service.confirmar` agora depende de `financeiro-service` (porta
      8093) estar de pé** — achado ao vivo na Entrega 15 (`cactus-erp-web`,
      2026-09-06): `POST /vendas/{id}/confirmar` chama
      `POST /contas-receber/gerar-de-venda` (integração fechada em
      2026-09-05, ver root `CLAUDE.md`) — sem `financeiro-service` rodando, a
      confirmação falha com 502 (`VENDAS_FALHA_INTEGRACAO_FINANCEIRO`), erro
      só visível no log do `vendas-service`, sem nada explícito na UI além do
      toast genérico "Falha ao confirmar venda." Registrar `financeiro-service`
      na lista de serviços mínimos pra qualquer teste ao vivo que envolva
      confirmar uma venda (não é mais só seguranca+cadastro-base+estoque+
      vendas — a lista de "serviços necessários" no `cactus-erp-web/CLAUDE.md`,
      seção "Como rodar localmente", precisa de uma revisão geral, não só
      deste item).
