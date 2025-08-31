package br.csi.sistema_saude.service;

import br.csi.sistema_saude.model.Relatorio;
import br.csi.sistema_saude.model.RelatorioId;
import br.csi.sistema_saude.repository.RelatorioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RelatorioService {

    private final RelatorioRepository relatorioRepository;

    public RelatorioService(RelatorioRepository relatorioRepository) {
        this.relatorioRepository = relatorioRepository;
    }

    public void salvarRelatorio(Relatorio relatorio) {
        relatorioRepository.save(relatorio);
    }

    public List<Relatorio> listarRelatorios() {
        return relatorioRepository.findAll();
    }


    public Relatorio buscarRelatorio(RelatorioId id) {
        return relatorioRepository.findById(id).orElse(null);
    }

    public void excluirRelatorio(RelatorioId id) {
        relatorioRepository.deleteById(id);
    }
}
