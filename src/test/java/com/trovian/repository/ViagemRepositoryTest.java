package com.trovian.repository;

import com.trovian.enums.StatusViagem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes de slice para ViagemRepository.
 *
 * Usa @DataJpaTest com H2 in-memory para testar apenas as queries JPA derivadas.
 * O banco H2 é populado via Hibernate DDL auto (create-drop), sem Liquibase.
 *
 * NOTA: Testes de queries QueryDSL (ViagemRepositoryCustom) requerem
 * configuração adicional de geração de Q-classes e devem ser adicionados
 * em testes de integração separados.
 */
@DataJpaTest
@TestPropertySource(properties = {
        "spring.liquibase.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class ViagemRepositoryTest {

    @Autowired
    private ViagemRepository viagemRepository;

    // ========== TESTES: contexto e autowiring ==========

    @Test
    @DisplayName("ViagemRepository deve ser injetado corretamente")
    void contextLoads_repositoryDeveSerInjetado() {
        assertThat(viagemRepository).isNotNull();
    }

    // ========== TESTES: findByStatusViagem ==========

    @Test
    @DisplayName("findByStatusViagem deve retornar página vazia quando banco está vazio")
    void findByStatusViagem_quandoBancoVazio_deveRetornarPaginaVazia() {
        Page<?> resultado = viagemRepository.findByStatusViagem(
                StatusViagem.ABERTA,
                PageRequest.of(0, 10)
        );

        assertThat(resultado).isEmpty();
        assertThat(resultado.getTotalElements()).isZero();
    }

    @Test
    @DisplayName("findByStatusViagem deve retornar página vazia para status FECHADA")
    void findByStatusViagem_comStatusFechada_deveRetornarVazio() {
        Page<?> resultado = viagemRepository.findByStatusViagem(
                StatusViagem.FECHADA,
                PageRequest.of(0, 10)
        );

        assertThat(resultado).isEmpty();
    }

    // ========== TESTES: findByVeiculoId ==========

    @Test
    @DisplayName("findByVeiculoId deve retornar página vazia quando nenhuma viagem existe")
    void findByVeiculoId_quandoBancoVazio_deveRetornarPaginaVazia() {
        Page<?> resultado = viagemRepository.findByVeiculoId(
                999L,
                PageRequest.of(0, 10)
        );

        assertThat(resultado).isEmpty();
    }

    // ========== TESTES: findByMotoristaId ==========

    @Test
    @DisplayName("findByMotoristaId deve retornar página vazia quando nenhuma viagem existe")
    void findByMotoristaId_quandoBancoVazio_deveRetornarPaginaVazia() {
        Page<?> resultado = viagemRepository.findByMotoristaId(
                999L,
                PageRequest.of(0, 10)
        );

        assertThat(resultado).isEmpty();
    }

    // ========== TESTES: findByClienteId ==========

    @Test
    @DisplayName("findByClienteId deve retornar página vazia quando nenhuma viagem existe")
    void findByClienteId_quandoBancoVazio_deveRetornarPaginaVazia() {
        Page<?> resultado = viagemRepository.findByClienteId(
                999L,
                PageRequest.of(0, 10)
        );

        assertThat(resultado).isEmpty();
    }

    // ========== TESTES: existsById / count ==========

    @Test
    @DisplayName("existsById deve retornar false para ID inexistente")
    void existsById_comIdInexistente_deveRetornarFalse() {
        assertThat(viagemRepository.existsById(9999L)).isFalse();
    }

    @Test
    @DisplayName("count deve retornar zero quando banco está vazio")
    void count_quandoBancoVazio_deveRetornarZero() {
        assertThat(viagemRepository.count()).isZero();
    }
}
