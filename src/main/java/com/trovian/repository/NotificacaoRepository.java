package com.trovian.repository;

import com.trovian.entity.Notificacao;
import com.trovian.entity.Notificacao.StatusNotificacao;
import com.trovian.entity.Notificacao.TipoNotificacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {

    Page<Notificacao> findByUsuarioIdOrderByDataCriacaoDesc(Long usuarioId, Pageable pageable);

    List<Notificacao> findByUsuarioIdAndStatusOrderByDataCriacaoDesc(Long usuarioId, StatusNotificacao status);

    long countByUsuarioIdAndStatus(Long usuarioId, StatusNotificacao status);

    List<Notificacao> findByMotoristaIdAndStatusOrderByDataCriacaoDesc(Long motoristaId, StatusNotificacao status);

    Page<Notificacao> findByUsuarioIdAndTipoOrderByDataCriacaoDesc(Long usuarioId, TipoNotificacao tipo, Pageable pageable);
}
