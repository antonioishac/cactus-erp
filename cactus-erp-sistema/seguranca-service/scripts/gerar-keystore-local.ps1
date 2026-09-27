# NÃO É NECESSÁRIO PRO DIA A DIA: rodando sem "--spring.profiles.active=prod" (o
# default), o Authorization Server gera uma chave RSA efêmera em memória sozinho (ver
# JwtKeyConfig) — token não sobrevive a um restart, mas pra desenvolvimento local isso
# não importa e elimina esse passo manual.
#
# Só rode este script se quiser testar de propósito o caminho de "prod" (chave
# persistida) localmente. Gera o keystore PKCS12 com o par de chaves RSA que o
# Authorization Server usa pra assinar os JWTs nesse perfil. Rode a partir da pasta
# cadastro-base-service/. O arquivo é escrito em src/main/resources (fora do git, ver
# .gitignore) para entrar no classpath e ser resolvido via ResourceLoader independente
# do working directory de quem inicia a aplicação (IDE, mvn, Docker, CI...).
#
# Uso: .\scripts\gerar-keystore-local.ps1  (depois rode com --spring.profiles.active=prod)

$ErrorActionPreference = "Stop"

$keystorePath = "src/main/resources/cactus-auth-keystore.p12"
$alias = "cactus-auth"
$password = "changeit"

if (Test-Path $keystorePath) {
    Write-Host "Já existe $keystorePath — apague antes se quiser gerar um novo."
    exit 0
}

keytool -genkeypair `
    -alias $alias `
    -keyalg RSA `
    -keysize 2048 `
    -storetype PKCS12 `
    -keystore $keystorePath `
    -storepass $password `
    -validity 3650 `
    -dname "CN=Cactus ERP, OU=Cactus Digital, O=Cactus Digital, L=Local, ST=Local, C=BR"

Write-Host "Keystore gerado em $keystorePath (alias=$alias, senha=$password — dev local apenas)."
Write-Host "Os valores batem com o default de application.yml (cactus.security.jwt-keystore.*);"
Write-Host "troque via variáveis de ambiente JWT_KEYSTORE_PATH/PASSWORD/ALIAS se preferir outros."
Write-Host "Se o app já rodou antes (target/classes existe), rode 'mvn compile' ou dê Build na"
Write-Host "IDE para o arquivo ser copiado pro classpath antes do próximo start."
