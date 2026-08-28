package com.trovian.comando;

import com.trovian.entity.Equipamento;
import com.trovian.entity.Veiculo;
import com.trovian.enums.StatusComando;
import com.trovian.repository.ComandoRepository;
import org.apache.commons.lang3.NotImplementedException;

public class ComandoQueclink extends ComandoMaster implements IComando {

    private Equipamento _equipamento;
    private final String FILA_COMANDO = "COMANDO_QUECLINK";

    public ComandoQueclink(ComandoRepository comandoRepository) {
        super(comandoRepository);
    }

    @Override
    public void setEquipamento(Equipamento equipamento) {
        this._equipamento = equipamento;
    }

    @Override
    public void bloquear(Veiculo veiculo) {
        String comando = "AT+GTOUT=gv300can,2,0,0,0,0,0,,,,0,,,,0,5,,FFFF$";
        this.inserirComandoGenerico(comando, veiculo, FILA_COMANDO, StatusComando.BLOQUEAR);
    }

    @Override
    public void desbloquear(Veiculo veiculo) {
        String comando = "AT+GTOUT=gv300can,0,0,0,0,0,0,,,,0,,,,0,5,,FFFF$";
        this.inserirComandoGenerico(comando, veiculo, FILA_COMANDO, StatusComando.DESBLOQUEAR);
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
