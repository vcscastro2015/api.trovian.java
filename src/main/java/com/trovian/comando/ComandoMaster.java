package com.trovian.comando;

import com.trovian.entity.Comando;
import com.trovian.entity.Veiculo;
import com.trovian.enums.StatusComando;
import com.trovian.repository.ComandoRepository;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Slf4j
public abstract class ComandoMaster {

    protected final ComandoRepository comandoRepository;

    protected ComandoMaster(ComandoRepository comandoRepository) {
        this.comandoRepository = comandoRepository;
    }

    public void inserirComandoGenerico(String valorComando, Veiculo veiculo, String fila, StatusComando statusComando) {
            try {
                Comando comando = new Comando();
                comando.setComando(statusComando.getDescricao());
                comando.setComandoEnviado(Boolean.FALSE);
                comando.setDataCadastro(LocalDateTime.now(ZoneId.of("America/Sao_Paulo")));
                comando.setRetornoRecebido(Boolean.FALSE);
                comando.setSigla(statusComando.getSigla());
                comando.setValorComando(valorComando);
                comando.setVeiculo(veiculo);
                comando.setComandoEmProcesso(Boolean.FALSE);
                comando.setMostrarRetorno(Boolean.FALSE);
                comando.setNomeDaFila(fila);
                this.comandoRepository.save(comando);
            } catch (Exception e) {
                log.error("inserirComandoGenerico", e);
            }
    }
}
