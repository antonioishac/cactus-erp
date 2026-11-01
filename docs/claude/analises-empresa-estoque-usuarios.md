<!-- Conteúdo movido do CLAUDE.md original em 2026-10-31, sem nenhuma alteração. Linhas originais: 192-253 -->

# ANÁLISE: EMPRESA MULTI-FILIAL
`Empresa` não pode ser um CRUD raso (razão social/CNPJ/endereço). Pra ser competitivo:
- **Matriz + filiais**: cada filial é um `Empresa` com CNPJ próprio (raiz igual à matriz, sufixo diferente — regra da Receita), IE própria, endereço próprio; razão social pode ser compartilhada.
- **Regime tributário com histórico**: CRT (Simples/Presumido/Real) pode mudar ano a ano — não é só um campo fixo, precisa de `EmpresaRegimeTributario` (empresa, anoCalendario, crt) pra manter histórico auditável (relevante pro cálculo de imposto retroativo).
- **Configuração fiscal por filial**: certificado digital (A1 arquivo / A3 hardware, validade, alias), série e numeração de NFe/NFCe/NFSe por filial, ambiente homologação/produção, CSC (Código de Segurança do Contribuinte, usado no QRCode da NFC-e) — isso é o que a integração real com SEFAZ (via ACBr, que o PDV já usa) precisa.
- **CNAE** principal + secundários (relevante pra retenção de ISS/tributação por atividade).
- **Contador responsável** (nome, CRC, contato) — como no `fenix.sql` (`CONTADOR`), pra troca de arquivos com a contabilidade.
- Isso não é tudo Fase 1 — mas o cadastro de `Empresa` precisa nascer com esses relacionamentos previstos (mesmo que as telas de configuração fiscal avançada venham depois), porque `UsuarioPapelEmpresa`, `ConfiguracaoTributaria` e a futura emissão fiscal dependem de `Empresa` ser o conceito certo desde o início.

# ANÁLISE: ESTOQUE NÍVEL ENTERPRISE
O módulo de Estoque (fase futura, `controle-estoque-service`) precisa competir com sistemas de mercado — não é só "quantidade no produto" (que é inclusive o que o PDV faz hoje, de forma simplificada, com `PRODUTO.QUANTIDADE_ESTOQUE`). O que um sistema de estoque de ponta cobre:
- **Múltiplos depósitos/armazéns por filial** (`Deposito`) — saldo é sempre por **produto × depósito**, nunca um campo único no produto. `Produto` no cadastro-base guarda no máximo uma política padrão (`estoqueMinimo`/`estoqueMaximo` como sugestão), o saldo real vive inteiramente no `controle-estoque-service`.
- **Custo médio ponderado móvel** — recalculado a cada entrada (obrigatório fiscalmente na maioria dos regimes no Brasil); FIFO como alternativa configurável por empresa.
- **Reserva de estoque** — quantidade comprometida por pedido de venda ainda não faturado, sem sair do saldo físico mas indisponível pra nova venda.
- **Transferência entre depósitos/filiais** — com estado "em trânsito" (saiu da origem, ainda não chegou no destino).
- **Inventário físico / balanço** — contagem cega ou por saldo, com apontamento por contador e ajuste automático com justificativa (gera `EstoqueMovimento` tipo ajuste).
- **Lote e validade** — obrigatório pra alimentício/farma (rastreabilidade, FEFO — first-expire-first-out).
- **Número de série** — pra eletrônicos/ativos que precisam rastreio unitário.
- **Curva ABC** — classificação de relevância/giro pra priorização de compra e contagem.
- **Unidade de compra × estoque × venda com fator de conversão** — ex: compra em caixa de 12, estoca em unidade, vende em unidade ou caixa fechada (o cactus-pdv já tem esse conceito parcialmente via `PRODUTO_FAIXA_PRECO`, mas isso é preço por quantidade, não conversão de unidade).
- **Kit / produto composto** — produto vendido como conjunto que baixa estoque dos componentes na venda (BOM simples, não é manufatura completa).
- Todo saldo muda **exclusivamente** via `EstoqueMovimento` (ledger append-only, no padrão que o próprio PDV já usa em `ESTOQUE_MOVIMENTO`) — nunca um `UPDATE` direto de saldo.

# ANÁLISE: HIERARQUIA DE USUÁRIOS/OPERADORES/GERENTES DO PDV
O cactus-pdv é **genérico** — atende bar, restaurante, supermercado, loja, autocenter, comércio em geral (por isso mesa/comanda/delivery não são casos de borda, são núcleo do produto). A hierarquia de quem opera o PDV:

- `COLABORADOR.NIVEL_AUTORIZACAO` (`char(1)`): **G**=Gerente | **S**=Supervisor | **O**=Operador | **E**=Entregador — nível fixo, um por funcionário, embutido no próprio cadastro (não é papel dinâmico). `COLABORADOR` também carrega `COMISSAO_VISTA`/`COMISSAO_PRAZO` (comissão por venda à vista/a prazo — vale pra vendedor de loja, garçom, etc, qualquer vertical) e `ENTREGADOR_VEICULO` (C=Carro/M=Moto/B=Bicicleta/A=Aplicativo — só relevante quando nível=E, essencial pro delivery de bar/restaurante).
- `PDV_OPERADOR` é só o login (liga `ID_COLABORADOR` a `LOGIN`/`SENHA`) — quem loga no terminal.

**Padrão de autorização gerencial** (repete em várias tabelas, sempre como FK pra `PDV_OPERADOR`, nunca um flag solto — é sempre o login de um gerente/supervisor real que autoriza):
| Onde | Obrigatoriedade |
|---|---|
| `PDV_SANGRIA.ID_GERENTE_AUTORIZADOR` | Condicional — só quando o valor excede o limite configurado |
| `PDV_SUPRIMENTO.ID_GERENTE_AUTORIZADOR` | Quase nunca (operação de baixo risco) |
| `PDV_CONFERENCIA_CAIXA.ID_GERENTE_AUTORIZADOR` | **Sempre obrigatório** |
| `PDV_DEVOLUCAO.ID_GERENTE_AUTORIZADOR` | **Sempre obrigatório** |
| `PDV_MOVIMENTO.ID_GERENTE_SUPERVISOR`/`ID_GERENTE_FECHAMENTO` | Abertura/fechamento de turno |
| Desconto/acréscimo acima do limite (`PDV_CONFIGURACAO`) | Condicional, e pode ser bloqueado até pra gerente (`PERMITE_DESCONTO_ACIMA_LIMITE=N`) |

Ou seja: **operador faz a ação; ação de risco acima de um limiar exige login de um superior que autoriza** — não é um `@PreAuthorize` estático, é uma aprovação nominal registrada no próprio registro da transação.

## O que isso muda no cadastro-base
- `Colaborador` (Fase 1a ✅ construído — especialização de `Pessoa` como `Cliente`) já nasceu com `comissaoVista`/`comissaoPrazo`/`entregadorVeiculo`, além dos campos de `Pessoa` e do RH básico (`Cargo`/`Setor`/matrícula/admissão/CTPS).
- Meu RBAC (`Papel`/`Funcionalidade`/`UsuarioPapelEmpresa`) é mais flexível que o enum fixo do PDV — mas hoje só semeei os papéis `ADMIN`/`OPERADOR`. Faltam `GERENTE`, `SUPERVISOR`, `ENTREGADOR` no seed (Fase 1a), e uma tabela de conversão `NIVEL_AUTORIZACAO` (PDV) ↔ `Papel` (ERP) pra quando a sincronização de `Colaborador`/`PDV_OPERADOR` (Fase 6) precisar provisionar um operador novo no PDV a partir do cadastro do ERP, e vice-versa.
- O padrão "aprovação de superior acima de um limiar" precisa virar um conceito reaproveitável, não só do PDV — `gestao-compras-service` (aprovar pedido de compra) e `controle-estoque-service` (aprovar ajuste de estoque acima de X) vão precisar do mesmo mecanismo: uma FK nominal pro aprovador (não um boolean), condicional a um limite configurável, com opção de "nem gerente pode passar" quando a política da empresa proibir.

## Validação cruzada com o RBAC e o Cadastro Base do `fenix.sql`
Cruzei meu RBAC (`Papel`/`Funcionalidade`/`Usuario`/`UsuarioPapelEmpresa`) e `Colaborador`/`Empresa` com o desenho equivalente do `fenix.sql` — achados reais, não só confirmação:

- **`USUARIO` do fenix é 1:1 obrigatório com `COLABORADOR`** (`ID_COLABORADOR NOT NULL`) — todo usuário do sistema é necessariamente um funcionário cadastrado. O meu `Usuario` hoje é solto, sem nenhum vínculo com `Colaborador`/`Pessoa`. Corrigir: `Usuario.colaboradorId` (FK obrigatória) na Fase 1a, depois de `Colaborador` existir. **Superado (2026-08-29)**: `Usuario` saiu do cadastro-base pro `seguranca-service` (ver EXTRAÇÃO DO `seguranca-service`) — `colaboradorId` ficou **opcional** (não obrigatório, contas de sistema/suporte não precisam de RH por trás) e virou referência solta sem FK de banco (cross-serviço, schemas separados), não dá mais pra ser uma FK de verdade mesmo que quiséssemos.
- **`USUARIO.ADMINISTRADOR`** (`char(1)`) — flag separada do papel, bypassa toda checagem granular. Útil como "escape hatch" de suporte/administração do sistema. **Decidido (2026-08-23)**: não adicionar um `Usuario.superAdmin` equivalente — o papel `ADMIN` já é semeado com TODAS as funcionalidades (`CROSS JOIN funcionalidade` na V7) e cobre o mesmo caso de uso, com a vantagem de ser auditável (aparece explicitamente em `usuario_papel_empresa`, não é um flag invisível que bypassa `@PreAuthorize`). Se um cenário real de emergência aparecer (ex: perder acesso ao próprio usuário ADMIN), a saída é operação de banco direta, não um flag de aplicação.
- **`PAPEL_FUNCAO`** do fenix não é join puro: tem `HABILITADO`/`PODE_INSERIR`/`PODE_ALTERAR`/`PODE_EXCLUIR` — uma `Funcao` = uma tela/botão, e o papel ganha CRUD granular sobre ela via 4 flags. O meu modelo (uma `Funcionalidade` = uma ação específica, ex: `CADASTRO_CLIENTE_CRIAR`) cobre o mesmo resultado de forma mais flexível (permite ações fora do CRUD clássico, tipo "aprovar"/"exportar") — validado, mantenho o meu, mas registrando que both são desenhos de mercado válidos.
- **`fenix.PAPEL` é 1:1 com `USUARIO`** (`ID_PAPEL NOT NULL`, singular) — um usuário, um papel. O meu é N:N por empresa (`UsuarioPapelEmpresa`), mais flexível (usuário pode acumular papéis) — mantenho o meu, mas é uma decisão deliberada, não descoberta agora.
- **`Colaborador` do fenix é RH básico de verdade**: `CARGO` (nome, descrição, salário, `CBO_1994`/`CBO_2002` — Classificação Brasileira de Ocupações), `SETOR` (departamento), `MATRICULA`, `DATA_ADMISSAO`/`DATA_DEMISSAO`, CTPS completa (número/série/data expedição/UF). Nenhum desses existe no que eu planejei pra `Colaborador` (que só tinha os campos do PDV: comissão/entregador). Entra tudo na Fase 1a.
- **`Vendedor`** é especialização própria de `Colaborador` (nem todo colaborador vende), com `ComissaoPerfil` reaproveitável (perfil de comissão) OU comissão+meta direto. Mais rico que o `comissaoVista`/`comissaoPrazo` flat que peguei só do PDV — **status**: `Colaborador` foi construído (Fase 1a) só com o flat `comissaoVista`/`comissaoPrazo` do PDV, deliberadamente sem `Vendedor`/`ComissaoPerfil` — fica como enriquecimento opcional em aberto, sem prazo definido (não é bloqueio de nenhuma fase do roadmap atual).
- **`PessoaEndereco`** do fenix tem `PRINCIPAL`/`ENTREGA`/`COBRANCA`/`CORRESPONDENCIA` (um endereço pode servir vários papéis) — meu `Endereco` só tem `principal`. Adicionar os outros 3 flags.
- **`Empresa`** do fenix tem `EMAIL`, `SITE`, `CONTATO` (nome do contato), `DATA_CONSTITUICAO`, `TIPO` (Matriz/Filial **explícito**, não inferido do sufixo do CNPJ como eu ia fazer), `INSCRICAO_JUNTA_COMERCIAL`+data, `CEI` (Cadastro Específico do INSS), `CODIGO_CNAE_PRINCIPAL`. Nenhum desses está no meu `Empresa` atual — entra na Fase 1c.
- **`TabelaPreco`/`TabelaPrecoProduto`** é um mecanismo **diferente e complementar** ao `ProdutoFaixaPreco` do PDV: múltiplas tabelas de preço por perfil/canal (ex: "Varejo", "Atacado", "Cliente VIP"), cada uma com um preço (ou coeficiente multiplicador) por produto — não é faixa de quantidade nem vigência de data. Preciso dos dois mecanismos, não só um: `TabelaPreco` (Fase 1b, junto do resto de produto) pra segmentar preço por canal/perfil de cliente, `ProdutoFaixaPreco` pra desconto por quantidade/promoção dentro de uma tabela.

## Transparência: o que eu NÃO validei ainda
Pra não fingir 100% quando não é: as tabelas de `GED_*`, `FIN_*`, `OS_*`, `NFE_*`, `NFCE_*`, `NFSE_*`, `CTE_*`, `ESTOQUE_REAJUSTE`/`GRADE`/`COR`/`TAMANHO`/`SABOR`/`MARCA` do `fenix.sql`, e as tabelas puramente operacionais do PDV (`PDV_VENDA_*`, `CONTA_ABERTA*`, `PDV_MOVIMENTO`/`SANGRIA`/`SUPRIMENTO`/`CONFERENCIA_CAIXA*`, `ESTOQUE_MOVIMENTO`, `CLIENTE_FIADO_LANCAMENTO`) eu só li uma vez, de forma geral, sem o mesmo cruzamento campo a campo que fiz em Produto/Cliente/Empresa/Colaborador/RBAC/Vendas/Compras. Elas pertencem a fases futuras (GED já feito na Fase 4; Fiscal/OS ainda não) — vou validar com esse mesmo rigor **antes de começar a construir cada uma**, não deixar pra descobrir no meio do caminho. Também não tenho acesso a schema real de SAP/TOTVS/Senior/Linx — quando cito esses sistemas é padrão de mercado por inferência (o que o `fenix.sql` já faz + conhecimento geral de domínio fiscal/ERP brasileiro), não validação documental de verdade. `COMPRA_*` e `VENDA_*` foram validados campo a campo nas Fases 3 e 7, respectivamente.

