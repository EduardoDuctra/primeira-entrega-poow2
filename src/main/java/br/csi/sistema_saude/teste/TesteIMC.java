package br.csi.sistema_saude.teste;

import br.csi.sistema_saude.model.*;
import br.csi.sistema_saude.service.UsuarioService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TesteIMC {

    public static void main(String[] args) {

        // --- Criação de usuários a partir do JSON ---
        List<Usuario> usuarios = new ArrayList<>();

        // Usuario 1
        Usuario usuario1 = new Usuario();
        usuario1.setCodUsuario(2);

        UsuarioConta conta1 = new UsuarioConta();
        conta1.setEmail("e@email.com");
        conta1.setSenha("123456");
        usuario1.setConta(conta1);

        UsuarioPerfil perfil1 = new UsuarioPerfil();
        perfil1.setNome("Usuario 1");
        perfil1.setSexo('M');
        perfil1.setAltura(1.73);
        usuario1.setPerfil(perfil1);

        usuarios.add(usuario1);

        // Usuario 2
        Usuario usuario2 = new Usuario();
        usuario2.setCodUsuario(22);

        UsuarioConta conta2 = new UsuarioConta();
        conta2.setEmail("atualizado@email.com");
        conta2.setSenha("novaSenha123");
        usuario2.setConta(conta2);

        UsuarioPerfil perfil2 = new UsuarioPerfil();
        perfil2.setNome("Usuario Atualizado");
        perfil2.setSexo('F');
        perfil2.setAltura(1.68);
        usuario2.setPerfil(perfil2);

        usuarios.add(usuario2);

        // --- Criando relatórios com pesos diferentes ---
        List<Relatorio> relatorios = new ArrayList<>();

        // Relatórios para usuario1
        Dados dados1 = new Dados();
        dados1.setPeso(70.5);
        Relatorio relatorio1 = new Relatorio(); // construtor padrão
        relatorio1.setId(new RelatorioId(usuario1.getCodUsuario(), 1, LocalDate.of(2025, 9, 15)));
        relatorio1.setUsuario(usuario1);
        relatorio1.setDados(dados1);
        relatorios.add(relatorio1);

        Dados dados2 = new Dados();
        dados2.setPeso(72.0);
        Relatorio relatorio2 = new Relatorio(); // construtor padrão
        relatorio2.setId(new RelatorioId(usuario1.getCodUsuario(), 2, LocalDate.of(2025, 9, 20)));
        relatorio2.setUsuario(usuario1);
        relatorio2.setDados(dados2);
        relatorios.add(relatorio2);

        // Relatório para usuario2
        Dados dados3 = new Dados();
        dados3.setPeso(65.0);
        Relatorio relatorio3 = new Relatorio(); // construtor padrão
        relatorio3.setId(new RelatorioId(usuario2.getCodUsuario(), 3, LocalDate.of(2025, 9, 18)));
        relatorio3.setUsuario(usuario2);
        relatorio3.setDados(dados3);
        relatorios.add(relatorio3);

        // --- Teste da função calcularIMC ---
        UsuarioService usuarioService = new UsuarioService(null, null); // Repositório não é necessário para teste em memória

        for (Usuario u : usuarios) {
            // Filtra apenas os relatórios do usuário atual
            List<Relatorio> relatoriosDoUsuario = new ArrayList<>();
            for (Relatorio r : relatorios) {
                if (r.getUsuario().getCodUsuario() == u.getCodUsuario()) {
                    relatoriosDoUsuario.add(r);
                }
            }

            double imc = usuarioService.calcularIMC(u, relatoriosDoUsuario);
            System.out.printf("IMC do usuário %s: %.2f%n", u.getPerfil().getNome(), imc);
        }
    }
}
