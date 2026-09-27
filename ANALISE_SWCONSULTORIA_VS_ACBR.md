# Análise: biblioteca `swconsultoria` (Samuel Oliveira) vs. ACBr — onde usar cada uma

Pedido do usuário (2026-09-06): levantar se é viável mesclar as bibliotecas Java
`swconsultoria` (NFe/CTe/MDFe) com o ACBr já em uso, através dos três projetos do
workspace (`cactus-pdv`, `cactus-erp-sistema`, `cactus-erp-web`) — usar
`swconsultoria` onde ela cobre, ACBr onde não cobre. Este documento é o
levantamento + a decisão proposta + o plano de implementação. Nenhum código foi
alterado ainda — é a fase de "pensar antes de codar", mesmo padrão já usado
antes de começar a Fase 13 (Loja Virtual) no `CLAUDE.md` raiz.

**Transparência sobre o método**: tudo abaixo marcado com fonte foi confirmado
via pesquisa direta (WebSearch/WebFetch) nesta sessão, não é conhecimento geral
por inferência — a informação teria risco real de estar desatualizada/errada
sem checar (bibliotecas de terceiro mudam, ficam abandonadas, mudam de
maintainer). Onde a pesquisa não deu uma resposta confiável, isso está
registrado explicitamente como incerto, não preenchido por adivinhação.

## 1. O que é a família `swconsultoria`

Não é uma biblioteca única — é uma família de projetos open source **separados**,
todos do mesmo autor (Samuel Oliveira, SW Software e Consultoria, Goiás/BR),
um projeto por tipo de documento fiscal, cada um com seu próprio Maven artifact
sob o groupId `br.com.swconsultoria`. Diferença arquitetural fundamental frente
ao ACBr: são **bibliotecas Java puras**, que falam **direto com o webservice
SOAP da SEFAZ** — sem processo externo, sem protocolo próprio, sem depender de
um executável rodando à parte (nada equivalente ao `ACBrMonitor.exe`/porta TCP
3434 que `cactus-pdv`/`fiscal-service` já usam pra ACBr). Pra um backend 100%
Java como o nosso, isso elimina uma camada inteira de integração.

| Projeto | Maven | Documento | Estrelas GitHub | Última atividade confirmada | Maturidade |
|---|---|---|---|---|---|
| [`Java_NFe`](https://github.com/Samuel-Oliveira/Java_NFe) | `br.com.swconsultoria:java-nfe:4.1.3` | **NFe** + **NFCe** + CCe (Carta de Correção) | 757 | **24/ago/2024** (release 4.1.3) | Alta — 2.157 testes unitários, já com suporte a IBS/CBS (Reforma Tributária) |
| [`Java_CTe`](https://github.com/Samuel-Oliveira/Java_CTe) | `br.com.swconsultoria:java-cte:4.00.14` | **CT-e** + **CT-e OS** | 98 | **21/mar/2026** (schema PL. RTC 2026.001) | Alta — manutenção ativa até *este ano*, já com grupos IBS/CBS automáticos |
| [`Java_MDFe`](https://github.com/Samuel-Oliveira/Java_MDFe) | `br.com.swconsultoria:java-mdfe` | **MDF-e** | 22 | **17/jun/2019** (último release Maven) | **Baixa/abandonado** — README mínimo, sem doc técnica, ~6 anos sem release |
| [`Java_Certificado`](https://github.com/Samuel-Oliveira/Java_Certificado) | `br.com.swconsultoria:java-certificado` | Gestão de certificado digital A1 (.pfx)/A3 (token) | 136 | não verificado nesta sessão | Utilitário de apoio, usado pelos 3 acima |
| `Java-eSocial` | — | eSocial (obrigação trabalhista) | 13 | não verificado | Fora do escopo fiscal de vendas — só citado por completude |
| `Java-Efd-Reinf` | — | EFD-Reinf (obrigação acessória) | 8 | não verificado | Fora do escopo fiscal de vendas — só citado por completude |

**Não existe projeto da família pra NFS-e nem SAT.** Confirmado por varredura
da lista completa de repositórios do autor — nenhum dos dois aparece. Isso não
é uma lacuna de pesquisa, é uma característica real do escopo do projeto: NFe/
NFCe/CTe/MDFe têm um layout **nacional único** definido pela SEFAZ, então uma
biblioteca genérica cobre o Brasil inteiro; NFS-e é fragmentada por ~5.500
prefeituras (cada uma podendo ter um provedor de software municipal diferente,
com XML e regras próprias) — nenhuma biblioteca, nem o próprio ACBr, cobre 100%
disso, só os padrões mais comuns (ABRASF/Padrão Nacional). SAT é tecnologia
paulista específica, já sendo descontinuada.

Licença: **MIT** em todos os projetos verificados — permissiva, compatível com
uso comercial sem restrição, sem exigência de copyleft.

## 2. O que o ACBr cobre, pra comparação

Registrado em detalhe no `CLAUDE.md` raiz (seção "ANÁLISE: MÓDULO COMERCIAL" e
Fases 8/9/11) — resumo aqui só pro contraste: ACBr é uma suíte **Delphi/Pascal**
multiplataforma que cobre **NFe, NFCe, SAT, NFS-e (múltiplos provedores/ADN),
CT-e, MDF-e, e dezenas de outras integrações fora do escopo fiscal** (boleto,
correios, etc.). Acessado a partir de um processo Java (`cactus-pdv`,
`fiscal-service`) via **ACBr Monitor** (processo externo `ACBrMonitor.exe`,
protocolo texto `Objeto.Comando(params)` sobre socket TCP, porta 3434) — essa
camada de transporte (`common.fiscal.AcbrMonitorClient`) e os builders de INI
(`NfeIniBuilder`/`NfceIniBuilder`/`CteIniBuilder`/`NfseIniBuilder`) já existem
no monorepo, portados de `cactus-pdv` (que já validou o protocolo contra uma
instância real em produção, 2026-08-22).

## 3. Comparação lado a lado

| Documento | swconsultoria | ACBr (já integrado) | Recomendação |
|---|---|---|---|
| **NF-e** (modelo 55) | ✅ `java-nfe`, ativo, maduro, já com IBS/CBS | ✅ `NfeIniBuilder`, nunca emitiu de verdade (bloqueio de certificado) | **Trocar pra swconsultoria** — ver seção 4 |
| **NFC-e** (modelo 65) | ✅ mesma lib `java-nfe` | ✅ `NfceIniBuilder`, é o de MAIOR confiança do projeto (testado contra ACBrMonitorPLUS real em produção no `cactus-pdv`, 2026-08-22) | **Manter ACBr** em `cactus-pdv` (não mexer em produção validada); **avaliar depois** pra `fiscal-service`/`loja-virtual-service` (nunca emitiram de verdade) |
| **CT-e** (modelo 57) | ✅ `java-cte`, ativo até mar/2026, já com IBS/CBS | ✅ `CteIniBuilder`, nunca emitiu de verdade | **Trocar pra swconsultoria** — ver seção 4 |
| **MDF-e** | ⚠️ `java-mdfe`, abandonado desde 2019 | Ainda não implementado no monorepo (fora de escopo até agora) | **Não usar swconsultoria** pra isso — se/quando MDF-e entrar em escopo, ir de ACBr (mesmo padrão TCP já validado) |
| **NFS-e** | ❌ não existe | ✅ `NfseIniBuilder`, comando confirmado (2026-09-05), layout de campo ainda incerto | **Só ACBr** — sem alternativa |
| **SAT** | ❌ não existe | Registrado no roadmap, nunca implementado (baixa prioridade, tecnologia em extinção) | **Só ACBr**, se algum dia for implementado |
| **Cancelamento/Inutilização/Carta de Correção (NFe)** | ✅ `java-nfe` cobre nativamente | Não implementado ainda no `fiscal-service` (fora de escopo registrado) | Ganho extra do swconsultoria — fecha esse gap "de graça" |
| **Consulta de status do serviço / distribuição DFe** | ✅ ambas libs cobrem | Possível via ACBr, não implementado | Ganho extra |
| **IBS/CBS (Reforma Tributária, EC 132/2023)** | ✅ já modelado e mantido ativamente nas 2 libs | ❌ nem o motor tributário do `cadastro-base-service` nem os builders ACBr atuais modelam isso — risco já registrado no `CLAUDE.md` raiz | **Argumento forte a favor do swconsultoria** — ver seção 5 |

## 4. Por que trocar NFe/CTe pro swconsultoria faz sentido técnico

Todo o `fiscal-service` (Fases 8/9/11) foi construído **sem nunca ter emitido
um documento real** — o `ACBrMonitor.ini` local tem um certificado que parece
de produção, e o usuário confirmou repetidamente não ter ainda um certificado
de homologação (`BACKLOG.md`, item "emissão real via ACBr Monitor nunca foi
testada"). Ou seja: **trocar a implementação de NFe/CT-e agora não descarta
nenhum trabalho validado em produção** — é trocar código nunca exercitado de
verdade por uma biblioteca que:

- É **Java nativo**, roda dentro do próprio processo Spring Boot — sem
  depender de um `ACBrMonitor.exe` (processo Windows-only) estar de pé, sem
  protocolo texto próprio pra montar/parsear, sem risco de timeout de socket.
  Isso também abre a porta pra rodar em Linux/container no futuro, coisa que a
  dependência de um executável Windows (`ACBrMonitor.exe`) impede hoje.
- Tem uma cobertura de teste real (2.157 testes unitários no `java-nfe`) e
  manutenção ativa e recente (CT-e atualizado em março de 2026, mês passado).
- Já resolve o risco da Reforma Tributária (IBS/CBS) que estava **registrado
  como pendência aberta** desde a Fase 8 (`CLAUDE.md` raiz: "nem `fenix.sql`
  nem o motor tributário atual modelam isso") — a biblioteca já calcula e
  preenche os grupos de IBS/CBS automaticamente.
- Fecha gaps de escopo já registrados como "fora de escopo" no `fiscal-service`
  atual: cancelamento de NF-e, Carta de Correção, DIFAL/ICMS-ST interestadual
  (a verificar se realmente cobre esse último — não confirmado nesta pesquisa).

**Contrapartida honesta**: essa troca ainda não foi testada contra nenhuma
instância real da SEFAZ nesta sessão — a pesquisa confirma que a biblioteca
existe, é madura e é mantida, mas não é uma validação ao vivo (a mesma limitação
que o ACBr atual já tem, então não é uma troca de "testado" por "não testado",
é uma troca de "não testado" por "não testado", só que com uma base de código
mais confiável e melhor alinhada à Reforma Tributária).

## 5. Por que NÃO mexer no `cactus-pdv` agora

`cactus-pdv` já tem NFCe via ACBr **validado contra uma instância real em
produção** (2026-08-22, `AcbrComandoFiscalBuilder`/`AcbrMonitorClient`) — é o
único ponto de emissão fiscal de todo o ecossistema que já foi de fato
exercitado contra o `ACBrMonitorPLUS`. Trocar isso por `java-nfe` eliminaria a
dependência do processo `ACBrMonitor.exe` também ali (ganho real de
arquitetura), mas é uma migração de código **que já funciona em produção**,
por uma alternativa nunca testada nesse contexto — risco de regressão real,
sem necessidade urgente (a decisão original de usar ACBr no PDV já foi tomada
e validada, não há problema em aberto que a troca resolveria). **Recomendação:
não mexer agora** — registrar como possibilidade de simplificação futura (um
processo a menos rodando por caixa), não como parte desta mudança.

## 6. Papel de cada um dos 3 projetos do workspace

| Projeto | Precisa mudar? | Motivo |
|---|---|---|
| **`cactus-erp-web`** | Não | Frontend não fala com SEFAZ nem com ACBr diretamente — só consome a API REST do `fiscal-service`. Troca de biblioteca no backend é invisível pra ele (mesmos endpoints, mesmos DTOs de resposta). |
| **`cactus-pdv`** | Não, por ora | NFCe via ACBr já validado em produção — ver seção 5. Fica registrado como possibilidade futura, não decidido agora. |
| **`cactus-erp-sistema` (`fiscal-service`)** | **Sim** | NFe (Fase 8) e CT-e (Fase 11) nunca emitiram de verdade — trocar a implementação interna (de "montar INI + falar com ACBrMonitor via TCP" pra "chamar `java-nfe`/`java-cte` direto") sem nenhum custo de regressão, com ganho real de arquitetura e de cobertura de Reforma Tributária. NFS-e (Fase 9) continua em ACBr (sem alternativa). NFC-e do `fiscal-service`/`loja-virtual-service` (Fase 13) fica como decisão em aberto — ver seção 8. |

## 7. Plano de implementação (faseado, quando for autorizado a codar)

Nenhuma destas fases foi iniciada — é o plano, não a execução.

1. **Adicionar as dependências Maven** `br.com.swconsultoria:java-nfe:4.1.3` e
   `br.com.swconsultoria:java-cte:4.00.14` ao `fiscal-service/pom.xml` (ou a um
   módulo `common` novo, se `java-certificado` for reaproveitado pra unificar
   a leitura do `.pfx` entre os dois). Verificar conflito de dependência
   transitiva (a lib usa JAXB/SOAP — checar contra o que o Spring Boot 4.1 já
   traz, mesmo tipo de achado real já visto com Flyway/EntityScan/RestClient
   ao longo do projeto).
2. **Criar um adaptador novo** (`NfeSwConsultoriaService`/`CteSwConsultoriaService`
   ou nome equivalente) implementando a MESMA interface de negócio que
   `NotaFiscalEletronicaService`/`CteCabecalhoService` já expõem hoje — troca
   de implementação por trás do mesmo contrato, sem mudar `Controller`/`DTO`/
   nada que o frontend ou outros serviços consomem.
3. **Não apagar os builders ACBr existentes** (`NfeIniBuilder`/`CteIniBuilder`)
   — continuam existindo como referência/fallback documentado, e
   `NfceIniBuilder`/`NfseIniBuilder` continuam em uso normal (NFCe/NFS-e não
   migram).
4. **Configuração de certificado**: `java-nfe`/`java-cte` esperam o certificado
   A1 (.pfx) direto (arquivo + senha) ou A3 (token, storage do Windows) — mesma
   informação que `EmpresaConfiguracaoFiscal` já modela
   (`certificadoAlias`/validade), só muda ONDE esse certificado é consumido
   (biblioteca embutida, não mais um `.ini` externo do `ACBrMonitor`).
5. **Validação**: continua bloqueada pelo mesmo motivo de sempre — sem
   certificado de homologação, nenhuma emissão real (nem via ACBr, nem via
   swconsultoria) pode ser testada. O código fica pronto e documentado como
   "nunca invocado contra a SEFAZ real nesta sessão", mesmo padrão de honestidade
   já seguido em todas as Fases 8/9/11.
6. **Atualizar `CLAUDE.md` raiz e `BACKLOG.md`** registrando a troca de
   implementação, o motivo, e o novo nível de confiança (mais alto que antes,
   por causa da cobertura de testes/Reforma Tributária da lib, mas ainda sem
   validação ao vivo).

## 8. Perguntas em aberto — decisão do usuário

1. **`cactus-pdv` migra também, ou fica só no ACBr por ora?** Recomendação
   registrada na seção 5 é "não mexer agora" — mas se o objetivo de longo prazo
   é eliminar o `ACBrMonitor.exe` como dependência de infraestrutura em TODO o
   ecossistema (não só no `fiscal-service`), essa migração precisaria entrar
   no roadmap do `cactus-pdv` (repositório próprio, fora deste monorepo).
2. **MDF-e**: fora de escopo dos dois lados hoje (nem ACBr nem swconsultoria
   têm implementação real no projeto) — só registrado aqui pra não ser
   esquecido quando o assunto entrar em pauta: `java-mdfe` está abandonado,
   ACBr (`ACBrMDFe`, mesmo padrão TCP) seria o caminho, não swconsultoria.
3. **NFC-e do `fiscal-service`/`loja-virtual-service`** (Fase 13, venda de
   balcão fora do PDV): hoje usa `NfceIniBuilder` (ACBr, alta confiança por
   herdar o layout já validado no `cactus-pdv`). Trocar por `java-nfe` uniformizaria
   com a decisão de NFe/CT-e (uma lib só pra tudo que não é NFS-e/SAT), mas
   perderia o lastro de "layout já testado contra instância real" que o
   builder ACBr atual tem (mesmo que o comando em si nunca tenha sido
   disparado neste projeto). Decisão não tomada — registrar preferência
   quando o usuário confirmar o plano geral.

## 9. Resumo executivo (pra decisão rápida)

- **Adotar `java-nfe` (NFe+NFCe+CCe) e `java-cte` (CT-e)** no `fiscal-service`
  no lugar dos builders ACBr atuais pra esses 3 documentos — ganho real
  (Java nativo, sem processo externo, Reforma Tributária já coberta, mais
  maduro), zero custo de regressão (nada disso foi validado em produção ainda).
- **Manter ACBr** pra NFS-e (sem alternativa) e SAT (sem alternativa, baixa
  prioridade).
- **Não usar `java-mdfe`** (abandonado) — se MDF-e entrar em escopo, ACBr.
- **Não mexer no `cactus-pdv`** agora — NFCe via ACBr lá já é produção
  validada, sem motivo pra trocar.
- **`cactus-erp-web`**: nenhuma mudança necessária.
