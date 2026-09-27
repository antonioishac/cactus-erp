# Roadmap de desenvolvimento — Cactus PDV

Backlog vivo do projeto. Atualizar este arquivo conforme itens forem
concluídos ou a prioridade mudar — é o documento de referência para "o que
falta" e "o que já foi feito", complementando as fases descritas no
`CLAUDE.md`.

Última revisão: 2026-08-22.

## Legenda

- ✅ Implementado (código existe e cobre o essencial do item)
- 🔜 Próximo passo ativo
- ⏳ Pendente (ainda não iniciado)
- ⚠️ Risco/decisão em aberto

## Passo 0 — Consolidação (sem prazo definido, a critério do usuário)

- ⚠️ Grande volume de arquivos novos não commitados na branch
  `feature/migrate-mysql` (entidades/serviços das fases 3 e 4). Usuário
  decidiu adiar o commit — retomar quando fizer sentido.
- ⏳ Confirmar que `mvnw clean compile` passa limpo antes de iniciar a fase 5.
- ⏳ Nenhuma migration/Flyway existe (`ddl-auto=none`); toda tabela nova
  (estoque, licenciamento se precisar de algo além de `PDV_CONFIGURACAO`)
  precisa ser criada manualmente no MySQL e documentada no `PDV.md`.

## Fases 1-4 — ✅ Implementadas

Login/sessão, operação de caixa completa (sangria/suprimento/desconto com
alçada/conferência), catálogo avançado (grupos/faixas de preço/imagens) e
mesas/comandas/delivery já têm entidade + repository + service + controller
no código atual. Não são o foco agora; retomar apenas se surgir bug ou
lacuna pontual durante o uso.

## Fase 5 — Estoque como livro-razão ✅ implementado (2026-08-22)

Hoje `PRODUTO.QUANTIDADE_ESTOQUE` é só um saldo corrente, sobrescrito a cada
venda — sem histórico. Objetivo: transformar em livro-razão auditável.

- [x] Tabela `ESTOQUE_MOVIMENTO` desenhada no `PDV.md` — colunas: `ID`,
      `ID_PRODUTO`, `TIPO_MOVIMENTO` (E=Entrada, S=Saída, A=Ajuste,
      P=Perda, D=Devolução), `QUANTIDADE`, `QUANTIDADE_ANTERIOR`,
      `QUANTIDADE_NOVA`, `ID_PDV_VENDA_DETALHE` (nullable), `ID_OPERADOR`,
      `DATA_MOVIMENTO`, `HORA_MOVIMENTO`, `OBSERVACAO`, `ID_DISPOSITIVO`.
- [x] Tabela aplicada no MySQL local (2026-08-22) — `SHOW CREATE TABLE
      ESTOQUE_MOVIMENTO` confere com o DDL do `PDV.md`. FKs pra `PRODUTO`,
      `PDV_VENDA_DETALHE` e `PDV_OPERADOR` validadas.
- [x] Entidade `EstoqueMovimento` + enum `TipoMovimentoEstoque`.
- [x] `EstoqueMovimentoRepository`.
- [x] `EstoqueMovimentoService` — método central `registrarMovimento(...)`,
      único ponto que deve alterar `PRODUTO.QUANTIDADE_ESTOQUE`.
- [x] Plugado em `VendaService.finalizarVenda` — baixa de estoque por item
      ativo com `MOVIMENTA_ESTOQUE=S` gera registro de movimento (`S`) via
      `registrarSaidaPorVenda`, idempotente (não duplica se reprocessado).
- [ ] Devolução (fase 8) ainda não reaproveita o mecanismo (`D`) — service
      já suporta, falta a tela/fluxo de devolução chamar.

## Fase 5.5 — Licenciamento / trava de mensalidade ✅ implementado em modo local (2026-08-22)

Design completo em `LICENCIAMENTO.md`. Status:

- [x] `LicencaService` — lê `PDV_CONFIGURACAO.PLANO_SITUACAO` via
      `PdvConfiguracaoRepository`, fail-open pra `A` quando não configurado
      (não trava instalação existente sem essa etapa feita).
- [x] Gate único em `PdvMovimentoService.abrirMovimento` — cobre tanto
      `LoginController` (fluxo ABRIR_MOVIMENTO) quanto
      `IniciarMovimentoController`, já que os dois passam por esse método.
      Bloqueia só quando `B`; movimento já aberto não é afetado.
- [x] Aviso quando `I` — `Alert` (WARNING) no `LoginController` logo após
      autenticar, antes de navegar. **Simplificação consciente**: o design
      original previa banner persistente na tela principal (Pdv4); optei
      por um alerta pontual no login pra não mexer no `Pdv4Controller`
      (arquivo grande, fora do escopo desta rodada). Revisitar se o aviso
      pontual não for suficiente na prática.
- [x] Auditoria de transição de estado (2026-08-23) — `LICENCA_CACHE`
      ganhou `SITUACAO_ANTERIOR`/`DISPOSITIVO` (`ALTER TABLE` aplicado no
      MySQL local) + nova origem `L` (leitura local, sem retaguarda
      configurada). `LicencaService.gravarCache` agora persiste toda
      transição real de estado: verificação remota (R) sempre grava (é o
      que sustenta `DATA_HORA_VERIFICACAO` da janela de tolerância);
      bloqueio forçado por tolerância expirada (F, antes só logava em
      `LOGGER.warn`) e leitura 100% local (L) só gravam quando a situação
      muda em relação à última linha conhecida, pra não inundar a tabela
      a cada consulta idêntica. `LicencaCacheRepository` ganhou
      `findFirstByOrigemInOrderByIdDesc` pra isolar R/F (janela de
      tolerância) de L (não deve resetar contagem de dias offline).
- [x] Cache local com timestamp e janela de tolerância offline (5-7 dias,
      configurável via `cactus.pdv.licenca.tolerancia-dias`) — **esta
      entrada estava desatualizada nesta revisão do roadmap**: o código já
      implementa isso (`LicencaCache`/`LicencaCacheRepository`/
      `LicencaService`, ver item de auditoria acima). `RetaguardaLicencaClient`
      tenta consulta remota primeiro; sem endpoint real configurado (o
      Cactus ERP/retaguarda ainda não existe), `estaConfigurado()` retorna
      falso e `LicencaService` cai direto pro modo 100% local (item abaixo)
      — não há, hoje, nenhuma instalação real usando o caminho remoto/cache
      ainda, mas o mecanismo está pronto pra quando o endpoint existir.

## Fase 6 — Fiscal 🔜 protocolo validado contra documentação oficial (2026-08-23), AINDA NÃO testado contra instância rodando

Decisões já travadas: ver "Decisões técnicas travadas" no `CLAUDE.md` (ACBr
Monitor, NFC-e principal / SAT-MFE contingência).

- [x] `PdvConfiguracao` estendida com `acbrMonitorEndereco`/`acbrMonitorPorta`/
      `moduloFiscalPrincipal`/`moduloFiscalContingencia`.
- [x] **Reescrito (2026-08-23) com protocolo validado contra documentação
      oficial do projeto ACBr** (acbr.sourceforge.io/ACBrMonitor +
      cliente Java de referência da comunidade,
      github.com/edsonmoretti/JConnACBRMonitorPlus) — correções em relação
      à versão anterior (que era 100% especulativa):
  - `AcbrMonitorClient`: terminador de comando é `CRLF.CRLF` (não `\n`
    simples), terminador de resposta é o byte ETX/`CHR(3)` (não heurística
    de linha em branco), charset Windows-1252.
  - `AcbrComandoFiscalBuilder`: reescrito do zero. O protocolo real da NFC-e
    **não é** uma sequência de comandos por item — é um único comando
    `NFe.CriarEnviarNFe(<caminho.ini>, lote, ...)` com o documento inteiro
    num arquivo INI (layout `NFCe.INI` do ACBr: seções `[Identificacao]`,
    `[Emitente]`, `[Destinatario]`, `[ProdutoNNN]`, `[ICMSNNN]`,
    `[PISNNN]`, `[COFINSNNN]`, `[Total]`, `[pagNNN]`). SAT/MFE têm layouts
    de comando próprios, diferentes — não implementados (lança
    `UnsupportedOperationException`, tratado como falha normal pelo
    `EmissaoFiscalService`).
  - `EmissaoFiscalService`: agora extrai `ChDFe` (chave de acesso) e
    `NProt` (protocolo de autorização) da resposta real via regex,
    fechando o gap de persistência que antes ficava vazio.
  - **Novo gap descoberto**: não existia em lugar nenhum do schema os
    dados cadastrais do emitente (CNPJ/IE/razão social/endereço),
    obrigatórios pra seção `[Emitente]`. Adicionados em
    `LOJA_CONFIGURACAO` (`ALTER TABLE` aplicado no MySQL local).
  - Simplificações conscientes documentadas no javadoc da classe: `nNF`
    usa o ID do cupom (não um contador sequencial real por série),
    `tpAmb` fixo em Homologação (2), `uCom` fixo em "UN".
- [x] **ACBr Monitor instalado e testado contra o código real (2026-08-23)**
      — usuário instalou ACBrMonitorPLUS DEMO 1.4.0.482 localmente
      (TCP/IP porta 3434). Validação em duas etapas:
  - Testes manuais via socket cru (PowerShell) confirmaram o protocolo:
    **banner de boas-vindas ao conectar** (bug real encontrado — o
    `AcbrMonitorClient` não descartava esse banner antes de mandar o
    comando, e acabava lendo o banner como se fosse a resposta; corrigido),
    terminador `CRLF.CRLF`, resposta terminada em ETX, formato
    `OK`/`ERRO: mensagem`, objeto válido é `NFE` (não `NFCe`).
  - Harness standalone (`javac`/`java` direto, sem Spring) exercitou o
    código de produção real (`AcbrComandoFiscalBuilder`/`AcbrMonitorClient`)
    contra a instância — revelou e corrigiu **dois bugs reais**:
    (1) helper `chave()` usava `valor == null` pra decidir entre "seção"
    e "chave vazia", corrompendo o INI quando um campo opcional legítimo
    (ex: `Fone`, `Complemento`) vinha nulo — separado em `secao()`/`chave()`
    sem ambiguidade; (2) `dEmi` em ISO 8601 (`yyyy-MM-dd'T'HH:mm:ss`) foi
    **rejeitado** pelo parser Delphi do ACBr — trocado por
    `dd/MM/yyyy HH:mm:ss` (confirmado aceito).
  - **Resultado**: com essas correções, o INI passou por TODA a validação
    de identificação/emitente/produto/ICMS/PIS/COFINS/pagamento, parando
    só no carregamento do certificado
    (`"CarregarCertificadoDeDadosPFX" não implementado em: TDFeSSLCryptClass`)
    — confirmado como problema **conhecido da comunidade ACBr** (config de
    biblioteca SSL/criptografia no ACBr Monitor, não bug nosso). Ainda não
    resolvido — próximo passo é o usuário ajustar a biblioteca SSL
    (WinCrypt/OpenSSL) na configuração do ACBr Monitor.
  - Também descoberto e corrigido: schema não tinha dados cadastrais do
    emitente (`ALTER TABLE LOJA_CONFIGURACAO` — CNPJ/IE/razão social/
    endereço/CRT), essenciais pra seção `[Emitente]` do INI.
- [x] `EmissaoFiscalService` — orquestra módulo principal → contingência →
      `PENDENTE_REPROCESSAMENTO`; nunca desfaz a venda já paga/fechada.
      Consumiu o TODO de `EncerramentoVendaController` (linha ~910).
- [x] Persistência de status fiscal (2026-08-22) — `ALTER TABLE
      PDV_VENDA_CABECALHO` aplicado no MySQL local + `PDV.md` atualizado:
      `STATUS_FISCAL` (P/E/N), `CHAVE_ACESSO_FISCAL`, `PROTOCOLO_AUTORIZACAO`,
      `MENSAGEM_FISCAL`. `EmissaoFiscalService` grava `STATUS_FISCAL` e
      `MENSAGEM_FISCAL` em todos os desfechos (emitido/pendente/não
      configurado). ⚠️ `CHAVE_ACESSO_FISCAL`/`PROTOCOLO_AUTORIZACAO` ainda
      NÃO são preenchidos — extrair da resposta do ACBr depende do formato
      de retorno, que segue não validado (mesma ressalva de
      `AcbrComandoFiscalBuilder`).
- [x] Job de reprocessamento — `ReprocessamentoFiscalService` (`@Scheduled`,
      intervalo configurável via
      `cactus.pdv.fiscal.reprocessamento.intervalo-ms`, padrão 5min) busca
      cupons `STATUS_FISCAL='P'` e tenta emitir de novo, isolando falha por
      cupom. `@EnableScheduling` habilitado em `PdvApplication`. Roda só
      enquanto o processo do PDV está aberto (app desktop, sem servidor
      separado) — retoma sozinho na próxima abertura.
- [x] **Cálculo tributário básico** (2026-08-22) — `CalculoTributarioService`
      aplica ICMS/PIS/COFINS/ISSQN já cadastrados no produto (`PRODUTO.TAXA_*`)
      sobre o valor do item, respeitando `PRODUTO.IAT` (arredondamento vs
      truncamento). Plugado em `VendaService` (insert, agrupamento, e
      agregação no cabeçalho ao finalizar). **Escopo deliberadamente
      limitado** — ver javadoc da classe: NÃO cobre ICMS-ST, DIFAL, redução
      de base, nem `VALOR_IMPOSTO_FEDERAL/ESTADUAL/MUNICIPAL` (depende de
      tabela IBPT externa, não modelada). Assume que as alíquotas no
      cadastro do produto já estão corretas — quem determina isso é
      responsabilidade do contador/fiscal, não do PDV. Recalculado também
      em `DescontoAcrescimoService.consolidarCamposDeItem` (único ponto
      onde `VALOR_TOTAL` do item muda por desconto/acréscimo/rateio) via
      `CalculoTributarioService.aplicarEm`, método público reaproveitado
      pelos dois services em vez de duplicar a lista de 9 campos em cada um.
- [x] Relatório Z real (2026-08-22) — `FechamentoMovimentoService` tinha um
      placeholder que assumia "100% das vendas em dinheiro"; agora
      reaproveita `ConferenciaCaixaService.calcularVendasPorForma`/
      `calcularSaldoEsperadoDinheiro` (breakdown real por forma de
      pagamento a partir de `PDV_TOTAL_TIPO_PAGAMENTO`, com fundo/
      suprimento/sangria/troco). Corrige também uma divergência latente:
      antes, Relatório Z e Conferência de Caixa podiam calcular dois
      valores diferentes de "saldo esperado em dinheiro" pro mesmo
      movimento — agora usam o mesmo cálculo.
- [x] Verificado (2026-08-22): `EmissaoFiscalService` não recalcula
      `HASH_REGISTRO` do cupom ao atualizar `STATUS_FISCAL`/`TIPO_OPERACAO`/
      `MENSAGEM_FISCAL` — confirmado que isso é correto e não é um bug: a
      fórmula do hash (`HashCalculator.hashDoCabecalho`) é intencionalmente
      restrita a id/data/hora/movimento/colaborador/valor/status, não inclui
      metadados operacionais de emissão. Trilha de item continua coberta
      por `PdvVendaDetalheHistorico`.
- [x] **Validação end-to-end concluída até o limite do que dá pra testar
      sem certificado pago (2026-08-23)**. Sequência completa de testes
      contra o ACBrMonitorPLUS demo real, corrigindo 4 bugs reais
      encontrados só ao testar (não visíveis só lendo documentação):
  1. Banner de boas-vindas não descartado (`AcbrMonitorClient`).
  2. `chave()`/`secao()` ambíguos por `null` corrompendo o INI
     (`AcbrComandoFiscalBuilder`).
  3. `dEmi` em ISO 8601 rejeitado pelo parser Delphi — trocado por
     `dd/MM/yyyy HH:mm:ss`.
  4. **CST vs CSOSN por regime tributário** — empresas Simples Nacional
     (CRT 1/2) precisam de `CSOSN` (de `PRODUTO.CSOSN`) em vez de `CST`
     no grupo ICMS; código antes só usava CST, causando rejeição de
     schema (`"N12a/CSOSN ... Nenhum valor informado"`). Corrigido com
     `ehSimplesNacional(loja)` decidindo qual campo usar.
  - Configuração de biblioteca SSL resolvida pelo usuário no ACBr Monitor
    (`libWinCrypt`/`cryWinCrypt`/`httpWinHttp`/`xsLibXml2`/`LT_TLSv1_2` —
    combinação oficial recomendada pela Base de Conhecimento do fórum
    ACBr pra Windows, sem DLL externa).
  - **Resultado final**: com certificado de teste autoassinado (gratuito,
    não-ICP-Brasil), o fluxo completo — TCP → comando → INI → assinatura
    → validação de schema XML — passou 100%, e o ACBr chegou a fazer a
    requisição HTTP real pro webservice de homologação da SEFAZ-SP
    (`homologacao.nfce.fazenda.sp.gov.br`), recebendo **403 Forbidden** —
    resultado esperado e correto: a SEFAZ exige certificado ICP-Brasil
    válido na camada TLS, o que um certificado autoassinado nunca vai
    satisfazer. **Este é o limite do que dá pra validar sem custo
    financeiro.**
- [x] Extração de `ChDFe`/`NProt` coberta por teste unitário (2026-08-23) —
      `EmissaoFiscalServiceTest` (primeiro teste automatizado do projeto;
      `spring-boot-starter-test` adicionado ao `pom.xml`). `extrair(...)`
      virou dois métodos estáticos package-private
      (`extrairChaveAcesso`/`extrairProtocoloAutorizacao`) só pra permitir
      teste direto sem subir contexto Spring. **Cobre só a lógica de
      regex contra textos sintéticos** (case-insensitive, espaços, erro
      sem os campos, truncamento em 44/30 chars) — não prova que é o
      formato real devolvido pela SEFAZ numa emissão de sucesso, que seria
      o item abaixo.
- [ ] **Falta** (exige certificado ICP-Brasil real, custo ~R$150-280/ano —
      decisão do usuário, não vou comprar): testar emissão de homologação
      de verdade, validar que `ChDFe`/`NProt` são extraídos corretamente
      de uma resposta de SUCESSO real da SEFAZ (só o caminho de erro foi
      exercitado contra uma instância de verdade até aqui — o teste acima
      valida a lógica, não o formato real).
- [ ] Testar módulos SAT/MFE (não implementados — só NFC-e tem o layout
      INI mapeado, ver javadoc de `AcbrComandoFiscalBuilder`).

## Fase 7 — Periféricos 🔜 groundwork implementado (2026-08-22)

Decisões travadas: TEF Hiperlink como prioridade 1, Toledo + Filizola nas
balanças (ver `CLAUDE.md`). Novas dependências: `jSerialComm` (porta
serial), `jackson-databind` (JSON — também usado pelo licenciamento
remoto).

- [x] `PdvConfiguracaoBalanca` (entidade/repositório) — mapeia
      `PDV_CONFIGURACAO_BALANCA`, que já existia no schema mas não tinha
      código. `ModeloBalanca` enum ORDINAL alinhado ao inteiro semeado
      (0=Nenhum...8=Magellan).
- [x] **Decodificador de código de barras com peso/valor embutido**
      (`BalancaCodigoDecoder`) — **alta confiança**: é lógica pura (sem
      I/O), decodifica o padrão EAN-13 "peso variável" brasileiro
      (prefixo 2 + código + peso/valor + dígito verificador), validado
      por checksum EAN-13 e por rastreamento manual de um exemplo
      completo. Plugado em `Pdv4Controller.addProduct` como fallback
      depois de GTIN e código-balança curto falharem, via novo
      `BalancaService` (resolve produto + calcula quantidade a partir do
      peso ou do valor da etiqueta).
- [x] Leitura serial AO VIVO (`BalancaSerialService`, jSerialComm) —
      **⚠️ não testada contra hardware real**. Implementa o comando ENQ
      genérico da família Toledo; protocolos de Filizola/Urano/outros
      ainda não implementados (ponto de extensão documentado no código).
      Não está plugada em nenhuma tela ainda (sem botão "consultar peso"
      na UI). **Bug corrigido (2026-08-23, achado em revisão de código,
      sem hardware pra confirmar em execução real)**: `abrirPorta` lia
      `PDV_CONFIGURACAO_BALANCA.HAND_SHAKE` (0=Nenhum/1=XON_XOFF/
      2=RTS_CTS/3=DTR_DSR) mas nunca aplicava esse valor na porta serial
      — configurava baud/dataBits/stopBits/parity e ignorava o
      handshake, o que travaria a leitura (timeout) em qualquer balança
      cadastrada com controle de fluxo por hardware ou software.
      Adicionado `traduzirHandShake` + `porta.setFlowControl(...)`.
  - [x] **Plugado na tela de venda, 100% por teclado (2026-08-23, 3
    iterações até fechar)** — cobre os dois cenários de balança lado a
    lado (ver `CLAUDE.md`): etiqueta pré-impressa (já existia) e pesagem
    ao vivo (produto sem etiqueta, ex: bananas soltas). **Desenho final**:
    não existe botão nem tela dedicados — pesar é uma CONSEQUÊNCIA de
    resolver um produto vendido por peso, não uma ação separada:
    - Bipar/digitar um código de balança (se o produto tiver um
      cadastrado) e apertar ENTER em `tfCampoBusca`: `Pdv4Controller.addProduct`
      resolve o produto e, via `adicionarProdutoResolvido`, pesa
      automaticamente se `BalancaService.vendidoPorPeso` for verdadeiro —
      sem pedir quantidade manual.
    - Produto SEM código nenhum (o caso real testado — "Banana Nanica"/
      "Patinho Bovino" no cadastro de demonstração, `GTIN` e
      `CODIGO_BALANCA` nulos): F11 abre a busca por nome
      (`Pdv4Controller.abrirPesquisaProduto`/`StageManager.showPesquisarProdutos`),
      digitar o nome + ENTER já seleciona o primeiro resultado (sem
      precisar de seta), e se o produto escolhido é vendido por peso,
      `Pdv4Controller` pesa direto (resolve pelo **ID** do
      `PesquisarProdutoDTO`, sem precisar de código nenhum) em vez de só
      preencher o campo de código.
    - `BalancaService.lerPesoAoVivo` lê a balança e o item entra pelo
      mesmo `adicionarProdutoVenda(prod, qtde)` já usado pela etiqueta.
    - **Duas correções em cima da primeira versão, ambas por feedback
      direto do usuário testando**: (1) a 1ª versão exigia um código já
      digitado antes de poder pesar — travava por completo o caso sem
      código nenhum, que é justamente o cenário que a feature deveria
      cobrir; corrigido abrindo a busca por nome quando o campo está
      vazio. (2) a 2ª versão resolvia isso com um **botão dedicado**
      ("Pesar", ao lado da quantidade) — rejeitado: "um PDV não pode
      depender de mouse". Removido o botão (e a tela/`modo pesagem`
      separados que vieram junto) e integrado direto no fluxo de teclado
      que já existia (código+ENTER, F11+nome+ENTER) — a decisão "pesar ou
      não" agora é automática, baseada só em `vendidoPorPeso`, sem
      nenhuma ação de UI nova pro operador aprender.
    Coberto por `ProdutoServiceTest` (reproduz o cadastro real da Banana
    Nanica sem GTIN/código de balança, junto com um produto normal na
    mesma busca).
  - [x] **Gap fechado**: `ProdutoUnidade` virou entidade JPA de verdade
    (`id`/`sigla`/`descricao`/`podeFracionar`, mapeando
    `PRODUTO_UNIDADE.PODE_FRACIONAR` via `SimNao`, seguindo a convenção do
    projeto de enum pra flag `CHAR(1)`) + `ProdutoUnidadeRepository`.
    `Produto.ID_PRODUTO_UNIDADE` continua `Long` cru (consistente com o
    resto daquela entidade, que é toda flat sem `@ManyToOne`) —
    `BalancaService.vendidoPorPeso(Produto)` faz a consulta pelo ID e
    decide se o produto pode ser pesado (testado, `BalancaServiceTest`).
  - [x] **Bug real corrigido ao testar (2026-08-23): produto vendido por
    LITRO travava tentando ler a balança.** `vendidoPorPeso` usava
    `ProdutoUnidade.podeSerFracionado()` (`PODE_FRACIONAR='S'`), que é
    verdadeiro pra KG **e também LT/GR/ML** — qualquer produto vendido
    por litro (ex: óleo a granel) era tratado como "produto de balança" e
    o PDV tentava ler peso físico, caindo no erro "balança não
    encontrada" mesmo pra um produto que nunca deveria pesar nada.
    **Causa raiz**: "fracionável" (admite quantidade decimal) e "pesável
    numa balança" (é unidade de massa) são perguntas diferentes — LT/ML
    são fracionáveis mas ninguém põe um litro de óleo numa balança de
    checkout, a quantidade é sempre digitada. Corrigido com
    `ProdutoUnidade.ehUnidadeDePeso()`, novo método que checa a SIGLA
    (KG/GR — as únicas unidades de massa nos dados semeados) em vez do
    flag de fracionamento; `podeSerFracionado()` continua existindo (é
    um flag legítimo, só não é o que decide isso). Os dois pontos que
    usavam o critério errado (`BalancaService.vendidoPorPeso` e
    `ProdutoService.paraDto`) foram corrigidos juntos. Coberto por testes
    novos em `BalancaServiceTest`/`ProdutoServiceTest` (KG/GR → true,
    LT/ML/UN → false).
  - [x] **Testado o que dá pra testar sem hardware** — `BalancaServiceTest`
    (`vendidoPorPeso`/`lerPesoAoVivo`, com `BalancaSerialService` mockado).
    Continua sujeito à mesma ressalva de sempre: comando ENQ genérico
    Toledo em si, **não testado contra balança física** — só dá pra
    validar de verdade com o hardware na mão.
  - [x] **Fallback de peso manual com autorização gerencial (2026-08-23,
    pedido do usuário — precisa testar o fluxo sem ter balança física
    ainda)**. Quando `lerPesoAoVivo` não consegue ler (sem hardware,
    balança desligada, cabo solto, porta errada — qualquer motivo),
    `Pdv4Controller.pesoManualComAutorizacao` oferece digitar o peso na
    mão, **sempre** atrás de autorização de gerente/supervisor (mesmo
    padrão de alçada de sangria/desconto/devolução — sem essa trava,
    qualquer operador poderia subfaturar um produto pesado digitando um
    peso menor que o real). Loga quem autorizou + peso + produto
    (`LOGGER.warn`, mesmo nível de sangria/desconto). **Não é só um hack
    de teste** — cobre também o caso real de balança quebrada/sem cabo no
    meio do expediente, quando a loja não pode simplesmente parar de
    vender produto pesado.
- [x] **Tela de pesquisa de produto padronizada (2026-08-23, pedido do
  usuário)** — `PesquisarProdutos.fxml`/`PesquisaProdutoController` (popup
  F11) redesenhada com o mesmo visual de `ConsultaProduto.fxml` (header
  azul, busca com ícone, tabela escura — reaproveita as classes
  `cp-*`/`cc2-*` de `consulta-produto.css` em vez de manter CSS próprio
  divergente). **3 bugs reais corrigidos**, todos ligados a "cobrir todas
  as possibilidades" (produto com/sem GTIN, vendido por peso ou não):
  1. A busca-enquanto-digita (`pesquisarProduto`) e a carga inicial
     (`initialize`) tinham lógicas DIFERENTES e inconsistentes pra decidir
     o código de volta pro PDV — a busca usava só `getGtin()` (sempre
     `null` pra produto vendido por peso!), então qualquer produto sem
     etiqueta virava inselecionável assim que o operador digitava
     qualquer letra pra filtrar. Unificado num único método,
     `ProdutoService.paraPesquisa`, usado tanto na carga inicial quanto
     na busca.
  2. `prod.getProdutoUnidade() == 2` — número mágico (2 = KG, descoberto
     consultando a tabela `PRODUTO_UNIDADE` semeada) E risco de
     `NullPointerException` (unboxing de `Long` nulo) pra qualquer produto
     sem unidade cadastrada. Trocado por
     `BalancaService`-style lookup via `ProdutoUnidadeRepository` +
     `PODE_FRACIONAR`, cobrindo também LT/GR/ML (não só KG).
  3. Produto sem GTIN E sem código de balança virava um código vazio
     (`""`) selecionável — agora a linha só fica desabilitada quando
     REALMENTE não há como adicionar o produto (sem código E não vendido
     por peso); produto sem código mas vendido por peso (a etapa 2 desse
     mesmo trabalho, ver acima) continua selecionável — quem decide o
     que fazer é `Pdv4Controller`, não a tela de pesquisa.
  Tabela agora mostra UNIDADE e PREÇO além de código/descrição (visão de
  "com peso, sem peso, etc" pedida), e os botões Pesquisar/Cancelar do
  layout antigo (que existiam no FXML mas não tinham `onAction` — não
  faziam nada) viraram um único botão "Fechar" de verdade. **100% por
  teclado**: digitar + ENTER seleciona o primeiro resultado direto (sem
  precisar de seta pra baixo) — ajuste feito junto da remoção do botão
  "Pesar" (ver item de balança acima), depois do feedback de que um PDV
  não pode depender de mouse.
- [x] Impressão térmica ESC/POS + gaveta de dinheiro (`EscPosBuilder` +
      `ImpressaoService`, via `javax.print` — sem dependência nova) —
      comandos ESC/POS em si têm alta confiança (padrão universal); a
      variável é se o driver da impressora Windows aceita RAW pass-through
      nesta máquina, **não testado contra impressora física**. Nova coluna
      `PDV_CONFIGURACAO.NOME_IMPRESSORA_CUPOM` (aplicada no MySQL local)
      + mapeamento de `GAVETA_SINAL_INVERTIDO`/`GAVETA_UTILIZACAO` (já
      existiam no schema, sem entidade até agora). Plugado no
      `EncerramentoVendaController` — imprime automaticamente ao
      finalizar, sem bloquear a venda se falhar.
- [x] `TefHiperlinkClient` — **peça de menor confiança da fase**: TEF
      Hiperlink não é um protocolo único (PayGo/SiTef/adquirentes
      próprias divergem). Arquitetura pronta (HTTP client, timeout,
      resultado tipado), contrato de payload é uma proposta não validada.
      Nova coluna `PDV_CONFIGURACAO.TEF_HIPERLINK_URL` (aplicada no MySQL
      local). **Não plugado em nenhuma tela** — falta decidir qual
      solução TEF a loja vai usar de verdade antes de integrar na tela de
      pagamento.
  - ⚠️ **Pesquisa feita (2026-08-23) — achado que provavelmente exige
    redesenho, não só validação de payload.** Investigando SiTef (sugestão
    do usuário) contra o que já usamos de referência (ACBr): o kit de
    integração OFICIAL tanto do SiTef (`CliSiTef.dll`, funções
    `ConfiguraIntSiTefInterativo`/`IniciaFuncaoSiTefInterativo`/
    `ContinuaFuncaoSiTefInterativo`/`FinalizaFuncaoSiTefInterativo`) quanto
    do Pay&Go Web ("Kit de Integração", também DLL Windows dedicada) é uma
    **DLL nativa**, não uma API HTTP/REST local como o `TefHiperlinkClient`
    atual assume. O próprio `ACBrTEFD` (componente irmão do
    `AcbrMonitorClient`/`ACBrMonitor` já usado no fiscal) integra com SiTef
    chamando essa DLL diretamente — não existe um "monitor TEF" via socket
    TCP equivalente ao que já validamos pro fiscal. Os comandos
    `ECF.PreparaTEF`/`ECF.EfetuaPagamento` do próprio `ACBrMonitor` que
    pareciam promissores no início da pesquisa são do módulo **ECF**
    (impressora fiscal antiga), não se aplicam ao nosso caso (NFC-e via
    `ACBrNFe`, sem ECF).
  - Consequência prática: integrar de verdade com SiTef ou PayGo
    provavelmente exige **JNA** (Java Native Access, chamada de DLL
    Win32 a partir do Java) em vez do `HttpClient` atual — mudança de
    arquitetura do cliente, não só de payload/URL.
  - Existe uma variante de terceiro (revenda NTK/ControlPay, projeto
    `github.com/autogeral/PayGoWeb`, em Java) que parece expor algo mais
    parecido com HTTP — não é o Pay&Go oficial, confiança baixa, não
    aprofundado.
  - **Bloqueio real**: documentação técnica completa de qualquer uma das
    duas opções (SiTef ou PayGo) fica atrás de cadastro/homologação
    comercial — Software Express pro SiTef, `devpaygo@setis.com.br` pro
    PayGo. Mesma dinâmica do certificado ICP-Brasil da fase 6: preciso de
    um passo comercial do usuário (cadastro como desenvolvedor de
    automação comercial, possivelmente com custo/prazo de homologação)
    antes de validar o protocolo de verdade e reescrever o cliente.
  - **Decisão pendente do usuário**: (1) confirmar SiTef ou avaliar PayGo
    mesmo com a DLL sendo o caminho oficial dos dois; (2) iniciar o
    cadastro de desenvolvedor com a empresa escolhida pra obter
    documentação/DLL/sandbox reais; só então faz sentido eu reescrever
    `TefHiperlinkClient` (provavelmente virando um cliente JNA, não HTTP).
- [x] **Tela de configuração de periféricos** (2026-08-23) —
      `ConfiguracaoPeriferico.fxml`/`Controller` + `ConfiguracaoPerifericoService`.
      Uma tela só com 3 seções (Balança/Impressora+Gaveta/TEF), editando
      `PdvConfiguracao` + `PdvConfiguracaoBalanca` numa transação.
      Combo de impressora é populado com as impressoras reais instaladas
      no Windows (`PrintServiceLookup`) — evita erro de digitação no nome.
      Acessível via Menu Principal → novo item `CONFIGURACOES` (`C`),
      **restrito a Gerente** (só `NivelAutorizacao.G`, diferente dos
      outros itens de sistema que aceitam G/S/O).

## Fase 8 — Diferenciais e retaguarda ⏳

- [x] **Bug corrigido (2026-08-22): cliente da venda de balcão nunca era
      persistido.** `Pdv4Controller` já tinha toda a UI de seleção de
      cliente (F-cliente → `abrirPesquisaCliente()` → `clienteAtual`,
      labels de nome/CPF na tela) e `ConsultaCupomController` já lia
      `cupom.getCliente()` esperando que estivesse preenchido — mas
      `PdvVendaCabecalho.setCliente(...)` nunca era chamado em lugar
      nenhum do fluxo de venda. O cliente selecionado ficava só em memória
      na UI e se perdia ao finalizar a venda (cupom salvo sempre com
      `ID_CLIENTE`/`NOME_CLIENTE`/`CPF_CNPJ_CLIENTE` nulos). Corrigido com
      `VendaService.atualizarCliente(cupom, cliente)`, chamado tanto ao
      selecionar cliente com cupom já aberto quanto ao criar o cupom com
      cliente pré-selecionado. Isso também **desbloqueia** fidelidade e
      fiado abaixo, que dependiam disso.
- [ ] JWT/OAuth2 via Spring Security substituindo o uso isolado de
      `BCryptPasswordEncoder` (pedido explícito do `CLAUDE.md`, seção
      "Segurança do usuário"). ⚠️ Não há nenhum endpoint HTTP no projeto
      hoje (sem `spring-boot-starter-web`, sem `@RestController`) — o login
      atual é local (BCrypt contra o banco), adequado pra um app desktop.
      Implementar JWT/OAuth2 sem uma superfície HTTP real pra proteger
      seria especulativo; falta decidir **o que** vai expor API primeiro
      (o próprio PDV para o Cactus ERP consumir? um endpoint local pro
      TEF/balança?) antes de desenhar a autenticação.
- [x] **Fidelidade** (2026-08-22) — `FidelidadeService` avalia sob demanda
      (sem tabela de acúmulo/pontos — conta/soma `PDV_VENDA_CABECALHO`
      finalizadas do cliente) contra `CLIENTE.FIDELIDADE_QUANTIDADE`/
      `_VALOR`. Aviso não-bloqueante (`Alert` INFORMATION) no
      `EncerramentoVendaController` ao finalizar venda com cliente que
      atingiu a meta. Limitação conhecida: não há campo de "última vez
      que atingiu a meta", então o aviso repete a cada venda enquanto o
      cliente continuar acima do limiar — resetar a meta é decisão de
      produto ainda não modelada.
- [x] **Fiado** (2026-08-22) — nova tabela `CLIENTE_FIADO_LANCAMENTO`
      (livro-razão, aplicada no MySQL local, mesmo padrão do
      `ESTOQUE_MOVIMENTO`) + `FiadoService`. Identifica venda fiado pelo
      `PDV_TIPO_PAGAMENTO.CODIGO = '010'` (Crediário Loja — o tipo semeado
      mais próximo semanticamente). Plugado em `VendaService.finalizarVenda`:
      valida cliente identificado + limite (`FIADO_VALOR_TETO`, fail-open
      se não configurado) ANTES de gravar qualquer pagamento; registra o
      lançamento DEPOIS de finalizar. Ambos na mesma transação da venda
      (ao contrário de fiscal/impressão, que são deliberadamente
      desacoplados).
- [x] **Devolução/troca pós-fechamento** (2026-08-22) — nova tabela
      `PDV_DEVOLUCAO` (aplicada no MySQL local) + `DevolucaoService`.
      Sempre exige autorização gerencial (sem alçada por valor). Reaproveita
      `EstoqueMovimentoService` (`TipoMovimentoEstoque.D`) pra reentrada de
      estoque. **Cobre só o lado operacional**: não emite documento fiscal
      de devolução (depende do ACBr, não validado) e não decide COMO o
      dinheiro volta pro cliente (dinheiro da gaveta, crédito, estorno de
      cartão — decisão operacional da loja).
- [x] **Tela de devolução** (2026-08-23) — `Devolucao.fxml`/`Controller`,
      modal reaproveitando o padrão visual de Sangria/Suprimento
      (`operacao-caixa.css`). Acessada pela tela de Consulta de Cupom
      (F12 → Consultar Cupom): botão "Devolver" por item, habilitado só
      quando o cupom está FINALIZADO e ainda há quantidade disponível
      (`DevolucaoService.quantidadeDisponivelParaDevolucao`). Pede
      motivo + autorização gerencial antes de confirmar.
- [x] Bug corrigido (2026-08-22): cliente da venda de balcão nunca era
      persistido. `Pdv4Controller` já tinha toda a UI de seleção de
      cliente (F-cliente → `abrirPesquisaCliente()` → `clienteAtual`,
      labels de nome/CPF na tela) e `ConsultaCupomController` já lia
      `cupom.getCliente()` esperando que estivesse preenchido — mas
      `PdvVendaCabecalho.setCliente(...)` nunca era chamado em lugar
      nenhum do fluxo de venda. Corrigido com
      `VendaService.atualizarCliente(cupom, cliente)`. Pré-requisito de
      fidelidade e fiado acima.
- [ ] JWT/OAuth2 via Spring Security — **deliberadamente não implementado**
      nesta rodada, apesar do pedido explícito no `CLAUDE.md`. Não há
      nenhum endpoint HTTP no projeto (sem `spring-boot-starter-web`, sem
      `@RestController`); implementar JWT/OAuth2 sem uma superfície real
      pra proteger seria infraestrutura especulativa. O que existe hoje na
      direção de "auth moderna": `RetaguardaLicencaClient` e
      `TefHiperlinkClient` já usam Bearer token / HTTP puro pra chamadas
      OUTBOUND. Falta decidir o que o PDV vai EXPOR via HTTP (se algo)
      antes de desenhar autenticação de servidor.
- [x] Marketing na tela (2026-08-23) — colunas `CAMINHO_IMAGENS_MARKETING`/
      `MARKETING_ATIVO` já existiam no schema desde antes da fase 8, sem
      nenhum código usando-as; agora mapeadas em `PdvConfiguracao` +
      `MarketingService` (lista imagens da pasta configurada, só quando
      `MARKETING_ATIVO='S'`, nunca lança exceção — testado com
      `@TempDir`, `MarketingServiceTest`). Plugado como slideshow de fundo
      na `TelaBloqueio` (idle da loja — F12→Bloquear), atrás do card de
      senha, trocando de imagem a cada 8s (`Timeline`, mesmo padrão do
      relógio já existente na tela); sem imagens configuradas, a tela
      continua exatamente como antes (fundo em gradiente). Configuração
      pela UI: nova seção "Marketing" na tela
      `ConfiguracaoPeriferico`/`ConfiguracaoPerifericoController`
      (checkbox ativo/inativo + campo de pasta), sem precisar editar o
      banco direto. **Escopo deliberadamente simples**: sem agendamento
      por dia/hora, sem métricas de exibição, sem upload de imagem pela
      UI (pasta preenchida manualmente pelo lojista/suporte) — nenhum
      desses foi pedido e adicionar agora seria especulativo.
- [x] Autorização por papel centralizada (2026-08-23) — dividido em duas
      partes ao investigar, porque eram dois problemas diferentes com o
      mesmo nome:
  - **Regra de negócio** ("quem conta como gerente/supervisor"): já estava
    centralizada em `LoginService.autenticarGerenteSupervisor` — cada
    service (sangria, desconto, devolução, fechamento) só faz um guard
    `gerenteAutorizador == null` local, com mensagem de erro própria por
    contexto (obrigatório sempre vs. só acima de um limite). Isso é
    validação de precondição normal por service, não duplicação real —
    forçar num helper genérico trocaria 3 linhas claras por indireção sem
    ganho. **Decisão: não mexer aqui.**
  - **UI do diálogo de autorização**: essa sim era duplicação real —
    9 controllers (`MenuPrincipalController`, `Pdv4Controller`,
    `RelatorioZController`, `OperacaoCaixaController`,
    `DescontoAcrescimoController`, `DevolucaoController`,
    `RecuperarCupomController`, `TelaBloqueioController`, e
    `LoginController` com uma variante) tinham cada um seu próprio método
    privado de ~35 linhas construindo o mesmo `Dialog<PdvOperador>`
    (login + senha + chamada pro `LoginService`). Extraído pra
    `config/util/AutorizacaoGerencialDialog` (8 dos 9 controllers
    migrados — `LoginController` manteve o layout próprio via
    `VBoxHelper`, por ser a tela de primeira impressão e não valer a pena
    arriscar mudar a aparência sem necessidade).
- [ ] Integração com retaguarda Cactus ERP (API/carga/mensageria/tópicos)
      — não iniciado; o Cactus ERP em si não existe ainda como módulo.

## Decisões em aberto

- Formato exato do payload/endpoint de licenciamento no Cactus ERP —
  `RetaguardaLicencaClient` assume um contrato provisório (documentado na
  própria classe), a confirmar quando o endpoint existir.
- Qual solução TEF a loja vai usar de fato (PayGo, SiTef, ou integração
  direta com uma adquirente) — `TefHiperlinkClient` assume um contrato
  provisório, não plugado em tela nenhuma até essa decisão.
- Se `EstoqueMovimento` deve gerar também baixa automática em sangria/
  suprimento (não deveria — são só movimentos financeiros, não de produto)
  — confirmar antes de implementar caso surja dúvida.
- Se vale criar telas FXML de configuração para balança/impressora/TEF ou
  se a configuração via banco direto é aceitável por enquanto.
