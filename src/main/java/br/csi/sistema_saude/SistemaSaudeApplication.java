package br.csi.sistema_saude;

import br.csi.sistema_saude.model.*;
import br.csi.sistema_saude.repository.DadosRepository;
import br.csi.sistema_saude.repository.MedicamentoRepository;
import br.csi.sistema_saude.repository.RelatorioRepository;
import br.csi.sistema_saude.repository.UsuarioRepository;
import br.csi.sistema_saude.service.UsuarioService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.util.List;

@SpringBootApplication
public class SistemaSaudeApplication {

    public static void main(String[] args) {
        SpringApplication.run(SistemaSaudeApplication.class, args);
    }

    @Bean
    public CommandLineRunner demo(
            DadosRepository dadosRepository,
            MedicamentoRepository medicamentoRepository,
            RelatorioRepository relatorioRepository,
            UsuarioRepository usuarioRepository,
            UsuarioService usuarioService
    ) {
        return args -> {

            // Declara fora do try para usar depois
            Usuario usuario1 = null;

            try {
                // --- CRIAÇÃO DE USUÁRIO ---
                usuario1 = new Usuario();

                UsuarioConta usuarioConta1 = new UsuarioConta();
                usuarioConta1.setEmail("e@email.com");
                usuarioConta1.setSenha("123456");

                UsuarioPerfil usuarioPerfil1 = new UsuarioPerfil();
                usuarioPerfil1.setNome("Usuario 1");
                usuarioPerfil1.setSexo('M');
                usuarioPerfil1.setAltura(1.73);

                usuario1.setConta(usuarioConta1);
                usuario1.setPerfil(usuarioPerfil1);

                usuarioRepository.save(usuario1);

                // --- CRIAÇÃO DE DADOS ---
                Dados dado1 = new Dados();
                dado1.setPeso(70.5);
                dado1.setGlicose(95);
                dado1.setColesterolHDL(60);
                dado1.setColesterolVLDL(20);
                dado1.setCreatina(1);
                dado1.setTrigliceridio(150);
                dado1.setUsuario(usuario1);

                dadosRepository.save(dado1);

                // --- CRIAÇÃO DE MEDICAMENTO ---
                Medicamento medicamento = new Medicamento();
                medicamento.setNomeMedicamento("Medicamento 1");
                medicamento.setDataInicio(LocalDate.of(2025, 9, 15));
                medicamento.setDoseDiaria(2);
                medicamento.setDuracaoTratamento(5);
                medicamento.setUsuario(usuario1);

                medicamentoRepository.save(medicamento);

                // --- CRIAÇÃO DE RELATÓRIO ---
                RelatorioId relatorioId = new RelatorioId(
                        usuario1.getCodUsuario(),
                        dado1.getCodDado(),
                        LocalDate.of(2025, 9, 15)
                );

                Relatorio relatorio = new Relatorio();
                relatorio.setId(relatorioId);
                relatorio.setUsuario(usuario1);
                relatorio.setDados(dado1);

                relatorioRepository.save(relatorio);

            } catch (Exception e) {
                System.out.println("Erro ao inserir dados: " + e.getMessage());
            }

        };
    }
}