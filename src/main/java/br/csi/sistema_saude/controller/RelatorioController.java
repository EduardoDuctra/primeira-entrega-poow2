package br.csi.sistema_saude.controller;

import br.csi.sistema_saude.model.Relatorio;
import br.csi.sistema_saude.model.RelatorioId;
import br.csi.sistema_saude.service.RelatorioService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/relatorios")
public class RelatorioController {

    private final RelatorioService relatorioService;

    public RelatorioController(RelatorioService relatorioService) {
        this.relatorioService = relatorioService;
    }

    // Salvar um relatório
    @PostMapping
    public Relatorio salvarRelatorio(@RequestBody Relatorio relatorio) {
        relatorioService.salvarRelatorio(relatorio);
        return relatorio;
    }

    // Listar todos os relatórios
    @GetMapping
    public List<Relatorio> listarRelatorios() {
        return relatorioService.listarRelatorios();
    }

    // Buscar um relatório pelo ID composto
    @GetMapping("/{codUsuario}/{codDado}/{data}")
    public Relatorio buscarRelatorio(
            @PathVariable int codUsuario,
            @PathVariable int codDado,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data
    ) {
        RelatorioId id = new RelatorioId(codUsuario, codDado, data);
        return relatorioService.buscarRelatorio(id);
    }

    // Excluir um relatório pelo ID composto
    @DeleteMapping("/{codUsuario}/{codDado}/{data}")
    public void excluirRelatorio(
            @PathVariable int codUsuario,
            @PathVariable int codDado,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data
    ) {
        RelatorioId id = new RelatorioId(codUsuario, codDado, data);
        relatorioService.excluirRelatorio(id);
    }
}
