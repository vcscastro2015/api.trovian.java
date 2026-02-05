package com.trovian.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.trovian.dto.EnderecoDTO;

import java.io.IOException;

public class EnderecoDeserializer extends JsonDeserializer<EnderecoDTO> {

    @Override
    public EnderecoDTO deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException {

        JsonNode node = p.getCodec().readTree(p);
        EnderecoDTO endereco = new EnderecoDTO();

        // Caso 1: veio como STRING (OCR bagunçado)
        if (node.isTextual()) {
            endereco.setEnderecoCompleto(node.asText());
            return endereco;
        }

        // Caso 2: veio como OBJETO (OCR mais “limpo”)
        if (node.isObject()) {
            endereco.setLogradouro(get(node, "logradouro"));
            endereco.setNumero(get(node, "numero"));
            endereco.setBairro(get(node, "bairro"));
            endereco.setMunicipio(get(node, "municipio"));
            endereco.setUf(get(node, "uf"));
            endereco.setCep(get(node, "cep"));

            // fallback
            endereco.setEnderecoCompleto(node.toString());
        }

        return endereco;
    }

    private String get(JsonNode node, String field) {
        return node.has(field) && !node.get(field).isNull()
                ? node.get(field).asText()
                : null;
    }
}