package br.csi.sistema_saude.controller;


import br.csi.sistema_saude.model.*;
import br.csi.sistema_saude.model.DTO.DadoUsuarioDTO;
import br.csi.sistema_saude.model.DTO.RecolhimentoDTO;
import br.csi.sistema_saude.model.DTO.RecolhimentoFarmaciaDTO;
import br.csi.sistema_saude.service.FarmaciaService;
import br.csi.sistema_saude.service.MedicamentoService;
import br.csi.sistema_saude.service.RecolhimentoService;
import br.csi.sistema_saude.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping("/recolhimento")
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Recolhimento", description = "Path relacionado as recolhimento de medicamentos")
public class RecolhimentoController {

    private final RecolhimentoService recolhimentoService;
    private final UsuarioService usuarioService;
    private final MedicamentoService medicamentoService;
    private final FarmaciaService farmaciaService;


    public RecolhimentoController(RecolhimentoService recolhimentoService, UsuarioService usuarioService, MedicamentoService medicamentoService, FarmaciaService farmaciaService) {
        this.recolhimentoService = recolhimentoService;
        this.usuarioService = usuarioService;
        this.medicamentoService = medicamentoService;
        this.farmaciaService = farmaciaService;
    }


    @PostMapping("/salvar")
    @Operation(summary = "Salva uma nova solicitação de recolhimento de medicamento", description = "Salva uma nova solicitação de recolhimento de medicamento, associado a um Usuário já autenticado da sessão")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Recolhimento salvo com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Recolhimento.class))),
            @ApiResponse(responseCode = "400", description = "Erro ao salvar recolhimento", content = @Content)
    })
    public ResponseEntity salvarRecolhimento(@Valid @RequestBody Recolhimento recolhimento, UriComponentsBuilder uriBuilder) {


        // usuário logado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Usuario usuario = usuarioService.buscarPorEmail(email);

        recolhimento.setEmailCliente(usuario.getConta().getEmail());

        // medicamento
        Medicamento medicamento = medicamentoService.buscarMedicamento(
                recolhimento.getMedicamento().getCodMedicamento()
        );
        recolhimento.setMedicamento(medicamento);


        // salvar recolhimento
        recolhimentoService.salvarRecolhimento(recolhimento);

        URI uri = uriBuilder.path("/recolhimento/{id}").buildAndExpand(recolhimento.getCodRecolhimento()).toUri();
        return ResponseEntity.created(uri).body(recolhimento);
    }

    @PutMapping("/atualizar/{codRecolhimento}")
    @Transactional
    @Operation(summary = "Atualizar um recolhimento", description = "Atualiza o status de um recolhimento existente, identificado pelo ID, para o usuário (farmácia) autenticado na sessão.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Atualização atualizada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Recolhimento.class))),
            @ApiResponse(responseCode = "400", description = "Erro ao atualizar recolhimento", content = @Content),
            @ApiResponse(responseCode = "404", description = "Erro ao encontrar recolhimento", content = @Content),
    })
    public ResponseEntity<RecolhimentoDTO> atualizarRecolhimento(
            @PathVariable int codRecolhimento,
            @RequestParam StatusRecolhimento novoStatus) {

        // Pega o usuário logado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Usuario logado = usuarioService.buscarPorEmail(email);

        // Busca a farmácia do usuário logado
        Farmacia farmacia = farmaciaService.buscarPorUsuario(logado.getCodUsuario());

        // Atualiza status e cria associação se necessário
        recolhimentoService.atualizarStatusRecolhimento(codRecolhimento, farmacia, novoStatus);

        return ResponseEntity.ok().build();
    }



    @GetMapping("/listar-todas-solicitacao-recolhimento")
    @Operation(summary = "Listar todas as solicitações de recolhimento", description = "Retorna uma lista com todas as solicitações de recolhimento associadas para as farmácias decidirem quais aceitar)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recolhimentos listados com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Recolhimento.class))),
            @ApiResponse(responseCode = "404", description = "Erro ao encontrar recolhimentos", content = @Content),
    })
    public ResponseEntity<List<RecolhimentoDTO>> listarTodosRecolhimentos() {


        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Usuario logado = usuarioService.buscarPorEmail(email);


        List<RecolhimentoDTO> recolhimentos = recolhimentoService.listarTodasSolicitacoes();

        if (recolhimentos.isEmpty()) {
            throw new NoSuchElementException("Nenhum recolhimento encontrado");
        }


        return ResponseEntity.ok(recolhimentos); // 200
    }




    @GetMapping("/listar-recolhimento-por-farmacia")
    @Operation(summary = "Listar todas as solicitações de recolhimento de uma farmácia", description = "Retorna uma lista com todas as solicitações de recolhimento associadas de uma farmácia")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recolhimentos listados com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Recolhimento.class))),
            @ApiResponse(responseCode = "404", description = "Erro ao encontrar recolhimentos", content = @Content),
    })
    public ResponseEntity<List<RecolhimentoFarmaciaDTO>> listarRecolhimentos() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Usuario logado = usuarioService.buscarPorEmail(email);

        Farmacia farmacia = farmaciaService.buscarPorUsuario(logado.getCodUsuario());


        List<RecolhimentoFarmaciaDTO> recolhimentos = recolhimentoService.listarPendentesPorFarmacia(farmacia.getCodFarmacia());

        if (recolhimentos.isEmpty()) {
            throw new NoSuchElementException("Nenhum recolhimento encontrado");
        }

        return ResponseEntity.ok(recolhimentos); // 200
    }
}
