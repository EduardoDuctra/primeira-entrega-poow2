package br.csi.sistema_saude.service;

import br.csi.sistema_saude.model.Dados;
import br.csi.sistema_saude.model.Usuario;
import br.csi.sistema_saude.repository.DadosRepository;
import br.csi.sistema_saude.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DadosService {

    private final DadosRepository dadosRepository;
    private final UsuarioRepository usuarioRepository;

    public DadosService(DadosRepository dadosRepository, UsuarioRepository usuarioRepository) {

        this.dadosRepository = dadosRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public void salvarDados(Dados dado) {

        Usuario usuario = usuarioRepository.findById(dado.getUsuario().getCodUsuario()).orElse(null);


        dado.setUsuario(usuario);
        dadosRepository.save(dado);
    }
    public List<Dados> listarDados() {
        return dadosRepository.findAll();
    }

    public Dados buscarDados(Integer codDado) {
        return this.dadosRepository.findById(codDado).get();

    }

    public List<Dados> buscarDadosUsuario(Integer codUsuario) {
        return dadosRepository.findByUsuario_CodUsuario(codUsuario);
    }

    public void excluirDados(Integer codDado) {
        dadosRepository.deleteById(codDado);
    }

    public void atualizarDados(Dados dado) {
        Dados d = this.dadosRepository.getReferenceById(dado.getCodDado());
        d.setColesterolVLDL(dado.getColesterolVLDL());
        d.setPeso(dado.getPeso());
        d.setCreatina(dado.getCreatina());
        d.setGlicose(dado.getGlicose());
        d.setTrigliceridio(dado.getTrigliceridio());
        d.setColesterolHDL(dado.getColesterolHDL());

        this.dadosRepository.save(d);
    }
}
