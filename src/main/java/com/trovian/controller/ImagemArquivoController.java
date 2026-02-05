package com.trovian.controller;

import com.trovian.dto.ImagemArquivoDTO;
import com.trovian.service.ImagemArquivoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/imagem-arquivo")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Imagem Arquivo", description = "Gerenciamento de imagens de arquivos")
public class ImagemArquivoController {

    @Autowired
    private ImagemArquivoService imagemArquivoService;

    @GetMapping("/conta-receber/{id}")
    @Operation(summary = "Buscar imagem do cte conta a receber por ID")
    public ResponseEntity<ImagemArquivoDTO> findByContaReceber(
            @Parameter(description = "ID da conta receber") @PathVariable Long id
    ) {
        ImagemArquivoDTO imagem = imagemArquivoService.findByContaReceberId(id);
        return ResponseEntity.ok(imagem);
    }

    @GetMapping("/abastecimento/{id}")
    @Operation(summary = "Buscar imagem abastecimento por ID")
    public ResponseEntity<ImagemArquivoDTO> findByAbastecimento(
            @Parameter(description = "ID do abastecimento") @PathVariable Long id
    ) {
        ImagemArquivoDTO imagem = imagemArquivoService.findByAbastecimentoId(id);
        return ResponseEntity.ok(imagem);
    }

}
