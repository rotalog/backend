# Rotalog Backend – PostgreSQL + PostGIS

## O que foi implementado

| Arquivo | O que faz |
|---|---|
| `docker-compose.yml` | Sobe PostgreSQL 16 com PostGIS 3.4 em container local |
| `docker/postgres/init.sql` | Habilita PostGIS, cria todas as tabelas e índices GIST espaciais |
| `src/main/resources/application.properties` | Dialeto PostGIS + HikariCP; Flyway removido |
| `pom.xml` | `hibernate-spatial` adicionado; dependências Flyway removidas |
| `model/Customer.java` | Campo `location` (GEOGRAPHY Point) |
| `model/Distributor.java` | Campo `location` (GEOGRAPHY Point) |
| `model/Order.java` | Campo `deliveryRoute` (GEOMETRY LineString) |
| `util/GeoUtils.java` | Helpers para criar Point e LineString com SRID 4326 |

---

## Como funciona a inicialização

Sem Flyway, o schema é criado em dois momentos:

```
docker compose up -d
       │
       ▼
PostgreSQL inicia o banco sistema_db
       │
       ▼
Executa docker/postgres/init.sql  (apenas na 1a vez, com banco vazio)
  - CREATE EXTENSION postgis
  - CREATE TABLE products, customers, distributors, orders...
  - CREATE INDEX ... USING GIST (indices espaciais)
       │
       ▼
./mvnw spring-boot:run
  - Hibernate (ddl-auto=update) verifica as tabelas
  - Nada e recriado; apenas colunas novas sao adicionadas se necessario
```

---

## Passo a Passo para Rodar

### 1. Subir o banco

```bash
docker compose up -d
```

Isso sobe o container `rotalog_postgres` com:
- Imagem: `postgis/postgis:16-3.4-alpine`
- Banco: `sistema_db` | Usuario/Senha: `postgres/postgres`
- Porta: `5432`

### 2. Verificar que o PostGIS foi instalado

```bash
docker exec -it rotalog_postgres psql -U postgres -d sistema_db -c "SELECT PostGIS_Version();"
```

Saida esperada: `3.4 USE_GEOS=1 USE_PROJ=1 ...`

### 3. Rodar a aplicacao

```bash
./mvnw spring-boot:run
```

### 4. Conectar com cliente externo (DBeaver, TablePlus, psql)

```
Host: localhost  |  Porta: 5432
Banco: sistema_db  |  Usuario/Senha: postgres/postgres
```

---

## Tabelas e Campos Geograficos

```
products              - Catalogo de produtos
customers             - Clientes + campo location (GEOGRAPHY POINT)
distributors          - Distribuidores + campo location (GEOGRAPHY POINT)
orders                - Pedidos + campo delivery_route (GEOMETRY LINESTRING)
order_items           - Itens de cada pedido
inventory             - Saldo de estoque por produto
inventory_movements   - Historico de movimentacoes
```

---

## Como usar os campos PostGIS no codigo Java

### Salvar a localizacao de um cliente

```java
import com.rotalog.api.util.GeoUtils;

Customer customer = Customer.builder()
    .name("Joao Silva")
    .email("joao@email.com")
    .location(GeoUtils.createPoint(-3.1190, -60.0217)) // Manaus, AM
    .build();

customerRepository.save(customer);
```

### Salvar a rota de entrega de um pedido

```java
LineString rota = GeoUtils.createLineString(new double[][]{
    {-3.1190, -60.0217},  // origem: Manaus
    {-3.4500, -60.1800},  // ponto intermediario
    {-3.8397, -60.6719}   // destino
});
order.setDeliveryRoute(rota);
orderRepository.save(order);
```

### Consultas geograficas com SQL nativo no Repository

Clientes em raio de 50 km de um ponto:
```java
@Query(value =
    "SELECT * FROM customers " +
    "WHERE ST_DWithin(location, ST_MakePoint(:lng, :lat)::geography, :radiusMeters)",
    nativeQuery = true)
List<Customer> findNearby(
    @Param("lat") double lat,
    @Param("lng") double lng,
    @Param("radiusMeters") double radiusMeters
);
```

Distribuidores mais proximos, ordenados por distancia:
```java
@Query(value =
    "SELECT *, ST_Distance(location, ST_MakePoint(:lng, :lat)::geography) AS dist " +
    "FROM distributors WHERE active = true ORDER BY dist LIMIT :limit",
    nativeQuery = true)
List<Distributor> findClosest(
    @Param("lat") double lat,
    @Param("lng") double lng,
    @Param("limit") int limit
);
```

---

## Proximos Passos

### Passo 1 - Variaveis de ambiente (seguranca)

Em producao, nunca deixe senha hardcoded. Crie um `.env`:

```
DB_URL=jdbc:postgresql://seu-host:5432/sistema_db
DB_USER=postgres
DB_PASSWORD=sua_senha_segura
```

E no `application.properties`:
```properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASSWORD}
```

### Passo 2 - Adicionar lat/lng nos DTOs

Os campos `location` dos models precisam de representacao nos DTOs:

```java
// CustomerDTO.Request
private Double latitude;
private Double longitude;

// CustomerService.create()
if (request.getLatitude() != null && request.getLongitude() != null) {
    customer.setLocation(
        GeoUtils.createPoint(request.getLatitude(), request.getLongitude())
    );
}
```

### Passo 3 - Endpoints de consulta geografica

Criar `GeoController.java` com endpoints como:
- GET /geo/customers/nearby?lat=-3.11&lng=-60.02&radius=50000
- GET /geo/distributors/closest?lat=-3.11&lng=-60.02&limit=5

### Passo 4 - Spring Security + JWT

Adicionar `spring-boot-starter-security`, criar tabela `users` no `init.sql`
e implementar `AuthController` com login/registro.

### Passo 5 - Deploy com banco em nuvem com PostGIS

- Supabase - ja vem com PostGIS ativo por padrao (gratuito)
- AWS RDS - habilitar via parametro de grupo
- Neon - PostgreSQL serverless com suporte a PostGIS

O codigo nao muda; apenas a variavel DB_URL.

---

## Comandos Uteis

```bash
# Parar o banco
docker compose down

# Parar E apagar os dados (reset completo)
docker compose down -v

# Ver tabelas criadas
docker exec -it rotalog_postgres psql -U postgres -d sistema_db -c "\dt"

# Ver indices espaciais GIST
docker exec -it rotalog_postgres psql -U postgres -d sistema_db -c "SELECT indexname FROM pg_indexes WHERE indexdef LIKE '%gist%';"

# Backup manual
docker exec rotalog_postgres pg_dump -U postgres sistema_db > backup.sql
```
