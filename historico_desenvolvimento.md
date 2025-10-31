# Histórico de Desenvolvimento - API Trovian

## 📋 Informações do Projeto

**Nome**: API Trovian
**Linguagem**: Java 17
**Framework**: Spring Boot 3.2.1
**Banco de Dados**: PostgreSQL 15
**Build Tool**: Maven
**Arquitetura**: Camadas (Controller → Service → Repository → Entity)

---

## 🏗️ Estrutura do Projeto

```
api.trovian.java/
├── src/main/java/com/trovian/
│   ├── Application.java                    # Entry point Spring Boot
│   ├── config/
│   │   ├── JmsConfig.java                 # Configuração ActiveMQ/JMS
│   │   └── OpenApiConfig.java             # Configuração Swagger/OpenAPI
│   ├── controller/                         # Camada REST
│   │   ├── ProductController.java
│   │   ├── CooperativaController.java
│   │   └── ClienteController.java
│   ├── dto/                                # Data Transfer Objects
│   │   ├── ProductDTO.java
│   │   ├── CooperativaDTO.java
│   │   └── ClienteDTO.java
│   ├── entity/                             # Entidades JPA
│   │   ├── Product.java
│   │   ├── Cooperativa.java
│   │   └── Cliente.java
│   ├── jms/                                # Mensageria
│   │   ├── MessageProducer.java
│   │   └── MessageListener.java
│   ├── repository/                         # Camada de dados
│   │   ├── ProductRepository.java
│   │   ├── CooperativaRepository.java
│   │   └── ClienteRepository.java
│   └── service/                            # Lógica de negócio
│       ├── ProductService.java
│       ├── CooperativaService.java
│       └── ClienteService.java
├── src/main/resources/
│   └── application.yml                     # Configurações da aplicação
├── docker-compose.yml                      # Serviços Docker (PostgreSQL, ActiveMQ)
└── pom.xml                                 # Dependências Maven
```

---

## 🛠️ Tecnologias e Dependências

### Core Framework
- **Spring Boot 3.2.1** - Framework principal
- **Spring Web** - REST APIs
- **Spring Data JPA** - Persistência com Hibernate
- **Spring Validation** - Validação de dados (Jakarta Validation)
- **Spring Actuator** - Monitoramento e métricas
- **Spring ActiveMQ** - Mensageria JMS

### Banco de Dados
- **PostgreSQL Driver** - Conexão com PostgreSQL
- **Hibernate** - ORM
- **HikariCP** - Pool de conexões (10 max, 5 min)

### Ferramentas
- **Lombok** - Redução de boilerplate (getters, setters, constructors)
- **SpringDoc OpenAPI 2.3.0** - Documentação Swagger/OpenAPI 3
- **SLF4J** - Logging

### Testes
- **Spring Boot Test** - Testes integrados
- **JUnit 5** - Framework de testes
- **Mockito** - Mocks para testes

---

## 📦 Implementações Realizadas

### 1. CRUD Product (Exemplo Base)
**Data**: Implementação inicial do projeto
**Arquivos Criados**:
- `entity/Product.java`
- `dto/ProductDTO.java`
- `repository/ProductRepository.java`
- `service/ProductService.java`
- `controller/ProductController.java`

**Funcionalidades**:
- CRUD completo (Create, Read, Update, Delete)
- Busca por nome (contém, case-insensitive)
- Busca por quantidade em estoque
- Validações de campos obrigatórios
- Auditoria automática (createdAt, updatedAt)

**Endpoints**: `/api/products/*`

---

### 2. CRUD Cooperativa
**Data**: 29/10/2025
**Branch**: `feature/cooperativa`

#### Atributos Implementados
- **nome** (String, 200 chars) - Obrigatório
- **cnpj** (String, 18 chars) - Obrigatório, Único
- **endereco** (String, 300 chars) - Opcional
- **cidade** (String, 100 chars) - Opcional
- **uf** (String, 2 chars) - Opcional
- **cep** (String, 9 chars) - Opcional
- **ativa** (Boolean) - Obrigatório, Default: true
- **dataCadastro** (LocalDateTime) - Auto-gerado
- **updatedAt** (LocalDateTime) - Auto-atualizado

#### Arquivos Criados

**Entity**
```java
src/main/java/com/trovian/entity/Cooperativa.java
```
- Anotações JPA (@Entity, @Table, @Id, @GeneratedValue)
- Validações Jakarta (@NotBlank, @NotNull, @Size)
- Constraint unique no CNPJ
- Auditoria com @PrePersist e @PreUpdate
- Lombok (@Data, @NoArgsConstructor, @AllArgsConstructor)

**DTO**
```java
src/main/java/com/trovian/dto/CooperativaDTO.java
```
- Validações de entrada
- Documentação Swagger (@Schema)
- Campos read-only (id, dataCadastro, updatedAt)

**Repository**
```java
src/main/java/com/trovian/repository/CooperativaRepository.java
```
- Extende JpaRepository<Cooperativa, Long>
- Query methods customizados:
  - `findByNomeContainingIgnoreCase(String nome)`
  - `findByCnpj(String cnpj)`
  - `findByCidadeIgnoreCase(String cidade)`
  - `findByUfIgnoreCase(String uf)`
  - `findByAtiva(Boolean ativa)`
  - `findByCidadeIgnoreCaseAndUfIgnoreCase(String cidade, String uf)`

**Service**
```java
src/main/java/com/trovian/service/CooperativaService.java
```
- CRUD completo com transações
- Validação de CNPJ duplicado (create e update)
- Logs SLF4J em todas operações
- Conversão manual DTO ↔ Entity
- Métodos de busca customizados

**Controller**
```java
src/main/java/com/trovian/controller/CooperativaController.java
```
- Base path: `/cooperativa`
- Documentação Swagger completa
- Response entities com status HTTP corretos

#### Endpoints REST - Cooperativa

| Método | Endpoint | Descrição | Status Code |
|--------|----------|-----------|-------------|
| GET | `/cooperativa` | Lista todas cooperativas | 200 |
| GET | `/cooperativa/{id}` | Busca por ID | 200 / 404 |
| POST | `/cooperativa` | Cria nova cooperativa | 201 |
| PUT | `/cooperativa/{id}` | Atualiza cooperativa | 200 / 404 |
| DELETE | `/cooperativa/{id}` | Deleta cooperativa | 204 / 404 |
| GET | `/cooperativa/search?nome={nome}` | Busca por nome (contém) | 200 |
| GET | `/cooperativa/cnpj/{cnpj}` | Busca por CNPJ | 200 / 404 |
| GET | `/cooperativa/cidade/{cidade}` | Busca por cidade | 200 |
| GET | `/cooperativa/uf/{uf}` | Busca por UF | 200 |
| GET | `/cooperativa/ativa/{ativa}` | Busca por status (true/false) | 200 |
| GET | `/cooperativa/cidade/{cidade}/uf/{uf}` | Busca por cidade e UF | 200 |

#### Regras de Negócio

1. **CNPJ Único**: Não permite cadastro de CNPJ duplicado
2. **Validação em Update**: Verifica CNPJ duplicado, exceto da própria cooperativa
3. **Status Padrão**: Cooperativa criada como ativa (true) por padrão
4. **Auditoria Automática**:
   - `dataCadastro` definido no momento da criação
   - `updatedAt` atualizado em cada modificação
5. **Buscas Case-Insensitive**: Todas as buscas por texto ignoram maiúsculas/minúsculas

---

### 3. CRUD Cliente
**Data**: 30/10/2025
**Branch**: `feature/cliente`

#### Atributos Implementados

**Dados Básicos**
- **id** (Long) - Chave primária auto-incrementada
- **uuid** (UUID) - Identificador único universal, gerado automaticamente
- **nome** (String, 200 chars) - Obrigatório
- **cnpjCpf** (String, 18 chars) - Obrigatório, Único
- **ie** (String, 20 chars) - Inscrição Estadual, Opcional
- **status** (Boolean) - Obrigatório, Default: true
- **cooperado** (Boolean) - Obrigatório, Default: false

**Endereço Completo**
- **endereco** (String, 300 chars) - Opcional
- **bairro** (String, 100 chars) - Opcional
- **complemento** (String, 100 chars) - Opcional
- **numero** (String, 20 chars) - Opcional
- **cep** (String, 9 chars) - Opcional
- **cidade** (String, 100 chars) - Opcional
- **uf** (String, 2 chars) - Opcional

**Contatos**
- **contatos** (String, 500 chars) - Informações de contato, Opcional
- **telefones** (String, 200 chars) - Telefones, Opcional

**Relacionamento**
- **cooperativa** (ManyToOne) - Relacionamento com Cooperativa (obrigatório se cooperado = true)

**Auditoria**
- **dataCadastro** (LocalDateTime) - Auto-gerado
- **updatedAt** (LocalDateTime) - Auto-atualizado

#### Arquivos Criados

**Entity**
```java
src/main/java/com/trovian/entity/Cliente.java
```
- Anotações JPA (@Entity, @Table, @Id, @GeneratedValue)
- Validações Jakarta (@NotBlank, @NotNull, @Size)
- Constraint unique no CNPJ/CPF e UUID
- Relacionamento ManyToOne com Cooperativa (FetchType.LAZY)
- UUID gerado automaticamente no @PrePersist
- Auditoria com @PrePersist e @PreUpdate
- Lombok (@Data, @NoArgsConstructor, @AllArgsConstructor)

**DTO**
```java
src/main/java/com/trovian/dto/ClienteDTO.java
```
- Validações de entrada
- Documentação Swagger (@Schema)
- Campos read-only (id, uuid, dataCadastro, updatedAt, cooperativaNome)
- Campo cooperativaId para relacionamento
- Campo cooperativaNome (read-only) para exibição

**Repository**
```java
src/main/java/com/trovian/repository/ClienteRepository.java
```
- Extende JpaRepository<Cliente, Long>
- Query methods customizados:
  - `findByCooperativa(Cooperativa, Pageable)` - Busca por cooperativa com paginação
  - `findByCooperativaId(Long, Pageable)` - Busca por ID da cooperativa com paginação
  - `findByUuid(UUID)` - Busca por UUID
  - `findByCnpjCpf(String)` - Busca por CNPJ/CPF

**Service**
```java
src/main/java/com/trovian/service/ClienteService.java
```
- CRUD completo com transações
- Validação de CNPJ/CPF duplicado (create e update)
- Validação de cooperativa obrigatória quando cooperado = true
- Logs SLF4J em todas operações
- Conversão manual DTO ↔ Entity
- Métodos de busca paginados

**Controller**
```java
src/main/java/com/trovian/controller/ClienteController.java
```
- Base path: `/cliente`
- Documentação Swagger completa
- Response entities com status HTTP corretos
- 3 endpoints GET (conforme especificado):
  1. Listar todos com paginação
  2. Buscar por ID
  3. Buscar por cooperativa com paginação

#### Endpoints REST - Cliente

| Método | Endpoint | Descrição | Status Code |
|--------|----------|-----------|-------------|
| GET | `/cliente` | Lista todos clientes com paginação | 200 |
| GET | `/cliente/{id}` | Busca por ID | 200 / 404 |
| GET | `/cliente/cooperativa/{cooperativaId}` | Busca por cooperativa com paginação | 200 |
| POST | `/cliente` | Cria novo cliente | 201 |
| PUT | `/cliente/{id}` | Atualiza cliente | 200 / 404 |
| DELETE | `/cliente/{id}` | Deleta cliente | 204 / 404 |

**Parâmetros de Paginação** (todos opcionais):
- `page` (int, default: 0) - Número da página
- `size` (int, default: 10) - Tamanho da página
- `sortBy` (String, default: "id") - Campo para ordenação
- `direction` (String, default: "ASC") - Direção (ASC/DESC)

#### Regras de Negócio

1. **CNPJ/CPF Único**: Não permite cadastro de CNPJ/CPF duplicado
2. **UUID Automático**: UUID gerado automaticamente no momento da criação
3. **Cooperativa Obrigatória**: Se cooperado = true, deve ter cooperativaId informado
4. **Validação em Update**: Verifica CNPJ/CPF duplicado, exceto do próprio cliente
5. **Status Padrão**: Cliente criado como ativo (status = true) por padrão
6. **Cooperado Padrão**: Cliente criado como não cooperado (cooperado = false) por padrão
7. **Auditoria Automática**:
   - `uuid` gerado no momento da criação
   - `dataCadastro` definido no momento da criação
   - `updatedAt` atualizado em cada modificação
8. **Lazy Loading**: Cooperativa carregada somente quando necessário

#### Exemplos de Uso

**Criar Cliente Cooperado**
```bash
curl -X POST http://localhost:8080/cliente \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "João da Silva",
    "cnpjCpf": "123.456.789-00",
    "ie": "123.456.789.012",
    "endereco": "Rua das Flores, 123",
    "bairro": "Centro",
    "numero": "123",
    "cep": "12345-678",
    "cidade": "São Paulo",
    "uf": "SP",
    "telefones": "(11) 98765-4321",
    "contatos": "joao@email.com",
    "status": true,
    "cooperado": true,
    "cooperativaId": 1
  }'
```

**Criar Cliente Não Cooperado**
```bash
curl -X POST http://localhost:8080/cliente \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Maria Santos",
    "cnpjCpf": "98.765.432/0001-00",
    "endereco": "Av. Paulista, 1000",
    "cidade": "São Paulo",
    "uf": "SP",
    "telefones": "(11) 91234-5678",
    "status": true,
    "cooperado": false
  }'
```

**Listar Todos os Clientes (primeira página, 10 itens)**
```bash
curl -X GET "http://localhost:8080/cliente?page=0&size=10&sortBy=nome&direction=ASC"
```

**Buscar Cliente por ID**
```bash
curl -X GET http://localhost:8080/cliente/1
```

**Buscar Clientes por Cooperativa (com paginação)**
```bash
curl -X GET "http://localhost:8080/cliente/cooperativa/1?page=0&size=20&sortBy=nome&direction=ASC"
```

**Atualizar Cliente**
```bash
curl -X PUT http://localhost:8080/cliente/1 \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "João da Silva - Atualizado",
    "cnpjCpf": "123.456.789-00",
    "endereco": "Rua das Flores, 456",
    "cidade": "São Paulo",
    "uf": "SP",
    "status": true,
    "cooperado": true,
    "cooperativaId": 1
  }'
```

**Deletar Cliente**
```bash
curl -X DELETE http://localhost:8080/cliente/1
```

#### Características Técnicas

1. **UUID Único**: Cada cliente possui um UUID único e imutável
2. **Relacionamento ManyToOne**: Vários clientes podem pertencer a uma cooperativa
3. **Lazy Loading**: Cooperativa carregada somente quando acessada
4. **Validações Completas**: CNPJ/CPF único, cooperativa obrigatória para cooperados
5. **Paginação Nativa**: Todos os endpoints de listagem suportam paginação
6. **Endereço Completo**: Campos separados para endereço detalhado
7. **Flexibilidade**: Cliente pode ser cooperado ou não
8. **Auditoria**: Rastreamento de criação e atualização

---

### 4. Paginação
**Data**: 30/10/2025
**Branch**: `feature/cooperativa` (continuação)

#### Implementação

A paginação foi implementada utilizando o suporte nativo do Spring Data JPA através da interface `Pageable`. A implementação mantém os endpoints originais sem paginação para retrocompatibilidade e adiciona novos endpoints com sufixo `/paginated`.

#### Recursos Implementados

**Parâmetros de Paginação**:
- `page` (int, default: 0) - Número da página (inicia em 0)
- `size` (int, default: 10) - Tamanho da página
- `sortBy` (String, default: "id") - Campo para ordenação
- `direction` (String, default: "ASC") - Direção da ordenação (ASC ou DESC)

**Resposta Paginada** (objeto `Page`):
```json
{
  "content": [...],           // Lista de objetos da página atual
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10,
    "sort": {...}
  },
  "totalPages": 5,            // Total de páginas
  "totalElements": 47,        // Total de elementos
  "last": false,              // É a última página?
  "first": true,              // É a primeira página?
  "numberOfElements": 10,     // Quantidade de elementos na página atual
  "size": 10,                 // Tamanho da página
  "number": 0,                // Número da página atual
  "sort": {...},
  "empty": false
}
```

#### Arquivos Modificados

**Product**
- `repository/ProductRepository.java` - Adicionado método `findByNameContainingIgnoreCase(String, Pageable)`
- `service/ProductService.java` - Adicionados métodos:
  - `findAllPaginated(Pageable)`
  - `searchByNamePaginated(String, Pageable)`
- `controller/ProductController.java` - Adicionados endpoints:
  - `GET /products/paginated`
  - `GET /products/search/paginated`

**Cooperativa**
- `repository/CooperativaRepository.java` - Adicionados métodos com `Pageable`:
  - `findByNomeContainingIgnoreCase(String, Pageable)`
  - `findByCidadeIgnoreCase(String, Pageable)`
  - `findByUfIgnoreCase(String, Pageable)`
  - `findByAtiva(Boolean, Pageable)`
  - `findByCidadeIgnoreCaseAndUfIgnoreCase(String, String, Pageable)`
- `service/CooperativaService.java` - Adicionados métodos:
  - `findAllPaginated(Pageable)`
  - `searchByNomePaginated(String, Pageable)`
  - `findByCidadePaginated(String, Pageable)`
  - `findByUfPaginated(String, Pageable)`
  - `findByAtivaPaginated(Boolean, Pageable)`
  - `findByCidadeAndUfPaginated(String, String, Pageable)`
- `controller/CooperativaController.java` - Adicionados endpoints:
  - `GET /cooperativa/paginated`
  - `GET /cooperativa/search/paginated`
  - `GET /cooperativa/cidade/{cidade}/paginated`
  - `GET /cooperativa/uf/{uf}/paginated`
  - `GET /cooperativa/ativa/{ativa}/paginated`
  - `GET /cooperativa/cidade/{cidade}/uf/{uf}/paginated`

#### Endpoints REST - Paginação

**Product - Endpoints Paginados**

| Método | Endpoint | Descrição | Parâmetros |
|--------|----------|-----------|------------|
| GET | `/products/paginated` | Lista produtos com paginação | page, size, sortBy, direction |
| GET | `/products/search/paginated` | Busca por nome com paginação | name, page, size, sortBy, direction |

**Cooperativa - Endpoints Paginados**

| Método | Endpoint | Descrição | Parâmetros |
|--------|----------|-----------|------------|
| GET | `/cooperativa/paginated` | Lista cooperativas com paginação | page, size, sortBy, direction |
| GET | `/cooperativa/search/paginated` | Busca por nome com paginação | nome, page, size, sortBy, direction |
| GET | `/cooperativa/cidade/{cidade}/paginated` | Busca por cidade com paginação | cidade, page, size, sortBy, direction |
| GET | `/cooperativa/uf/{uf}/paginated` | Busca por UF com paginação | uf, page, size, sortBy, direction |
| GET | `/cooperativa/ativa/{ativa}/paginated` | Busca por status com paginação | ativa, page, size, sortBy, direction |
| GET | `/cooperativa/cidade/{cidade}/uf/{uf}/paginated` | Busca por cidade e UF com paginação | cidade, uf, page, size, sortBy, direction |

#### Exemplos de Uso

**Listar produtos - primeira página, 10 itens, ordenado por nome**
```bash
curl -X GET "http://localhost:8080/products/paginated?page=0&size=10&sortBy=name&direction=ASC"
```

**Buscar cooperativas por cidade - segunda página, 20 itens**
```bash
curl -X GET "http://localhost:8080/cooperativa/cidade/São Paulo/paginated?page=1&size=20"
```

**Buscar produtos por nome com paginação**
```bash
curl -X GET "http://localhost:8080/products/search/paginated?name=teste&page=0&size=5&sortBy=price&direction=DESC"
```

#### Características Técnicas

1. **Retrocompatibilidade**: Endpoints originais sem paginação continuam funcionando
2. **Ordenação Flexível**: Permite ordenar por qualquer campo da entidade
3. **Valores Padrão**: Todos os parâmetros possuem valores padrão sensatos
4. **Documentação Swagger**: Todos os endpoints documentados com exemplos
5. **Type-Safe**: Utiliza tipos do Spring Data (`Page<T>`, `Pageable`, `Sort`)
6. **Performance**: Queries otimizadas pelo Spring Data JPA
7. **Metadata Completa**: Response inclui informações sobre total de páginas, elementos, etc.

#### Benefícios

- **Performance**: Reduz carga de rede e processamento ao retornar apenas dados necessários
- **UX**: Melhora experiência do usuário em listas grandes
- **Escalabilidade**: Permite trabalhar com grandes volumes de dados
- **Flexibilidade**: Ordenação customizável por qualquer campo
- **Padrão REST**: Segue convenções REST para paginação

---

## 🎯 Padrões e Convenções Adotados

### Nomenclatura
- **Entities**: Singular (Product, Cooperativa)
- **DTOs**: Sufixo "DTO" (ProductDTO, CooperativaDTO)
- **Repositories**: Sufixo "Repository"
- **Services**: Sufixo "Service"
- **Controllers**: Sufixo "Controller"

### Estrutura de Camadas
```
Controller (REST)
    ↓
Service (Lógica de Negócio + DTO)
    ↓
Repository (Spring Data JPA)
    ↓
Entity (JPA/Hibernate)
    ↓
Database (PostgreSQL)
```

### Annotations Lombok
- `@Data` - Gera getters, setters, toString, equals, hashCode
- `@NoArgsConstructor` - Construtor vazio
- `@AllArgsConstructor` - Construtor com todos os campos
- `@RequiredArgsConstructor` - Injeção de dependências (final fields)
- `@Slf4j` - Logger SLF4J

### Validações
- Jakarta Validation (Bean Validation 3.0)
- `@NotBlank` - Strings não vazias
- `@NotNull` - Campos obrigatórios
- `@Size` - Tamanho min/max
- `@Positive` - Números positivos
- `@Valid` - Ativa validação no Controller

### Transações
- `@Transactional` - Operações de escrita (create, update, delete)
- `@Transactional(readOnly = true)` - Operações de leitura (otimização)

### HTTP Status Codes
- **200 OK** - Sucesso em GET/PUT
- **201 Created** - Sucesso em POST
- **204 No Content** - Sucesso em DELETE
- **404 Not Found** - Recurso não encontrado
- **400 Bad Request** - Validação falhou

### Logging
```java
log.info("Mensagem informativa");
log.error("Mensagem de erro", exception);
log.debug("Mensagem de debug");
```

### Documentação Swagger
- `@Tag` - Agrupa endpoints
- `@Operation` - Descreve operação
- `@ApiResponses` - Possíveis respostas HTTP
- `@Parameter` - Documenta parâmetros
- `@Schema` - Documenta DTOs

---

## 🔧 Configurações

### Banco de Dados (application.yml)
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/trovian_db
    username: trovian_user
    password: trovian_pass
  jpa:
    hibernate:
      ddl-auto: update  # Cria/atualiza schema automaticamente
    show-sql: true
```

### Servidor
```yaml
server:
  port: 8080
  servlet:
    context-path: /api
```

### Swagger UI
- URL: `http://localhost:8080/api/swagger-ui/index.html`

### Actuator
- Health: `http://localhost:8080/api/actuator/health`
- Metrics: `http://localhost:8080/api/actuator/metrics`

---

## 📝 Notas de Desenvolvimento

### Decisões Técnicas

1. **Jakarta Persistence vs javax.persistence**
   - Projeto usa Jakarta (Spring Boot 3+)
   - Migração do javax para jakarta

2. **Conversão DTO ↔ Entity**
   - Conversão manual nos Services
   - Alternativa futura: MapStruct ou ModelMapper

3. **Exception Handling**
   - Atualmente: RuntimeException com mensagens descritivas
   - Melhoria futura: Criar exceptions customizadas e @ControllerAdvice

4. **CNPJ como String**
   - Armazenado como String para manter formatação
   - Validação de formato pode ser adicionada futuramente

5. **Endpoint Base Path**
   - Product: `/api/products`
   - Cooperativa: `/cooperativa` (sem `/api` - já está no context-path)

### Melhorias Futuras Sugeridas

1. **Exception Handling Global**
   - Criar `@ControllerAdvice` para tratamento centralizado
   - Exceptions customizadas (CooperativaNaoEncontradaException, etc.)
   - Response padronizada de erros

2. **Paginação**
   - Adicionar suporte a Pageable nos métodos `findAll()`
   - Retornar Page<T> ao invés de List<T>

3. **Validação de CNPJ**
   - Implementar validação de formato e dígitos verificadores
   - Criar annotation customizada `@CNPJ`

4. **Testes Unitários e Integração**
   - Testes para Services (Mockito)
   - Testes para Controllers (MockMvc)
   - Testes de integração com banco H2

5. **Auditoria Avançada**
   - Implementar Spring Data JPA Auditing
   - Campos `createdBy` e `updatedBy`

6. **Soft Delete**
   - Implementar deleção lógica ao invés de física
   - Adicionar campo `deleted` (Boolean) e `deletedAt` (LocalDateTime)

7. **Cache**
   - Implementar Spring Cache para buscas frequentes
   - Redis como cache distribuído

8. **Mensageria**
   - Enviar eventos JMS quando cooperativa for criada/atualizada
   - Integração com sistema de notificações

9. **Segurança**
   - Adicionar Spring Security
   - Autenticação JWT
   - Autorização por roles (ADMIN, USER)

10. **Internacionalização**
    - Mensagens de erro em português/inglês
    - `MessageSource` para i18n

---

## 🚀 Como Executar

### Pré-requisitos
```bash
# Java 17+
java -version

# Maven 3.6+
mvn -version

# Docker (para PostgreSQL e ActiveMQ)
docker --version
```

### Iniciar Serviços Docker
```bash
docker-compose up -d
```

### Compilar e Executar
```bash
# Compilar
mvn clean install

# Executar
mvn spring-boot:run

# Ou executar JAR
java -jar target/api-trovian-1.0.0.jar
```

### Acessar Aplicação
- **API Base**: http://localhost:8080/api
- **Swagger UI**: http://localhost:8080/api/swagger-ui/index.html
- **Health Check**: http://localhost:8080/api/actuator/health

---

## 🧪 Exemplos de Uso (cURL)

### Criar Cooperativa
```bash
curl -X POST http://localhost:8080/cooperativa \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Cooperativa Central do Brasil",
    "cnpj": "12.345.678/0001-90",
    "endereco": "Av. Paulista, 1000",
    "cidade": "São Paulo",
    "uf": "SP",
    "cep": "01310-100",
    "ativa": true
  }'
```

### Listar Todas Cooperativas
```bash
curl -X GET http://localhost:8080/cooperativa
```

### Buscar por ID
```bash
curl -X GET http://localhost:8080/cooperativa/1
```

### Buscar por CNPJ
```bash
curl -X GET http://localhost:8080/cooperativa/cnpj/12.345.678/0001-90
```

### Atualizar Cooperativa
```bash
curl -X PUT http://localhost:8080/cooperativa/1 \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Cooperativa Central do Brasil - Atualizada",
    "cnpj": "12.345.678/0001-90",
    "endereco": "Av. Paulista, 2000",
    "cidade": "São Paulo",
    "uf": "SP",
    "cep": "01310-100",
    "ativa": true
  }'
```

### Deletar Cooperativa
```bash
curl -X DELETE http://localhost:8080/cooperativa/1
```

### Buscar por Cidade
```bash
curl -X GET http://localhost:8080/cooperativa/cidade/São%20Paulo
```

### Buscar por UF
```bash
curl -X GET http://localhost:8080/cooperativa/uf/SP
```

### Buscar Ativas
```bash
curl -X GET http://localhost:8080/cooperativa/ativa/true
```

---

## 📊 Status do Projeto

### ✅ Concluído
- [x] CRUD Product (exemplo base)
- [x] CRUD Cooperativa completo
- [x] CRUD Cliente completo com relacionamento ManyToOne
- [x] Validações de dados
- [x] Documentação Swagger
- [x] Logs SLF4J
- [x] Transações JPA
- [x] Auditoria básica (timestamps)
- [x] Buscas customizadas
- [x] Paginação completa (Product, Cooperativa e Cliente)
- [x] UUID único para Clientes
- [x] Relacionamento Cliente-Cooperativa

### 🔄 Em Desenvolvimento
- [ ] Testes unitários e integração
- [ ] Exception handling global
- [ ] Validação de CNPJ

### 📋 Backlog
- [ ] Cache
- [ ] Segurança (JWT)
- [ ] Soft delete
- [ ] Auditoria avançada
- [ ] Internacionalização
- [ ] Mensageria para eventos de cooperativa

---

## 📚 Referências

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [Jakarta Validation](https://jakarta.ee/specifications/bean-validation/)
- [Lombok](https://projectlombok.org/)
- [SpringDoc OpenAPI](https://springdoc.org/)

---

## 🔄 Changelog

### v1.3.0 - 30/10/2025
- ✨ Implementado CRUD completo de Cliente
- ✨ Relacionamento ManyToOne Cliente-Cooperativa
- ✨ UUID único e automático para cada cliente
- ✨ Validação de CNPJ/CPF único
- ✨ Validação de cooperativa obrigatória para cooperados
- ✨ Endereço completo (rua, bairro, complemento, número, CEP, cidade, UF)
- ✨ Campos de contato (telefones e contatos)
- ✨ 3 endpoints GET com paginação (todos, por ID, por cooperativa)
- ✨ Paginação nativa em todos endpoints de listagem
- 📝 Documentação completa no histórico de desenvolvimento

### v1.2.0 - 30/10/2025
- ✨ Implementada paginação completa em Product e Cooperativa
- ✨ Adicionados 8 novos endpoints paginados
- ✨ Suporte a ordenação customizável (ASC/DESC)
- ✨ Resposta com metadata completa (total de páginas, elementos, etc.)
- ✨ Endpoints retrocompatíveis (mantidos endpoints originais)
- 📝 Documentação Swagger atualizada com endpoints paginados
- 📝 Histórico de desenvolvimento atualizado

### v1.1.0 - 29/10/2025
- ✨ Adicionado CRUD completo de Cooperativa
- ✨ Endpoints REST para gerenciamento de cooperativas
- ✨ Validação de CNPJ único
- ✨ Múltiplas opções de busca (nome, CNPJ, cidade, UF, status)
- 📝 Documentação Swagger completa

### v1.0.0 - Inicial
- ✨ Estrutura base do projeto Spring Boot
- ✨ CRUD de Product (exemplo)
- ✨ Configuração PostgreSQL
- ✨ Configuração ActiveMQ/JMS
- ✨ Swagger/OpenAPI
- ✨ Spring Actuator

---

**Última Atualização**: 30/10/2025
**Desenvolvedor**: Claude Code
**Branch Atual**: feature/cooperativa
