package br.csi.sistema_saude.controller;

import br.csi.sistema_saude.model.Dados;
import br.csi.sistema_saude.model.Usuario;
import br.csi.sistema_saude.service.DadosService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dados")
public class DadosController {
    private DadosService dadosService;

    public DadosController(DadosService dadosService) {
        this.dadosService = dadosService;
    }

    @GetMapping("/listar-dados")
    public List<Dados> listarTodosDados() {
        return dadosService.listarDados();
    }

    @GetMapping("/{codDado}")
    public Dados buscarDado(@PathVariable Integer codDado) {
        return this.dadosService.buscarDados(codDado);
    }

    @GetMapping("/buscar-por-usuario/{codUsuario}")
    public List<Dados> buscarDadosUsuario(@PathVariable Integer codUsuario) {
        return this.dadosService.buscarDadosUsuario(codUsuario);
    }

    @PostMapping("/salvar")
    public void salvarDados(@RequestBody Dados dados) {
        this.dadosService.salvarDados(dados);
    }

    @PutMapping("/atualizar")
    public void atualizarDados(@RequestBody Dados dados) {
        this.dadosService.atualizarDados(dados);
    }

    @DeleteMapping("/deletar/{codDado}")
    public void excluirDados(@PathVariable Integer codDado) {
        this.dadosService.excluirDados(codDado);
    }
}
