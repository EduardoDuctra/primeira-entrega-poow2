package br.csi.sistema_saude.controller;


import br.csi.sistema_saude.model.Medicamento;

import br.csi.sistema_saude.service.MedicamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/medicamentos")
@Tag(name = "Mediacamentos", description = "Path relacionado aos medicamentos")
public class MedicamentoController {

    private MedicamentoService medicamentoService;
    public MedicamentoController(MedicamentoService medicamentoService) {
        this.medicamentoService = medicamentoService;
    }

    @Operation(summary = "Listar todo os medicamentos", description = "Retorna uma lista com todo os medicamentos cadastrados no banco de dados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Medicamentos listados com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Medicamento.class))),
            @ApiResponse(responseCode = "400", description = "Erro ao listar medicamentos", content = @Content),
            @ApiResponse(responseCode = "404", description = "Erro ao encontrar dados", content = @Content)
    })
    @GetMapping("/listar-medicamentos")
    public ResponseEntity<List<Medicamento>> listarTodosMedicamentos() {
        List <Medicamento> medicamentos = medicamentoService.buscarMedicamentos();
        if (medicamentos.isEmpty()) {
            throw new NoSuchElementException("Medicamentos não encontrados");
        }
        return ResponseEntity.ok(medicamentos); //200 OK
    }

    @GetMapping("/{codMedicamento}")
    @Operation(summary = "Listar um medicamentos a partir do seu código", description = "Retorna um medicamentos a partir do ID fornecido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Medicamento listado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Medicamento.class))),
            @ApiResponse(responseCode = "400", description = "Erro ao listar medicamento", content = @Content),
            @ApiResponse(responseCode = "404", description = "Erro ao encontrar dados", content = @Content)
    })
    public ResponseEntity <Medicamento> buscarMedicamento(@PathVariable Integer codMedicamento) {

        Medicamento medicamento = medicamentoService.buscarMedicamento(codMedicamento);
        if (medicamento == null) {
            throw new NoSuchElementException("Medicamento não encontrado"); //chama o metodo NoSuchElementException da classe Tratador de Error
        }
        return ResponseEntity.ok(medicamento); // 200

    }

    @GetMapping("/buscar-por-usuario/{codUsuario}")
    @Operation(summary = "Listar os medicamentos a partir do código do usuário", description = "Retorna uma lista os medicamentos associados a um ID de usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Medicamentos listados com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Medicamento.class))),
            @ApiResponse(responseCode = "400", description = "Erro ao listar medicamentos do usuário", content = @Content),
            @ApiResponse(responseCode = "404", description = "Erro ao encontrar dados", content = @Content)
    })
    public ResponseEntity<List<Medicamento>> buscarMedicamentosUsuario(@PathVariable Integer codUsuario) {
        List <Medicamento> medicamentos = this.medicamentoService.buscarMedicamentoUsuario(codUsuario);
        return ResponseEntity.ok(medicamentos);
    }

    @PostMapping("/salvar")
    @Operation(summary = "Criar um novo medicamento", description = "Salva um novo medicamento no banco de dados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Medicamento salvo com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Medicamento.class))),
            @ApiResponse(responseCode = "400", description = "Erro ao salvar medicamento", content = @Content)
    })
    public ResponseEntity salvarMedicamento(@RequestBody Medicamento medicamento, UriComponentsBuilder uriBuilder) {
        this.medicamentoService.salvarMedicamento(medicamento);
        URI uri = uriBuilder.path("/medicamentos/{id}").buildAndExpand(medicamento.getCodMedicamento()).toUri();
        return ResponseEntity.created(uri).body(medicamento);
    }

    @PutMapping("/atualizar")
    @Operation(summary = "Atualizar um  medicamento", description = "Recebe um Medicamento e atualiza seus informações no banco de dados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Medicamento atualizado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Medicamento.class))),
            @ApiResponse(responseCode = "400", description = "Erro ao atualizar medicamento", content = @Content),
            @ApiResponse(responseCode = "404", description = "Erro ao encontrar dados", content = @Content)
    })
    public ResponseEntity atualizarMedicamento(@RequestBody Medicamento medicamento) {
        this.medicamentoService.atualizarMedicamento(medicamento);
        return ResponseEntity.ok(medicamento);
    }

    @DeleteMapping("/deletar/{codMedicamento}")
    @Operation(summary = "Deletar um medicamento", description = "Deleta um medicamento do banco de dados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Medicamento deletado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Medicamento.class))),
            @ApiResponse(responseCode = "400", description = "Erro ao deletar medicamento", content = @Content),
            @ApiResponse(responseCode = "404", description = "Erro ao encontrar dados", content = @Content)
    })
    public ResponseEntity excluirMedicamento(@PathVariable Integer codMedicamento) {
        this.medicamentoService.excluirMedicamento(codMedicamento);
        return ResponseEntity.noContent().build();
    }


}
