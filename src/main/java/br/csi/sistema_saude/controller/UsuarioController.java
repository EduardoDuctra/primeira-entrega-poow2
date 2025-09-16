package br.csi.sistema_saude.controller;

import br.csi.sistema_saude.model.Dados;
import br.csi.sistema_saude.model.Relatorio;
import br.csi.sistema_saude.model.Usuario;
import br.csi.sistema_saude.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {

    private UsuarioService usuarioService;
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/listar-usuarios")
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        List<Usuario> usuarios = this.usuarioService.listarUsuarios();
        if (usuarios.isEmpty()) {
            throw new NoSuchElementException(); //chama o metodo NoSuchElementException da classe Tratador de Error

        }
        return ResponseEntity.ok(usuarios); // 200
    }


    @GetMapping("/{codUsuario}")
    public ResponseEntity<Usuario> buscarUsuario(@PathVariable Integer codUsuario) {
        Usuario usuario = this.usuarioService.buscarUsuario(codUsuario);
        if (usuario == null) {
            throw new NoSuchElementException(); //chama o metodo NoSuchElementException da classe Tratador de Error

        }
        return ResponseEntity.ok(usuario); // 200
    }

    @PostMapping("/salvar")
    @Transactional
    public ResponseEntity salvarUsuario(@RequestBody @Valid Usuario usuario, UriComponentsBuilder uriBuilder) {
        this.usuarioService.salvarUsuario(usuario);
        URI uri = uriBuilder.path("/usuario/{codUsuario}").buildAndExpand(usuario.getCodUsuario()).toUri();
        return ResponseEntity.created(uri).body(usuario);
    }

    @PutMapping("/atualizar")
    @Transactional
    public ResponseEntity atualizarUsuario(@RequestBody @Valid Usuario usuario) {
        this.usuarioService.atualizarUsuario(usuario);
        return ResponseEntity.ok(usuario);
    }

    @DeleteMapping("/deletar/{codUsuario}")
    public ResponseEntity deleteUsuario(@PathVariable Integer codUsuario) {
        this.usuarioService.excluirUsuario(codUsuario);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String email,
                                   @RequestParam String senha,
                                   HttpSession session) {

        Usuario usuario = usuarioService.validarUsuario(email, senha);

        if (usuario == null) {
            throw new IllegalArgumentException("Usuário ou senha inválidos");
        }

        session.setAttribute("usuarioLogado", usuario);
        return ResponseEntity.ok(usuario);
    }

    @GetMapping("/{codUsuario}/imc")
    public ResponseEntity<?> calcularIMC(@PathVariable Integer codUsuario) {
        Usuario usuario = usuarioService.buscarUsuario(codUsuario);
        if (usuario == null) {
            throw new NoSuchElementException("Usuário não encontrado");
        }

        // Busca os relatórios do usuário
        List<Relatorio> relatoriosDoUsuario = usuarioService.buscarRelatoriosPorUsuario(usuario);

        try {
            double imc = usuarioService.calcularIMC(usuario, relatoriosDoUsuario);
            return ResponseEntity.ok("IMC do usuário " + usuario.getPerfil().getNome() + ": " + imc);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //validar se está buscando o email certo.
    @GetMapping("/buscar-email")
    public ResponseEntity<?> buscarPorEmail(@RequestParam String email) {
        Usuario usuario = usuarioService.buscarPorEmail(email);

        if (usuario == null) {

            throw new NoSuchElementException("Usuário não encontrado com o email: " + email);
        }


        return ResponseEntity.ok(usuario);
    }



}
