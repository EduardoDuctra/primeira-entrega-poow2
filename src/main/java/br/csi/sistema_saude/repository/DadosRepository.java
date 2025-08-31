package br.csi.sistema_saude.repository;

import br.csi.sistema_saude.model.Dados;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DadosRepository extends JpaRepository<Dados, Integer> {

    // Busca todos os registros de Dados que pertencem a um usuário específico
    //Ele precisa estar no repositório porque é o repositório que faz a ponte com o banco de dados
    List<Dados> findByUsuario_CodUsuario(Integer codUsuario);
}
