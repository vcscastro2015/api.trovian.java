package com.trovian.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trovian.dto.ViagemDTO;
import com.trovian.enums.StatusViagem;
import com.trovian.service.CustomUserDetailsService;
import com.trovian.service.JwtService;
import com.trovian.service.ViagemService;
import com.trovian.util.builders.ViagemDTOBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de slice para ViagemController.
 *
 * Estratégia de segurança:
 * - SecurityConfig customizado é carregado normalmente pelo @WebMvcTest
 * - JwtService e CustomUserDetailsService são mockados via @MockBean
 *   (satisfaz as dependências de SecurityConfig e JwtAuthenticationFilter)
 * - Requisições sem header Authorization passam pelo filtro JWT sem autenticação
 * - @WithMockUser injeta um usuário autenticado no SecurityContext para cada teste
 */
@WebMvcTest(ViagemController.class)
class ViagemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ViagemService viagemService;

    // Necessários para que SecurityConfig e JwtAuthenticationFilter inicializem
    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    // ========== TESTES: GET /viagem ==========

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("GET /viagem deve retornar status 200 com página de viagens")
    void findAll_deveRetornar200ComPagina() throws Exception {
        ViagemDTO dto = ViagemDTOBuilder.umaViagem().comId(1L).build();
        given(viagemService.findAll(any(Pageable.class))).willReturn(new PageImpl<>(List.of(dto), PageRequest.of(0, 10), 1));

        mockMvc.perform(get("/viagem").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("GET /viagem deve retornar status 200 com página vazia quando não há viagens")
    void findAll_quandoVazio_deveRetornar200ComPaginaVazia() throws Exception {
        given(viagemService.findAll(any(Pageable.class))).willReturn(new PageImpl<>(List.of(), PageRequest.of(0, 10), 0));

        mockMvc.perform(get("/viagem").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    // ========== TESTES: GET /viagem/{id} ==========

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("GET /viagem/{id} deve retornar status 200 com viagem encontrada")
    void findById_quandoEncontrado_deveRetornar200() throws Exception {
        ViagemDTO dto = ViagemDTOBuilder.umaViagem().comId(1L)
                .comStatusViagem(StatusViagem.ABERTA)
                .build();
        given(viagemService.findById(1L)).willReturn(dto);

        mockMvc.perform(get("/viagem/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.statusViagem").value("ABERTA"));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("GET /viagem/{id} deve retornar status 400 quando RuntimeException (GlobalExceptionHandler)")
    void findById_quandoNaoEncontrado_deveRetornar400() throws Exception {
        given(viagemService.findById(99L)).willThrow(new RuntimeException("Viagem não encontrada com ID: 99"));

        mockMvc.perform(get("/viagem/99").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Viagem não encontrada com ID: 99"));
    }

    // ========== TESTES: GET /viagem/veiculo/{veiculoId} ==========

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("GET /viagem/veiculo/{veiculoId} deve retornar status 200 com viagens do veículo")
    void findByVeiculo_deveRetornar200() throws Exception {
        given(viagemService.findByVeiculo(any(Long.class), any(Pageable.class)))
                .willReturn(new PageImpl<>(List.of(), PageRequest.of(0, 10), 0));

        mockMvc.perform(get("/viagem/veiculo/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    // ========== TESTES: POST /viagem/calcular ==========

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("POST /viagem/calcular deve retornar status 200 com ViagemDTO calculado")
    void calcular_comDadosValidos_deveRetornar200() throws Exception {
        ViagemDTO input = ViagemDTOBuilder.umaViagem().build();
        ViagemDTO output = ViagemDTOBuilder.umaViagem().comId(null).build();
        output.setValorTotalBrutoIda(BigDecimal.valueOf(5000));
        output.setValorTotalBrutoViagem(BigDecimal.valueOf(5000));

        given(viagemService.calcular(any(ViagemDTO.class))).willReturn(output);

        mockMvc.perform(post("/viagem/calcular")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valorTotalBrutoViagem").value(5000));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("POST /viagem/calcular deve retornar status 400 quando dados obrigatórios faltam")
    void calcular_comDadosInvalidos_deveRetornar400() throws Exception {
        // DTO vazio — campos @NotNull não preenchidos disparam MethodArgumentNotValidException
        ViagemDTO dtoInvalido = new ViagemDTO();

        mockMvc.perform(post("/viagem/calcular")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoInvalido)))
                .andExpect(status().isBadRequest());
    }

    // ========== TESTES: POST /viagem ==========

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("POST /viagem deve retornar status 201 ao criar viagem com sucesso")
    void create_comDadosValidos_deveRetornar201() throws Exception {
        ViagemDTO input = ViagemDTOBuilder.umaViagem().build();
        ViagemDTO created = ViagemDTOBuilder.umaViagem().comId(10L).build();

        given(viagemService.create(any(ViagemDTO.class))).willReturn(created);

        mockMvc.perform(post("/viagem")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10));
    }

    // ========== TESTES: DELETE /viagem/{id} ==========

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("DELETE /viagem/{id} deve retornar status 204 ao deletar com sucesso")
    void delete_quandoExiste_deveRetornar204() throws Exception {
        mockMvc.perform(delete("/viagem/1").with(csrf()))
                .andExpect(status().isNoContent());

        verify(viagemService).delete(1L);
    }

    // ========== TESTES: GET /viagem/status-viagem/{statusViagem} ==========

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("GET /viagem/status-viagem/FECHADA deve filtrar por status")
    void findByStatusViagem_comStatusFechada_deveRetornar200() throws Exception {
        ViagemDTO dto = ViagemDTOBuilder.umaViagem().comStatusViagem(StatusViagem.FECHADA).build();
        given(viagemService.findByStatusViagem(any(StatusViagem.class), any(Pageable.class)))
                .willReturn(new PageImpl<>(List.of(dto), PageRequest.of(0, 10), 1));

        mockMvc.perform(get("/viagem/status-viagem/FECHADA").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }
}
