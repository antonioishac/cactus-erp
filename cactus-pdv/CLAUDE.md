# Cactus PDV
O Cactus pdv é um projeto do módulo cactus erp.
O cactus pdv é um sistema para atender praticamente todo o comércio para utilização de frentes de caixa
e controle de supermercados, bares, restaurantes, pizzarias, lanchonetes, farmácias, etc, ou seja
o comércio em geral e também oficinas mecanicas, auto elétricas, etc.

Esse sistema precisa estar de acordo com as especificações do governo para emissão de cupom fiscal.
NFC-e, nota fiscal eletrônica, SAT, SEFAZ e as outras normas que o governo do Brasil exige.

Esse sistema também precisa ler os códigos de balanças e alimentar as balanças com as atualizaçoes e novos cadastrod de produtos, exemplo, toledo, filizola, etc.

A integração com balança cobre **dois cenários distintos** (não confundir):
1. **Etiqueta pré-impressa** — o cliente já pesou o produto numa balança da seção
   (com etiqueta impressa, código de barras com peso/valor embutido) e leva até o
   caixa. O operador só passa o código no leitor, igual um produto qualquer.
2. **Pesagem ao vivo no caixa** — o produto NÃO tem etiqueta (ex: cliente chegou
   com bananas soltas, sem passar pela balança da seção). O operador busca o
   produto pelo NOME/código no PDV (sem digitar peso nenhum, porque ele não
   sabe o peso ainda), coloca o item na balança que fica ligada no próprio
   terminal de caixa, e o PDV precisa ler o peso ATUAL da balança em tempo
   real (comunicação ao vivo balança↔PDV) pra calcular a quantidade e o total
   do item automaticamente — sem o operador digitar nada manualmente.

Ver `ROADMAP.md` fase 7 (seção "Balança") pro estado de cada um desses dois
fluxos — **ambos implementados no código** (2026-08-23), **100% por
teclado, sem botão nenhum** (PDV não pode depender de mouse — feedback
direto do usuário, uma versão anterior com botão dedicado foi rejeitada e
removida): pesar é consequência automática de resolver um produto vendido
por peso (`BalancaService.vendidoPorPeso`), não uma ação separada que o
operador precisa lembrar de acionar.
- Código digitado/bipado + ENTER (`Pdv4Controller.addProduct`) → pesa
  sozinho se o produto for vendido por peso.
- Sem código nenhum (produto sem GTIN/código de balança cadastrado — caso
  real testado) → F11 busca por nome, ENTER seleciona o primeiro
  resultado direto, e se for vendido por peso o produto é resolvido pelo
  ID (`PesquisarProdutoDTO`), sem depender de código nenhum.
- `BalancaService.lerPesoAoVivo` (delega pra `BalancaSerialService`) faz a
  leitura de verdade.

⚠️ **"Vendido por peso" ≠ "fracionável"** (bug real corrigido 2026-08-23:
produto vendido por LITRO tentava ler a balança e travava). Fracionável
(admite quantidade com casas decimais) é KG, LT, GR, ML; vendido por peso
de verdade (unidade de MASSA, a única coisa que faz sentido pesar numa
balança de checkout) é só KG/GR — ver `ProdutoUnidade.ehUnidadeDePeso()`.

⚠️ Ressalva que continua valendo pros dois cenários: o protocolo serial
(comando ENQ genérico Toledo) **não foi testado contra balança física** —
só dá pra confirmar de verdade com o hardware na mão.

Módulo de frente de caixa (PDV) do Cactus ERP. Aplicação desktop JavaFX rodando
dentro de um processo Spring Boot, com persistência em MySQL. Atende varejo em
geral (mercados, bares, restaurantes, farmácias) com suporte a venda de balcão,
mesas/comandas/delivery e conformidade fiscal brasileira (NFC-e/SAT/MFE).

O DDL completo do banco de dados está documentado em `PDV.md` — consulte esse
arquivo antes de criar ou alterar entidades, já que o schema é a fonte de
verdade (`spring.jpa.hibernate.ddl-auto=none`, nada é gerado automaticamente).

O Sistema Cactus PDV será o sistema de frente de caixa e o sistema Cactus ERP vai ser a retaguarda onde será desenvolvido outros módulos.
O sistema cactus ERP deve estar preparado para receber os dados que virão do Cactus ERP, Via consumo de API, Carga, 
Mensageria, consumidor e produtor de tópicos, ou seja, o que tiver de mais moderno de envio e sincronização de dodos e informações.

# Stack de desenvolvimento
Esse sistema iniciou-se com as ferramentas, Java 21, Spring Boot, JavaFX, JPA Hibernate e Banco de dados MySql.
- **Java 21**
- **Spring Boot 3.5** (Spring Data JPA, Spring Security Crypto — usado apenas
  para `BCryptPasswordEncoder`, não há filtro de autenticação web)
- **JavaFX 21.0.2 + FXML** para a interface desktop
- **MySQL** via `mysql-connector-j`, Hibernate/JPA
- **Ikonli (Material Design 2) + FontAwesomeFX** para ícones
- **jSerialComm** (leitura serial de balança, fase 7 — `BalancaSerialService`)
- **jackson-databind** (JSON — `RetaguardaLicencaClient`, `TefHiperlinkClient`)
- **Maven** (`javafx-maven-plugin` para empacotar/rodar a aplicação desktop)
- Sem Lombok — entidades usam getters/setters escritos manualmente
- Suíte de testes iniciada em 2026-08-23 (`spring-boot-starter-test` no
  `pom.xml`, `src/test/java`) — ainda pequena, hoje só
  `EmissaoFiscalServiceTest` (regex de extração de `ChDFe`/`NProt`, ver
  `ROADMAP.md` fase 6). Sem padrão de integração/contexto Spring definido
  ainda — usar JUnit 5 puro (+ AssertJ, já incluído) pra lógica isolada
  antes de justificar subir `@SpringBootTest`.
- Sem `spring-boot-starter-web` — não há endpoint HTTP no projeto (só
  clientes HTTP outbound via `java.net.http.HttpClient`, sem dependência
  extra). É por isso que JWT/OAuth2 (pedido abaixo) segue não implementado
  — não há superfície de servidor pra proteger ainda.

# Segurança do usuário
Para segurança do usuário, precisamos colocar o spring security com autenticação JWT ouath2+, o que tiver de mais modermo referente as essas tecnologias

# Segurança do sistemas
O sistema precisa ter uma trava de segurança, esse sistema, por ser um PDV, frente de caixa, precisa ser controlado se o usuário pagou ou não a mensalidade para poder
estar utilizando ou não o sistema, isso garante para a minha empresa que está desenvolvendo o sistema a manutenção e atualização.

Desenho completo (estados, fluxo de verificação online/offline, tolerância de
dias sem contato com a retaguarda, vínculo por dispositivo) documentado em
`LICENCIAMENTO.md`. Estados usam a coluna já existente
`PDV_CONFIGURACAO.PLANO_SITUACAO` (`A`=Ativo, `I`=Inadimplente, `B`=Bloqueado)
— não criar uma tabela/coluna nova para isso.

## Decisões técnicas travadas

Escolhas já fechadas para as fases 6 e 7 (evitar reabrir a discussão a cada
sessão de trabalho):

- **Fiscal**: **ACBr Monitor** como motor único (NFC-e principal, SAT/MFE em
  contingência), integrando via `PDV_CONFIGURACAO.ACBR_MONITOR_ENDERECO`/
  `ACBR_MONITOR_PORTA`, que já existem no schema para esse fim. APIs fiscais
  em nuvem (TecnoSpeed, Focus NFe, eNotas etc.) ficam de fora do MVP — exigem
  internet no momento da venda, o que não é aceitável para contingência de
  PDV.
- **TEF**: prioridade 1 é **TEF Hiperlink** (protocolo HTTP, padrão atual do
  mercado com as principais adquirentes — Stone, Cielo, Rede, GetNet,
  PagSeguro). **TEF Dial** (CliSiTef sobre modem) fica como modo legado de
  compatibilidade via o mesmo campo `PDV_CONFIGURACAO.TEF_TIPO_GP`. TECBAN
  fora de escopo (é rede de saque, não TEF de checkout). ⚠️ **Reabrir se
  necessário (2026-08-23)**: pesquisa pra escolher entre SiTef/PayGo achou
  que o kit de integração oficial de AMBOS é uma **DLL nativa Windows**
  (`CliSiTef.dll` / DLL própria do Pay&Go Web), não uma API HTTP local como
  o nome "Hiperlink" sugeria — a integração real provavelmente exige JNA,
  não `HttpClient`. Ver `ROADMAP.md` fase 7 (`TefHiperlinkClient`) pro
  detalhe e o bloqueio de cadastro/homologação comercial pendente.
- **Balanças**: prioridade **Toledo + Filizola** (líderes de mercado no
  varejo brasileiro), depois Urano se necessário. Ver
  `PDV_CONFIGURACAO_BALANCA.MODELO` no `PDV.md` para a enumeração completa
  já prevista no schema.
- **Licenciamento/trava de mensalidade**: antecipado para logo após a
  consolidação atual (não esperar a fase 8) por ser crítico para o negócio.
  Ver `LICENCIAMENTO.md`.

Acompanhamento do backlog e do que já foi entregue por fase: `ROADMAP.md`.

# Banco de dados atual do projeto
Segue o DDL do banco de dados atual do projeto em desenvolvimento.

## Fases de desenvolvimento

Do mais simples ao mais complexo. Status real (checado em 2026-08-22 contra
o código existente — ver `ROADMAP.md` para o detalhamento):

1. **Fundação** ✅ implementada — login/sessão de operador, abertura/
   fechamento simples de caixa, cadastro básico de produto/cliente, venda de
   balcão sem fiscal, pagamento único.
2. **Operação de caixa completa** ✅ implementada — múltiplas formas de
   pagamento, desconto/acréscimo com alçada gerencial, sangria/suprimento,
   cancelamento com auditoria, conferência de caixa no fechamento.
3. **Catálogo avançado de produtos** ✅ implementada — grupos/subgrupos/
   unidades, faixas de preço por quantidade (atacarejo), galeria de imagens,
   busca por GTIN/código de balança.
4. **Mesas, comandas e delivery** ✅ implementada — conta aberta como camada
   intermediária, lançamento em mesa/comanda, conversão em venda fiscal,
   delivery.
5. **Estoque como livro-razão** ✅ implementada e tabela aplicada no MySQL
   local (2026-08-22) — `EstoqueMovimentoService` é o único ponto que deve
   alterar `PRODUTO.QUANTIDADE_ESTOQUE`, plugado em
   `VendaService.finalizarVenda` e em `DevolucaoService` (fase 8).
5.5. **Licenciamento / trava de mensalidade** ✅ implementada, incluindo
   cache local + janela de tolerância offline (2026-08-22) —
   `LicencaService` tenta `RetaguardaLicencaClient` (HTTP, contrato
   provisório), cai pro cache (`LICENCA_CACHE`, aplicada no MySQL local)
   dentro da tolerância configurável, força bloqueio se expirar, e opera
   100% local (`PDV_CONFIGURACAO.PLANO_SITUACAO`) se nunca houve contato
   remoto — cenário mais comum hoje, já que o endpoint da retaguarda ainda
   não existe. Gate em `PdvMovimentoService.abrirMovimento`. Falta apenas
   auditoria de transição de estado (hoje só loga). Ver `LICENCIAMENTO.md`.
6. **Fiscal** ✅ validado end-to-end contra ACBr Monitor real
   (2026-08-23), até o limite do que dá pra testar sem certificado
   ICP-Brasil pago — `EmissaoFiscalService`/`AcbrMonitorClient`/
   `AcbrComandoFiscalBuilder` testados contra um ACBrMonitorPLUS demo
   local de verdade (não só documentação): TCP → comando
   `NFE.CriarEnviarNFe` → INI → assinatura com certificado → validação de
   schema XML da NFe — tudo passou, terminando numa chamada HTTP real ao
   webservice de homologação da SEFAZ-SP (403, esperado — certificado de
   teste autoassinado não é ICP-Brasil). 4 bugs reais corrigidos nesse
   processo (banner de conexão não descartado, `null` corrompendo o INI,
   formato de data ISO 8601 rejeitado, CST/CSOSN trocado por regime
   tributário). Plugado em `EncerramentoVendaController`, com contingência
   principal→backup, persistência de `STATUS_FISCAL` (P/E/N) em
   `PDV_VENDA_CABECALHO`, job de reprocessamento automático
   (`ReprocessamentoFiscalService`) e Relatório Z com breakdown real por
   forma de pagamento. **Cálculo tributário básico** (`CalculoTributarioService`)
   aplica ICMS/PIS/COFINS/ISSQN já cadastrados no produto — sem ICMS-ST/
   DIFAL/redução de base/tabela IBPT (deliberado, ver javadoc da classe).
   Falta apenas: testar emissão de homologação de verdade com certificado
   real (decisão financeira do usuário) e validar extração de
   `ChDFe`/`NProt` numa resposta de SUCESSO (só o caminho de erro foi
   exercitado) — ver `ROADMAP.md` para o checklist completo.
7. **Periféricos** 🔜 groundwork implementado (2026-08-22) — decodificador
   de código de barras de balança (`BalancaCodigoDecoder`, alta confiança,
   plugado no PDV) + leitura serial ao vivo (`BalancaSerialService`,
   jSerialComm, ⚠️ não testada) + impressão ESC/POS e gaveta
   (`ImpressaoService`, via `javax.print`, ⚠️ não testada contra
   impressora física, mas já plugada no fechamento de venda) + TEF
   Hiperlink (`TefHiperlinkClient`, ⚠️ contrato não validado, não plugado
   em tela nenhuma). Ver `ROADMAP.md` fase 7 para o detalhamento por item.
8. **Diferenciais e retaguarda** 🔜 avançou bastante (2026-08-23) —
   corrigido o bug de base (cliente da venda de balcão nunca era
   persistido, ver `VendaService.atualizarCliente`), o que desbloqueou
   **fidelidade** (`FidelidadeService`, sob demanda) e **fiado**
   (`FiadoService` + tabela nova `CLIENTE_FIADO_LANCAMENTO`, mesmo padrão
   de livro-razão do estoque). **Devolução/troca pós-fechamento** também
   implementada, com tela própria (`DevolucaoService` + tabela
   `PDV_DEVOLUCAO` + `Devolucao.fxml`/`Controller`, reaproveita estoque,
   sempre exige gerente). **Marketing na tela** (`MarketingService`,
   slideshow na `TelaBloqueio`) e **autorização por papel centralizada**
   (`AutorizacaoGerencialDialog`, diálogo de gerente/supervisor extraído
   de 8 controllers) também implementados. Ainda falta: integração com
   retaguarda (TXT/XML/JSON/replicação — o Cactus ERP em si não existe
   ainda). **JWT/OAuth2 deliberadamente não implementado** — sem endpoint
   HTTP no projeto pra proteger, seria infraestrutura especulativa (ver
   "Stack de desenvolvimento" acima).

## Estrutura de pastas

```
src/main/java/br/com/cactuserp/pdv/
├── PdvApplication.java       # main() — inicializa JavaFX via Application.launch
├── config/                   # bootstrap Spring+JavaFX, navegação, utilitários
│   ├── JavaFxApplication.java    # ponte entre JavaFX Application e o contexto Spring
│   ├── JavaFxConfig.java
│   ├── SecurityConfig.java       # bean de BCryptPasswordEncoder
│   ├── SessaoPdv.java             # estado da sessão (operador/caixa logados)
│   ├── StageManager.java          # orquestra troca de telas/FXML, cache de cenas
│   └── util/                      # formatação, data/hora, hash, geração de senha
├── controllers/               # 1 controller por tela FXML (@Component, @FXML)
├── entity/                    # entidades JPA — mapeiam 1:1 as tabelas do PDV.md
├── repository/                 # interfaces Spring Data JPA
├── service/                    # regras de negócio, orquestram repositories
│   └── dto/                     # DTOs de transporte entre service e controller/UI
└── menu/                       # itens do menu principal

src/main/resources/
├── application.properties     # datasource MySQL, porta, pasta de imagens
└── fxml/
    ├── *.fxml                  # uma tela por controller, mesmo nome base
    ├── images/
    └── styles/CactusPdv.css    # estilo global da aplicação
```

## Comandos para rodar e testar

Pré-requisito: MySQL rodando localmente com o schema `cactus_pdv` aplicado
(DDL em `PDV.md`) e credenciais compatíveis com `application.properties`.

```bash
# compilar
./mvnw clean compile

# rodar a aplicação desktop (JavaFX + Spring Boot no mesmo processo)
./mvnw javafx:run

# empacotar
./mvnw clean package

# rodar testes
./mvnw test
```

No Windows, usar `mvnw.cmd` no lugar de `./mvnw`.

`./mvnw test` já executa a suíte (pequena, ver "Stack de desenvolvimento"
acima) — `spring-boot-starter-test` está no `pom.xml` desde 2026-08-23.

## Convenções de código

- **Idioma**: nomes de classes, métodos e variáveis em português (domínio de
  negócio), alinhados com os nomes de tabela/coluna do banco (ex:
  `PdvSangria.gerenteAutorizador` ↔ `ID_GERENTE_AUTORIZADOR`).
- **Injeção de dependência**: sempre via construtor, nunca `@Autowired` em
  campo. Services e o `StageManager` seguem esse padrão.
- **Entidades**: getters/setters escritos manualmente (sem Lombok), construtor
  padrão vazio, `@Column(name = "...")` explícito replicando o nome da coluna
  do DDL. Javadoc na classe explicando o papel da entidade e relação com
  outras (`@link`), especialmente quando envolve regra fiscal ou de alçada.
- **Controllers de tela**: anotados com `@Component`, campos `@FXML` privados,
  um controller por arquivo FXML com nomes correspondentes (`Login.fxml` ↔
  `LoginController`). Logger próprio via SLF4J (`LoggerFactory.getLogger`),
  não `System.out`/`printStackTrace` em código novo.
- **Navegação entre telas**: centralizada no `StageManager`, que carrega FXML
  via `FXMLLoader`, guarda cena anterior para permitir "voltar" e mantém cache
  de tela quando o estado não pode ser perdido (ex: PDV com venda em
  andamento). Novas telas devem seguir esse mesmo padrão em vez de navegar
  diretamente pelos controllers.
- **Services**: sem interface (classe concreta com `@Service`), métodos em
  português no infinitivo/substantivo (`listarTodos`, `buscarPorCodigo`,
  `salvar`, `deletar`). Regra de negócio fica no service, não no controller.
- **Valores monetários e de quantidade**: sempre `BigDecimal`, nunca
  `double`/`float`, dado o uso em cálculo fiscal.
- **Datas/horas**: `LocalDate` para data e `String` de 8 caracteres para hora
  (`HH:mm:ss`), seguindo o tipo das colunas existentes — manter consistência
  em vez de migrar para `LocalDateTime` sem necessidade.
- **Flags do banco**: `char(1)` do tipo `S`/`N` ou letras (`A`/`F`/`T` etc.)
  são mapeadas para enums Java (`StatusMovimento`, `SituacaoItemConta`, etc.)
  em vez de `String`/`boolean` cru — seguir esse padrão para novos campos de
  flag.
- **Segurança**: nunca commitar credenciais reais em `application.properties`;
  se precisar alterar, usar variável de ambiente ou perfil local não
  versionado.
