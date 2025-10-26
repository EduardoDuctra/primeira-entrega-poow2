package br.csi.sistema_saude.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "banco_de_medicamentos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidade que representa o banco de medicamento")
public class BancoMedicamentos {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cod_nome_medicamento")
    private int codNomeMedicamento;

    @Column(name = "nome", unique = true)
    @Schema(description = "Nome do medicamento. Exemplo: Dipirona")
    private String nome;

    public int getCodNomeMedicamento() {
        return codNomeMedicamento;
    }

    public void setCodNomeMedicamento(int codNomeMedicamento) {
        this.codNomeMedicamento = codNomeMedicamento;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
