package com.trovian.service;

import com.trovian.dto.InspecaoPneuDTO;
import com.trovian.entity.*;
import com.trovian.enums.CondicaoVisual;
import com.trovian.enums.PrioridadeAlerta;
import com.trovian.enums.TipoAlerta;
import com.trovian.repository.*;
import com.trovian.util.builders.ClienteBuilder;
import com.trovian.util.builders.PneuBuilder;
import com.trovian.util.builders.VeiculoBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InspecaoPneuServiceTest {

    @InjectMocks
    private InspecaoPneuService inspecaoPneuService;

    @Mock private InspecaoPneuRepository inspecaoPneuRepository;
    @Mock private PneuRepository pneuRepository;
    @Mock private VeiculoRepository veiculoRepository;
    @Mock private MotoristaRepository motoristaRepository;
    @Mock private AlocacaoPneuRepository alocacaoPneuRepository;
    @Mock private AlertaManutencaoRepository alertaManutencaoRepository;

    // ========== TESTES: alertas de sulco ==========

    @Test
    @DisplayName("registrarInspecao deve gerar alerta CRITICA quando sulco está abaixo de 1.6mm")
    void registrarInspecao_comSulcoAbaixoMinimoLegal_deveGerarAlertaCritica() {
        Cliente cliente = ClienteBuilder.umCliente().build();
        Pneu pneu = PneuBuilder.umPneu().comCliente(cliente).build();
        Veiculo veiculo = VeiculoBuilder.umVeiculo().build();

        InspecaoPneuDTO dto = new InspecaoPneuDTO();
        dto.setPneuId(1L);
        dto.setVeiculoId(1L);
        dto.setProfundidadeSulco(new BigDecimal("1.2"));

        given(pneuRepository.findById(1L)).willReturn(Optional.of(pneu));
        given(veiculoRepository.findById(1L)).willReturn(Optional.of(veiculo));

        InspecaoPneu inspecaoSalva = new InspecaoPneu();
        inspecaoSalva.setId(1L);
        inspecaoSalva.setPneu(pneu);
        inspecaoSalva.setVeiculo(veiculo);
        inspecaoSalva.setProfundidadeSulco(new BigDecimal("1.2"));
        inspecaoSalva.setGeraAlerta(false);
        given(inspecaoPneuRepository.save(any(InspecaoPneu.class))).willReturn(inspecaoSalva);

        inspecaoPneuService.registrarInspecao(dto);

        ArgumentCaptor<AlertaManutencao> alertaCaptor = ArgumentCaptor.forClass(AlertaManutencao.class);
        verify(alertaManutencaoRepository).save(alertaCaptor.capture());

        AlertaManutencao alertaGerado = alertaCaptor.getValue();
        assertThat(alertaGerado.getTipoAlerta()).isEqualTo(TipoAlerta.TROCA_PNEUS);
        assertThat(alertaGerado.getPrioridade()).isEqualTo(PrioridadeAlerta.CRITICA);
    }

    @Test
    @DisplayName("registrarInspecao deve gerar alerta ALTA quando sulco está entre 1.6mm e 3.0mm")
    void registrarInspecao_comSulcoAbaixoRecomendado_deveGerarAlertaAlta() {
        Cliente cliente = ClienteBuilder.umCliente().build();
        Pneu pneu = PneuBuilder.umPneu().comCliente(cliente).build();
        Veiculo veiculo = VeiculoBuilder.umVeiculo().build();

        InspecaoPneuDTO dto = new InspecaoPneuDTO();
        dto.setPneuId(1L);
        dto.setVeiculoId(1L);
        dto.setProfundidadeSulco(new BigDecimal("2.5"));

        given(pneuRepository.findById(1L)).willReturn(Optional.of(pneu));
        given(veiculoRepository.findById(1L)).willReturn(Optional.of(veiculo));

        InspecaoPneu inspecaoSalva = new InspecaoPneu();
        inspecaoSalva.setId(1L);
        inspecaoSalva.setPneu(pneu);
        inspecaoSalva.setVeiculo(veiculo);
        inspecaoSalva.setProfundidadeSulco(new BigDecimal("2.5"));
        inspecaoSalva.setGeraAlerta(false);
        given(inspecaoPneuRepository.save(any(InspecaoPneu.class))).willReturn(inspecaoSalva);

        inspecaoPneuService.registrarInspecao(dto);

        ArgumentCaptor<AlertaManutencao> alertaCaptor = ArgumentCaptor.forClass(AlertaManutencao.class);
        verify(alertaManutencaoRepository).save(alertaCaptor.capture());

        AlertaManutencao alertaGerado = alertaCaptor.getValue();
        assertThat(alertaGerado.getTipoAlerta()).isEqualTo(TipoAlerta.TROCA_PNEUS);
        assertThat(alertaGerado.getPrioridade()).isEqualTo(PrioridadeAlerta.ALTA);
    }

    @Test
    @DisplayName("registrarInspecao não deve gerar alerta quando sulco está acima de 3mm")
    void registrarInspecao_comSulcoBom_naoDeveGerarAlerta() {
        Cliente cliente = ClienteBuilder.umCliente().build();
        Pneu pneu = PneuBuilder.umPneu().comCliente(cliente).build();
        Veiculo veiculo = VeiculoBuilder.umVeiculo().build();

        InspecaoPneuDTO dto = new InspecaoPneuDTO();
        dto.setPneuId(1L);
        dto.setVeiculoId(1L);
        dto.setProfundidadeSulco(new BigDecimal("8.0"));

        given(pneuRepository.findById(1L)).willReturn(Optional.of(pneu));
        given(veiculoRepository.findById(1L)).willReturn(Optional.of(veiculo));

        InspecaoPneu inspecaoSalva = new InspecaoPneu();
        inspecaoSalva.setId(1L);
        inspecaoSalva.setPneu(pneu);
        inspecaoSalva.setVeiculo(veiculo);
        inspecaoSalva.setProfundidadeSulco(new BigDecimal("8.0"));
        inspecaoSalva.setGeraAlerta(false);
        given(inspecaoPneuRepository.save(any(InspecaoPneu.class))).willReturn(inspecaoSalva);

        inspecaoPneuService.registrarInspecao(dto);

        verify(alertaManutencaoRepository, never()).save(any());
    }

    @Test
    @DisplayName("registrarInspecao deve gerar alerta MEDIA quando pressão está fora de ±10%")
    void registrarInspecao_comPressaoForaDaFaixa_deveGerarAlertaMedia() {
        Cliente cliente = ClienteBuilder.umCliente().build();
        Pneu pneu = PneuBuilder.umPneu().comCliente(cliente).build();
        Veiculo veiculo = VeiculoBuilder.umVeiculo().build();

        InspecaoPneuDTO dto = new InspecaoPneuDTO();
        dto.setPneuId(1L);
        dto.setVeiculoId(1L);
        dto.setPressaoMedida(new BigDecimal("90.0"));
        dto.setPressaoRecomendada(new BigDecimal("120.0"));

        given(pneuRepository.findById(1L)).willReturn(Optional.of(pneu));
        given(veiculoRepository.findById(1L)).willReturn(Optional.of(veiculo));

        InspecaoPneu inspecaoSalva = new InspecaoPneu();
        inspecaoSalva.setId(1L);
        inspecaoSalva.setPneu(pneu);
        inspecaoSalva.setVeiculo(veiculo);
        inspecaoSalva.setPressaoMedida(new BigDecimal("90.0"));
        inspecaoSalva.setPressaoRecomendada(new BigDecimal("120.0"));
        inspecaoSalva.setGeraAlerta(false);
        given(inspecaoPneuRepository.save(any(InspecaoPneu.class))).willReturn(inspecaoSalva);

        inspecaoPneuService.registrarInspecao(dto);

        ArgumentCaptor<AlertaManutencao> alertaCaptor = ArgumentCaptor.forClass(AlertaManutencao.class);
        verify(alertaManutencaoRepository).save(alertaCaptor.capture());

        assertThat(alertaCaptor.getValue().getPrioridade()).isEqualTo(PrioridadeAlerta.MEDIA);
        assertThat(alertaCaptor.getValue().getTipoAlerta()).isEqualTo(TipoAlerta.MANUTENCAO_PENDENTE);
    }

    @Test
    @DisplayName("registrarInspecao deve gerar alerta quando condição visual é CRITICA")
    void registrarInspecao_comCondicaoVisualCritica_deveGerarAlerta() {
        Cliente cliente = ClienteBuilder.umCliente().build();
        Pneu pneu = PneuBuilder.umPneu().comCliente(cliente).build();
        Veiculo veiculo = VeiculoBuilder.umVeiculo().build();

        InspecaoPneuDTO dto = new InspecaoPneuDTO();
        dto.setPneuId(1L);
        dto.setVeiculoId(1L);
        dto.setCondicaoVisual(CondicaoVisual.CRITICA);

        given(pneuRepository.findById(1L)).willReturn(Optional.of(pneu));
        given(veiculoRepository.findById(1L)).willReturn(Optional.of(veiculo));

        InspecaoPneu inspecaoSalva = new InspecaoPneu();
        inspecaoSalva.setId(1L);
        inspecaoSalva.setPneu(pneu);
        inspecaoSalva.setVeiculo(veiculo);
        inspecaoSalva.setCondicaoVisual(CondicaoVisual.CRITICA);
        inspecaoSalva.setGeraAlerta(false);
        given(inspecaoPneuRepository.save(any(InspecaoPneu.class))).willReturn(inspecaoSalva);

        inspecaoPneuService.registrarInspecao(dto);

        verify(alertaManutencaoRepository).save(any(AlertaManutencao.class));
    }
}
