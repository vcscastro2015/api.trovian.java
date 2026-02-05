package com.trovian.service;

import com.trovian.dto.*;
import com.trovian.entity.Cliente;
import com.trovian.entity.Funcionalidade;
import com.trovian.entity.Usuario;
import com.trovian.repository.ClienteRepository;
import com.trovian.repository.FuncionalidadeRepository;
import com.trovian.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final ClienteRepository clienteRepository;
    private final FuncionalidadeRepository funcionalidadeRepository;

    // Listar todos com paginação
    @Transactional(readOnly = true)
    public UsuarioPageResponse listarTodos(int pagina, int tamanho, String ordenarPor, String direcao) {
        Sort.Direction sortDirection = direcao.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(pagina, tamanho, Sort.by(sortDirection, ordenarPor));

        Page<Usuario> paginaUsuarios = usuarioRepository.findAll(pageable);

        return construirPageResponse(paginaUsuarios);
    }

    // Listar com filtros
    @Transactional(readOnly = true)
    public UsuarioPageResponse listarComFiltros(
            Long clienteId,
            UsuarioFiltroRequest filtro,
            int pagina,
            int tamanho,
            String ordenarPor,
            String direcao) {

        Sort.Direction sortDirection = direcao.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(pagina, tamanho, Sort.by(sortDirection, ordenarPor));

        Page<Usuario> paginaUsuarios = usuarioRepository.findByFiltrosCompletos(
                clienteId,
                filtro.getNome(),
                filtro.getEmail(),
                filtro.getAtivo(),
                filtro.getRole(),
                pageable
        );

        return construirPageResponse(paginaUsuarios);
    }

    @Transactional(readOnly = true)
    public UsuarioPageResponse findByCliente(Long clienteId, Pageable pageable) {
        Page<Usuario> paginaUsuarios =  usuarioRepository.findByClienteId(clienteId, pageable);
        return construirPageResponse(paginaUsuarios);
    }

    // Buscar por ID
    @Transactional(readOnly = true)
    public UsuarioResponse buscarPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com ID: " + id));

        return converterParaResponse(usuario);
    }

    // Criar novo usuário
    @Transactional
    public UsuarioResponse criar(UsuarioCriarRequest request) {
        // Validar email único
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email já cadastrado: " + request.getEmail());
        }

        // Validar cliente
        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com ID: " + request.getClienteId()));


        Usuario usuario = new Usuario();
        usuario.setNome(request.getNome());
        usuario.setEmail(request.getEmail());
        usuario.setSenha(passwordEncoder.encode(request.getSenha()));
        usuario.setTelefone(request.getTelefone());
        usuario.setAtivo(request.getAtivo() != null ? request.getAtivo() : true);
        usuario.setRoles(request.getRoles());
        usuario.setCliente(cliente);

        // Buscar funcionalidades pelos códigos
        if (request.getFuncionalidades() != null && request.getFuncionalidades().length > 0) {
            Set<Funcionalidade> funcionalidades = new HashSet<>();
            for (String codigo : request.getFuncionalidades()) {
                Funcionalidade funcionalidade = funcionalidadeRepository.findByCodigo(codigo)
                        .orElseThrow(() -> new RuntimeException("Funcionalidade não encontrada com código: " + codigo));
                funcionalidades.add(funcionalidade);
            }
            usuario.setFuncionalidades(funcionalidades);
        }

        usuario = usuarioRepository.save(usuario);

        return converterParaResponse(usuario);
    }

    // Atualizar usuário
    @Transactional
    public UsuarioResponse atualizar(Long id, UsuarioAtualizarRequest request) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com ID: " + id));

        // Atualizar apenas campos não nulos
        if (request.getNome() != null) {
            usuario.setNome(request.getNome());
        }

        if (request.getEmail() != null && !request.getEmail().equals(usuario.getEmail())) {
            // Validar se novo email já existe
            if (usuarioRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Email já cadastrado: " + request.getEmail());
            }
            usuario.setEmail(request.getEmail());
        }

        if (request.getTelefone() != null) {
            usuario.setTelefone(request.getTelefone());
        }

        if (request.getAtivo() != null) {
            usuario.setAtivo(request.getAtivo());

            // Se desativar, invalidar sessões
            if (!request.getAtivo()) {
                usuario.setTokenDispositivo(null);
                refreshTokenService.deleteByUsuario(usuario);
            }
        }

        if (request.getRoles() != null) {
            usuario.setRoles(request.getRoles());
        }

        // Validar cliente
        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com ID: " + request.getClienteId()));
        usuario.setCliente(cliente);

        // Buscar funcionalidades pelos códigos
        if (request.getFuncionalidades() != null && request.getFuncionalidades().length > 0) {
            Set<Funcionalidade> funcionalidades = new HashSet<>();
            for (String codigo : request.getFuncionalidades()) {
                Funcionalidade funcionalidade = funcionalidadeRepository.findByCodigo(codigo)
                        .orElseThrow(() -> new RuntimeException("Funcionalidade não encontrada com código: " + codigo));
                funcionalidades.add(funcionalidade);
            }
            usuario.setFuncionalidades(funcionalidades);
        }

        usuario = usuarioRepository.save(usuario);

        return converterParaResponse(usuario);
    }

    // Deletar usuário (soft delete - apenas desativa)
    @Transactional
    public MessageResponse deletar(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com ID: " + id));

        // Desativar ao invés de deletar
        usuario.setAtivo(false);
        usuario.setTokenDispositivo(null);
        usuarioRepository.save(usuario);

        // Invalidar refresh tokens
        refreshTokenService.deleteByUsuario(usuario);

        return new MessageResponse("Usuário desativado com sucesso");
    }

    // Deletar permanentemente (usar com cuidado)
    @Transactional
    public MessageResponse deletarPermanente(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com ID: " + id));

        // Invalidar refresh tokens antes de deletar
        refreshTokenService.deleteByUsuario(usuario);

        usuarioRepository.delete(usuario);

        return new MessageResponse("Usuário deletado permanentemente");
    }

    // Ativar usuário
    @Transactional
    public MessageResponse ativar(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com ID: " + id));

        usuario.setAtivo(true);
        usuarioRepository.save(usuario);

        return new MessageResponse("Usuário ativado com sucesso");
    }

    // Desativar usuário
    @Transactional
    public MessageResponse desativar(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com ID: " + id));

        usuario.setAtivo(false);
        usuario.setTokenDispositivo(null);
        usuarioRepository.save(usuario);

        // Invalidar sessões
        refreshTokenService.deleteByUsuario(usuario);

        return new MessageResponse("Usuário desativado com sucesso");
    }

    // Atualizar roles
    @Transactional
    public UsuarioResponse atualizarRoles(Long id, UsuarioRolesRequest request) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com ID: " + id));

        usuario.setRoles(request.getRoles());
        usuario = usuarioRepository.save(usuario);

        // Invalidar sessões para forçar novo login com novas permissões
        usuario.setTokenDispositivo(null);
        refreshTokenService.deleteByUsuario(usuario);

        return converterParaResponse(usuario);
    }

    // Trocar senha (pelo próprio usuário)
    @Transactional
    public MessageResponse trocarSenha(Long id, UsuarioTrocarSenhaRequest request) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com ID: " + id));

        // Validar senha atual
        if (!passwordEncoder.matches(request.getSenhaAtual(), usuario.getSenha())) {
            throw new RuntimeException("Senha atual incorreta");
        }

        // Validar se nova senha é diferente da atual
        if (passwordEncoder.matches(request.getNovaSenha(), usuario.getSenha())) {
            throw new RuntimeException("A nova senha deve ser diferente da senha atual");
        }

        usuario.setSenha(passwordEncoder.encode(request.getNovaSenha()));
        usuarioRepository.save(usuario);

        // Invalidar sessões para forçar novo login
        usuario.setTokenDispositivo(null);
        refreshTokenService.deleteByUsuario(usuario);

        return new MessageResponse("Senha alterada com sucesso. Faça login novamente.");
    }

    // Trocar senha (por admin - não requer senha atual)
    @Transactional
    public MessageResponse trocarSenhaAdmin(Long id, UsuarioTrocarSenhaAdminRequest request) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com ID: " + id));

        usuario.setSenha(passwordEncoder.encode(request.getNovaSenha()));
        usuarioRepository.save(usuario);

        // Invalidar sessões
        usuario.setTokenDispositivo(null);
        refreshTokenService.deleteByUsuario(usuario);

        return new MessageResponse("Senha alterada com sucesso pelo administrador");
    }

    // Buscar por termo (autocomplete)
    @Transactional(readOnly = true)
    public List<UsuarioResponse> buscarPorTermo(String termo) {
        List<Usuario> usuarios = usuarioRepository.buscarPorTermo(termo);
        return usuarios.stream()
                .map(this::converterParaResponse)
                .collect(Collectors.toList());
    }

    // Estatísticas
    @Transactional(readOnly = true)
    public UsuarioEstatisticasResponse obterEstatisticasAdm() {
        long totalUsuarios = usuarioRepository.count();
        long usuariosAtivos = usuarioRepository.countUsuariosAtivos();
        long usuariosInativos = usuarioRepository.countUsuariosInativos();

        return UsuarioEstatisticasResponse.builder()
                .totalUsuarios(totalUsuarios)
                .usuariosAtivos(usuariosAtivos)
                .usuariosInativos(usuariosInativos)
                .build();
    }

    @Transactional(readOnly = true)
    public UsuarioEstatisticasResponse obterEstatisticas(Long clienteId) {
        long totalUsuarios = usuarioRepository.countByClienteId(clienteId);
        long usuariosAtivos = usuarioRepository.countUsuariosAtivosByClienteId(clienteId);
        long usuariosInativos = usuarioRepository.countUsuariosInativosByClienteId(clienteId);

        return UsuarioEstatisticasResponse.builder()
                .totalUsuarios(totalUsuarios)
                .usuariosAtivos(usuariosAtivos)
                .usuariosInativos(usuariosInativos)
                .build();
    }

    // Métodos auxiliares
    private UsuarioResponse converterParaResponse(Usuario usuario) {
        Set<String> funcionalidadeCodigos = usuario.getFuncionalidades().stream()
                .map(Funcionalidade::getCodigo)
                .collect(Collectors.toSet());

        return UsuarioResponse.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .telefone(usuario.getTelefone())
                .ativo(usuario.getAtivo())
                .roles(usuario.getRoles())
                .funcionalidades(funcionalidadeCodigos)
                .ultimoLogin(usuario.getUltimoLogin())
                .criadoEm(usuario.getCriadoEm())
                .atualizadoEm(usuario.getAtualizadoEm())
                .clienteId(usuario.getCliente().getId())
                .clienteNome(usuario.getCliente().getNome())
                .build();
    }

    private UsuarioPageResponse construirPageResponse(Page<Usuario> paginaUsuarios) {
        List<UsuarioResponse> usuarios = paginaUsuarios.getContent().stream()
                .map(this::converterParaResponse)
                .collect(Collectors.toList());

        return UsuarioPageResponse.builder()
                .usuarios(usuarios)
                .paginaAtual(paginaUsuarios.getNumber())
                .totalPaginas(paginaUsuarios.getTotalPages())
                .totalElementos(paginaUsuarios.getTotalElements())
                .tamanhoPagina(paginaUsuarios.getSize())
                .primeira(paginaUsuarios.isFirst())
                .ultima(paginaUsuarios.isLast())
                .build();
    }
}
