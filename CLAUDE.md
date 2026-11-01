# PERSONA
Você é um especialista sênior em desenvolvimento de sistemas e um especialista Java

# TAREFA
Voce deve desenvolver o sistema de erp da empresa Cactus Digital o sistema é o Cactus ERP.
Essa tarefa vai ser desenvolver o módulo de suprimentos (controle de estoque e Gestão de compras) e o módulo Administrativo,
(Cadastro Base, BI - Business Intelligence e GED - Gestão de Documentos)

# DESCRIÇÃO DA TAREFA
- o sistema Cactus ERP deve ser um mono repo em que os módulos serão acrescentados conforme o desenvolvimento for sendo feito.
- Desenvolvimento completo dos cadastros base:
	* Desenvolvimento completo de todas as funcionalidades de BI - Business Intelligence.
	* Desenvolvimento completo do módulo de GED - Gestão eletrônica de documentos
- Desenvolvimento completo do módulo de suprimentos:
	* Desenvolvimento completo de todas as funcionalidades de gestão de compras.
	* Desenvolvimento completo de todas as funcionalidades do módulo controle de estoque.

# EXEMPLOS
Segue um exemplo de um ótimo desenvolvimento:
- As APIs de todos os módulos devem ter os principais verbos HTTP, GET, POST, DELETE, PUT e PATCH quando houver necessidade.
- Para um sistema de grande porte devemos ter vários filtros de buscas e paginação pensando em performance.
- As tabelas devem ser normalizadas e indexadas para não travar as buscas quando haver grandes quantidades de registros.
- Boas prática serão primordiais, SOLID, padrão de arquitetura, design pattern
- Spring boot 4, JPA Hibernate, MySql, Lombok, Virtual Threads, SOLID, boas práticas.

# FORMATO
- O formato será em mono repo mas utilizando o contexto de micro serviços.
- As classes precisam seguir o cenceito do SOLID e principalmente a responsabilidade única.

# CHAIN OF THOUGHT (COM EXEMPLOS)
- Valide todos os requisitos antes de dar sua resposta
- Valide todo esse arquivo .md e modifique se for necessário, preciso ter um sistema competitivo e muito profissional, sistema de ponta.
- Se encontrar qualquer inconsistência, refaça o processo e verifique novamente
- Tenho um banco de dados do meu sistema de frente de caixa e esse sistema é a parte da retaguarda.
- Você precisa entender profundamente o contexto e a ideia desse sistema.
- Esse sistema é a retaguarda e precisa de alguma forma alimentar o sistema de frente de caixa que é o cactus-pdv.
- O arquivo BANCO_PDV.md é o DDL atual do sistema do cactus-pdv, analise, o banco do ERP precisa ser completo para ser um sistema competitivo com o mercado. 
- Entenda qual é a melhor forma de sincronização e envio desses dados para o PDV.

# ESTADO ATUAL DO REPOSITÓRIO
- **Layout de pastas (atualizado 2026-09-27)**: a raiz de trabalho é `cactus-erp`, que contém `cactus-erp-sistema` (o monorepo Maven descrito nesta seção — todo o backend Java: `common` + os services), `cactus-erp-web` (frontend do ERP, React+Vite+TS+Ant Design) e `cactus-pdv` (frente de caixa). Os arquivos compartilhados (`CLAUDE.md`, `BANCO_PDV.md`, `fenix.sql`, `FASE6_SINCRONIZACAO_PDV.md`, `BACKLOG.md`, `.claude/`, `.gitignore`) vivem na raiz `cactus-erp`, um nível acima do `pom.xml` do monorepo, porque servem de contexto pra todos os três. `BACKLOG.md` (criado 2026-09-02) é o índice acionável de itens pendentes não bloqueantes (frontend/backend/operacional) — atualizar sempre que um item novo for identificado ou resolvido, não deixar a lista ficar desatualizada — todo o conteúdo técnico abaixo (módulos, roadmap, análises) continua sendo especificamente sobre `cactus-erp-sistema`, salvo indicação em contrário.
  - **Decisão revertida (2026-09-27)**: até então `cactus-pdv` era documentado como "projeto irmão com repositório git próprio, nunca versionar aqui" (git init original em 2026-08-31 o excluía via `.gitignore`). A pedido do usuário ("ele precisa estar dentro do cactus-erp igual o cactus-web"), essa decisão foi revertida — `cactus-pdv` agora vive como pasta comum dentro do monorepo `cactus-erp` no GitHub (`github.com/antonioishac/cactus-erp`), mesmo tratamento do `cactus-erp-web`, sem submodule. O histórico standalone original (repositório git próprio, com remote `github.com/antonioishac/cactus-pdv` nunca publicado de fato — "Repository not found" ao tentar) foi dobrado pra dentro deste repo por squash simples (não `git subtree`, que preservaria o histórico commit a commit) — o `.git` original foi renomeado pra `cactus-pdv/.git-standalone-backup` (fora do controle de versão, ver `.gitignore`) como cópia de segurança local, não publicado em lugar nenhum. `cactus-pdv` mantém seu próprio `CLAUDE.md` (documentação específica do PDV, não duplicada aqui).
  - **Achado de segurança real, corrigido antes de publicar**: `cactus-pdv/src/main/resources/application.properties` tinha uma senha de banco MySQL em texto puro (`spring.datasource.password=...`), presente em 3+ commits do histórico original. Removida de TODO o histórico via `git filter-repo --replace-text` (instalado via `pip install git-filter-repo`, já que não havia SSH/gh CLI nem o pacote disponível de outra forma) antes de trazer pro monorepo — nunca chegou a ser publicada no GitHub. O arquivo atual usa `${DB_PASSWORD:changeme}` (variável de ambiente, mesmo padrão já usado em todo o resto do monorepo) em vez do valor hardcoded. Lição: sempre `grep` por `password|senha|secret|token` em TODO o histórico (`git log --all -p`), não só no estado atual, antes de publicar qualquer repositório pré-existente que ainda não tinha esse hábito.
- Monorepo Maven multi-módulo em andamento (dentro de `cactus-erp-sistema/`): `common` (lib compartilhada), `seguranca-service` (Spring Boot 4.1.1 + Authorization Server OAuth2/JWT + RBAC) e `cadastro-base-service` (Spring Boot 4.1.1, Resource Server puro) já compilam. `docker` não é mais usado por este repo para banco — ver seção de infraestrutura local mais abaixo (usamos um MySQL local já existente, `cactus-mysql`).
- **Nomenclatura de módulo**: sem prefixo `cactus-` em nenhum artifactId/pasta de módulo — já estamos dentro de `cactus-erp-sistema`, repetir o prefixo é redundante. Padrão: `[domínio]-service` (ex: `seguranca-service`, `cadastro-base-service`, `controle-estoque-service`, `gestao-compras-service`, `gestao-documentos-service`, `business-intelligence-service`), `common` pra lib compartilhada.
- **Pacote Java / groupId Maven**: `br.com.cactusdigital.erp.*` (ex: `br.com.cactusdigital.erp.cadastro.*`, `br.com.cactusdigital.erp.common.*`) — sem `cactuserp`, mesmo motivo do item acima. `groupId` de todo módulo Maven é `br.com.cactusdigital.erp` (igual ao pacote raiz, convenção padrão).
- **Organização de pacotes dentro de cada módulo: por camada técnica, global pro módulo inteiro** (não por domínio/feature). Dentro de `br.com.cactusdigital.erp.<módulo>.*`: `controller/`, `service/` (interfaces + serviços concretos sem interface) com `service/impl/` só para os pares interface+implementação, `entity/` (entidades JPA, enums de domínio, converters), `repository/`, `dto/` (request/response de todos os domínios juntos), `mapper/` (MapStruct), `specification/` (filtros dinâmicos JPA Criteria), `config/` (fica como está, cross-cutting). Aplicado no `cadastro-base-service` (185 arquivos reorganizados de `cliente/`, `colaborador/`, `empresa/`, `pessoa/`, `produto/`, `seguranca/`, `tributacao/`, `auth/` para essa estrutura única) — repetir esse padrão em todo módulo novo (`controle-estoque-service`, `gestao-compras-service`, etc.).
- **Git (atualizado 2026-09-27)**: `git init` feito na raiz `cactus-erp` em 2026-08-31 (repositório ficou vazio até então). Primeiro push em 2026-09-27: `main` no GitHub (`github.com/antonioishac/cactus-erp`, HTTPS + Git Credential Manager — sem chave SSH configurada nesta máquina) cobrindo `cactus-erp-sistema` + `cactus-erp-web`; `cactus-pdv` dobrado pra dentro do mesmo repo/commit logo em seguida (ver "Decisão revertida" acima).
- **Revisão em andamento (feedback do usuário)**: o modelo de `Produto` implementado na primeira passada estava raso demais — sem motor tributário, sem visão de multi-filial de verdade, sem desenho de estoque por depósito. As seções abaixo ("ANÁLISE TRIBUTÁRIA", "ANÁLISE: EMPRESA MULTI-FILIAL", "ANÁLISE: ESTOQUE NÍVEL ENTERPRISE") registram a análise correta feita a partir do `BANCO_PDV.md` (produção, não legado) e do `fenix.sql`, e o ROADMAP REVISADO no final substitui o roadmap anterior. Antes de tocar em código de novo, qualquer instância deste projeto deve ler essas seções.

# DOCUMENTAÇÃO DETALHADA DO PROJETO (ler sob demanda)

Para manter este arquivo leve (ele é carregado em TODA conversa), as análises, o roadmap e o histórico detalhado de cada fase ficam em `docs/claude/`. O conteúdo foi movido sem nenhuma alteração. As seções citadas acima como "abaixo" ou "no final" (ANÁLISE TRIBUTÁRIA, ANÁLISE: EMPRESA MULTI-FILIAL, ANÁLISE: ESTOQUE NÍVEL ENTERPRISE, ROADMAP REVISADO etc.) estão nestes arquivos, com os mesmos títulos.

**Antes de mexer em um módulo, leia o arquivo correspondente.** Para achar uma seção citada por nome (ex.: "ver ANÁLISE: MÓDULO COMERCIAL"), faça grep pelo título em `docs/claude/`.

- `docs/claude/fiscal-tributario.md`: Análise tributária do `Produto` e motor tributário do `fenix.sql`; ANÁLISE: MÓDULO CONTÁBIL E FISCAL (SPED, arquitetura, decisões pendentes, roadmap das Fases 14-16); REFORMA TRIBUTÁRIA IBS/CBS na NF-e (fase 1, migrations, bugs, validação).
- `docs/claude/sincronizacao-erp-pdv.md`: MAPA DE SINCRONIZAÇÃO CAMPO A CAMPO ERP ↔ PDV (`PRODUTO`, `CLIENTE`, `EMPRESA`/`LOJA_CONFIGURACAO`, `PRODUTO_FAIXA_PRECO`, inventário das 33 tabelas do `BANCO_PDV.md`, `HASH_REGISTRO`); REFERÊNCIA do `BANCO_PDV.md` e padrão de sincronização observado no DDL.
- `docs/claude/analises-empresa-estoque-usuarios.md`: ANÁLISE: EMPRESA MULTI-FILIAL; ANÁLISE: ESTOQUE NÍVEL ENTERPRISE; ANÁLISE: HIERARQUIA DE USUÁRIOS/OPERADORES/GERENTES DO PDV (impacto no cadastro-base, validação com RBAC).
- `docs/claude/comercial-e-loja-virtual.md`: ANÁLISE: MÓDULO COMERCIAL (inventário fenix, decisões, roadmap); ANÁLISE: LOJA VIRTUAL B2C white label multi-nicho (Fase 13 e sub-fases 1-4: modelo de dados, carrinho/checkout, NFC-e, reserva de estoque).
- `docs/claude/financeiro.md`: `financeiro-service`: Contas a Receber/Pagar, Caixa/Bancos, mapeamento campo a campo com o `fenix.sql`, Tesouraria, Conciliação Bancária, EDI Bancário.
- `docs/claude/roadmap-backend-fases-1-a-6.md`: ROADMAP REVISADO, parte 1: Swagger, Fases 1a/1b/1c (Cadastro Base), primeira subida real, extração do `seguranca-service`, sessão multi-empresa/refresh_token, Fases 2-5 (estoque, compras, GED, BI), varredura de N+1, Fase 6 (sincronização ERP ↔ PDV) e autenticação do PDV.
- `docs/claude/frontend-login-e-entregas.md`: ROADMAP REVISADO, parte 2: CORS, bugs de login/logout OAuth2 (achados #0-#4), tela de login e as passadas de design da marca (com lições), renovação silenciosa de token, `cactus-erp-web` Entregas 2 e 7-13.
- `docs/claude/roadmap-fase6b-vendas-e-saga.md`: ROADMAP REVISADO, parte 3: Fase 6-B (caixa e fiado do PDV), Fase 7 `vendas-service`, varredura do bug "item id null", e o padrão Saga orquestrada (vendas, compras, numeração de NF-e no `fiscal-service`).
- `docs/claude/relatorios.md`: ANÁLISE: MÓDULO DE RELATÓRIOS (Fase 17): decisões, achados técnicos, `common.report`, e os relatórios de cada serviço (estoque, vendas, financeiro, contabilidade, compras, BI, cadastro, GED, OS, CRM, fiscal, loja virtual) + frontend.

Outros arquivos de contexto na raiz: `BACKLOG.md` (pendências), `BANCO_PDV.md` (DDL do cactus-pdv), `FASE6_SINCRONIZACAO_PDV.md` (detalhe da sincronização), `fenix.sql` (banco legado de referência).

## Como manter este arquivo
- O `CLAUDE.md` guarda só o essencial: objetivo, convenções, estado atual e este índice.
- Registro de fase concluída, bug encontrado, análise nova ou decisão detalhada vai no arquivo de `docs/claude/` do assunto (ou num arquivo novo, adicionado a este índice), nunca aqui.
- Mantenha este arquivo abaixo de ~40 mil caracteres.
