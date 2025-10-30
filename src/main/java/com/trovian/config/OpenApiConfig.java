package com.trovian.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Trovian - Documentação")
                        .version("1.0.0")
                        .description("API REST desenvolvida com Spring Boot 3.2, Java 17, PostgreSQL e ActiveMQ. " +
                                "Esta API fornece endpoints para gerenciamento de produtos e integração com mensageria JMS.")
                        .contact(new Contact()
                                .name("Equipe Trovian")
                                .email("contato@trovian.com")
                                .url("https://trovian.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080/api")
                                .description("Servidor de Desenvolvimento"),
                        new Server()
                                .url("https://api.trovian.com")
                                .description("Servidor de Produção")
                ));
    }
}
