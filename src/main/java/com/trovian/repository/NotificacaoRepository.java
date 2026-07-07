package com.trovian.repository;

import com.trovian.entity.Notificacao;
import com.trovian.entity.Notificacao.StatusNotificacao;
import com.trovian.entity.Notificacao.TipoNotificacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {

    Page<Notificacao> findByUsuarioIdOrderByDataCriacaoDesc(Long usuarioId, Pageable pageable);

    List<Notificacao> findByUsuarioIdAndStatusOrderByDataCriacaoDesc(Long usuarioId, StatusNotificacao status);

    long countByUsuarioIdAndStatus(Long usuarioId, StatusNotificacao status);

    List<Notificacao> findByMotoristaIdAndStatusOrderByDataCriacaoDesc(Long motoristaId, StatusNotificacao status);

    Page<Notificacao> findByUsuarioIdAndTipoOrderByDataCriacaoDesc(Long usuarioId, TipoNotificacao tipo, Pageable pageable);

    // Notificações dos motoristas pertencentes ao cliente do usuário logado
    Page<Notificacao> findByMotoristaClienteIdOrderByDataCriacaoDesc(Long clienteId, Pageable pageable);

    // Dashboard: contagem agrupada por referenciaTipo (cobre os 16 templates)
    @Query("SELECT n.referenciaTipo, COUNT(n) FROM Notificacao n " +
           "WHERE n.usuario.id = :usuarioId OR n.motorista.cliente.id = :clienteId " +
           "GROUP BY n.referenciaTipo")
    List<Object[]> countByRefTipo(@Param("usuarioId") Long usuarioId, @Param("clienteId") Long clienteId);

    // Dashboard: contagem agrupada por status
    @Query("SELECT n.status, COUNT(n) FROM Notificacao n " +
           "WHERE n.usuario.id = :usuarioId OR n.motorista.cliente.id = :clienteId " +
           "GROUP BY n.status")
    List<Object[]> countByStatus(@Param("usuarioId") Long usuarioId, @Param("clienteId") Long clienteId);

    // Dashboard: contagem agrupada por tipo (enum TipoNotificacao)
    @Query("SELECT n.tipo, COUNT(n) FROM Notificacao n " +
           "WHERE n.usuario.id = :usuarioId OR n.motorista.cliente.id = :clienteId " +
           "GROUP BY n.tipo")
    List<Object[]> countByTipo(@Param("usuarioId") Long usuarioId, @Param("clienteId") Long clienteId);

    // Dashboard: tendência diária de criação de notificações
    @Query("SELECT CAST(n.dataCriacao AS date), COUNT(n) FROM Notificacao n " +
           "WHERE (n.usuario.id = :usuarioId OR n.motorista.cliente.id = :clienteId) " +
           "AND n.dataCriacao >= :dataInicio " +
           "GROUP BY CAST(n.dataCriacao AS date) ORDER BY CAST(n.dataCriacao AS date)")
    List<Object[]> tendenciaDiaria(@Param("usuarioId") Long usuarioId,
                                   @Param("clienteId") Long clienteId,
                                   @Param("dataInicio") LocalDateTime dataInicio);

    // Dashboard: notificações recentes (limite via Pageable)
    @Query("SELECT n FROM Notificacao n " +
           "WHERE n.usuario.id = :usuarioId OR n.motorista.cliente.id = :clienteId " +
           "ORDER BY n.dataCriacao DESC")
    List<Notificacao> findRecentes(@Param("usuarioId") Long usuarioId,
                                   @Param("clienteId") Long clienteId,
                                   Pageable pageable);
}
