package com.trovian.config;

import com.trovian.entity.Funcionalidade;
import com.trovian.repository.FuncionalidadeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class FuncionalidadeSeed implements CommandLineRunner {

    private final FuncionalidadeRepository repository;

    public FuncionalidadeSeed(FuncionalidadeRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {

        // PRINCIPAIS
        criar("MAPA", "Mapa Principal", "Principal", 1);
        criar("DASHBOARD", "Dashboard de Gestão", "Principal", 2);
        criar("USUARIO", "Usuários", "Principal", 3);

        // CADASTROS
        criar("CLIENTE", "Cliente", "Cadastros", 1);
        criar("COOPERATIVA", "Cooperativa", "Cadastros", 2);
        criar("EQUIPAMENTO", "Equipamento", "Cadastros", 3);
        criar("LOCAL", "Local", "Cadastros", 4);
        criar("MOTORISTA", "Motorista", "Cadastros", 5);
        criar("MODELO_EQUIPAMENTO", "Modelo Equipamento", "Cadastros", 6);
        criar("MODELO_VEICULO", "Modelo Veículo", "Cadastros", 7);
        criar("VEICULO", "Veículo", "Cadastros", 8);

        // OPERACIONAL
        criar("ABASTECIMENTO", "Abastecimento", "Operacional", 1);
        criar("COMISSAO_MOTORISTA", "Comissão Motorista", "Operacional", 2);
        criar("ROTA", "Rota", "Operacional", 3);
        criar("VIAGEM", "Lista de Viagens", "Operacional", 4);
        criar("NOVA_VIAGEM", "Nova Viagem", "Operacional", 5);

        // FINANCEIRO
        criar("CONTA_PAGAR", "Contas a Pagar", "Financeiro", 1);
        criar("CONTA_RECEBER", "Contas a Receber", "Financeiro", 2);
        criar("FORNECEDOR", "Fornecedores", "Financeiro", 3);
        criar("CATEGORIA_CONTA", "Categorias", "Financeiro", 4);
        criar("CENTRO_CUSTO", "Centros de Custo", "Financeiro", 5);
        criar("FORMA_PAGAMENTO", "Formas de Pagamento", "Financeiro", 6);

        // MANUTENÇÃO
        criar("MANUTENCAO", "Dashboard Manutenção", "Manutenção", 1);
        criar("ORDEM_SERVICO", "Ordens de Serviço", "Manutenção", 2);
        criar("PECA", "Peças e Estoque", "Manutenção", 3);
        criar("MOVIMENTACAO_ESTOQUE", "Movimentação de Estoque", "Manutenção", 4);
        criar("ALERTA_MANUTENCAO", "Alertas", "Manutenção", 5);

        // CHECKLIST
        criar("CHECKLIST_DASHBOARD", "Dashboard Checklist", "Checklist", 1);
        criar("CHECKLIST_REALIZAR", "Realizar Checklist", "Checklist", 2);
        criar("CHECKLIST_HISTORICO", "Histórico", "Checklist", 3);
        criar("MODELO_CHECKLIST", "Modelos de Checklist", "Checklist", 4);

        // RELATORIO
        criar("RELATORIO", "Relatorios", "Relatorios", 9);
    }

    private void criar(String codigo, String nome, String categoria, Integer ordem) {
        repository.findByCodigo(codigo)
            .orElseGet(() -> {
                Funcionalidade f = new Funcionalidade();
                f.setCodigo(codigo);
                f.setNome(nome);
                f.setCategoria(categoria);
                f.setOrdemMenu(ordem);
                return repository.save(f);
            });
    }
}
