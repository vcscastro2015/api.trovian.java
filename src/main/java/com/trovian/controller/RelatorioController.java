package com.trovian.controller;

import com.trovian.dto.relatorios.*;
import com.trovian.entity.relatorios.CategoriaRelatorio;
import com.trovian.entity.relatorios.FormatoRelatorio;
import com.trovian.service.RelatorioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/relatorios")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Relatórios", description = "API para geração de relatórios dinâmicos")
public class RelatorioController {

    private final RelatorioService relatorioService;

    @Operation(summary = "Lista todos os templates de relatórios ativos")
    @GetMapping("/templates")
    public ResponseEntity<List<RelatorioTemplateDTO>> listarTemplates() {
        return ResponseEntity.ok(relatorioService.listarTemplates());
    }

    @Operation(summary = "Lista templates por categoria")
    @GetMapping("/templates/categoria/{categoria}")
    public ResponseEntity<List<RelatorioTemplateDTO>> listarPorCategoria(
            @PathVariable CategoriaRelatorio categoria) {
        return ResponseEntity.ok(relatorioService.listarTemplatesPorCategoria(categoria));
    }

    @Operation(summary = "Busca um template específico por ID")
    @GetMapping("/templates/{id}")
    public ResponseEntity<RelatorioTemplateDTO> buscarTemplate(@PathVariable Long id) {
        return ResponseEntity.ok(relatorioService.buscarTemplatePorId(id));
    }

    @Operation(summary = "Gera um relatório no formato solicitado")
    @PostMapping("/gerar")
    public ResponseEntity<byte[]> gerarRelatorio(@RequestBody RelatorioRequestDTO request) {
        byte[] arquivo = relatorioService.gerarRelatorio(request);

        FormatoRelatorio formato = request.getFormato();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(formato.getMimeType()));
        headers.setContentDispositionFormData("attachment",
                "relatorio_" + System.currentTimeMillis() + formato.getExtensao());

        return ResponseEntity.ok()
                .headers(headers)
                .body(arquivo);
    }

    @Operation(summary = "Preview de relatório (retorna apenas os dados)")
    @PostMapping("/preview")
    public ResponseEntity<RelatorioResultadoDTO> previewRelatorio(
            @RequestBody RelatorioRequestDTO request) {
        // Retorna apenas os dados sem gerar arquivo
        // Útil para preview na tela antes de exportar
        return ResponseEntity.ok(new RelatorioResultadoDTO());
    }
}
