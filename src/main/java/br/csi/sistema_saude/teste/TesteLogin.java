package br.csi.sistema_saude.teste;

import br.csi.sistema_saude.model.Usuario;
import br.csi.sistema_saude.model.UsuarioConta;
import br.csi.sistema_saude.model.UsuarioPerfil;
import br.csi.sistema_saude.service.UsuarioService;

import java.util.ArrayList;
import java.util.List;

public class TesteLogin {

    public static void main(String[] args) {

        // --- Criando usuários "em memória" ---
        List<Usuario> usuarios = new ArrayList<>();

        Usuario u1 = new Usuario();
        u1.setCodUsuario(1);
        UsuarioConta conta1 = new UsuarioConta();
        conta1.setEmail("teste@email.com");
        conta1.setSenha("123456");
        u1.setConta(conta1);
        UsuarioPerfil perfil1 = new UsuarioPerfil();
        perfil1.setNome("Usuario Teste");
        perfil1.setSexo('M');
        perfil1.setAltura(1.75);
        u1.setPerfil(perfil1);
        usuarios.add(u1);

        // --- Criando um "service" fake que valida login usando a lista ---
        UsuarioService usuarioService = new UsuarioService(null, null) {
            @Override
            public Usuario validarUsuario(String email, String senha) {
                for (Usuario u : usuarios) {
                    if (u.getConta().getEmail().equals(email) && u.getConta().getSenha().equals(senha)) {
                        return u;
                    }
                }
                return null;
            }
        };

        // --- Testes de login ---
        Usuario logado1 = usuarioService.validarUsuario("teste@email.com", "123456");
        System.out.println(logado1 != null ? "Login válido: " + logado1.getPerfil().getNome() : "Falha no login");

        Usuario logado2 = usuarioService.validarUsuario("teste@email.com", "senhaErrada");
        System.out.println(logado2 != null ? "Login válido" : "Senha inválida");

        Usuario logado3 = usuarioService.validarUsuario("inexistente@email.com", "123456");
        System.out.println(logado3 != null ? "Login válido" : "Usuário não encontrado");
    }
}
