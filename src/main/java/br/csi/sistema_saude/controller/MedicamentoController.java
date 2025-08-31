package br.csi.sistema_saude.controller;


import br.csi.sistema_saude.model.Dados;
import br.csi.sistema_saude.model.Medicamento;
import br.csi.sistema_saude.service.MedicamentoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/medicamentos")
public class MedicamentoController {

    private MedicamentoService medicamentoService;
    public MedicamentoController(MedicamentoService medicamentoService) {
        this.medicamentoService = medicamentoService;
    }

    @GetMapping("/listar-medicamentos")
    public List<Medicamento> listarTodosMedicamentod() {
        return medicamentoService.buscarMedicamentos();
    }

    @GetMapping("/{codMedicamento}")
    public Medicamento buscarMedicamento(@PathVariable Integer codMedicamento) {
        return this.medicamentoService.buscarMedicamento(codMedicamento);

    }

    @GetMapping("/buscar-por-usuario/{codUsuario}")
    public List<Medicamento> buscarMedicamentosUsuario(@PathVariable Integer codUsuario) {
        return this.medicamentoService.buscarMedicamentoUsuario(codUsuario);
    }

    @PostMapping("/salvar")
    public void salvarMedicamento(@RequestBody Medicamento medicamento) {
        this.medicamentoService.salvarMedicamento(medicamento);
    }

    @PutMapping("/atualizar")
    public void atualizarMedicamento(@RequestBody Medicamento medicamento) {
        this.medicamentoService.atualizarMedicamento(medicamento);
    }

    @DeleteMapping("/deletar/{codMedicamento}")
    public void excluirMedicamento(@PathVariable Integer codMedicamento) {
        this.medicamentoService.excluirMedicamento(codMedicamento);
    }


}
