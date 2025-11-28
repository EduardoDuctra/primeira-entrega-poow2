package br.csi.sistema_saude.controller;

import br.csi.sistema_saude.model.DTO.RelatorioCompletoDTO;
import br.csi.sistema_saude.model.DTO.RelatorioDTO;
import br.csi.sistema_saude.model.DTO.RelatorioSalvarDTO;
import br.csi.sistema_saude.model.Dados;
import br.csi.sistema_saude.model.Relatorio;
import br.csi.sistema_saude.model.RelatorioId;
import br.csi.sistema_saude.model.Usuario;
import br.csi.sistema_saude.service.DadosService;
import br.csi.sistema_saude.service.RelatorioService;
import br.csi.sistema_saude.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/relatorios")
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Relatórios", description = "Path relacionado aos relatórios")
public class RelatorioController {

    private final RelatorioService relatorioService;
    private final UsuarioService usuarioService;
    private final DadosService dadosService;

    public RelatorioController(RelatorioService relatorioService, UsuarioService usuarioService, DadosService dadosService) {
        this.relatorioService = relatorioService;
        this.usuarioService = usuarioService;
        this.dadosService = dadosService;
    }


    @PostMapping("/salvar")
    @Transactional
    @Operation(summary = "Criar um relatório", description = "Cadastra um novo relatório ao banco de dados. Já associado ao Usuário autenticado da sessão")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Relatório salvo com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Relatorio.class))),
            @ApiResponse(responseCode = "400", description = "Erro ao salvar relatório", content = @Content)
    })
    public ResponseEntity<Relatorio> salvarRelatorio(
            @RequestBody @Valid RelatorioSalvarDTO dto,
            UriComponentsBuilder uriBuilder) {

        // pega usuário logado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Usuario usuario = usuarioService.buscarPorEmail(email);

        // cria o objeto Dados
        Dados dados = new Dados();
        dados.setPeso(dto.dados().peso());
        dados.setGlicose(dto.dados().glicose());
        dados.setColesterolHDL(dto.dados().colesterolHDL());
        dados.setColesterolVLDL(dto.dados().colesterolVLDL());
        dados.setCreatina(dto.dados().creatina());
        dados.setTrigliceridio(dto.dados().trigliceridio());
        dados.setUsuario(usuario);

        dados = dadosService.salvarDados(dados);

        // cria o relatório
        Relatorio relatorio = new Relatorio();
        RelatorioId id = new RelatorioId();
        id.setCodUsuario(usuario.getCodUsuario());
        id.setCodDado(dados.getCodDado());
        id.setData(LocalDate.parse(dto.data()));
        relatorio.setId(id);
        relatorio.setUsuario(usuario);
        relatorio.setDados(dados);

        relatorioService.salvarRelatorio(relatorio);

        URI uri = uriBuilder
                .path("/relatorios/{codDado}/{data}")
                .buildAndExpand(relatorio.getId().getCodDado(), relatorio.getId().getData())
                .toUri();

        return ResponseEntity.created(uri).body(relatorio);
    }

    // Buscar um relatório pelo ID composto
    @GetMapping("/{codDado}/{data}")
    @Operation(summary = "Retornar um relatório com base na chave primária", description = "Retorna um relatório com base na chave primária (código do usuário + código do dado + data). Já associado ao Usuário autenticado da sessão")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Relatório retornado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Relatorio.class))),
            @ApiResponse(responseCode = "404", description = "Erro ao encontrar dados", content = @Content),
    })
    public ResponseEntity<Relatorio> buscarRelatorio(
            @PathVariable int codDado,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data
    ) {

        //retorno o usuário logado
        //retorno o usuário do BD pelo email
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Usuario usuario = usuarioService.buscarPorEmail(email);

        RelatorioId id = new RelatorioId(usuario.getCodUsuario(), codDado, data); //codUsuario + codDado + data
        Relatorio relatorio = relatorioService.buscarRelatorio(id);

        if (relatorio == null) {
            throw new NoSuchElementException("Relatório não encontrado"); //chama o metodo NoSuchElementException da classe Tratador de Error
        }

        return ResponseEntity.ok(relatorio); //  200
    }


    @GetMapping("/todos-usuario")
    @Operation(summary = "Listar todos os relatórios do usuário logado",
            description = "Retorna todos os relatórios completos do usuário autenticado na sessão")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Relatórios retornados com sucesso"),
            @ApiResponse(responseCode = "404", description = "Não há relatórios para este usuário")
    })
    public ResponseEntity<List<RelatorioCompletoDTO>> listarTodosRelatoriosUsuarioLogado() {

        // Pega o usuário logado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Usuario usuario = usuarioService.buscarPorEmail(email);

        if (usuario == null) {
            return ResponseEntity.status(401).build(); // usuário não autenticado
        }

        // Busca todos os relatórios do usuário logado
        List<RelatorioCompletoDTO> relatorios = relatorioService.listarTodosRelatoriosPorUsuario(usuario.getCodUsuario());

        if (relatorios.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 se não houver relatórios
        }

        return ResponseEntity.ok(relatorios); // 200 com lista de DTOs
    }


    // Listar relatorio de um tipo de dado especifico
//    http://localhost:8080/sistema-saude/relatorios/listar-por-tipo?tipoDado=glicose
    @GetMapping("/listar-por-tipo")
    @Operation(summary = "Listar os dados por tipo", description = "Retorna uma lista de valores do tipo especificado (ex.: Glicose, ColesterolHDL, Peso) " +
            "para o usuário identificado pelo ID fornecido. Já associado ao Usuário autenticado da sessão")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Relatório retornado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Relatorio.class))),
            @ApiResponse(responseCode = "404", description = "Erro ao encontrar dados", content = @Content),
    })
    public ResponseEntity<List<RelatorioDTO>> listarTipoDado(@RequestParam String tipoDado) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Usuario usuario = usuarioService.buscarPorEmail(email);

        List<RelatorioDTO> dtoList = relatorioService.listarRelatoriosPorUsuarioETipo(usuario.getCodUsuario(), tipoDado);

        if (dtoList.isEmpty()) {
            throw new NoSuchElementException("Valores vazios");
        }

        return ResponseEntity.ok(dtoList);
    }

    // Excluir um relatório pelo ID composto
    @DeleteMapping("/{codDado}/{data}")
    @Operation(summary = "Deletar um relatório com base na chave primária", description = "Deleta um relatório com base na chave primária (código do usuário + código do dado + data. Já associado ao Usuário autenticado da sessão")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Relatório deletado com sucesso",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Erro ao encontrar dados", content = @Content),
    })
    public ResponseEntity excluirRelatorio(
            @PathVariable int codDado,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data
    ) {

        //retorno o usuário logado
        //retorno o usuário do BD pelo email
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Usuario usuario = usuarioService.buscarPorEmail(email);


        RelatorioId id = new RelatorioId(usuario.getCodUsuario(), codDado, data);
        relatorioService.excluirRelatorio(id);
        return ResponseEntity.noContent().build(); //204 sem conteudo
    }

}
