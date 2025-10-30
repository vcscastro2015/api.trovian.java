package com.trovian.service;

import com.trovian.dto.ProductDTO;
import com.trovian.entity.Product;
import com.trovian.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<ProductDTO> findAll() {
        log.info("Buscando todos os produtos");
        return productRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductDTO findById(Long id) {
        log.info("Buscando produto por ID: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado com ID: " + id));
        return toDTO(product);
    }

    @Transactional
    public ProductDTO create(ProductDTO productDTO) {
        log.info("Criando novo produto: {}", productDTO.getName());
        Product product = toEntity(productDTO);
        Product savedProduct = productRepository.save(product);
        log.info("Produto criado com ID: {}", savedProduct.getId());
        return toDTO(savedProduct);
    }

    @Transactional
    public ProductDTO update(Long id, ProductDTO productDTO) {
        log.info("Atualizando produto ID: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado com ID: " + id));

        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setQuantity(productDTO.getQuantity());

        Product updatedProduct = productRepository.save(product);
        log.info("Produto atualizado com ID: {}", updatedProduct.getId());
        return toDTO(updatedProduct);
    }

    @Transactional
    public void delete(Long id) {
        log.info("Deletando produto ID: {}", id);
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Produto não encontrado com ID: " + id);
        }
        productRepository.deleteById(id);
        log.info("Produto deletado com ID: {}", id);
    }

    @Transactional(readOnly = true)
    public List<ProductDTO> searchByName(String name) {
        log.info("Buscando produtos por nome: {}", name);
        return productRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private ProductDTO toDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setQuantity(product.getQuantity());
        return dto;
    }

    private Product toEntity(ProductDTO dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setQuantity(dto.getQuantity());
        return product;
    }
}
