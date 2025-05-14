package src;

import java.util.Scanner;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GerenciaBanco {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("=== Sistema de Gerenciamento Bancário ===");
        System.out.println("Por favor, informe seus dados para criar uma conta:");
        
        System.out.print("Nome: ");
        String nome = scanner.nextLine();
        
        System.out.print("Sobrenome: ");
        String sobrenome = scanner.nextLine();
        
        System.out.print("CPF: ");
        String cpf = scanner.nextLine();
        
        ContaBancaria conta = new ContaBancaria(nome, sobrenome, cpf);
        
        System.out.println("\nConta criada com sucesso!");
        System.out.println("Titular: " + conta.getNomeCompleto());
        
        // Log de criação da conta
        registrarLog(conta, "Criação de conta", "Conta criada com sucesso para " + conta.getNomeCompleto());
        
        exibirMenu(scanner, conta);
        
        scanner.close();
    }
    
    public static void exibirMenu(Scanner scanner, ContaBancaria conta) {
        int opcao;
        
        do {
            System.out.println("\n=== Menu Principal ===");
            System.out.println("1 - Consultar saldo");
            System.out.println("2 - Realizar depósito");
            System.out.println("3 - Realizar saque");
            System.out.println("4 - Exibir dados da conta");
            System.out.println("5 - Consultar histórico de operações");
            System.out.println("0 - Sair do sistema");
            System.out.print("Escolha uma opção: ");
            
            try {
                opcao = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("\nEntrada inválida! Por favor, digite apenas números.");
                opcao = -1; // Valor inválido para cair no caso default
            }
            
            switch (opcao) {
                case 1:
                    System.out.println("\nSeu saldo atual é: R$ " + String.format("%.2f", conta.getSaldo()));
                    registrarLog(conta, "Consulta de Saldo", "Saldo consultado: R$ " + String.format("%.2f", conta.getSaldo()));
                    break;
                    
                case 2:
                    try {
                        System.out.print("\nInforme o valor para depósito: R$ ");
                        double valorDeposito = Double.parseDouble(scanner.nextLine());
                        
                        if (valorDeposito > 0) {
                            double saldoAnterior = conta.getSaldo();
                            conta.realizarDeposito(valorDeposito);
                            System.out.println("Depósito realizado com sucesso!");
                            System.out.println("Novo saldo: R$ " + String.format("%.2f", conta.getSaldo()));
                            
                            // Registrar log de depósito
                            registrarLog(conta, "Depósito", 
                                "Valor: R$ " + String.format("%.2f", valorDeposito) + 
                                " | Saldo anterior: R$ " + String.format("%.2f", saldoAnterior) + 
                                " | Novo saldo: R$ " + String.format("%.2f", conta.getSaldo()));
                        } else {
                            System.out.println("Valor inválido! O depósito deve ser maior que zero.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Valor inválido! Por favor, digite um número válido.");
                    }
                    break;
                    
                case 3:
                    try {
                        System.out.print("\nInforme o valor para saque: R$ ");
                        double valorSaque = Double.parseDouble(scanner.nextLine());
                        
                        if (valorSaque > 0) {
                            double saldoAnterior = conta.getSaldo();
                            if (conta.realizarSaque(valorSaque)) {
                                System.out.println("Saque realizado com sucesso!");
                                System.out.println("Novo saldo: R$ " + String.format("%.2f", conta.getSaldo()));
                                
                                // Registrar log de saque
                                registrarLog(conta, "Saque", 
                                    "Valor: R$ " + String.format("%.2f", valorSaque) + 
                                    " | Saldo anterior: R$ " + String.format("%.2f", saldoAnterior) + 
                                    " | Novo saldo: R$ " + String.format("%.2f", conta.getSaldo()));
                            } else {
                                System.out.println("Saldo insuficiente para realizar esta operação!");
                                registrarLog(conta, "Tentativa de Saque (Falha)", 
                                    "Valor: R$ " + String.format("%.2f", valorSaque) + 
                                    " | Saldo insuficiente: R$ " + String.format("%.2f", conta.getSaldo()));
                            }
                        } else {
                            System.out.println("Valor inválido! O saque deve ser maior que zero.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Valor inválido! Por favor, digite um número válido.");
                    }
                    break;
                    
                case 4:
                    System.out.println("\n=== Dados da Conta ===");
                    System.out.println("Titular: " + conta.getNomeCompleto());
                    System.out.println("CPF: " + conta.getCpf());
                    System.out.println("Saldo: R$ " + String.format("%.2f", conta.getSaldo()));
                    registrarLog(conta, "Consulta de Dados", "Dados da conta consultados");
                    break;
                    
                case 5:
                    System.out.println("\n=== Histórico de Operações ===");
                    exibirHistoricoOperacoes(conta);
                    break;
                    
                case 0:
                    System.out.println("\nObrigado por utilizar nosso sistema bancário!");
                    System.out.println("Tenha um excelente dia, " + conta.getNome() + "!");
                    registrarLog(conta, "Encerramento", "Sessão encerrada pelo usuário");
                    break;
                    
                default:
                    System.out.println("\nOpção inválida. Por favor, tente novamente.");
            }
            
        } while (opcao != 0);
    }
    
    /**
     * Função para registrar logs de operações no sistema
     * @param conta A conta bancária relacionada à operação
     * @param tipoOperacao O tipo de operação realizada
     * @param detalhes Detalhes adicionais sobre a operação
     */
    public static void registrarLog(ContaBancaria conta, String tipoOperacao, String detalhes) {
        try {
            // Obter o caminho do diretório onde a aplicação está sendo executada
            String diretorioAtual = System.getProperty("user.dir");
            
            // Criar diretório de logs no diretório do projeto
            File diretorio = new File(diretorioAtual + File.separator + "logs");
            if (!diretorio.exists()) {
                diretorio.mkdir();
            }
            
            // Nome do arquivo baseado no CPF do cliente (usando caminho absoluto)
            String nomeArquivo = diretorio.getAbsolutePath() + File.separator + "conta_" + conta.getCpf() + ".log";
            
            // Data e hora atual formatada
            SimpleDateFormat formatoData = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            String dataHora = formatoData.format(new Date());
            
            // Conteúdo do log
            String conteudoLog = "[" + dataHora + "] - " + tipoOperacao + " - " + detalhes + "\n";
            
            // Escrever no arquivo (append = true para adicionar ao final)
            FileWriter escritor = new FileWriter(nomeArquivo, true);
            escritor.write(conteudoLog);
            escritor.close();
            
            System.out.println("Log salvo em: " + nomeArquivo);
            
        } catch (IOException e) {
            System.out.println("Erro ao registrar log: " + e.getMessage());
        }
    }
    
    /**
     * Exibe o histórico de operações da conta
     * @param conta A conta bancária
     */
    public static void exibirHistoricoOperacoes(ContaBancaria conta) {
        try {
            // Obter o caminho do diretório onde a aplicação está sendo executada
            String diretorioAtual = System.getProperty("user.dir");
            File arquivo = new File(diretorioAtual + File.separator + "logs" + File.separator + "conta_" + conta.getCpf() + ".log");
            
            if (!arquivo.exists()) {
                System.out.println("Não há histórico de operações disponível.");
                return;
            }
            
            Scanner leitor = new Scanner(arquivo);
            int contador = 0;
            
            while (leitor.hasNextLine()) {
                String linha = leitor.nextLine();
                System.out.println(linha);
                contador++;
            }
            
            leitor.close();
            
            if (contador == 0) {
                System.out.println("Não há operações registradas.");
            }
            
        } catch (IOException e) {
            System.out.println("Erro ao ler o histórico: " + e.getMessage());
        }
    }
}

class ContaBancaria {
    private String nome;
    private String sobrenome;
    private String cpf;
    private double saldo;
    
    public ContaBancaria(String nome, String sobrenome, String cpf) {
        this.nome = nome;
        this.sobrenome = sobrenome;
        this.cpf = cpf;
        this.saldo = 0.0;
    }
    
    public String getNome() {
        return nome;
    }
    
    public String getSobrenome() {
        return sobrenome;
    }
    
    public String getNomeCompleto() {
        return nome + " " + sobrenome;
    }
    
    public String getCpf() {
        return cpf;
    }
    
    public double getSaldo() {
        return saldo;
    }
    
    public void realizarDeposito(double valor) {
        if (valor > 0) {
            this.saldo += valor;
        }
    }
    
    public boolean realizarSaque(double valor) {
        if (valor > 0 && this.saldo >= valor) {
            this.saldo -= valor;
            return true;
        }
        return false;
    }
}