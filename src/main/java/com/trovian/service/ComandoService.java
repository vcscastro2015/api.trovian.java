package com.trovian.service;

import com.trovian.comando.ComandoQueclink;
import com.trovian.comando.ComandoSuntech;
import com.trovian.comando.IComando;
import com.trovian.dto.ComandoDTO;
import com.trovian.entity.Comando;
import com.trovian.entity.Equipamento;
import com.trovian.entity.Veiculo;
import com.trovian.repository.ComandoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class ComandoService {

    private final VeiculoService veiculoService;
    private final ComandoRepository comandoRepository;

    @Transactional
    public void inserirComando(Integer idComando, Long idVeiculo) {
        try {
            Veiculo veiculo = veiculoService.buscarVeiculo(idVeiculo);
            IComando comando = buscarComando(veiculo);
            if (idComando == 1) {
                Objects.requireNonNull(comando).bloquear(veiculo);
            }
            if (idComando == 2) {
                Objects.requireNonNull(comando).desbloquear(veiculo);
            }
        } catch (Exception e) {
            log.error("inserirComando", e);
        }
    }

    private IComando buscarComando(Veiculo veiculo){
        try {
            if(Objects.nonNull(veiculo.getEquipamento())){
                IComando ic = identificarEquipamento(veiculo.getEquipamento());
                if(Objects.nonNull(ic)){
                   return ic;
                }
            }
        }catch (Exception e){
            log.error("processarComando", e);
        }
        return null;
    }

    private IComando identificarEquipamento(Equipamento equipamento){
        IComando iComando = null;
        try {
            if("Suntech".equalsIgnoreCase(equipamento.getModelo().getFabricante())){
                iComando = new ComandoSuntech(comandoRepository);
                iComando.setEquipamento(equipamento);
            }
            if("Queclink".equalsIgnoreCase(equipamento.getModelo().getFabricante())){
                iComando = new ComandoQueclink(comandoRepository);
                iComando.setEquipamento(equipamento);
            }
        }catch (Exception e){
            log.error("identificarEquipamento", e);
        }
        return iComando;
    }

    public ComandoDTO buscarUltimoComando(Long idVeiculo) {
        return comandoRepository
                .findFirstByVeiculoIdOrderByDataCadastroDesc(idVeiculo)
                .map(this::toDTO)
                .orElse(null);
    }

    private ComandoDTO toDTO(Comando comando) {
        return ComandoDTO.builder()
                .id(comando.getId())
                .comando(comando.getComando())
                .sigla(comando.getSigla())
                .valorComando(comando.getValorComando())
                .comandoEnviado(comando.getComandoEnviado())
                .retornoRecebido(comando.getRetornoRecebido())
                .comandoEmProcesso(comando.getComandoEmProcesso())
                .comandoComErro(comando.getComandoComErro())
                .retornoAparelho(comando.getRetornoAparelho())
                .dependeOutroComando(comando.getDependeOutroComando())
                .idComandoReferente(comando.getIdComandoReferente())
                .mostrarRetorno(comando.getMostrarRetorno())
                .temMaisIbutton(comando.getTemMaisIbutton())
                .ultimoIbuttonInserido(comando.getUltimoIbuttonInserido())
                .cliente(comando.getCliente())
                .sequencia(comando.getSequencia())
                .nomeDaFila(comando.getNomeDaFila())
                .dataCadastro(comando.getDataCadastro())
                .dataFimProcesso(comando.getDataFimProcesso())
                .veiculoId(comando.getVeiculo().getId())
                .status(resolverStatus(comando))
                .build();
    }

    private String resolverStatus(Comando comando) {
        if (Boolean.TRUE.equals(comando.getComandoComErro())) return "ERRO";
        if (Boolean.TRUE.equals(comando.getRetornoRecebido()))  return "CONCLUIDO";
        if (Boolean.TRUE.equals(comando.getComandoEmProcesso())) return "EM_PROCESSO";
        if (Boolean.TRUE.equals(comando.getComandoEnviado()))    return "ENVIADO";
        return "PENDENTE";
    }

}
