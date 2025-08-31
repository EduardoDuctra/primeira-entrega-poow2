package br.csi.sistema_saude.controller;

import br.csi.sistema_saude.model.Usuario;
import br.csi.sistema_saude.service.UsuarioService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {

    private UsuarioService usuarioService;
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/listar-usuarios")
    public List<Usuario> listarUsuarios() {
        return this.usuarioService.listarUsuarios();
    }

    @GetMapping("/{codUsuario}")
    public Usuario buscarUsuario(@PathVariable Integer codUsuario) {
        return this.usuarioService.buscarUsuario(codUsuario);
    }

    @PostMapping("/salvar")
    public void salvarUsuario(@RequestBody  Usuario usuario) {
        this.usuarioService.salvarUsuario(usuario);
    }

    @PutMapping("/atualizar")
    public void atualizarUsuario(@RequestBody Usuario usuario) {
        this.usuarioService.atualizarUsuario(usuario);
    }

    @DeleteMapping("/deletar/{codUsuario}")
    public void deleteUsuario(@PathVariable Integer codUsuario) {
        this.usuarioService.excluirUsuario(codUsuario);
    }
}
