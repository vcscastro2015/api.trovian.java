package com.trovian.repository;

import com.trovian.entity.Viagem;
import com.trovian.enums.StatusViagem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository para operações de banco de dados de Viagem
 */
@Repository
public interface ViagemRepository extends JpaRepository<Viagem, Long>, ViagemRepositoryCustom {

    /**
     * Busca viagens por veículo com paginação
     *
     * @param veiculoId ID do veículo
     * @param pageable  Configuração de paginação
     * @return Página de viagens
     */
    Page<Viagem> findByVeiculoId(Long veiculoId, Pageable pageable);

    /**
     * Busca viagens por motorista com paginação
     *
     * @param motoristaId ID do motorista
     * @param pageable    Configuração de paginação
     * @return Página de viagens
     */
    Page<Viagem> findByMotoristaId(Long motoristaId, Pageable pageable);

    /**
     * Busca viagens por status com paginação
     *
     * @param status   Status da viagem
     * @param pageable Configuração de paginação
     * @return Página de viagens
     */
    Page<Viagem> findByStatus(Boolean status, Pageable pageable);

    /**
     * Busca viagens por cliente com paginação
     *
     * @param clienteId ID do cliente
     * @param pageable  Configuração de paginação
     * @return Página de viagens
     */
    Page<Viagem> findByClienteId(Long clienteId, Pageable pageable);

    /**
     * Busca viagens por status da viagem (ABERTA, ANALISE, FECHADA) com paginação
     *
     * @param statusViagem Status da viagem
     * @param pageable     Configuração de paginação
     * @return Página de viagens
     */
    Page<Viagem> findByStatusViagem(StatusViagem statusViagem, Pageable pageable);
}
