package com.trovian.comando;

import com.trovian.entity.Equipamento;
import com.trovian.entity.Veiculo;
import com.trovian.enums.StatusComando;
import com.trovian.repository.ComandoRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class ComandoSuntech extends ComandoMaster implements IComando {

    private Equipamento _equipamento;

    public ComandoSuntech(ComandoRepository comandoRepository) {
        super(comandoRepository);
    }

    @Override
    public void setEquipamento(Equipamento equipamento) {
        this._equipamento = equipamento;
    }

    @Transactional
    @Override
    public void bloquear(Veiculo veiculo) {
        String comando = "ST300CMD;" + this._equipamento.getImei().trim() + ";02;Enable1";
        this.inserirComandoGenerico(comando, veiculo, "COMANDO_SUNTECH", StatusComando.BLOQUEAR);
    }

    @Transactional
    @Override
    public void desbloquear(Veiculo veiculo) {
        String comando = "ST300CMD;" + this._equipamento.getImei().trim() + ";02;Disable1";
        this.inserirComandoGenerico(comando, veiculo, "COMANDO_SUNTECH", StatusComando.DESBLOQUEAR);
    }

    @Override
    public void versaoDoFirmaware(Veiculo veiculo) {
        throw new NotImplementedException();
    }

    @Override
    public void buscarNumeroDeSerie(Veiculo veiculo) {
        throw new NotImplementedException();
    }

    @Override
    public void bloquearIbutton(Veiculo veiculo) {
        throw new NotImplementedException();
    }

    @Override
    public void desbloquearIbutton(Veiculo veiculo) {
        throw new NotImplementedException();
    }

}
