package com.trovian.dto;

import com.trovian.enums.FuncaoLocal;
import com.trovian.enums.TipoLocal;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para Local")
public class LocalDTO {

    @Schema(description = "ID do local", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotNull(message = "Nome é obrigatório")
    @NotBlank(message = "Nome não pode ser vazio")
    @Size(min = 1, max = 255, message = "Nome deve ter entre 1 e 255 caracteres")
    @Schema(description = "Nome do local", example = "Armazém Central", required = true)
    private String nome;

    @NotNull(message = "Status é obrigatório")
    @Schema(description = "Status do local (ativo/inativo)", example = "true", required = true)
    private Boolean ativo;

    @Schema(description = "Código único do local", example = "12345")
    private Integer codigoUnico;

    @Schema(description = "Mostrar no mapa principal", example = "true")
    private Boolean mostrarNoMapaPrincipal;

    @Schema(description = "Mostrar nome no mapa", example = "true")
    private Boolean mostrarNomeNoMapa;

    @Schema(description = "Notificar evento", example = "false")
    private Boolean notificaEvento;

    @Schema(description = "Função do local", example = "CARGA", allowableValues = {"CARGA", "DESCARGA", "OUTROS"})
    private FuncaoLocal funcao;

    @Schema(description = "Endereço do local", example = "Rua das Flores, 123")
    private String endereco;

    @Schema(description = "Bairro", example = "Centro")
    private String bairro;

    @Schema(description = "Complemento", example = "Galpão 5")
    private String complemento;

    @Schema(description = "Cidade", example = "São Paulo")
    private String cidade;

    @Schema(description = "UF", example = "SP")
    private String uf;

    @Schema(description = "Tipo do local", example = "EMPRESA", allowableValues = {"EMPRESA", "OFICINA", "POSTO_DE_ABASTECIMENTO", "POSTO_DE_FISCALIZACAO"})
    private TipoLocal tipo;

    @Schema(description = "Permite descanso", example = "true")
    private Boolean permiteDescanso;

    @NotNull(message = "Cliente é obrigatório")
    @Schema(description = "ID do cliente", example = "1", required = true)
    private Long clienteId;

    @Schema(description = "Nome do cliente", accessMode = Schema.AccessMode.READ_ONLY)
    private String clienteNome;

    @Schema(description = "Lista de coordenadas do local")
    private List<CoordenadaDTO> listaDeCoordenadas = new ArrayList<>();

    @Schema(description = "Parâmetros do local")
    private ParametroLocalDTO parametroLocal;

    @Schema(description = "Data de cadastro", accessMode = Schema.AccessMode.READ_ONLY)
    private Date dataCadastro;

    @Schema(description = "Data de atualização", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updatedAt;
}
