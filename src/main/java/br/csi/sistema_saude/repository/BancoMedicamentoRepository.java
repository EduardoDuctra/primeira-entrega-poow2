package br.csi.sistema_saude.repository;

import br.csi.sistema_saude.model.BancoMedicamentos;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BancoMedicamentoRepository extends JpaRepository<BancoMedicamentos, Integer> {

    Optional<BancoMedicamentos> findByNome(String nome);
}
