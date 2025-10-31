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
│   │   └── CooperativaController.java
│   ├── dto/                                # Data Transfer Objects
│   │   ├── ProductDTO.java
│   │   └── CooperativaDTO.java
│   ├── entity/                             # Entidades JPA
│   │   ├── Product.java
│   │   └── Cooperativa.java
│   ├── jms/                                # Mensageria
│   │   ├── MessageProducer.java
│   │   └── MessageListener.java
│   ├── repository/                         # Camada de dados
│   │   ├── ProductRepository.java
│   │   └── CooperativaRepository.java
│   └── service/                            # Lógica de negócio
│       ├── ProductService.java
│       └── CooperativaService.java
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
- [x] Validações de dados
- [x] Documentação Swagger
- [x] Logs SLF4J
- [x] Transações JPA
- [x] Auditoria básica (timestamps)
- [x] Buscas customizadas

### 🔄 Em Desenvolvimento
- [ ] Testes unitários e integração
- [ ] Exception handling global
- [ ] Validação de CNPJ

### 📋 Backlog
- [ ] Paginação
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

**Última Atualização**: 29/10/2025
**Desenvolvedor**: Claude Code
**Branch Atual**: feature/cooperativa
