package com.trovian.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para representação de Veículo")
public class VeiculoDTO {

    @Schema(description = "ID do veículo", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Size(min = 1, max = 5, message = "Ano de fabricação deve ter entre 1 e 5 caracteres")
    @Schema(description = "Ano de fabricação", example = "2023")
    private String anoFabricacao;

    @Size(min = 1, max = 5, message = "Ano do modelo deve ter entre 1 e 5 caracteres")
    @Schema(description = "Ano do modelo", example = "2024")
    private String anoModelo;

    @Size(min = 1, max = 20, message = "Chassi deve ter entre 1 e 20 caracteres")
    @Schema(description = "Número do chassi", example = "9BWZZZ377VT004251")
    private String chassi;

    @Size(min = 1, max = 15, message = "Cor deve ter entre 1 e 15 caracteres")
    @Schema(description = "Cor do veículo", example = "Branco")
    private String cor;

    @Schema(description = "Data de cadastro", example = "2025-01-15T10:30:00", accessMode = Schema.AccessMode.READ_ONLY)
    private Date dataCadastro;

    @Schema(description = "Observações sobre o veículo", example = "Veículo em bom estado")
    private String observacao;

    @NotNull(message = "Placa é obrigatória")
    @Size(min = 1, max = 15, message = "Placa deve ter entre 1 e 15 caracteres")
    @Schema(description = "Placa do veículo", example = "ABC-1234", required = true)
    private String placa;

    @Schema(description = "Velocidade máxima (km/h)", example = "120")
    private Integer velocidadeMaxima;

    @Schema(description = "Velocidade máxima em condições de chuva (km/h)", example = "100")
    private Integer velocidadeMaximaChuva;

    @Schema(description = "Velocidade máxima de desaceleração (km/h)", example = "80")
    private Integer velocidadeMaximaDesaceleracao;

    @Schema(description = "Velocidade máxima em curvas (km/h)", example = "60")
    private Integer velocidadeMaximaCurva;

    @Size(min = 1, max = 255, message = "Renavam deve ter entre 1 e 255 caracteres")
    @Schema(description = "Número do Renavam", example = "12345678901")
    private String renavam;

    @NotNull(message = "Status é obrigatório")
    @Schema(description = "Indica se o veículo está ativo", example = "true")
    private Boolean status;

    @NotNull(message = "Tipo é obrigatório")
    @Size(min = 1, max = 20, message = "Tipo deve ter entre 1 e 20 caracteres")
    @Schema(description = "Tipo do veículo", example = "Carro", required = true,
            allowableValues = {"Moto", "Carro", "Onibus", "Caminhao", "Carreta", "Implemento"})
    private String tipo;

    @Schema(description = "Capacidade máxima de tração (toneladas)", example = "5.0")
    private Double capacidadeMaximaTracao;

    @Schema(description = "Usa entrada digital 1", example = "false")
    private Boolean usaEntradaDigitalUm;

    @Schema(description = "Usa entrada digital 2", example = "false")
    private Boolean usaEntradaDigitalDois;

    @Schema(description = "Usa entrada digital 3", example = "false")
    private Boolean usaEntradaDigitalTres;

    @Schema(description = "Usa entrada digital 4", example = "false")
    private Boolean usaEntradaDigitalQuatro;

    @Schema(description = "Possui alerta de excesso de velocidade", example = "true")
    private Boolean excessoVelocidade;

    @Schema(description = "Possui alerta de bateria baixa", example = "false")
    private Boolean bateriaCarroBaixa;

    @Schema(description = "Possui alerta de falta de energia principal", example = "false")
    private Boolean faltaEnergiaPrincipal;

    @Schema(description = "Possui controle de dias sem transmissão", example = "false")
    private Boolean quantidadeDiasSemTrasmissao;

    @Schema(description = "Possui alerta de sem comunicação", example = "false")
    private Boolean semComunicacao;

    @Schema(description = "Ativa exibição de rota no mapa", example = "true")
    private Boolean ativaRotaNoMapa;

    @Schema(description = "Ativa validação de cerca virtual", example = "true")
    private Boolean ativaValidacaoDeCerca;

    @Schema(description = "Possui troca de horímetro", example = "false")
    private Boolean trocaDeHorimetro;

    @Schema(description = "RPM modo econômico mínimo", example = "1000")
    private Integer rpmModoEconomicoMinimo;

    @Schema(description = "RPM modo econômico máximo", example = "1500")
    private Integer rpmModoEconomicoMaximo;

    @Schema(description = "RPM máximo", example = "5000")
    private Integer rpmMaximo;

    @Schema(description = "RPM início faixa azul", example = "1500")
    private Integer rpmInicioFaixaAzul;

    @Schema(description = "RPM fim faixa azul", example = "2000")
    private Integer rpmFimFaixaAzul;

    @Schema(description = "RPM início faixa econômica", example = "1000")
    private Integer rpmInicioFaixaEconomica;

    @Schema(description = "RPM fim faixa econômica", example = "1800")
    private Integer rpmFimFaixaEconomica;

    @Schema(description = "RPM início faixa verde", example = "1800")
    private Integer rpmInicioFaixaVerde;

    @Schema(description = "RPM fim faixa verde", example = "2500")
    private Integer rpmFimFaixaVerde;

    @Schema(description = "RPM início faixa amarela", example = "2500")
    private Integer rpmInicioFaixaAmarela;

    @Schema(description = "RPM fim faixa amarela", example = "3500")
    private Integer rpmFimFaixaAmarela;

    @Schema(description = "RPM início marcha lenta", example = "600")
    private Integer rpmInicioMarchaLenta;

    @Schema(description = "RPM fim marcha lenta", example = "900")
    private Integer rpmFimMarchaLenta;

    @Schema(description = "Gera endereço automaticamente", example = "true")
    private Boolean geraEnderecoAutomatico;

    @Schema(description = "Validar iButton", example = "false")
    private Boolean validarIbutton;

    @Schema(description = "Carga máxima (toneladas)", example = "10.5")
    private Double cargaMaxima;

    @Schema(description = "Tipo de combustível", example = "Diesel",
            allowableValues = {"Gasolina", "Alcool", "Diesel"})
    private String combustivel;

    @Schema(description = "Validar rota", example = "true")
    private Boolean validarRota;

    @NotNull(message = "Modelo é obrigatório")
    @Schema(description = "ID do modelo do veículo", example = "1", required = true)
    private Long modeloId;

    @Schema(description = "Marca do modelo (somente leitura)", example = "Gol", accessMode = Schema.AccessMode.READ_ONLY)
    private String modeloMarca;

    @Schema(description = "Fabricante do modelo (somente leitura)", example = "Volkswagen", accessMode = Schema.AccessMode.READ_ONLY)
    private String modeloFabricante;

    @Schema(description = "ID do equipamento (opcional)", example = "1")
    private Long equipamentoId;

    @Schema(description = "IMEI do equipamento (somente leitura)", example = "123456789012345", accessMode = Schema.AccessMode.READ_ONLY)
    private String equipamentoImei;

    @NotNull(message = "Cliente é obrigatório")
    @Schema(description = "ID do cliente proprietário do veículo", example = "1", required = true)
    private Long clienteId;

    @Schema(description = "Nome do cliente (somente leitura)", example = "João da Silva", accessMode = Schema.AccessMode.READ_ONLY)
    private String clienteNome;

    @Schema(description = "Data da última atualização", example = "2025-01-20T14:45:00", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updatedAt;
}
