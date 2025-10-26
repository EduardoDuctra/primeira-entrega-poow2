package br.csi.sistema_saude.service;

import br.csi.sistema_saude.model.DTO.DadoUsuario;
import br.csi.sistema_saude.model.DTO.IMCDTO;
import br.csi.sistema_saude.model.DTO.UsuarioPerfilDTO;
import br.csi.sistema_saude.model.Relatorio;
import br.csi.sistema_saude.model.Usuario;
import br.csi.sistema_saude.repository.RelatorioRepository;
import br.csi.sistema_saude.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;


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
        usuario.getConta().setSenha(new BCryptPasswordEncoder().encode(usuario.getConta().getSenha()));
        usuarioRepository.save(usuario);
    }

    public List<DadoUsuario> listarUsuarios() {
        return usuarioRepository.findAll()
                .stream()
                .map(DadoUsuario::new)
                .toList();
    }

    public DadoUsuario buscarUsuario(Integer codUsuario) {
        Usuario usuario = usuarioRepository.findById(codUsuario).get();
        return new DadoUsuario(usuario);
    }

    public void excluirUsuario(Integer codUsuario) {
        usuarioRepository.deleteById(codUsuario);
    }

    public Usuario atualizarUsuario(Usuario usuario) {
        Usuario u = usuarioRepository.findById(usuario.getCodUsuario())
                .orElseThrow(() -> new NoSuchElementException("Usuário não encontrado"));

        u.getConta().setEmail(usuario.getConta().getEmail());

        if (usuario.getConta().getSenha() != null && !usuario.getConta().getSenha().isEmpty()) {
            u.getConta().setSenha(new BCryptPasswordEncoder().encode(usuario.getConta().getSenha()));
        }

        u.getPerfil().setNome(usuario.getPerfil().getNome());
        u.getPerfil().setSexo(usuario.getPerfil().getSexo());
        u.getPerfil().setAltura(usuario.getPerfil().getAltura());

        return usuarioRepository.save(u);
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


    public Usuario buscarPorId(Integer codUsuario) {
        return usuarioRepository.findById(codUsuario)
                .orElseThrow(() -> new NoSuchElementException("Usuário não encontrado"));
    }


    public IMCDTO calcularIMC(Usuario usuario, List<Relatorio> relatorios) {
        if (usuario == null || usuario.getPerfil() == null) {
            throw new NoSuchElementException("Usuário ou perfil não podem ser nulos");
        }

        // Converte para DTO de perfil
        UsuarioPerfilDTO perfilDTO = new UsuarioPerfilDTO(usuario);

        double altura = perfilDTO.altura();
        if (altura <= 0) {
            throw new NoSuchElementException("Altura inválida");
        }

        // Pega o relatório mais recente
        Relatorio relatorioRecente = relatorios.stream()
                .max(Comparator.comparing(r -> r.getId().getData()))
                .orElseThrow(() -> new NoSuchElementException("Nenhum relatório encontrado"));

        double peso = relatorioRecente.getDados().getPeso();
        double imc = peso / Math.pow(altura, 2);

        // Retorna o DTO com nome, altura e IMC
        return new IMCDTO(usuario.getPerfil().getNome(), altura, imc);
    }


    public List<Relatorio> buscarRelatoriosPorUsuario(Usuario usuario) {
        return relatorioRepository.findAll()
                .stream()
                .filter(r -> r.getUsuario().getCodUsuario() == usuario.getCodUsuario())
                .toList();
    }

}
