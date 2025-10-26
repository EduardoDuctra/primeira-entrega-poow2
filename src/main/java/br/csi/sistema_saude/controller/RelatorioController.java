package br.csi.sistema_saude.controller;

import br.csi.sistema_saude.model.Relatorio;
import br.csi.sistema_saude.model.RelatorioId;
import br.csi.sistema_saude.model.Usuario;
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
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/relatorios")
@Tag(name = "Relatórios", description = "Path relacionado aos relatórios")
public class RelatorioController {

    private final RelatorioService relatorioService;
    private final UsuarioService usuarioService;

    public RelatorioController(RelatorioService relatorioService, UsuarioService usuarioService) {
        this.relatorioService = relatorioService;
        this.usuarioService = usuarioService;
    }


    @PostMapping("/salvar")
    @Transactional
    @Operation(summary = "Criar um relatório", description = "Cadastra um novo relatório ao banco de dados. Já associado ao Usuário autenticado da sessão")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Relatório salvo com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Relatorio.class))),
            @ApiResponse(responseCode = "400", description = "Erro ao salvar relatório", content = @Content)
    })
    public ResponseEntity salvarRelatorio(@RequestBody @Valid Relatorio relatorio, UriComponentsBuilder uriBuilder) {


        //retorno o usuário logado
        //retorno o usuário do BD pelo email
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Usuario usuario = usuarioService.buscarPorEmail(email);

        relatorio.getId().setCodUsuario(usuario.getCodUsuario());
        relatorio.setUsuario(usuario);

        relatorioService.salvarRelatorio(relatorio);
        URI uri = uriBuilder
                .path("/relatorios/{codDado}/{data}")
                .buildAndExpand(relatorio.getId().getCodUsuario(),
                        relatorio.getId().getCodDado(),
                        relatorio.getId().getData())
                .toUri();
        return ResponseEntity.created(uri).body(relatorio); //201 criado
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
    public ResponseEntity<List<Double>> listarTipoDado(
            @RequestParam String tipoDado) {

        //retorno o usuário logado
        //retorno o usuário do BD pelo email
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Usuario usuario = usuarioService.buscarPorEmail(email);

        List<Double> valores = relatorioService.listarValoresPorUsuarioETipo(usuario.getCodUsuario(), tipoDado);

        if (valores.isEmpty()) {
            throw new NoSuchElementException("Valores vazio"); //chama o metodo NoSuchElementException da classe Tratador de Error
        }
        return ResponseEntity.ok(valores); //200
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
