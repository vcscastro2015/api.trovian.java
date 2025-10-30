# API Trovian - Spring Boot Application

API REST desenvolvida com Spring Boot 3.2, Java 17, PostgreSQL e ActiveMQ.

## Tecnologias Utilizadas

- **Java 17**
- **Spring Boot 3.2.1**
- **Spring Data JPA** (Hibernate)
- **PostgreSQL** (Banco de dados)
- **ActiveMQ** (Mensageria JMS)
- **SpringDoc OpenAPI 3** (Swagger - Documentação API)
- **Lombok** (Redução de código boilerplate)
- **Spring Validation**
- **Spring Actuator** (Monitoramento)
- **Maven** (Gerenciamento de dependências)

## Estrutura do Projeto

```
api.trovian.java/
├── src/
│   ├── main/
│   │   ├── java/com/trovian/
│   │   │   ├── Application.java                 # Classe principal
│   │   │   ├── config/
│   │   │   │   ├── JmsConfig.java              # Configuração JMS
│   │   │   │   └── OpenApiConfig.java          # Configuração Swagger
│   │   │   ├── controller/
│   │   │   │   └── ProductController.java      # REST Controller
│   │   │   ├── dto/
│   │   │   │   └── ProductDTO.java             # Data Transfer Object
│   │   │   ├── entity/
│   │   │   │   └── Product.java                # Entidade JPA
│   │   │   ├── jms/
│   │   │   │   ├── MessageProducer.java        # Produtor de mensagens
│   │   │   │   └── MessageListener.java        # Consumidor de mensagens
│   │   │   ├── repository/
│   │   │   │   └── ProductRepository.java      # Repository JPA
│   │   │   └── service/
│   │   │       └── ProductService.java         # Lógica de negócio
│   │   └── resources/
│   │       └── application.yml                  # Configurações da aplicação
│   └── test/
├── docker-compose.yml                           # Docker para PostgreSQL e ActiveMQ
├── pom.xml                                      # Dependências Maven
└── README.md
```

## Pré-requisitos

- Java 17 ou superior
- Maven 3.6+
- Docker e Docker Compose (opcional, para executar PostgreSQL e ActiveMQ)

## Configuração

### 1. Iniciar PostgreSQL e ActiveMQ com Docker

```bash
docker-compose up -d
```

Isso iniciará:
- **PostgreSQL** na porta `5432`
  - Database: `troviandb`
  - User: `trovian`
  - Password: `trovian123`

- **ActiveMQ** nas portas:
  - `61616` (JMS)
  - `8161` (Web Console)
  - User: `admin`
  - Password: `admin`

### 2. Verificar os containers

```bash
docker-compose ps
```

### 3. Acessar o ActiveMQ Web Console

Abra o navegador em: http://localhost:8161

Credenciais:
- Username: `admin`
- Password: `admin`

## Como Executar a Aplicação

### Usando Maven

```bash
# Compilar o projeto
mvn clean install

# Executar a aplicação
mvn spring-boot:run
```

### Usando Java diretamente

```bash
# Compilar
mvn clean package

# Executar
java -jar target/api.trovian.java-1.0.0-SNAPSHOT.jar
```

A aplicação estará disponível em: **http://localhost:8080/api**

## Documentação da API (Swagger)

Após iniciar a aplicação, a documentação interativa da API estará disponível através do Swagger UI:

- **Swagger UI**: http://localhost:8080/api/swagger-ui/index.html
- **OpenAPI JSON**: http://localhost:8080/api/v3/api-docs
- **OpenAPI YAML**: http://localhost:8080/api/v3/api-docs.yaml

O Swagger UI permite:
- Visualizar todos os endpoints disponíveis
- Testar as APIs diretamente pelo navegador
- Ver exemplos de requisições e respostas
- Consultar schemas e validações

## Endpoints da API

### Products CRUD

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/products` | Lista todos os produtos |
| GET | `/api/products/{id}` | Busca produto por ID |
| GET | `/api/products/search?name={name}` | Busca produtos por nome |
| POST | `/api/products` | Cria novo produto |
| PUT | `/api/products/{id}` | Atualiza produto existente |
| DELETE | `/api/products/{id}` | Remove produto |

### Exemplos de requisições

#### Criar um produto (POST)

```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Notebook Dell",
    "description": "Notebook Dell Inspiron 15",
    "price": 3500.00,
    "quantity": 10
  }'
```

#### Listar todos os produtos (GET)

```bash
curl http://localhost:8080/api/products
```

#### Buscar produto por ID (GET)

```bash
curl http://localhost:8080/api/products/1
```

#### Atualizar produto (PUT)

```bash
curl -X PUT http://localhost:8080/api/products/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Notebook Dell Atualizado",
    "description": "Notebook Dell Inspiron 15 - 16GB RAM",
    "price": 4000.00,
    "quantity": 5
  }'
```

#### Deletar produto (DELETE)

```bash
curl -X DELETE http://localhost:8080/api/products/1
```

#### Buscar por nome (GET)

```bash
curl "http://localhost:8080/api/products/search?name=Dell"
```

## ActiveMQ / JMS

A aplicação possui configuração completa de JMS com filas:

- `product.queue` - Fila de produtos
- `notification.queue` - Fila de notificações

### Producer

O `MessageProducer` pode ser injetado em qualquer componente para enviar mensagens:

```java
@Autowired
private MessageProducer messageProducer;

messageProducer.sendProductMessage("Nova mensagem de produto");
messageProducer.sendNotification("Notificação importante");
```

### Listener

O `MessageListener` escuta automaticamente as filas configuradas e processa as mensagens recebidas.

## Actuator Endpoints

A aplicação expõe endpoints de monitoramento:

- http://localhost:8080/api/actuator/health
- http://localhost:8080/api/actuator/info
- http://localhost:8080/api/actuator/metrics
- http://localhost:8080/api/actuator/env

## Variáveis de Ambiente

Você pode sobrescrever as configurações usando variáveis de ambiente:

```bash
# Database
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=troviandb
export DB_USER=trovian
export DB_PASSWORD=trovian123

# ActiveMQ
export ACTIVEMQ_BROKER_URL=tcp://localhost:61616
export ACTIVEMQ_USER=admin
export ACTIVEMQ_PASSWORD=admin

# Server
export SERVER_PORT=8080
```

## Parando os Containers Docker

```bash
docker-compose down
```

Para remover também os volumes:

```bash
docker-compose down -v
```

## Logs

Os logs da aplicação estão configurados para exibir:
- Queries SQL (formatadas)
- Mensagens JMS
- Informações de debug do pacote `com.trovian`

## Próximos Passos

- Adicionar testes unitários e de integração
- Implementar tratamento de exceções global
- Adicionar segurança (Spring Security)
- Implementar paginação nos endpoints
- Configurar perfis de ambiente (dev, prod)

## Licença

Este projeto é de uso livre para fins educacionais.
