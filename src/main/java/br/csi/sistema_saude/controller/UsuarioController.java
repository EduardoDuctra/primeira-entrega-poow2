package br.csi.sistema_saude.controller;

import br.csi.sistema_saude.model.DTO.DadoUsuario;
import br.csi.sistema_saude.model.DTO.IMCDTO;
import br.csi.sistema_saude.model.DTO.UsuarioPerfilDTO;
import br.csi.sistema_saude.model.Dados;
import br.csi.sistema_saude.model.Relatorio;
import br.csi.sistema_saude.model.Usuario;
import br.csi.sistema_saude.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

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
            @ApiResponse(responseCode = "400", description = "Usuários invalidos", content = @Content),
            @ApiResponse(responseCode = "404", description = "Erro ao encontrar dados", content = @Content),
    })
    public ResponseEntity<List<DadoUsuario>> listarUsuarios() {
        List<DadoUsuario> usuarios = this.usuarioService.listarUsuarios();

        if (usuarios.isEmpty()) {
            throw new NoSuchElementException("Nenhum usuário encontrado"); // chama o método do Tratador de Error
        }

        return ResponseEntity.ok(usuarios); // 200
    }


    @GetMapping("/{codUsuario}")
    @Operation(summary = "Listar usuário pelo código dele", description = "Retorna um usuário através do seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Usuario.class))),
            @ApiResponse(responseCode = "400", description = "Código invalido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Erro ao encontrar dados", content = @Content),
    })
    public ResponseEntity<DadoUsuario> buscarUsuario(@PathVariable Integer codUsuario) {
        DadoUsuario dto = this.usuarioService.buscarUsuario(codUsuario);

        if (dto == null) {
            throw new NoSuchElementException("Usuário não encontrado");
        }

        return ResponseEntity.ok(dto);
    }

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
    @Operation(summary = "Atualizar um usuário", description = "Recebe um Usuário e atualiza seus dados no banco de dados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Usuario.class))),
            @ApiResponse(responseCode = "400", description = "Erro ao atualizar usuário", content = @Content),
            @ApiResponse(responseCode = "404", description = "Erro ao encontrar dados", content = @Content),
    })
    public ResponseEntity atualizarUsuario(@RequestBody @Valid Usuario usuario) {
        this.usuarioService.atualizarUsuario(usuario);
        return ResponseEntity.ok(usuario);
    }

    @DeleteMapping("/deletar/{codUsuario}")
    @Operation(summary = "Deletar um usuário", description = "Deleta um usuário do banco de dados através do ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuário deletado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Usuario.class))),
            @ApiResponse(responseCode = "400", description = "Erro ao deletar usuário", content = @Content),
            @ApiResponse(responseCode = "404", description = "Erro ao encontrar dados", content = @Content),
    })
    public ResponseEntity deleteUsuario(@PathVariable Integer codUsuario) {
        this.usuarioService.excluirUsuario(codUsuario);
        return ResponseEntity.noContent().build();
    }

//    @PostMapping("/login")
//    @Operation(summary = "Validar do login", description = "Recebe um email e senha para verificar no banco de dados sua validação")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "201", description = "Login efetuado com sucesso",
//                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Dados.class))),
//            @ApiResponse(responseCode = "400", description = "Erro ao loggar", content = @Content)
//    })
//    public ResponseEntity<?> login(@RequestParam String email,
//                                   @RequestParam String senha,
//                                   HttpSession session) {
//
//        Usuario usuario = usuarioService.validarUsuario(email, senha);
//
//        if (usuario == null) {
//            throw new IllegalArgumentException("Usuário ou senha inválidos");
//        }
//
//        session.setAttribute("usuarioLogado", usuario);
//        return ResponseEntity.ok(usuario);
//    }

    @GetMapping("/{codUsuario}/imc")
    @Operation(summary = "Calcular o IMC de um usuário", description = "Cria uma lista com os reatórios a partir do ID do usuário. " +
            " Chama a função calcularIMC e envia os relatórios, para filtrar o mais recente e obter os dados ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cálculo efetuado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Usuario.class))),
            @ApiResponse(responseCode = "400", description = "Erro ao calcular IMC", content = @Content),
            @ApiResponse(responseCode = "404", description = "Erro ao encontrar dados", content = @Content),
    })
    public ResponseEntity<?> calcularIMC(@PathVariable Integer codUsuario) {


            Usuario usuario = usuarioService.buscarPorId(codUsuario);
            if (usuario == null) {
                throw new NoSuchElementException("Usuário não encontrado");
            }

            List<Relatorio> relatoriosDoUsuario = usuarioService.buscarRelatoriosPorUsuario(usuario);


            IMCDTO imcDTO = usuarioService.calcularIMC(usuario, relatoriosDoUsuario);


            return ResponseEntity.ok(imcDTO);

    }

    //validar se está buscando o email certo.
    @GetMapping("/buscar-email")
    @Operation(summary = "Buscar um usuário a partir do seu email", description = "Retorna um usuário no banco de dados a partir do seu email. " +
            "Ela é necessária pois a classe onde está o email é UsuarioConta, associada a classe Usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Usuario.class))),
            @ApiResponse(responseCode = "400", description = "Usuário não encontrado", content = @Content),
            @ApiResponse(responseCode = "404", description = "Erro ao encontrar dados", content = @Content),
    })
    public ResponseEntity<?> buscarPorEmail(@RequestParam String email) {
        Usuario usuario = usuarioService.buscarPorEmail(email);

        if (usuario == null) {

            throw new NoSuchElementException("Usuário não encontrado com o email: " + email);
        }


        return ResponseEntity.ok(usuario);
    }



}
