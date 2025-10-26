package br.csi.sistema_saude.controller;

import br.csi.sistema_saude.model.Dados;
import br.csi.sistema_saude.model.Usuario;
import br.csi.sistema_saude.service.DadosService;
import br.csi.sistema_saude.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/dados")
@Tag(name = "Dados", description = "Path relacionado aos dados")
public class DadosController {
    private final DadosService dadosService;
    private final UsuarioService usuarioService;


    public DadosController(DadosService dadosService, UsuarioService usuarioService) {
        this.dadosService = dadosService;
        this.usuarioService = usuarioService;
    }


    @GetMapping("/{codDado}")
    @Operation(summary = "Buscar dados a partir do seu código", description = "Retorna um Dado a partir de um ID fornecido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dado retornado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Dados.class))),
            @ApiResponse(responseCode = "400", description = "Dado invalido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Erro ao encontrar dados", content = @Content)
    })
    public ResponseEntity<Dados> buscarDado(@PathVariable Integer codDado) {

        Dados dados = this.dadosService.buscarDados(codDado);
        if(dados == null) {
            throw new NoSuchElementException("Dado não encontrado"); //chama o metodo NoSuchElementException da classe Tratador de Error
        }
        return ResponseEntity.ok(dados); //200
    }

    @GetMapping("/buscar-por-usuario")
    @Operation(summary = "Busca todos os dados a partir do código do usuário", description = "Retorna uma lista de Dados a partir do ID do usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dados retornados com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Dados.class))),
            @ApiResponse(responseCode = "400", description = "Dados invalidos", content = @Content),
            @ApiResponse(responseCode = "404", description = "Erro ao encontrar dados", content = @Content)
    })
    public ResponseEntity<List<Dados>> buscarDadosUsuario() {

        //retorno o usuário logado
        //retorno o usuário do BD pelo email
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Usuario usuario = usuarioService.buscarPorEmail(email);

        List<Dados> dados = dadosService.buscarDadosUsuario(usuario.getCodUsuario());

        if (dados.isEmpty()) {
            throw new NoSuchElementException("Usuário não encontrado"); //chama o metodo NoSuchElementException da classe Tratador de Error
        }
        return ResponseEntity.ok(dados); // 200 OK
    }


    @PostMapping("/salvar")
    @Operation(summary = "Criar um novo dado", description = "Salva um novo Dado no banco de dados, já associado ao Usuário autenticado da sessão")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Dado salvo com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Dados.class))),
            @ApiResponse(responseCode = "400", description = "Erro ao salvar", content = @Content)
    })
    public ResponseEntity salvarDados(@RequestBody Dados dados, UriComponentsBuilder uriBuilder) {

        //retorno o usuário logado
        //retorno o usuário do BD pelo email
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Usuario usuario = usuarioService.buscarPorEmail(email);

        dados.setUsuario(usuario);
        dadosService.salvarDados(dados);

        URI uri = uriBuilder.path("/dados/{codDado}").buildAndExpand(dados.getCodDado()).toUri();
        return ResponseEntity.created(uri).body(dados); //200 OK
    }

    @PutMapping("/atualizar")
    @Transactional
    @Operation(summary = "Atualizar um dado existente", description = "Recebe um Dado e realiza a atualização de informações no banco de dados. Já associado ao Usuário autenticado da sessão")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dado atualizado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Dados.class))),
            @ApiResponse(responseCode = "400", description = "Erro ao atualizar", content = @Content),
            @ApiResponse(responseCode = "404", description = "Erro ao encontrar dados", content = @Content)
    })
    public ResponseEntity atualizarDados(@RequestBody Dados dados) {

        //retorno o usuário logado
        //retorno o usuário do BD pelo email
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Usuario usuario = usuarioService.buscarPorEmail(email);

        dados.setUsuario(usuario);

        this.dadosService.atualizarDados(dados);
        return ResponseEntity.ok(dados);

    }

    @DeleteMapping("/deletar/{codDado}")
    @Operation(summary = "Deletar um dado existente", description = "Deleta um dado existente do banco de dados. Já associado ao Usuário autenticado da sessão")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Dado deletado com sucesso",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Erro ao encontrar dados", content = @Content)
    })
    public ResponseEntity excluirDados(@PathVariable Integer codDado) {

        this.dadosService.excluirDados(codDado);
        return ResponseEntity.noContent().build();
    }
}
