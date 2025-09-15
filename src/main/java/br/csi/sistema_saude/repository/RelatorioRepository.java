package br.csi.sistema_saude.repository;

import br.csi.sistema_saude.model.Relatorio;
import br.csi.sistema_saude.model.RelatorioId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RelatorioRepository extends JpaRepository<Relatorio, RelatorioId> {

    List<Relatorio> findByUsuario_CodUsuario(int codUsuario);

    @Query("SELECT r FROM Relatorio r WHERE r.usuario.codUsuario = :codUsuario ORDER BY r.id.data DESC")
    Optional<Relatorio> findTopByUsuarioOrderByIdDataDesc(@Param("codUsuario") int codUsuario);
}
