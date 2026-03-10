package com.trovian.service;

import com.trovian.entity.Motorista;
import com.trovian.entity.Notificacao;
import com.trovian.entity.Notificacao.TipoNotificacao;
import com.trovian.entity.Usuario;
import com.trovian.repository.NotificacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificacaoService {

    private final NotificacaoRepository notificacaoRepository;

    @Transactional
    public void criarNotificacaoBemVindo(Usuario usuario) {
        String mensagem = String.format(
                "Bem vindo %s a Trajetto. Por aqui você vai receber notificações de Motoristas e Veículos. " +
                "Você também pode digitar uma placa da sua frota e receber dados do veiculo.",
                usuario.getNome()
        );

        Notificacao notificacao = new Notificacao();
        notificacao.setUsuario(usuario);
        notificacao.setMensagem(mensagem);
        notificacao.setTipo(TipoNotificacao.MENSAGEM_BEM_VINDO);

        notificacaoRepository.save(notificacao);
    }

    @Transactional
    public void criarNotificacaoBemVindoMotorista(Motorista motorista) {
        String mensagem = String.format(
                "Bem vindo %s a Trajetto. Por aqui você vai receber notificações da base de operações. " +
                "Se você digitar Menu, você tera opções de trabalho.",
                motorista.getNome()
        );

        Notificacao notificacao = new Notificacao();
        notificacao.setMotorista(motorista);
        notificacao.setMensagem(mensagem);
        notificacao.setTipo(TipoNotificacao.MENSAGEM_BEM_VINDO);

        notificacaoRepository.save(notificacao);
    }
}
