package br.csi.sistema_saude.controller;

import br.csi.sistema_saude.model.DTO.DadoUsuario;
import br.csi.sistema_saude.model.DTO.IMCDTO;
import br.csi.sistema_saude.model.Relatorio;
import br.csi.sistema_saude.model.Usuario;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.net.URI;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/usuario")
@Tag(name = "Usuário", description = "Path relacionado aos usuários")
public class UsuarioController {

    private UsuarioService usuarioService;
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }



    @GetMapping("/listar-usuarios")
    @Operation(summary = "Listar todos os usuário", description = "Retorna uma lista com todos os usuários")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuários listados com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Usuario.class))),
            @ApiResponse(responseCode = "404", description = "Erro ao encontrar dados", content = @Content),
    })
    public ResponseEntity<List<DadoUsuario>> listarUsuarios() {
        List<DadoUsuario> usuarios = this.usuarioService.listarUsuarios();

        if (usuarios.isEmpty()) {
            throw new NoSuchElementException("Nenhum usuário encontrado"); // chama o método do Tratador de Error
        }

        return ResponseEntity.ok(usuarios); // 200
    }


    //URL púlbica
    @PostMapping("/salvar")
    @Transactional
    @Operation(summary = "Criar novo usuário", description = "Cadastra um novo usuário ao banco de dados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Usuario.class))),
            @ApiResponse(responseCode = "400", description = "Erro ao criar usuário", content = @Content)
    })
    public ResponseEntity salvarUsuario(@RequestBody @Valid Usuario usuario, UriComponentsBuilder uriBuilder) {
        this.usuarioService.salvarUsuario(usuario);
        URI uri = uriBuilder.path("/usuario/{codUsuario}").buildAndExpand(usuario.getCodUsuario()).toUri();
        return ResponseEntity.created(uri).body(usuario);
    }

    @PutMapping("/atualizar")
    @Transactional
    @Operation(summary = "Atualizar um usuário", description = "Recebe um Usuário e atualiza seus dados no banco de dados. Já associado ao Usuário autenticado da sessão.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Usuario.class))),
            @ApiResponse(responseCode = "400", description = "Erro ao atualizar usuário", content = @Content),
            @ApiResponse(responseCode = "404", description = "Erro ao encontrar dados", content = @Content),
    })
    public ResponseEntity atualizarUsuario(@RequestBody @Valid Usuario usuario) {


        //retorno o usuário logado
        //retorno o usuário do BD pelo email
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Usuario logado  = usuarioService.buscarPorEmail(email);

        usuario.setCodUsuario(logado.getCodUsuario());

        Usuario atualizado = usuarioService.atualizarUsuario(usuario);
        return ResponseEntity.ok(new DadoUsuario(atualizado));

    }

    @DeleteMapping("/deletar")
    @Operation(summary = "Deletar um usuário", description = "Deleta um usuário do banco de dados. Já associado ao Usuário autenticado da sessão")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuário deletado com sucesso",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Erro ao encontrar dados", content = @Content),
    })
    public ResponseEntity deleteUsuario() {

        //retorno o usuário logado
        //retorno o usuário do BD pelo email
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Usuario usuario = usuarioService.buscarPorEmail(email);

        usuarioService.excluirUsuario(usuario.getCodUsuario());
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/imc")
    @Operation(summary = "Calcular o IMC de um usuário", description = "Cria uma lista com os reatórios a partir do ID do usuário. " +
            " Chama a função calcularIMC e envia os relatórios, para filtrar o mais recente e obter os dados. Já associado ao Usuário autenticado da sessão")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cálculo efetuado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Usuario.class))),
            @ApiResponse(responseCode = "404", description = "Erro ao encontrar dados", content = @Content),
    })
    public ResponseEntity<?> calcularIMC() {

        //retorno o usuário logado
        //retorno o usuário do BD pelo email
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Usuario usuario = usuarioService.buscarPorEmail(email);


            if (usuario == null) {
                throw new NoSuchElementException("Usuário não encontrado");
            }

            List<Relatorio> relatoriosDoUsuario = usuarioService.buscarRelatoriosPorUsuario(usuario);


            IMCDTO imcDTO = usuarioService.calcularIMC(usuario, relatoriosDoUsuario);


            return ResponseEntity.ok(imcDTO);

    }


    @GetMapping("/perfil")
    @Operation(summary = "Busca o usuário logado", description = "Retorna um usuário logado, buscando pelo email no banco de dados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Usuario.class))),
            @ApiResponse(responseCode = "404", description = "Erro ao encontrar usuário", content = @Content),
    })
    public ResponseEntity<DadoUsuario> buscarUsuarioLogado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Usuario usuario = usuarioService.buscarPorEmail(email);

        if (usuario == null) {
            throw new NoSuchElementException("Usuário não encontrado");
        }

        return ResponseEntity.ok(new DadoUsuario(usuario));
    }

}
