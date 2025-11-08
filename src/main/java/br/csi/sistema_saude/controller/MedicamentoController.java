package br.csi.sistema_saude.controller;


import br.csi.sistema_saude.model.DTO.BancoMedicamentoDTO;
import br.csi.sistema_saude.model.DTO.MedicamentoDTO;
import br.csi.sistema_saude.model.Medicamento;

import br.csi.sistema_saude.model.Usuario;
import br.csi.sistema_saude.service.MedicamentoService;
import br.csi.sistema_saude.service.BancoMedicamentoService;
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

@RestController
@RequestMapping("/medicamentos")
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Mediacamentos", description = "Path relacionado aos medicamentos")
public class MedicamentoController {

    private MedicamentoService medicamentoService;
    private BancoMedicamentoService bancoMedicamentoService;
    private final UsuarioService usuarioService;

    public MedicamentoController(MedicamentoService medicamentoService, BancoMedicamentoService bancoMedicamentoService, UsuarioService usuarioService) {
        this.medicamentoService = medicamentoService;
        this.bancoMedicamentoService = bancoMedicamentoService;
        this.usuarioService = usuarioService;
    }


    @GetMapping("/{codMedicamento}")
    @Operation(summary = "Listar um medicamentos a partir do seu código", description = "Retorna um medicamentos a partir do ID fornecido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Medicamento listado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Medicamento.class))),
            @ApiResponse(responseCode = "404", description = "Erro ao encontrar dados", content = @Content)
    })
    public ResponseEntity <Medicamento> buscarMedicamento(@PathVariable Integer codMedicamento) {

        Medicamento medicamento = medicamentoService.buscarMedicamento(codMedicamento);
        if (medicamento == null) {
            throw new NoSuchElementException("Medicamento não encontrado"); //chama o metodo NoSuchElementException da classe Tratador de Error
        }
        return ResponseEntity.ok(medicamento); // 200

    }


    @GetMapping("/buscar-por-usuario")
    @Operation(summary = "Listar os medicamentos a partir do código do usuário", description = "Retorna uma lista os medicamentos associados a um Usuário já autenticado da sessão")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Medicamentos listados com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Medicamento.class))),
            @ApiResponse(responseCode = "404", description = "Erro ao encontrar dados", content = @Content)
    })
    public ResponseEntity<List<MedicamentoDTO>> buscarMedicamentosUsuario() {

        //retorno o usuário logado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Usuario usuario = usuarioService.buscarPorEmail(email);


        List<Medicamento> medicamentos = medicamentoService.buscarMedicamentoUsuario(usuario.getCodUsuario());


        List<MedicamentoDTO> medicamentosDTO = new ArrayList<>();

        for (Medicamento m : medicamentos) {

            // crio o DTO do banco de medicamentos se for != null
            BancoMedicamentoDTO bancoMedicamentoDTO = null;
            if (m.getBancoMedicamentos() != null) {
                bancoMedicamentoDTO = new BancoMedicamentoDTO(
                        m.getBancoMedicamentos().getCodNomeMedicamento(),
                        m.getBancoMedicamentos().getNome()
                );
            }


            // crio o DTO medicamento com o DTO de bancoMedicamentoDTO
            MedicamentoDTO dto = new MedicamentoDTO(
                    bancoMedicamentoDTO,
                    m.getCodMedicamento(),
                    m.getBancoMedicamentos().getNome(),
                    m.getDoseDiaria(),
                    m.getDataInicio(),
                    m.getDuracaoTratamento()
            );

            medicamentosDTO.add(dto);
        }

        return ResponseEntity.ok(medicamentosDTO);
    }

    @PostMapping("/salvar")
    @Operation(summary = "Salva o novo medicamento as necessidades do paciente", description = "Salva um novo medicamento ao paciente, associado a um Usuário já autenticado da sessão, conforme suas necessidades")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Medicamento salvo com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Medicamento.class))),
            @ApiResponse(responseCode = "400", description = "Erro ao salvar medicamento", content = @Content)
    })
    public ResponseEntity salvarMedicamento(@Valid @RequestBody Medicamento medicamento, UriComponentsBuilder uriBuilder) {


        //retorno o usuário logado
        //retorno o usuário do BD pelo email
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Usuario usuario = usuarioService.buscarPorEmail(email);

        medicamento.setUsuario(usuario);

        medicamentoService.salvarMedicamento(medicamento);

        URI uri = uriBuilder.path("/medicamentos/{id}").buildAndExpand(medicamento.getCodMedicamento()).toUri();
        return ResponseEntity.created(uri).body(medicamento);
    }

    @PutMapping("/atualizar")
    @Transactional
    @Operation(summary = "Atualizar um medicamento", description = "Recebe um Medicamento e atualiza seus informações no banco de dados. Já associado ao Usuário autenticado da sessão")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Medicamento atualizado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Medicamento.class))),
            @ApiResponse(responseCode = "400", description = "Erro ao atualizar medicamento", content = @Content),
            @ApiResponse(responseCode = "404", description = "Erro ao encontrar dados", content = @Content)
    })
    public ResponseEntity atualizarMedicamento(@Valid @RequestBody Medicamento medicamento) {

        //retorno o usuário logado
        //retorno o usuário do BD pelo email
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Usuario usuario = usuarioService.buscarPorEmail(email);


        medicamento.setUsuario(usuario);
        medicamentoService.atualizarMedicamento(medicamento);

        return ResponseEntity.ok(medicamento);
    }

    @DeleteMapping("/deletar/{codMedicamento}")
    @Operation(summary = "Deletar um medicamento", description = "Deleta um medicamento do banco de dados. Já associado ao Usuário autenticado da sessão")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Medicamento deletado com sucesso",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Erro ao encontrar dados", content = @Content)
    })
    public ResponseEntity excluirMedicamento(@PathVariable Integer codMedicamento) {


        this.medicamentoService.excluirMedicamento(codMedicamento);
        return ResponseEntity.noContent().build();
    }


}
