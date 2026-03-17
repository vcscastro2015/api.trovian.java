package com.trovian.service;

import com.trovian.dto.DadosFilaDTO;
import com.trovian.dto.DocumentoVeiculoDTO;
import com.trovian.entity.DocumentoVeiculo;
import com.trovian.entity.Motorista;
import com.trovian.entity.Veiculo;
import com.trovian.repository.DocumentoVeiculoRepository;
import com.trovian.repository.MotoristaRepository;
import com.trovian.repository.VeiculoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentoVeiculoService {

    private final DocumentoVeiculoRepository documentoVeiculoRepository;
    private final VeiculoRepository veiculoRepository;
    private final MotoristaRepository motoristaRepository;

    @Transactional
    public void processarDocumentoWhatsApp(DadosFilaDTO dto) {
        log.info("Processando documento do veículo via WhatsApp. Placa: {}, Arquivo: {}", dto.getPlaca(), dto.getNomeDoArquivo());

        Veiculo veiculo = veiculoRepository.findByPlacaIgnoreCase(dto.getPlaca())
                .orElseThrow(() -> new RuntimeException("Veículo não encontrado com placa: " + dto.getPlaca()));

        Motorista motorista = null;
        if (dto.getMotoristaId() != null) {
            motorista = motoristaRepository.findById(dto.getMotoristaId()).orElse(null);
        }

        byte[] conteudo = Base64.getDecoder().decode(dto.getBase64());

        DocumentoVeiculo documento = new DocumentoVeiculo();
        documento.setVeiculo(veiculo);
        documento.setMotorista(motorista);
        documento.setCliente(veiculo.getCliente());
        documento.setNomeArquivo(dto.getNomeDoArquivo());
        documento.setConteudoBinario(conteudo);
        documento.setDataEnvio(dto.getDataEnvio());
        documento.setNumeroTelefone(dto.getNumeroTelefone());

        documentoVeiculoRepository.save(documento);
        log.info("Documento salvo com sucesso. ID: {}", documento.getId());
    }

    @Transactional(readOnly = true)
    public List<DocumentoVeiculoDTO> listarPorVeiculo(Long veiculoId, LocalDateTime inicio, LocalDateTime fim) {
        List<DocumentoVeiculo> documentos;
        if (inicio != null && fim != null) {
            documentos = documentoVeiculoRepository.findByVeiculoIdAndDataEnvioBetween(veiculoId, inicio, fim);
        } else {
            documentos = documentoVeiculoRepository.findByVeiculoId(veiculoId);
        }
        return documentos.stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public byte[] gerarZip(Long veiculoId, LocalDateTime inicio, LocalDateTime fim) {
        List<DocumentoVeiculo> documentos;
        if (inicio != null && fim != null) {
            documentos = documentoVeiculoRepository.findByVeiculoIdAndDataEnvioBetween(veiculoId, inicio, fim);
        } else {
            documentos = documentoVeiculoRepository.findByVeiculoId(veiculoId);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(baos)) {

            for (DocumentoVeiculo doc : documentos) {
                String dataStr = doc.getDataEnvio() != null
                        ? doc.getDataEnvio().format(formatter)
                        : "semdata";
                String entryName = dataStr + "_" + doc.getNomeArquivo();
                zos.putNextEntry(new ZipEntry(entryName));
                zos.write(doc.getConteudoBinario());
                zos.closeEntry();
            }

            zos.finish();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar ZIP dos documentos", e);
        }
    }

    private DocumentoVeiculoDTO toDTO(DocumentoVeiculo doc) {
        DocumentoVeiculoDTO dto = new DocumentoVeiculoDTO();
        dto.setId(doc.getId());
        dto.setNomeArquivo(doc.getNomeArquivo());
        dto.setMimeType(doc.getMimeType());
        dto.setDataEnvio(doc.getDataEnvio());
        dto.setNumeroTelefone(doc.getNumeroTelefone());
        dto.setMotoristaNome(doc.getMotorista() != null ? doc.getMotorista().getNome() : null);
        dto.setVeiculoPlaca(doc.getVeiculo() != null ? doc.getVeiculo().getPlaca() : null);
        return dto;
    }
}
