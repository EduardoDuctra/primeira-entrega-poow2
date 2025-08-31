package br.csi.sistema_saude.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "dados")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Dados {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cod_dado")
    private int codDado;
    @UuidGenerator
    private UUID uuid_dado;
    @Column(name = "peso")
    private double peso;
    @Column(name = "glicose")
    private int glicose;
    @Column(name = "colesterol_hdl")
    private int colesterolHDL;
    @Column(name = "colesterol_vldl")
    private int colesterolVLDL;
    @Column(name = "creatina")
    private int creatina;
    @Column(name = "trigliceridio")
    private int trigliceridio;

    @ManyToOne
    @JoinColumn(name = "cod_usuario")
    private Usuario usuario;

    public int getCodDado() {
        return codDado;
    }

    public void setCodDado(int codDado) {
        this.codDado = codDado;
    }

    public UUID getUuid_dado() {
        return uuid_dado;
    }

    public void setUuid_dado(UUID uuid_dado) {
        this.uuid_dado = uuid_dado;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    public int getGlicose() {
        return glicose;
    }

    public void setGlicose(int glicose) {
        this.glicose = glicose;
    }

    public int getColesterolHDL() {
        return colesterolHDL;
    }

    public void setColesterolHDL(int colesterolHDL) {
        this.colesterolHDL = colesterolHDL;
    }

    public int getColesterolVLDL() {
        return colesterolVLDL;
    }

    public void setColesterolVLDL(int colesterolVLDL) {
        this.colesterolVLDL = colesterolVLDL;
    }

    public int getCreatina() {
        return creatina;
    }

    public void setCreatina(int creatina) {
        this.creatina = creatina;
    }

    public int getTrigliceridio() {
        return trigliceridio;
    }

    public void setTrigliceridio(int trigliceridio) {
        this.trigliceridio = trigliceridio;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}


