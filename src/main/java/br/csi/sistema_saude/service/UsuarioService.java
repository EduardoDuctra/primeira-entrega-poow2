package br.csi.sistema_saude.service;

import br.csi.sistema_saude.model.Dados;
import br.csi.sistema_saude.model.Relatorio;
import br.csi.sistema_saude.model.Usuario;
import br.csi.sistema_saude.repository.RelatorioRepository;
import br.csi.sistema_saude.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RelatorioRepository relatorioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          RelatorioRepository relatorioRepository) {
        this.usuarioRepository = usuarioRepository;
        this.relatorioRepository = relatorioRepository;
    }

    public void salvarUsuario(Usuario usuario) {
        usuarioRepository.save(usuario);
    }

    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    public Usuario buscarUsuario(Integer codUsuario) {
        return usuarioRepository.findById(codUsuario).get();
    }

    public void excluirUsuario(Integer codUsuario) {
        usuarioRepository.deleteById(codUsuario);
    }

    public void atualizarUsuario(Usuario usuario) {

        Usuario u = this.usuarioRepository.getReferenceById(usuario.getCodUsuario());

        u.getConta().setEmail(usuario.getConta().getEmail());
        u.getConta().setSenha(usuario.getConta().getSenha());

        u.getPerfil().setNome(usuario.getPerfil().getNome());
        u.getPerfil().setSexo(usuario.getPerfil().getSexo());
        u.getPerfil().setAltura(usuario.getPerfil().getAltura());


        this.usuarioRepository.save(u);
    }

    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByContaEmail(email);
    }

    public Usuario validarUsuario(String email, String senha) {
        Usuario usuario = usuarioRepository.findByContaEmail(email);

        if (usuario == null) {
            return null;
        }

        if(!usuario.getConta().getSenha().equals(senha)) {
            return null;
        }

        return usuario;

    }

    public double calcularIMC(Usuario usuario, List<Relatorio> relatorios) {
        if (usuario == null || usuario.getPerfil() == null) {
            throw new IllegalArgumentException("Usuário ou perfil não podem ser nulos");
        }

        double altura = usuario.getPerfil().getAltura();

        if (altura <= 0) {
            throw new IllegalArgumentException("Altura inválida");
        }

        // Pega o relatório mais recente
        Optional<Relatorio> relatorioRecente = relatorios.stream()
                .max(Comparator.comparing(r -> r.getId().getData()));

        if (relatorioRecente.isEmpty()) {
            throw new IllegalArgumentException("Nenhum relatório encontrado para o usuário");
        }

        double peso = relatorioRecente.get().getDados().getPeso();

        // IMC = peso / (altura^2)
        return peso / (altura * altura);
    }


    public List<Relatorio> buscarRelatoriosPorUsuario(Usuario usuario) {
        return relatorioRepository.findAll()
                .stream()
                .filter(r -> r.getUsuario().getCodUsuario() == usuario.getCodUsuario())
                .toList();
    }

}
