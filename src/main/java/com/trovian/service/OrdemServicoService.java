package com.trovian.service;

import com.trovian.dto.ItemManutencaoDTO;
import com.trovian.dto.OrdemServicoDTO;
import com.trovian.entity.*;
import com.trovian.enums.StatusConta;
import com.trovian.enums.StatusOrdemServico;
import com.trovian.enums.TipoConta;
import com.trovian.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrdemServicoService {

    private final OrdemServicoRepository ordemServicoRepository;
    private final ItemManutencaoRepository itemManutencaoRepository;
    private final VeiculoRepository veiculoRepository;
    private final MotoristaRepository motoristaRepository;
    private final FornecedorRepository fornecedorRepository;
    private final ContaPagarRepository contaPagarRepository;
    private final CategoriaContaRepository categoriaContaRepository;

    @Transactional
    public OrdemServicoDTO create(OrdemServicoDTO dto) {
        log.info("Criando ordem de serviço: {}", dto.getNumeroOs());

        if (ordemServicoRepository.findByNumeroOs(dto.getNumeroOs()).isPresent()) {
            throw new RuntimeException("Já existe uma OS com o número: " + dto.getNumeroOs());
        }

        OrdemServico ordemServico = toEntity(dto);
        OrdemServico saved = ordemServicoRepository.save(ordemServico);

        if (dto.getItens() != null && !dto.getItens().isEmpty()) {
            for (ItemManutencaoDTO itemDTO : dto.getItens()) {
                ItemManutencao item = toItemEntity(itemDTO);
                item.setOrdemServico(saved);
                itemManutencaoRepository.save(item);
            }
            saved = ordemServicoRepository.findById(saved.getId()).orElseThrow();
            saved.calcularValorTotal();
            saved = ordemServicoRepository.save(saved);
        }

        log.info("Ordem de serviço criada com sucesso. ID: {}", saved.getId());
        return toDTO(saved);
    }

    @Transactional(readOnly = true)
    public Page<OrdemServicoDTO> findAll(int page, int size, String sortBy, String direction) {
        log.info("Buscando ordens de serviço - página: {}, tamanho: {}", page, size);
        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC")
            ? Sort.Direction.DESC
            : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return ordemServicoRepository.findAll(pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public OrdemServicoDTO findById(Long id) {
        log.info("Buscando ordem de serviço por ID: {}", id);
        OrdemServico ordemServico = ordemServicoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Ordem de serviço não encontrada com ID: " + id));
        return toDTO(ordemServico);
    }

    @Transactional(readOnly = true)
    public OrdemServicoDTO findByNumeroOs(String numeroOs) {
        log.info("Buscando ordem de serviço por número: {}", numeroOs);
        OrdemServico ordemServico = ordemServicoRepository.findByNumeroOs(numeroOs)
            .orElseThrow(() -> new RuntimeException("Ordem de serviço não encontrada: " + numeroOs));
        return toDTO(ordemServico);
    }

    @Transactional
    public OrdemServicoDTO update(Long id, OrdemServicoDTO dto) {
        log.info("Atualizando ordem de serviço ID: {}", id);
        OrdemServico ordemServico = ordemServicoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Ordem de serviço não encontrada com ID: " + id));

        updateEntityFromDTO(ordemServico, dto);
        OrdemServico updated = ordemServicoRepository.save(ordemServico);
        log.info("Ordem de serviço atualizada com sucesso. ID: {}", id);
        return toDTO(updated);
    }

    @Transactional
    public void delete(Long id) {
        log.info("Deletando ordem de serviço ID: {}", id);
        if (!ordemServicoRepository.existsById(id)) {
            throw new RuntimeException("Ordem de serviço não encontrada com ID: " + id);
        }
        ordemServicoRepository.deleteById(id);
        log.info("Ordem de serviço deletada com sucesso. ID: {}", id);
    }

    @Transactional(readOnly = true)
    public Page<OrdemServicoDTO> findByStatus(StatusOrdemServico status, Pageable pageable) {
        return ordemServicoRepository.findByStatus(status, pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<OrdemServicoDTO> findByVeiculoId(Long veiculoId, Pageable pageable) {
        return ordemServicoRepository.findByVeiculoId(veiculoId, pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public List<OrdemServicoDTO> findOrdensAtrasadas() {
        return ordemServicoRepository.findOrdensAtrasadas(LocalDate.now())
            .stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    @Transactional
    public OrdemServicoDTO concluirOrdemServico(Long id, String diagnostico) {
        log.info("Concluindo ordem de serviço ID: {}", id);
        OrdemServico ordemServico = ordemServicoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Ordem de serviço não encontrada com ID: " + id));

        ordemServico.setStatus(StatusOrdemServico.CONCLUIDA);
        ordemServico.setDataConclusao(LocalDate.now());
        if (diagnostico != null && !diagnostico.isEmpty()) {
            ordemServico.setDiagnostico(diagnostico);
        }

        OrdemServico updated = ordemServicoRepository.save(ordemServico);

        // Gerar conta a pagar automaticamente
        gerarContaPagarParaOrdemServico(updated);

        log.info("Ordem de serviço concluída com sucesso. ID: {}", id);
        return toDTO(updated);
    }

    private void gerarContaPagarParaOrdemServico(OrdemServico ordemServico) {
        if (ordemServico.getValorTotal() == null || ordemServico.getValorTotal().compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("Ordem de serviço {} sem valor total. Conta a pagar não será gerada.", ordemServico.getNumeroOs());
            return;
        }

        try {
            // Buscar categoria "Manutenção"
            CategoriaConta categoriaManutencao = categoriaContaRepository.findByTipo(TipoConta.PAGAR)
                .stream()
                .filter(c -> c.getNome().toUpperCase().contains("MANUTENÇÃO") || c.getNome().toUpperCase().contains("MANUTENCAO"))
                .findFirst()
                .orElse(null);

            if (categoriaManutencao == null) {
                log.warn("Categoria 'Manutenção' não encontrada. Conta a pagar não será gerada automaticamente.");
                return;
            }

            // Obter fornecedor do primeiro item (se houver)
            Fornecedor fornecedor = null;
            if (ordemServico.getItens() != null && !ordemServico.getItens().isEmpty()) {
                fornecedor = ordemServico.getItens().get(0).getFornecedor();
            }

            if (fornecedor == null) {
                log.warn("Nenhum fornecedor encontrado nos itens da OS. Conta a pagar não será gerada.");
                return;
            }

            ContaPagar contaPagar = new ContaPagar();
            contaPagar.setDescricao("Manutenção - OS " + ordemServico.getNumeroOs());
            contaPagar.setNumeroDocumento(ordemServico.getNumeroOs());
            contaPagar.setFornecedor(fornecedor);
            contaPagar.setCategoria(categoriaManutencao);
            contaPagar.setVeiculo(ordemServico.getVeiculo());
            contaPagar.setMotorista(ordemServico.getMotorista());
            contaPagar.setValorOriginal(ordemServico.getValorTotal());
            contaPagar.setValorTotal(ordemServico.getValorTotal());
            contaPagar.setDataEmissao(LocalDate.now());
            contaPagar.setDataVencimento(LocalDate.now().plusDays(30)); // 30 dias para pagamento
            contaPagar.setDataCompetencia(ordemServico.getDataConclusao());
            contaPagar.setStatus(StatusConta.PENDENTE);
            contaPagar.setObservacao("Gerado automaticamente a partir da OS " + ordemServico.getNumeroOs());

            contaPagarRepository.save(contaPagar);
            log.info("Conta a pagar gerada automaticamente para OS {} no valor de {}",
                ordemServico.getNumeroOs(), ordemServico.getValorTotal());

        } catch (Exception e) {
            log.error("Erro ao gerar conta a pagar para OS {}: {}", ordemServico.getNumeroOs(), e.getMessage(), e);
        }
    }

    private OrdemServicoDTO toDTO(OrdemServico entity) {
        OrdemServicoDTO dto = new OrdemServicoDTO();
        dto.setId(entity.getId());
        dto.setNumeroOs(entity.getNumeroOs());
        dto.setTipoManutencao(entity.getTipoManutencao());
        dto.setKmVeiculo(entity.getKmVeiculo());
        dto.setDataAbertura(entity.getDataAbertura());
        dto.setDataPrevista(entity.getDataPrevista());
        dto.setDataConclusao(entity.getDataConclusao());
        dto.setStatus(entity.getStatus());
        dto.setDescricaoProblema(entity.getDescricaoProblema());
        dto.setDiagnostico(entity.getDiagnostico());
        dto.setValorTotal(entity.getValorTotal());
        dto.setObservacoes(entity.getObservacoes());

        if (entity.getVeiculo() != null) {
            dto.setVeiculoId(entity.getVeiculo().getId());
            dto.setVeiculoPlaca(entity.getVeiculo().getPlaca());
        }

        if (entity.getMotorista() != null) {
            dto.setMotoristaId(entity.getMotorista().getId());
            dto.setMotoristaNome(entity.getMotorista().getNome());
        }

        if (entity.getItens() != null) {
            dto.setItens(entity.getItens().stream()
                .map(this::toItemDTO)
                .collect(Collectors.toList()));
        }

        return dto;
    }

    private ItemManutencaoDTO toItemDTO(ItemManutencao entity) {
        ItemManutencaoDTO dto = new ItemManutencaoDTO();
        dto.setId(entity.getId());
        dto.setOrdemServicoId(entity.getOrdemServico().getId());
        dto.setTipo(entity.getTipo());
        dto.setDescricao(entity.getDescricao());
        dto.setQuantidade(entity.getQuantidade());
        dto.setValorUnitario(entity.getValorUnitario());
        dto.setValorTotal(entity.getValorTotal());

        if (entity.getFornecedor() != null) {
            dto.setFornecedorId(entity.getFornecedor().getId());
            dto.setFornecedorNome(entity.getFornecedor().getNomeFantasia() != null
                ? entity.getFornecedor().getNomeFantasia()
                : entity.getFornecedor().getRazaoSocial());
        }

        return dto;
    }

    private OrdemServico toEntity(OrdemServicoDTO dto) {
        OrdemServico entity = new OrdemServico();
        entity.setNumeroOs(dto.getNumeroOs());
        entity.setTipoManutencao(dto.getTipoManutencao());
        entity.setKmVeiculo(dto.getKmVeiculo());
        entity.setDataAbertura(dto.getDataAbertura());
        entity.setDataPrevista(dto.getDataPrevista());
        entity.setDataConclusao(dto.getDataConclusao());
        entity.setStatus(dto.getStatus() != null ? dto.getStatus() : StatusOrdemServico.ABERTA);
        entity.setDescricaoProblema(dto.getDescricaoProblema());
        entity.setDiagnostico(dto.getDiagnostico());
        entity.setValorTotal(dto.getValorTotal() != null ? dto.getValorTotal() : BigDecimal.ZERO);
        entity.setObservacoes(dto.getObservacoes());

        if (dto.getVeiculoId() != null) {
            Veiculo veiculo = veiculoRepository.findById(dto.getVeiculoId())
                .orElseThrow(() -> new RuntimeException("Veículo não encontrado com ID: " + dto.getVeiculoId()));
            entity.setVeiculo(veiculo);
        }

        if (dto.getMotoristaId() != null) {
            Motorista motorista = motoristaRepository.findById(dto.getMotoristaId())
                .orElse(null);
            entity.setMotorista(motorista);
        }

        return entity;
    }

    private ItemManutencao toItemEntity(ItemManutencaoDTO dto) {
        ItemManutencao entity = new ItemManutencao();
        entity.setTipo(dto.getTipo());
        entity.setDescricao(dto.getDescricao());
        entity.setQuantidade(dto.getQuantidade());
        entity.setValorUnitario(dto.getValorUnitario());

        if (dto.getFornecedorId() != null) {
            Fornecedor fornecedor = fornecedorRepository.findById(dto.getFornecedorId())
                .orElse(null);
            entity.setFornecedor(fornecedor);
        }

        return entity;
    }

    private void updateEntityFromDTO(OrdemServico entity, OrdemServicoDTO dto) {
        entity.setNumeroOs(dto.getNumeroOs());
        entity.setTipoManutencao(dto.getTipoManutencao());
        entity.setKmVeiculo(dto.getKmVeiculo());
        entity.setDataPrevista(dto.getDataPrevista());
        entity.setDataConclusao(dto.getDataConclusao());
        entity.setStatus(dto.getStatus());
        entity.setDescricaoProblema(dto.getDescricaoProblema());
        entity.setDiagnostico(dto.getDiagnostico());
        entity.setObservacoes(dto.getObservacoes());

        if (dto.getVeiculoId() != null) {
            Veiculo veiculo = veiculoRepository.findById(dto.getVeiculoId())
                .orElseThrow(() -> new RuntimeException("Veículo não encontrado com ID: " + dto.getVeiculoId()));
            entity.setVeiculo(veiculo);
        }

        if (dto.getMotoristaId() != null) {
            Motorista motorista = motoristaRepository.findById(dto.getMotoristaId())
                .orElse(null);
            entity.setMotorista(motorista);
        }
    }
}
