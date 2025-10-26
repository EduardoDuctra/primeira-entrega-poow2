package br.csi.sistema_saude.repository;

import br.csi.sistema_saude.model.Dados;
import br.csi.sistema_saude.model.Medicamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicamentoRepository extends JpaRepository<Medicamento, Integer> {

    List<Medicamento> findByUsuario_CodUsuario(Integer codUsuario);


}
