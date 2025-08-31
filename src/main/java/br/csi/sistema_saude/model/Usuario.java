package br.csi.sistema_saude.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table (name = "usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cod_usuario")
    private int codUsuario;
    @UuidGenerator
    private UUID uuidUsuario;

    @Embedded
    private UsuarioConta conta;
    @Embedded
    private UsuarioPerfil perfil;

    public int getCodUsuario() {
        return codUsuario;
    }

    public void setCodUsuario(int codUsuario) {
        this.codUsuario = codUsuario;
    }

    public UUID getUuidUsuario() {
        return uuidUsuario;
    }

    public void setUuidUsuario(UUID uuidUsuario) {
        this.uuidUsuario = uuidUsuario;
    }

    public UsuarioConta getConta() {
        return conta;
    }

    public void setConta(UsuarioConta conta) {
        this.conta = conta;
    }

    public UsuarioPerfil getPerfil() {
        return perfil;
    }

    public void setPerfil(UsuarioPerfil perfil) {
        this.perfil = perfil;
    }
}
