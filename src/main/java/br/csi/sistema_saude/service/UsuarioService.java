package br.csi.sistema_saude.service;

import br.csi.sistema_saude.model.Usuario;
import br.csi.sistema_saude.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
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

}
