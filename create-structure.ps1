# Roda dentro da pasta raiz do projeto (onde está o pom.xml)
# Cria toda a estrutura de pastas do rotalog-api com .gitkeep

$base = "src/main/java/com/rotalog/api"
$resources = "src/main/resources"

$folders = @(
    # Camada de configuração
    "$base/config",

    # Controllers
    "$base/controller",

    # Services
    "$base/service",

    # Repositories
    "$base/repository",

    # Entities JPA
    "$base/entity",

    # DTOs
    "$base/dto/request",
    "$base/dto/response",

    # Enums de domínio
    "$base/enums",

    # Exceptions customizadas
    "$base/exception",

    # Filtros e providers JWT
    "$base/security",

    # Mappers entity <-> DTO
    "$base/mapper",

    # Handlers WebSocket STOMP
    "$base/websocket",

    # Migrations Flyway
    "$resources/db/migration"
)

foreach ($folder in $folders) {
    New-Item -ItemType Directory -Path $folder -Force | Out-Null
    New-Item -ItemType File -Path "$folder/.gitkeep" -Force | Out-Null
    Write-Host "OK  $folder"
}

Write-Host ""
Write-Host "Estrutura criada. Pastas prontas para o git."