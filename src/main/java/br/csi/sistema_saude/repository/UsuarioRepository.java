package br.csi.sistema_saude.repository;

import br.csi.sistema_saude.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
}
