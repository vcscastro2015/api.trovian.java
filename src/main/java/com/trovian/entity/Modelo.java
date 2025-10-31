package com.trovian.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "modelo")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Modelo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Fabricante é obrigatório")
    @Column(nullable = false, length = 200)
    private String fabricante;

    @NotBlank(message = "Marca é obrigatória")
    @Column(nullable = false, length = 200)
    private String marca;

    @NotBlank(message = "Tipo é obrigatório")
    @Column(nullable = false, length = 50)
    private String tipo; // "Equipamento" ou "Veiculo"

    @NotNull(message = "Status é obrigatório")
    @Column(nullable = false)
    private Boolean status;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
