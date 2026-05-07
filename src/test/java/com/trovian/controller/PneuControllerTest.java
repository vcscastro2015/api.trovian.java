package com.trovian.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trovian.dto.CpkPneuDTO;
import com.trovian.dto.PneuDTO;
import com.trovian.enums.StatusPneu;
import com.trovian.service.CustomUserDetailsService;
import com.trovian.service.JwtService;
import com.trovian.service.PneuService;
import com.trovian.service.RelatorioPneuService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PneuController.class)
class PneuControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PneuService pneuService;

    @MockBean
    private RelatorioPneuService relatorioPneuService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @WithMockUser
    @DisplayName("POST /pneu deve retornar 201 quando dados são válidos")
    void cadastrar_comDadosValidos_deveRetornar201() throws Exception {
        PneuDTO dto = new PneuDTO();
        dto.setNumeroDot("DOT HJ8X RWLX 2423");
        dto.setClienteId(1L);

        PneuDTO resposta = new PneuDTO();
        resposta.setId(1L);
        resposta.setNumeroDot("DOT HJ8X RWLX 2423");
        resposta.setStatus(StatusPneu.NOVO);

        given(pneuService.cadastrar(any(PneuDTO.class))).willReturn(resposta);

        mockMvc.perform(post("/pneu")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.numeroDot").value("DOT HJ8X RWLX 2423"))
                .andExpect(jsonPath("$.status").value("NOVO"));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /pneu deve retornar 200 com lista paginada")
    void listar_deveRetornar200() throws Exception {
        PneuDTO pneuDTO = new PneuDTO();
        pneuDTO.setId(1L);
        pneuDTO.setNumeroDot("DOT-001");

        Page<PneuDTO> page = new PageImpl<>(List.of(pneuDTO));
        given(pneuService.listar(eq(1L), any(), any(Pageable.class))).willReturn(page);

        mockMvc.perform(get("/pneu")
                        .param("clienteId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /pneu/{id}/cpk deve retornar CPK calculado")
    void calcularCpk_deveRetornarCpk() throws Exception {
        CpkPneuDTO cpk = new CpkPneuDTO();
        cpk.setPneuId(1L);
        cpk.setCpk(new BigDecimal("0.0100"));
        cpk.setCustoTotal(new BigDecimal("450.00"));
        cpk.setKmTotal(45000);

        given(pneuService.calcularCpk(1L)).willReturn(cpk);

        mockMvc.perform(get("/pneu/1/cpk"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cpk").value(0.01))
                .andExpect(jsonPath("$.kmTotal").value(45000));
    }

    @Test
    @WithMockUser
    @DisplayName("DELETE /pneu/{id} deve retornar 204 quando pneu é descartado")
    void descartar_deveRetornar204() throws Exception {
        mockMvc.perform(delete("/pneu/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
}
