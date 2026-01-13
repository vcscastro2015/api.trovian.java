package com.trovian.service;

import com.trovian.dto.relatorios.RelatorioResultadoDTO;
import com.trovian.entity.relatorios.FormatoRelatorio;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExportacaoService {

    public byte[] exportar(RelatorioResultadoDTO resultado, FormatoRelatorio formato, Boolean incluirGraficos) {
        return switch (formato) {
            case PDF -> exportarPDF(resultado, incluirGraficos);
            case EXCEL -> exportarExcel(resultado);
            case CSV -> exportarCSV(resultado);
            case JSON -> exportarJSON(resultado);
        };
    }

    private byte[] exportarExcel(RelatorioResultadoDTO resultado) {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet(resultado.getNomeRelatorio());

            // Estilo do cabeçalho
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            // Criar cabeçalho
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < resultado.getColunas().size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(resultado.getColunas().get(i));
                cell.setCellStyle(headerStyle);
            }

            // Estilo de dados
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);

            // Criar linhas de dados
            int rowNum = 1;
            for (Map<String, Object> registro : resultado.getDados()) {
                Row row = sheet.createRow(rowNum++);
                int colNum = 0;

                for (String coluna : resultado.getColunas()) {
                    Cell cell = row.createCell(colNum++);
                    Object valor = registro.get(coluna.toLowerCase().replace(" ", "_"));

                    if (valor != null) {
                        if (valor instanceof Number) {
                            cell.setCellValue(((Number) valor).doubleValue());
                        } else if (valor instanceof LocalDate) {
                            cell.setCellValue(valor.toString());
                        } else if (valor instanceof LocalDateTime) {
                            cell.setCellValue(valor.toString());
                        } else {
                            cell.setCellValue(valor.toString());
                        }
                    }
                    cell.setCellStyle(dataStyle);
                }
            }

            // Ajustar largura das colunas
            for (int i = 0; i < resultado.getColunas().size(); i++) {
                sheet.autoSizeColumn(i);
            }

            // Adicionar totalizadores se existirem
            if (resultado.getTotalizadores() != null && !resultado.getTotalizadores().isEmpty()) {
                rowNum++; // Linha em branco
                Row totalRow = sheet.createRow(rowNum);

                CellStyle totalStyle = workbook.createCellStyle();
                Font totalFont = workbook.createFont();
                totalFont.setBold(true);
                totalStyle.setFont(totalFont);
                totalStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                totalStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

                Cell labelCell = totalRow.createCell(0);
                labelCell.setCellValue("TOTAIS:");
                labelCell.setCellStyle(totalStyle);

                // Aqui você pode adicionar os valores dos totalizadores nas colunas correspondentes
            }

            workbook.write(outputStream);
            return outputStream.toByteArray();

        } catch (IOException e) {
            log.error("Erro ao gerar Excel", e);
            throw new RuntimeException("Erro ao gerar arquivo Excel", e);
        }
    }

    private byte[] exportarPDF(RelatorioResultadoDTO resultado, Boolean incluirGraficos) {
        // Implementar com iText7 ou JasperReports
        log.warn("Exportação PDF ainda não implementada");
        throw new UnsupportedOperationException("Exportação PDF em desenvolvimento");
    }

    private byte[] exportarCSV(RelatorioResultadoDTO resultado) {
        StringBuilder csv = new StringBuilder();

        // Cabeçalho
        csv.append(String.join(",", resultado.getColunas())).append("\n");

        // Dados
        for (Map<String, Object> registro : resultado.getDados()) {
            List<String> valores = resultado.getColunas().stream()
                    .map(coluna -> {
                        Object valor = registro.get(coluna.toLowerCase().replace(" ", "_"));
                        return valor != null ? valor.toString() : "";
                    })
                    .toList();
            csv.append(String.join(",", valores)).append("\n");
        }

        return csv.toString().getBytes();
    }

    private byte[] exportarJSON(RelatorioResultadoDTO resultado) {
        // Usar ObjectMapper do Jackson
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper =
                new com.fasterxml.jackson.databind.ObjectMapper();
            return mapper.writeValueAsBytes(resultado);
        } catch (Exception e) {
            log.error("Erro ao gerar JSON", e);
            throw new RuntimeException("Erro ao gerar JSON", e);
        }
    }
}
