package com.trovian.service;

import com.trovian.dto.ViagemDTO;
import com.trovian.entity.*;
import com.trovian.enums.StatusViagem;
import com.trovian.repository.*;
import com.trovian.util.builders.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Testes unitários para ViagemService.
 * Usa Mockito puro - nenhum contexto Spring é carregado.
 */
@ExtendWith(MockitoExtension.class)
class ViagemServiceTest {

    @InjectMocks
    private ViagemService viagemService;

    @Mock private ViagemRepository viagemRepository;
    @Mock private VeiculoRepository veiculoRepository;
    @Mock private MotoristaRepository motoristaRepository;
    @Mock private RotaRepository rotaRepository;
    @Mock private AbastecimentoRepository abastecimentoRepository;
    @Mock private ClienteRepository clienteRepository;
    @Mock private ConsumoDetalhadoRepository consumoDetalhadoRepository;
    @Mock private ComissaoMotoristaService comissaoMotoristaService;
    @Mock private ChecklistRealizadoService checklistRealizadoService;
    @Mock private ContaReceberService contaReceberService;

    // ========== TESTES: findAll ==========

    @Test
    @DisplayName("findAll deve retornar página vazia quando não há viagens")
    void findAll_quandoNaoHaViagens_deveRetornarPaginaVazia() {
        Pageable pageable = PageRequest.of(0, 10);
        given(viagemRepository.findAll(pageable)).willReturn(Page.empty());

        Page<ViagemDTO> resultado = viagemService.findAll(pageable);

        assertThat(resultado).isEmpty();
        verify(viagemRepository).findAll(pageable);
    }

    @Test
    @DisplayName("findAll deve mapear entidade para DTO corretamente")
    void findAll_comViagensExistentes_deveMapearParaDTO() {
        Pageable pageable = PageRequest.of(0, 10);
        Viagem viagem = buildViagemCompletoParaTestes();
        given(viagemRepository.findAll(pageable)).willReturn(new PageImpl<>(List.of(viagem)));

        Page<ViagemDTO> resultado = viagemService.findAll(pageable);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.getContent().get(0).getId()).isEqualTo(1L);
        assertThat(resultado.getContent().get(0).getStatusViagem()).isEqualTo(StatusViagem.ABERTA);
    }

    // ========== TESTES: findById ==========

    @Test
    @DisplayName("findById deve retornar DTO quando viagem existe")
    void findById_quandoExiste_deveRetornarDTO() {
        Viagem viagem = buildViagemCompletoParaTestes();
        given(viagemRepository.findById(1L)).willReturn(Optional.of(viagem));

        ViagemDTO resultado = viagemService.findById(1L);

        assertThat(resultado.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("findById deve lançar exceção quando viagem não existe")
    void findById_quandoNaoExiste_deveLancarExcecao() {
        given(viagemRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> viagemService.findById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("99");
    }

    // ========== TESTES: findByVeiculo ==========

    @Test
    @DisplayName("findByVeiculo deve retornar viagens do veículo informado")
    void findByVeiculo_comVeiculoId_deveRetornarPagina() {
        Pageable pageable = PageRequest.of(0, 10);
        given(viagemRepository.findByVeiculoId(1L, pageable)).willReturn(Page.empty());

        Page<ViagemDTO> resultado = viagemService.findByVeiculo(1L, pageable);

        assertThat(resultado).isEmpty();
        verify(viagemRepository).findByVeiculoId(1L, pageable);
    }

    // ========== TESTES: findByStatusViagem ==========

    @Test
    @DisplayName("findByStatusViagem deve filtrar por status corretamente")
    void findByStatusViagem_comStatus_deveRetornarPaginaFiltrada() {
        Pageable pageable = PageRequest.of(0, 10);
        given(viagemRepository.findByStatusViagem(StatusViagem.FECHADA, pageable)).willReturn(Page.empty());

        Page<ViagemDTO> resultado = viagemService.findByStatusViagem(StatusViagem.FECHADA, pageable);

        assertThat(resultado).isEmpty();
        verify(viagemRepository).findByStatusViagem(StatusViagem.FECHADA, pageable);
    }

    // ========== TESTES: delete ==========

    @Test
    @DisplayName("delete deve chamar deleteById quando viagem existe")
    void delete_quandoExiste_deveDeletar() {
        given(viagemRepository.existsById(1L)).willReturn(true);

        viagemService.delete(1L);

        verify(viagemRepository).deleteById(1L);
    }

    @Test
    @DisplayName("delete deve lançar exceção quando viagem não existe")
    void delete_quandoNaoExiste_deveLancarExcecao() {
        given(viagemRepository.existsById(99L)).willReturn(false);

        assertThatThrownBy(() -> viagemService.delete(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("99");

        verify(viagemRepository, never()).deleteById(any());
    }

    // ========== TESTES: calcular - Exceções ==========

    @Test
    @DisplayName("calcular deve lançar exceção quando cliente não encontrado")
    void calcular_quandoClienteNaoEncontrado_deveLancarExcecao() {
        ViagemDTO dto = ViagemDTOBuilder.umaViagem().build();
        given(clienteRepository.findById(dto.getClienteId())).willReturn(Optional.empty());

        assertThatThrownBy(() -> viagemService.calcular(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Cliente");
    }

    @Test
    @DisplayName("calcular deve lançar exceção quando veículo não encontrado")
    void calcular_quandoVeiculoNaoEncontrado_deveLancarExcecao() {
        ViagemDTO dto = ViagemDTOBuilder.umaViagem().build();
        given(clienteRepository.findById(dto.getClienteId()))
                .willReturn(Optional.of(ClienteBuilder.umCliente().build()));
        given(veiculoRepository.findById(dto.getVeiculoId())).willReturn(Optional.empty());

        assertThatThrownBy(() -> viagemService.calcular(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Veículo");
    }

    @Test
    @DisplayName("calcular deve lançar exceção quando motorista não encontrado")
    void calcular_quandoMotoristaVNaoEncontrado_deveLancarExcecao() {
        ViagemDTO dto = ViagemDTOBuilder.umaViagem().build();
        given(clienteRepository.findById(dto.getClienteId()))
                .willReturn(Optional.of(ClienteBuilder.umCliente().build()));
        given(veiculoRepository.findById(dto.getVeiculoId()))
                .willReturn(Optional.of(VeiculoBuilder.umVeiculo().build()));
        given(motoristaRepository.findById(dto.getMotoristaId())).willReturn(Optional.empty());

        assertThatThrownBy(() -> viagemService.calcular(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Motorista");
    }

    @Test
    @DisplayName("calcular deve lançar exceção quando rota de ida não encontrada")
    void calcular_quandoRotaIdaNaoEncontrada_deveLancarExcecao() {
        ViagemDTO dto = ViagemDTOBuilder.umaViagem().build();
        given(clienteRepository.findById(dto.getClienteId()))
                .willReturn(Optional.of(ClienteBuilder.umCliente().build()));
        given(veiculoRepository.findById(dto.getVeiculoId()))
                .willReturn(Optional.of(VeiculoBuilder.umVeiculo().build()));
        given(motoristaRepository.findById(dto.getMotoristaId()))
                .willReturn(Optional.of(MotoristaBuilder.umMotorista().build()));
        given(rotaRepository.findById(dto.getRotaIdaId())).willReturn(Optional.empty());

        assertThatThrownBy(() -> viagemService.calcular(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Rota");
    }

    // ========== TESTES: calcular - Cálculos ==========

    @Test
    @DisplayName("calcular deve retornar receita bruta positiva com dados válidos")
    void calcular_comDadosValidos_deveRetornarReceitaBrutaPositiva() {
        ViagemDTO dto = ViagemDTOBuilder.umaViagem()
                .comQuantidadeToneladasIda(25.0)
                .comValorToneladaBrutaIda(BigDecimal.valueOf(200.00))
                .comImpostoBaseIda(BigDecimal.ZERO)
                .build();

        mockRepositoriosParaCalculo();

        ViagemDTO resultado = viagemService.calcular(dto);

        // 25 toneladas * R$200 = R$5000 bruto (imposto zero)
        assertThat(resultado.getValorTotalBrutoIda())
                .isEqualByComparingTo(BigDecimal.valueOf(5000.00));
    }

    @Test
    @DisplayName("calcular deve descontar imposto da receita bruta da ida")
    void calcular_comImposto_deveDescontarDaReceitaBruta() {
        ViagemDTO dto = ViagemDTOBuilder.umaViagem()
                .comQuantidadeToneladasIda(10.0)
                .comValorToneladaBrutaIda(BigDecimal.valueOf(100.00))
                .comImpostoBaseIda(BigDecimal.valueOf(10.0))  // 10% de imposto
                .build();

        mockRepositoriosParaCalculo();

        ViagemDTO resultado = viagemService.calcular(dto);

        // 10 * 100 = 1000, menos 10% = 900
        assertThat(resultado.getValorTotalBrutoIda())
                .isEqualByComparingTo(BigDecimal.valueOf(900.00));
    }

    @Test
    @DisplayName("calcular com veículo sem carga máxima deve usar fator de carga 1.0")
    void calcular_comVeiculoSemCargaMaxima_deveUsarFatorCargaPadrao() {
        ViagemDTO dto = ViagemDTOBuilder.umaViagem().build();
        mockRepositoriosParaCalculo(VeiculoBuilder.umVeiculo().semCargaMaxima().build());

        ViagemDTO resultado = viagemService.calcular(dto);

        assertThat(resultado.getFatorCarga()).isEqualTo(1.0);
    }

    @Test
    @DisplayName("calcular com motorista sem comissão deve definir comissão como zero")
    void calcular_comMotoristaSemComissao_deveDefinirComissaoZero() {
        ViagemDTO dto = ViagemDTOBuilder.umaViagem().build();
        mockRepositoriosParaCalculo(
                VeiculoBuilder.umVeiculo().build(),
                MotoristaBuilder.umMotorista().semComissao().build()
        );

        ViagemDTO resultado = viagemService.calcular(dto);

        assertThat(resultado.getComissaoMotorista())
                .isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("calcular deve gerar resultado final com lucro líquido")
    void calcular_comDadosCompletos_deveGerarResultadoFinal() {
        ViagemDTO dto = ViagemDTOBuilder.umaViagem().build();
        mockRepositoriosParaCalculo();

        ViagemDTO resultado = viagemService.calcular(dto);

        assertThat(resultado.getValorTotalBrutoViagem()).isNotNull();
        assertThat(resultado.getValorTotalCustos()).isNotNull();
        assertThat(resultado.getValorTotalLiquido()).isNotNull();
        assertThat(resultado.getMargemPercentual()).isNotNull();
    }

    // ========== HELPERS ==========

    private void mockRepositoriosParaCalculo() {
        mockRepositoriosParaCalculo(
                VeiculoBuilder.umVeiculo().build(),
                MotoristaBuilder.umMotorista().build()
        );
    }

    private void mockRepositoriosParaCalculo(Veiculo veiculo) {
        mockRepositoriosParaCalculo(veiculo, MotoristaBuilder.umMotorista().build());
    }

    private void mockRepositoriosParaCalculo(Veiculo veiculo, Motorista motorista) {
        given(clienteRepository.findById(any()))
                .willReturn(Optional.of(ClienteBuilder.umCliente().build()));
        given(veiculoRepository.findById(any()))
                .willReturn(Optional.of(veiculo));
        given(motoristaRepository.findById(any()))
                .willReturn(Optional.of(motorista));
        given(rotaRepository.findById(any()))
                .willReturn(Optional.of(RotaBuilder.umaRota().build()));
    }

    /**
     * Monta um Viagem completo com todas as associações necessárias para toDTO().
     */
    private Viagem buildViagemCompletoParaTestes() {
        Viagem viagem = new Viagem();
        viagem.setId(1L);
        viagem.setCliente(ClienteBuilder.umCliente().build());
        viagem.setVeiculo(VeiculoBuilder.umVeiculo().build());
        viagem.setMotorista(MotoristaBuilder.umMotorista().build());
        viagem.setRotaIda(RotaBuilder.umaRota().build());
        viagem.setDataViagem(new Date());
        viagem.setStatus(true);
        viagem.setStatusViagem(StatusViagem.ABERTA);
        viagem.setQuantidadeToneladasIda(25.0);
        viagem.setValorToneladaBrutaIda(BigDecimal.valueOf(200));
        viagem.setImpostoBaseIda(BigDecimal.ZERO);
        viagem.setValorTotalBrutoIda(BigDecimal.valueOf(5000));
        viagem.setValorTotalBrutoVolta(BigDecimal.ZERO);
        viagem.setValorTotalBrutoViagem(BigDecimal.valueOf(5000));
        viagem.setValorTotalCombustivel(BigDecimal.valueOf(500));
        viagem.setValorTotalPedagios(BigDecimal.valueOf(225));
        viagem.setComissaoMotorista(BigDecimal.valueOf(400));
        viagem.setValorTotalCustos(BigDecimal.valueOf(1125));
        viagem.setValorTotalLiquido(BigDecimal.valueOf(3875));
        viagem.setMargemPercentual(77.5);
        viagem.setMediaVeiculoKmL(2.5);
        viagem.setAndarVazioVolta(false);
        return viagem;
    }
}
