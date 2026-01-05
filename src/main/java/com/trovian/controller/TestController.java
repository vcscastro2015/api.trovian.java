package com.trovian.controller;

import com.trovian.config.JmsConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Test", description = "API de testes - Apenas para desenvolvimento")
public class TestController {

    private final JmsTemplate jmsTemplate;

    @Operation(summary = "Inserir CT-e de teste na fila",
               description = "Envia um CT-e de exemplo para a fila documentos.cte para testar o processamento automático")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "CT-e enviado para a fila com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro ao enviar CT-e para a fila")
    })
    @GetMapping("/inserirCteNaFila")
    public ResponseEntity<String> inserirCteNaFila() {
        try {
            // String JSON do CT-e conforme script.txt
            String cteJson = """
                {
                  "numero": "000028529",
                  "serie": "1",
                  "chaveAcesso": "312514218980618001740579010009285291009023026",
                  "dataEmissao": "2023-11-29T08:21:58",
                  "modelo": "57",
                  "tipoDocumento": "NORMAL",
                  "emitente": {
                    "razaoSocial": "TRANSCOOPA - COOP DE TRANSP ROD DE CARG",
                    "cnpj": "21647397000192",
                    "ie": "7010073042",
                    "endereco": "RUA JOSE SALGADO DE MELO, 226, PARQUE SAO FRANCISCO",
                    "municipio": "SANTO ANTONIO DO AMPARO",
                    "uf": "MG",
                    "cep": "37262000"
                  },
                  "remetente": {
                    "razaoSocial": "CARGILL AGRICOLA S.A.",
                    "cnpj": "60435228000130",
                    "ie": "702024703776",
                    "endereco": "AV PRESIDENTE KENNEDY, 3501, PARTE - INDUSTRIAL",
                    "municipio": "UBERLANDIA",
                    "uf": "MG",
                    "cep": "38402450"
                  },
                  "destinatario": {
                    "razaoSocial": "ARAPE AGROINDUSTRIA LTDA",
                    "cnpj": "261347530169",
                    "ie": "3327231722",
                    "endereco": "FAZ CABURAI DA AREIA, SN, ZONA RURAL",
                    "municipio": "FORMIGA",
                    "uf": "MG",
                    "cep": "35573000",
                    "pais": "BRASIL"
                  },
                  "tomador": {
                    "razaoSocial": "ARAPE AGROINDUSTRIA LTDA",
                    "cnpj": "261347530169",
                    "ie": "3327231722",
                    "endereco": "FAZ CABURAI DA AREIA, SN, ZONA RURAL",
                    "municipio": "FORMIGA",
                    "uf": "MG",
                    "cep": "35573000",
                    "pais": "BRASIL"
                  },
                  "dadosCarga": {
                    "produtoPredominante": "FARINHA DE SOJA 46% PELLET GR",
                    "outrasCaracteristicas": "GRANEL",
                    "quantidade": 4,
                    "unidade": "UN",
                    "pesoBruto": 22400,
                    "pesoLiquido": 22400,
                    "valorTotalMercadoria": 52000.00
                  },
                  "valoresPrestacao": {
                    "valorTotalPrestacao": 4364.80,
                    "pedagio": 0.00,
                    "despachoArifa": 133.48,
                    "nomesComponentes": [
                      "FRETE VALOR",
                      "PEDAGIO COBRADO",
                      "DESPACHO/ARIFA"
                    ],
                    "outrosValores": 60.00
                  },
                  "observacoes": ""
                }
                """;

            log.info("Enviando CT-e de teste para a fila {}", JmsConfig.DOCUMENTOS_CTE);
            jmsTemplate.convertAndSend(JmsConfig.DOCUMENTOS_CTE, cteJson);
            log.info("CT-e enviado com sucesso para a fila");

            return ResponseEntity.ok("CT-e enviado com sucesso para a fila " + JmsConfig.DOCUMENTOS_CTE +
                                     ". Verifique os logs para acompanhar o processamento.");

        } catch (Exception e) {
            log.error("Erro ao enviar CT-e para a fila", e);
            return ResponseEntity.internalServerError()
                    .body("Erro ao enviar CT-e para a fila: " + e.getMessage());
        }
    }
}
