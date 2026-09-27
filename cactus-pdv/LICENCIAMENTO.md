# Licenciamento e trava de segurança (mensalidade)

Este documento descreve o desenho da trava de segurança do Cactus PDV:
o mecanismo que controla se o estabelecimento está com a mensalidade em dia
e pode operar o sistema. Segue o padrão adotado por players do mercado de
PDV/ERP no Brasil (Linx, TOTVS, Bling, ContaAzul): verificação online quando
possível, com uma janela de tolerância offline para não travar a loja no
meio de uma venda por causa de uma queda de internet.

Este é um documento de **design** — a implementação (Passo 0.5 do
`ROADMAP.md`) ainda não foi feita neste repositório.

## Por que existe

O Cactus PDV é vendido como assinatura (mensal/semestral/anual, campo
`PDV_CONFIGURACAO.PLANO`). Sem uma trava, um cliente inadimplente continua
usando o sistema indefinidamente — inviabilizando o modelo de negócio da
empresa que mantém e atualiza o software.

## Modelo de dados (já existe, nenhuma tabela nova necessária)

A tabela `PDV_CONFIGURACAO` (ver `PDV.md`) já tem as colunas necessárias:

| Coluna | Uso |
|---|---|
| `PLANO` | `M`=Mensal, `S`=Semestral, `A`=Anual |
| `PLANO_VALOR` | valor contratado |
| `PLANO_SITUACAO` | `A`=Ativo, `I`=Inadimplente, `B`=Bloqueado |

E `DISPOSITIVO_CAIXA` (hostname + MAC address) já vincula fisicamente uma
máquina a um `PDV_CAIXA`, servindo de base para o vínculo de licença por
dispositivo (evita clonar a instalação em máquina não licenciada).

## Estados e comportamento

- **`A` — Ativo**: uso normal, sem restrições.
- **`I` — Inadimplente**: uso liberado, mas exibe um banner de aviso
  persistente na tela principal do PDV (ex: "Pagamento pendente — regularize
  para evitar bloqueio"). Não bloqueia a operação: a loja não pode ficar
  impedida de vender no meio do expediente por atraso recém-detectado. Grava
  esse estado no log de auditoria.
- **`B` — Bloqueado**: impede abrir um **novo** movimento de caixa (login e
  `IniciarMovimentoController` recusam). Um movimento **já aberto** pode
  continuar até o fechamento — não trava dinheiro em caixa no meio da
  operação. Consultas a vendas/relatórios já feitos continuam acessíveis.

## Fluxo de verificação

1. **No boot do PDV** e **a cada abertura de movimento de caixa**, o
   `LicencaService` consulta a situação do plano:
   - localmente, em `PDV_CONFIGURACAO.PLANO_SITUACAO`;
   - remotamente, via chamada a um endpoint de licenciamento exposto pela
     retaguarda (Cactus ERP) — ainda a ser criado nesse outro módulo.
2. O resultado da verificação remota é **cacheado localmente com
   timestamp** (arquivo ou tabela local, não editável pelo operador do PDV
   sem detecção — ideia: hash/assinatura simples do payload). Isso garante
   que a última situação conhecida sobrevive a quedas de internet.
3. **Janela de tolerância offline**: se o PDV não conseguir contatar a
   retaguarda por mais que um número configurável de dias (sugestão inicial:
   5 a 7 dias), a situação é automaticamente rebaixada para `B` (Bloqueado)
   por segurança — evita que alguém desligue a internet do PDV
   propositalmente para burlar a trava.
4. **Vínculo por dispositivo**: a verificação remota inclui o
   hostname/MAC de `DISPOSITIVO_CAIXA`, para impedir reaproveitar a mesma
   licença em outra instalação não autorizada.
5. **Auditoria**: toda transição de estado (`A→I`, `I→B`, `B→A` etc.) grava
   data/hora, dispositivo e situação anterior/nova — mesmo padrão já usado
   em `PDV_VENDA_DETALHE_HISTORICO` para trilha PAF-NFC-e.

## Onde entra no código (quando for implementado)

- `service/LicencaService.java` — expõe `podeOperar()` e
  `podeAbrirCaixa()`, encapsula a lógica de estado + cache + janela de
  tolerância. Sem interface, seguindo o padrão de `service/` do projeto.
- Gate no `StageManager` ou no `LoginController`/`IniciarMovimentoController`
  chamando `LicencaService` antes de liberar a tela — **não** espalhar a
  checagem por múltiplos controllers.
- Endpoint de validação do lado da retaguarda (Cactus ERP) — fora deste
  repositório. Até esse endpoint existir, o `LicencaService` opera apenas em
  modo local (lê `PLANO_SITUACAO` direto do banco, situação alterada
  manualmente/por suporte).

## Fora de escopo deste documento

- Cobrança/gateway de pagamento (fica no Cactus ERP).
- Geração de boleto/nota da mensalidade.
- Portal de autoatendimento do cliente para consultar/regularizar situação.
