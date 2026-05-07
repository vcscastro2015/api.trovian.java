package com.trovian.service;

import com.trovian.dto.AlocacaoPneuDTO;
import com.trovian.entity.AlocacaoPneu;
import com.trovian.entity.Pneu;
import com.trovian.entity.Veiculo;
import com.trovian.enums.MotivoDesmontagem;
import com.trovian.enums.PosicaoPneu;
import com.trovian.enums.StatusPneu;
import com.trovian.repository.AlocacaoPneuRepository;
import com.trovian.repository.PneuRepository;
import com.trovian.repository.VeiculoRepository;
import com.trovian.util.builders.PneuBuilder;
import com.trovian.util.builders.VeiculoBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AlocacaoPneuServiceTest {

    @InjectMocks
    private AlocacaoPneuService alocacaoPneuService;

    @Mock private AlocacaoPneuRepository alocacaoPneuRepository;
    @Mock private PneuRepository pneuRepository;
    @Mock private VeiculoRepository veiculoRepository;

    // ========== TESTES: montar ==========

    @Test
    @DisplayName("montar deve criar alocação e mudar status do pneu para EM_USO")
    void montar_comPneuNovoEPosicaoLivre_deveCriarAlocacao() {
        Pneu pneu = PneuBuilder.umPneu().comStatus(StatusPneu.NOVO).build();
        Veiculo veiculo = VeiculoBuilder.umVeiculo().build();

        given(pneuRepository.findById(1L)).willReturn(Optional.of(pneu));
        given(veiculoRepository.findById(1L)).willReturn(Optional.of(veiculo));
        given(alocacaoPneuRepository.existsAlocacaoAtivaByVeiculoIdAndPosicao(1L, PosicaoPneu.DIANTEIRO_ESQUERDO))
                .willReturn(false);

        AlocacaoPneu alocacaoSalva = new AlocacaoPneu();
        alocacaoSalva.setId(10L);
        alocacaoSalva.setPneu(pneu);
        alocacaoSalva.setVeiculo(veiculo);
        alocacaoSalva.setPosicao(PosicaoPneu.DIANTEIRO_ESQUERDO);
        alocacaoSalva.setDataCadastro(LocalDateTime.now());
        given(alocacaoPneuRepository.save(any(AlocacaoPneu.class))).willReturn(alocacaoSalva);
        given(pneuRepository.save(any(Pneu.class))).willReturn(pneu);

        AlocacaoPneuDTO resultado = alocacaoPneuService.montar(1L, 1L, PosicaoPneu.DIANTEIRO_ESQUERDO, 50000, "Técnico");

        assertThat(pneu.getStatus()).isEqualTo(StatusPneu.EM_USO);
        assertThat(resultado.getPosicao()).isEqualTo(PosicaoPneu.DIANTEIRO_ESQUERDO);
        verify(alocacaoPneuRepository).save(any(AlocacaoPneu.class));
    }

    @Test
    @DisplayName("montar deve lançar exceção quando posição já está ocupada")
    void montar_comPosicaoOcupada_deveLancarExcecao() {
        Pneu pneu = PneuBuilder.umPneu().comStatus(StatusPneu.NOVO).build();
        Veiculo veiculo = VeiculoBuilder.umVeiculo().build();

        given(pneuRepository.findById(1L)).willReturn(Optional.of(pneu));
        given(veiculoRepository.findById(1L)).willReturn(Optional.of(veiculo));
        given(alocacaoPneuRepository.existsAlocacaoAtivaByVeiculoIdAndPosicao(1L, PosicaoPneu.DIANTEIRO_ESQUERDO))
                .willReturn(true);

        assertThatThrownBy(() -> alocacaoPneuService.montar(1L, 1L, PosicaoPneu.DIANTEIRO_ESQUERDO, 50000, null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Já existe pneu montado");
    }

    @Test
    @DisplayName("montar deve lançar exceção quando pneu não está disponível")
    void montar_comPneuEmUso_deveLancarExcecao() {
        Pneu pneu = PneuBuilder.umPneu().comStatus(StatusPneu.EM_USO).build();
        given(pneuRepository.findById(1L)).willReturn(Optional.of(pneu));

        assertThatThrownBy(() -> alocacaoPneuService.montar(1L, 1L, PosicaoPneu.DIANTEIRO_ESQUERDO, 50000, null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("não disponível");
    }

    // ========== TESTES: desmontar ==========

    @Test
    @DisplayName("desmontar deve fechar alocação e acumular km no pneu")
    void desmontar_comAlocacaoAtiva_deveFecharEAcumularKm() {
        Pneu pneu = PneuBuilder.umPneu().comStatus(StatusPneu.EM_USO).comKmAcumulado(10000).build();
        Veiculo veiculo = VeiculoBuilder.umVeiculo().build();

        AlocacaoPneu alocacao = new AlocacaoPneu();
        alocacao.setId(10L);
        alocacao.setPneu(pneu);
        alocacao.setVeiculo(veiculo);
        alocacao.setKmMontagem(50000);
        alocacao.setDataMontagem(LocalDateTime.now().minusDays(30));
        alocacao.setDataCadastro(LocalDateTime.now().minusDays(30));

        given(alocacaoPneuRepository.findById(10L)).willReturn(Optional.of(alocacao));
        given(alocacaoPneuRepository.save(any(AlocacaoPneu.class))).willReturn(alocacao);
        given(pneuRepository.save(any(Pneu.class))).willReturn(pneu);

        alocacaoPneuService.desmontar(10L, 60000, MotivoDesmontagem.RODIZIO);

        assertThat(pneu.getKmAcumulado()).isEqualTo(20000);
        assertThat(alocacao.getDataRemocao()).isNotNull();
        assertThat(alocacao.getKmRemocao()).isEqualTo(60000);
    }

    @Test
    @DisplayName("desmontar deve lançar exceção quando alocação já está encerrada")
    void desmontar_comAlocacaoJaEncerrada_deveLancarExcecao() {
        AlocacaoPneu alocacao = new AlocacaoPneu();
        alocacao.setId(10L);
        alocacao.setDataRemocao(LocalDateTime.now().minusDays(1));
        alocacao.setDataCadastro(LocalDateTime.now().minusDays(10));

        given(alocacaoPneuRepository.findById(10L)).willReturn(Optional.of(alocacao));

        assertThatThrownBy(() -> alocacaoPneuService.desmontar(10L, 60000, MotivoDesmontagem.RODIZIO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("já foi encerrada");
    }
}
