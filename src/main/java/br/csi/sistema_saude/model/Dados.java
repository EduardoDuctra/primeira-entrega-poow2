package br.csi.sistema_saude.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "dados")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "Entidade que representa um dados")

public class Dados {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cod_dado")
    @Schema(description = "ID do dado")
    private int codDado;


    @Column(name = "peso")
    @Schema(nullable = true,  description = "Peso do usuário em KG. Exemplo: 65.5kg Pode ser nulo se não informado")
    private Double peso;

    @Column(name = "glicose")
    @Schema(nullable = true, description = "Glicose do usuário. Exemplo: 98. Pode ser nulo se não informado")
    private Integer glicose;

    @Column(name = "colesterol_hdl")
    @Schema(nullable = true, description = "Colesterol HDL do usuário. Exemplo: 55. Pode ser nulo se não informado")
    private Integer colesterolHDL;

    @Column(name = "colesterol_vldl")
    @Schema(nullable = true, description = "Colesterol VLDL do usuário. Exemplo: 18. Pode ser nulo se não informado")
    private Integer colesterolVLDL;

    @Column(name = "creatina")
    @Schema(nullable = true, description = "Creatina do usuário. Exemplo: 2. Pode ser nulo se não informado")
    private Integer creatina;

    @Column(name = "trigliceridio")
    @Schema(nullable = true, description = "Trigicerídios do usuário. Exemplo: 140. Pode ser nulo se não informado")
    private Integer trigliceridio;

    @ManyToOne
    @JoinColumn(name = "cod_usuario")
    @Schema(description = "Objeto Usuário")

    private Usuario usuario;

    public int getCodDado() {
        return codDado;
    }

    public void setCodDado(int codDado) {
        this.codDado = codDado;
    }


    public Double getPeso() {
        return peso;
    }

    public void setPeso(Double peso) {
        this.peso = peso;
    }

    public Integer getGlicose() {
        return glicose;
    }

    public void setGlicose(Integer glicose) {
        this.glicose = glicose;
    }

    public Integer getColesterolHDL() {
        return colesterolHDL;
    }

    public void setColesterolHDL(Integer colesterolHDL) {
        this.colesterolHDL = colesterolHDL;
    }

    public Integer getColesterolVLDL() {
        return colesterolVLDL;
    }

    public void setColesterolVLDL(Integer colesterolVLDL) {
        this.colesterolVLDL = colesterolVLDL;
    }

    public Integer getCreatina() {
        return creatina;
    }

    public void setCreatina(Integer creatina) {
        this.creatina = creatina;
    }

    public Integer getTrigliceridio() {
        return trigliceridio;
    }

    public void setTrigliceridio(Integer trigliceridio) {
        this.trigliceridio = trigliceridio;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}


