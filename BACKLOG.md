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
- [ ] **Financeiro: desconciliar item já conciliado sem botão na tela** — a
      API (`useDesconciliarItem`) já existe e está pronta, `ConciliacaoBancariaPage`
      só nunca ligou nenhum botão a ela (só concilia manual/automático/gera
      ajuste). Pequeno, isolado — próxima vez que a tela de Conciliação
      Bancária for revisitada. (2026-09-07, `cactus-erp-web/CLAUDE.md`,
      Entrega 21)
- [ ] **Financeiro: `AplicacoesFinanceirasListPage` sem filtro por conta
      bancária** — as outras 3 listagens do módulo (Movimentos/Cheques
      Emitidos/Cheques Recebidos) já têm um `Select` de "filtrar por conta",
      só Aplicações Financeiras não — descoberto como ambiguidade real
      durante o próprio teste ao vivo desta entrega (`.first()` no script de
      teste resgatou a aplicação errada por engano, sem impacto no produto,
      mas evidenciando que um usuário real teria a mesma confusão visual
      sem esse filtro). (2026-09-07, `cactus-erp-web/CLAUDE.md`, Entrega 21)
- [ ] **`sincronizacao-service` sem nenhuma tela no `cactus-erp-web`** — o
      serviço (Fase 6, porta 8087 — outbox/RabbitMQ/DLQ/auditoria da sync
      ERP↔PDV, provisionamento de client-por-loja) só é operável hoje via
      Swagger/API direta; `SINCRONIZACAO_*` existe no catálogo real de
      authorities (confirmado na auditoria de `NavConfig.tsx` de
      2026-09-13) sem nenhuma entrada de menu. Candidato a módulo próprio
      futuro (telas de: registros de sincronização/status/motivo de erro,
      reprocessamento manual, gestão de clients-loja). (2026-09-13, achado
      durante a auditoria de `NavConfig.tsx`)
- [ ] **Segurança: fluxo de criação de usuário + papel** —
      `PapeisEmpresaTab`/`AtribuirPapelEmpresaModal` não têm um "criar usuário
      já com um papel" num passo só, hoje é sempre 2 passos. Aceitável, mas
      vale reavaliar se virar atrito real no uso. (2026-09-02,
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
      cliente final, admin frontend em dois níveis (plataforma Cactus vs cada
      loja/lojista — nenhum construído), modo canal (sincronização de
      catálogo com um `cadastro-base-service` de cliente que já tem o Cactus
      ERP completo). (2026-09-04, root `CLAUDE.md`, Fase 13; atualizado
      2026-09-12)

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
- [ ] **Fase 16 — MDF-e (extensão do `fiscal-service`)** — ainda não iniciada.
      `MdfeCabecalho`+`MdfeDocumentoVinculado`, agrupando N `CteCabecalho` já
      emitidos numa viagem. Comando ACBr de alta confiança já confirmado por
      pesquisa (`MDFE.CriarEnviarMDFe(cIniMDFe,[nLote],...)`, tiro único,
      mesma família de `NFE.CriarEnviarNFe`/`CTE.CriarEnviarCTe`) — layout
      campo-a-campo do `MDFe.INI` ainda não levantado. Nunca será invocado
      contra o ACBr Monitor real nesta sessão (mesma decisão de segurança das
      Fases 8/9/11 — certificado de produção configurado, sem homologação).
      (2026-09-13, root `CLAUDE.md`, ANÁLISE: MÓDULO CONTÁBIL E FISCAL)
- [ ] **Reforma Tributária (IBS/CBS/Imposto Seletivo) — motor tributário
      desatualizado, já é exigência legal VENCIDA, não mais risco futuro** —
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
      ANÁLISE: MÓDULO DE RELATÓRIOS no root `CLAUDE.md`). Falta replicar o
      padrão pro resto: `financeiro-service` (export do Fluxo
      de Caixa já existente + relatório novo de Contas a Pagar/Receber em
      aberto), `contabilidade-service` (export de Balancete/Razão já
      existente), `gestao-compras-service` (pedidos por período/fornecedor),
      `business-intelligence-service` (export dos 3 indicadores já
      existentes), `cadastro-base-service` (listagem cadastral), e desenho
      ainda em aberto pra `gestao-documentos-service`/`os-service`/
      `crm-service`/`fiscal-service`/`loja-virtual-service`.
      **Requisito confirmado pelo usuário (2026-09-27): todo relatório
      precisa ganhar integração com o frontend (`cactus-erp-web`), não só a
      API** — botão de exportar/baixar PDF/Excel em cada tela relevante,
      sempre que for possível. Isso não é mais um "fica pra depois do
      backend" implícito — é parte do escopo de cada relatório entregue daqui
      pra frente. Nenhum relatório desta fase (nem o piloto Curva ABC, nem os
      3 de Vendas) tem essa integração ainda: `estoque/CurvaAbcPage.tsx` só
      mostra a tabela JSON, sem botão de exportar; não existe nenhuma tela de
      "Vendas por Período"/"Produtos Mais Vendidos"/"Comissões por Vendedor"
      no frontend hoje. Ao retomar esta fase, cada módulo devia sair com
      backend + frontend juntos (ou pelo menos o botão de exportar
      adicionado às telas já existentes que exibem o relatório em JSON), não
      backend isolado esperando uma "Entrega" de frontend à parte.
      **PAUSADO a pedido do usuário (2026-09-27)**: antes de seguir pro
      próximo módulo, o usuário quer validar por conta própria e averiguar
      como ficaram os layouts dos relatórios já gerados (PDF/Excel de
      Curva ABC e dos 3 relatórios de Vendas) — os arquivos de teste foram
      gerados em `/tmp` durante a validação ao vivo desta sessão, não ficaram
      salvos no repo; pode ser necessário regerar via `GET
      /api/v1/relatorios/.../pdf|xlsx` pra essa revisão. Não continuar o
      rollout pro próximo módulo sem essa validação de layout confirmada.
      (2026-09-27, root `CLAUDE.md`)

## Operacional / ambiente

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
