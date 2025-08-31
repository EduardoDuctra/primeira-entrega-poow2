package br.csi.sistema_saude.model;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class RelatorioId implements Serializable {
    private int codUsuario;
    private int codDado;
    private LocalDate data;

    public RelatorioId() {
    }

    public RelatorioId(int cod_usuario, int cod_dado, LocalDate data) {
        this.codUsuario = cod_usuario;
        this.codDado = cod_dado;
        this.data = data;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RelatorioId)) return false;
        RelatorioId that = (RelatorioId) o;
        return codUsuario == that.codUsuario &&
                codDado == that.codDado &&
                data.equals(that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codUsuario, codDado, data);
    }

    public int getCodUsuario() {
        return codUsuario;
    }

    public void setCodUsuario(int codUsuario) {
        this.codUsuario = codUsuario;
    }

    public int getCodDado() {
        return codDado;
    }

    public void setCodDado(int codDado) {
        this.codDado = codDado;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }
}
