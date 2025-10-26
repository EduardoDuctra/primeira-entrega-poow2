package br.csi.sistema_saude.controller;

import br.csi.sistema_saude.model.BancoMedicamentos;
import br.csi.sistema_saude.service.BancoMedicamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/banco-medicamentos")
@Tag(name = "Nome Medicamentos", description = "Path relacionado ao banco de dados dos nomes de medicamentos")
public class BancoMedicamentoController {

    private final BancoMedicamentoService bancoMedicamentoService;

    public BancoMedicamentoController(BancoMedicamentoService bancoMedicamentoService) {
        this.bancoMedicamentoService = bancoMedicamentoService;
    }

    @Operation(summary = "Listar todos os nomes de medicamentos", description = "Retorna uma lista com todos os nomes de medicamentos cadastrados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nomes listados com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BancoMedicamentos.class))),
            @ApiResponse(responseCode = "404", description = "Nenhum nome de medicamento encontrado", content = @Content)
    })
    @GetMapping("/listar-todos")
    public ResponseEntity<List<BancoMedicamentos>> listarTodos() {
        List<BancoMedicamentos> nomes = bancoMedicamentoService.listarTodos();

        if (nomes == null || nomes.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204
        }

        return ResponseEntity.ok(nomes); //200
    }

    @PostMapping("/salvar")
    @Operation(summary = "Salvar um novo nome de medicamento", description = "Salva um novo nome de medicamento no banco de dados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Nome de medicamento salvo com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BancoMedicamentos.class))),
            @ApiResponse(responseCode = "400", description = "Erro ao salvar nome de medicamento", content = @Content)
    })
    public ResponseEntity<BancoMedicamentos> salvar(@RequestBody BancoMedicamentos bancoMedicamentos, UriComponentsBuilder uriBuilder) {
        bancoMedicamentoService.salvarNomeMedicamento(bancoMedicamentos);
        URI uri = uriBuilder.path("/nome-medicamentos/{id}")
                .buildAndExpand(bancoMedicamentos.getCodNomeMedicamento()).toUri();
        return ResponseEntity.created(uri).body(bancoMedicamentos);
    }

    @PutMapping("/atualizar")
    @Transactional
    @Operation(summary = "Atualizar um nome de medicamento", description = "Recebe um NomeMedicamento e atualiza suas informações no banco de dados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nome de medicamento atualizado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BancoMedicamentos.class))),
            @ApiResponse(responseCode = "400", description = "Erro ao atualizar nome de medicamento", content = @Content),
            @ApiResponse(responseCode = "404", description = "Nome de medicamento não encontrado", content = @Content)
    })
    public ResponseEntity<BancoMedicamentos> atualizar(@RequestBody BancoMedicamentos bancoMedicamentos) {

        bancoMedicamentoService.atualizarNomeMedicamento(bancoMedicamentos.getCodNomeMedicamento(), bancoMedicamentos.getNome());
        return ResponseEntity.ok(bancoMedicamentos);
    }

    @DeleteMapping("/deletar/{id}")
    @Operation(summary = "Deletar um nome de medicamento", description = "Deleta um nome de medicamento do banco de dados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Nome de medicamento deletado com sucesso", content = @Content),
            @ApiResponse(responseCode = "404", description = "Nome de medicamento não encontrado", content = @Content)
    })
    public ResponseEntity<Void> deletar(@PathVariable int id) {

        bancoMedicamentoService.excluirNomeMedicamento(id);
        return ResponseEntity.noContent().build(); //204
    }
}
