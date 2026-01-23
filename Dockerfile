# Build stage
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copiar arquivos de dependência primeiro (cache de camadas)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar código fonte e compilar
COPY src ./src
RUN mvn clean package -DskipTests -B

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Criar usuário não-root para segurança
RUN addgroup -S spring && adduser -S spring -G spring

# Copiar JAR do estágio de build
COPY --from=build /app/target/*.jar app.jar

# Alterar proprietário do arquivo
RUN chown spring:spring app.jar

# Usar usuário não-root
USER spring:spring

# Expor porta
EXPOSE 8081

# Health check
HEALTHCHECK --interval=30s --timeout=5s --start-period=120s --retries=5 \
  CMD nc -z localhost 8081 || exit 1

# Executar aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
