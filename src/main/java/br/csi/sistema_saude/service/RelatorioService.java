package br.csi.sistema_saude.service;


import br.csi.sistema_saude.model.Dados;
import br.csi.sistema_saude.model.Relatorio;
import br.csi.sistema_saude.model.RelatorioId;
import br.csi.sistema_saude.model.Usuario;
import br.csi.sistema_saude.repository.DadosRepository;
import br.csi.sistema_saude.repository.RelatorioRepository;
import br.csi.sistema_saude.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class RelatorioService {

    private final RelatorioRepository relatorioRepository;
    private final UsuarioRepository usuarioRepository;
    private final DadosRepository dadosRepository;

    public RelatorioService(RelatorioRepository relatorioRepository, UsuarioRepository usuarioRepository, DadosRepository dadosRepository) {
        this.relatorioRepository = relatorioRepository;
        this.usuarioRepository = usuarioRepository;
        this.dadosRepository = dadosRepository;
    }

    public void salvarRelatorio(Relatorio relatorio) {

        Usuario usuario = usuarioRepository.findById(relatorio.getUsuario().getCodUsuario())
                .orElseThrow(() -> new NoSuchElementException("Usuário não encontrado"));

        Dados dado = dadosRepository.findById(relatorio.getDados().getCodDado())
                .orElseThrow(() -> new NoSuchElementException("Dado não encontrado"));

        relatorio.setUsuario(usuario);
        relatorio.setDados(dado);

        relatorioRepository.save(relatorio);
    }


    public Relatorio buscarRelatorio(RelatorioId id) {
        return relatorioRepository.findById(id).orElse(null);
    }

    public void excluirRelatorio(RelatorioId id) {
        relatorioRepository.deleteById(id);
    }

    public List<Double> listarValoresPorUsuarioETipo(int codUsuario, String tipoDado) {
        return relatorioRepository.findByUsuario_CodUsuario(codUsuario)
                .stream()
                .map(rel -> {
                    switch (tipoDado.toLowerCase()) {
                        case "glicose":
                            return rel.getDados().getGlicose().doubleValue();
                        case "colesterolhdl":
                            return rel.getDados().getColesterolHDL().doubleValue();
                        case "colesterolvldl":
                            return rel.getDados().getColesterolVLDL().doubleValue();
                        case "peso":
                            return rel.getDados().getPeso();
                        case "creatina":
                            return rel.getDados().getCreatina().doubleValue();
                        case "trigliceridio":
                            return rel.getDados().getTrigliceridio().doubleValue();
                        default:
                            throw new NoSuchElementException("Tipo de dado inválido: " + tipoDado);
                    }
                })
                .toList();
    }

}
