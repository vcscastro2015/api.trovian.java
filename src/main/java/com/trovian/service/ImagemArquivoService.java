package com.trovian.service;

import com.trovian.dto.ImagemArquivoDTO;
import com.trovian.entity.ImagemArquivo;
import com.trovian.repository.ImagemArquivoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;


@Service
@RequiredArgsConstructor
@Slf4j
public class ImagemArquivoService {

    @Autowired
    private ImagemArquivoRepository arquivoRepository;

    @Transactional(readOnly = true)
    public ImagemArquivoDTO findByContaReceberId(Long contaReceberId) {
        ImagemArquivo imagemArquivo = arquivoRepository.findByContaReceberId(contaReceberId)
                .orElseThrow(() -> new RuntimeException("Imagem não encontrada com código: " + contaReceberId));

        String base64 = Base64.getEncoder().encodeToString(imagemArquivo.getConteudoBinario());
        ImagemArquivoDTO imagemArquivoDTO = new ImagemArquivoDTO();
        imagemArquivoDTO.setId(imagemArquivoDTO.getId());
        imagemArquivoDTO.setBase64("data:image/png;base64,"+base64);
        return imagemArquivoDTO;

    }

    @Transactional(readOnly = true)
    public ImagemArquivoDTO findByAbastecimentoId(Long abastecimentoId) {
        ImagemArquivo imagemArquivo = arquivoRepository.findByAbastecimentoId(abastecimentoId)
                .orElseThrow(() -> new RuntimeException("Imagem não encontrada com código: " + abastecimentoId));

        String base64 = Base64.getEncoder().encodeToString(imagemArquivo.getConteudoBinario());
        ImagemArquivoDTO imagemArquivoDTO = new ImagemArquivoDTO();
        imagemArquivoDTO.setId(imagemArquivoDTO.getId());
        imagemArquivoDTO.setBase64("data:image/png;base64,"+base64);
        return imagemArquivoDTO;

    }

}
