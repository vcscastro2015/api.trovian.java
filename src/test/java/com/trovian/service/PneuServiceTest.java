package com.trovian.service;

import com.trovian.dto.CpkPneuDTO;
import com.trovian.dto.PneuDTO;
import com.trovian.entity.Cliente;
import com.trovian.entity.Pneu;
import com.trovian.enums.StatusPneu;
import com.trovian.repository.*;
import com.trovian.util.builders.ClienteBuilder;
import com.trovian.util.builders.PneuBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PneuServiceTest {

    @InjectMocks
    private PneuService pneuService;

    @Mock private PneuRepository pneuRepository;
    @Mock private ClienteRepository clienteRepository;
    @Mock private FornecedorRepository fornecedorRepository;
    @Mock private AlocacaoPneuRepository alocacaoPneuRepository;
    @Mock private RecapagemPneuRepository recapagemPneuRepository;
    @Mock private AlertaManutencaoRepository alertaManutencaoRepository;
    @Mock private VeiculoRepository veiculoRepository;

    // ========== TESTES: cadastrar ==========

    @Test
    @DisplayName("cadastrar deve persistir pneu quando DOT é único para o cliente")
    void cadastrar_comDotUnico_devePersistirPneu() {
        Cliente cliente = ClienteBuilder.umCliente().build();
        PneuDTO dto = new PneuDTO();
        dto.setNumeroDot("DOT-UNICO-001");
        dto.setClienteId(1L);

        given(pneuRepository.findByNumeroDotAndClienteId("DOT-UNICO-001", 1L)).willReturn(Optional.empty());
        given(clienteRepository.findById(1L)).willReturn(Optional.of(cliente));
        Pneu pneuSalvo = PneuBuilder.umPneu().comDot("DOT-UNICO-001").comCliente(cliente).build();
        given(pneuRepository.save(any(Pneu.class))).willReturn(pneuSalvo);
        given(alocacaoPneuRepository.findAlocacaoAtivaByPneuId(any())).willReturn(Optional.empty());

        PneuDTO resultado = pneuService.cadastrar(dto);

        assertThat(resultado.getNumeroDot()).isEqualTo("DOT-UNICO-001");
        verify(pneuRepository).save(any(Pneu.class));
    }

    @Test
    @DisplayName("cadastrar deve lançar exceção quando DOT já existe para o cliente")
    void cadastrar_comDotDuplicado_deveLancarExcecao() {
        PneuDTO dto = new PneuDTO();
        dto.setNumeroDot("DOT-DUPLICADO");
        dto.setClienteId(1L);

        Pneu pneuExistente = PneuBuilder.umPneu().comDot("DOT-DUPLICADO").build();
        given(pneuRepository.findByNumeroDotAndClienteId("DOT-DUPLICADO", 1L))
                .willReturn(Optional.of(pneuExistente));

        assertThatThrownBy(() -> pneuService.cadastrar(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("DOT");
    }

    // ========== TESTES: descartar ==========

    @Test
    @DisplayName("descartar deve lançar exceção quando pneu está em uso")
    void descartar_comPneuEmUso_deveLancarExcecao() {
        Pneu pneu = PneuBuilder.umPneu().comStatus(StatusPneu.EM_USO).build();
        given(pneuRepository.findById(1L)).willReturn(Optional.of(pneu));

        assertThatThrownBy(() -> pneuService.descartar(1L, "motivo"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("em uso");
    }

    @Test
    @DisplayName("descartar deve mudar status para DESCARTADO quando pneu não está em uso")
    void descartar_comPneuDisponivel_deveMudarStatusParaDescartado() {
        Pneu pneu = PneuBuilder.umPneu().comStatus(StatusPneu.INATIVO).build();
        given(pneuRepository.findById(1L)).willReturn(Optional.of(pneu));
        given(pneuRepository.save(any(Pneu.class))).willReturn(pneu);

        pneuService.descartar(1L, "Fim de vida útil");

        assertThat(pneu.getStatus()).isEqualTo(StatusPneu.DESCARTADO);
        verify(pneuRepository).save(pneu);
    }

    // ========== TESTES: calcularCpk ==========

    @Test
    @DisplayName("calcularCpk deve retornar CPK correto para pneu com km acumulado")
    void calcularCpk_comDadosValidos_deveRetornarCpkCorreto() {
        Pneu pneu = PneuBuilder.umPneu()
                .comValorCompra(new BigDecimal("300.00"))
                .comKmAcumulado(45000)
                .build();
        given(pneuRepository.findById(1L)).willReturn(Optional.of(pneu));
        given(recapagemPneuRepository.sumValorRecapagensByPneuId(1L)).willReturn(new BigDecimal("150.00"));

        CpkPneuDTO resultado = pneuService.calcularCpk(1L);

        // CPK = (300 + 150) / 45000 = 0.01 R$/km
        assertThat(resultado.getCustoTotal()).isEqualByComparingTo(new BigDecimal("450.00"));
        assertThat(resultado.getCpk()).isEqualByComparingTo(new BigDecimal("0.0100"));
        assertThat(resultado.getKmTotal()).isEqualTo(45000);
    }

    @Test
    @DisplayName("calcularCpk deve retornar CPK zero quando km acumulado é zero")
    void calcularCpk_comKmZero_deveRetornarCpkZero() {
        Pneu pneu = PneuBuilder.umPneu().comKmAcumulado(0).build();
        given(pneuRepository.findById(1L)).willReturn(Optional.of(pneu));
        given(recapagemPneuRepository.sumValorRecapagensByPneuId(1L)).willReturn(BigDecimal.ZERO);

        CpkPneuDTO resultado = pneuService.calcularCpk(1L);

        assertThat(resultado.getCpk()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    // ========== TESTES: buscarPorId ==========

    @Test
    @DisplayName("buscarPorId deve lançar exceção quando pneu não existe")
    void buscarPorId_comIdInexistente_deveLancarExcecao() {
        given(pneuRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> pneuService.buscarPorId(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("não encontrado");
    }
}
