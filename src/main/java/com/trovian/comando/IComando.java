package com.trovian.comando;

import com.trovian.entity.Equipamento;
import com.trovian.entity.Veiculo;

public interface IComando {
    void setEquipamento(Equipamento equipamento);
    void bloquear(Veiculo veiculo);
    void desbloquear(Veiculo veiculo);
    void versaoDoFirmaware(Veiculo veiculo);
    void buscarNumeroDeSerie(Veiculo veiculo);
    void bloquearIbutton(Veiculo veiculo);
    void desbloquearIbutton(Veiculo veiculo);
}
