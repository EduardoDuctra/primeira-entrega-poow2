package br.csi.sistema_saude.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "medicamento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Medicamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cod_medicamento")
    private int codMedicamento;
    @UuidGenerator
    private UUID uuidMedicamento;
    @Column(name = "nome_medicamento")
    private String nomeMedicamento;
    @Column(name = "duracao_tratamento")
    private int duracaoTratamento;
    @Column(name = "data_inicio")
    private LocalDate dataInicio;
    @Column(name = "dose_diaria")
    private int doseDiaria;

    @ManyToOne
    @JoinColumn(name = "cod_usuario")
    private Usuario usuario;

    public int getCodMedicamento() {
        return codMedicamento;
    }

    public void setCodMedicamento(int codMedicamento) {
        this.codMedicamento = codMedicamento;
    }

    public UUID getUuidMedicamento() {
        return uuidMedicamento;
    }

    public void setUuidMedicamento(UUID uuidMedicamento) {
        this.uuidMedicamento = uuidMedicamento;
    }

    public String getNomeMedicamento() {
        return nomeMedicamento;
    }

    public void setNomeMedicamento(String nomeMedicamento) {
        this.nomeMedicamento = nomeMedicamento;
    }

    public int getDuracaoTratamento() {
        return duracaoTratamento;
    }

    public void setDuracaoTratamento(int duracaoTratamento) {
        this.duracaoTratamento = duracaoTratamento;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public int getDoseDiaria() {
        return doseDiaria;
    }

    public void setDoseDiaria(int doseDiaria) {
        this.doseDiaria = doseDiaria;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}


