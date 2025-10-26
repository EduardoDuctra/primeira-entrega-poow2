package br.csi.sistema_saude.service;

import br.csi.sistema_saude.model.BancoMedicamentos;
import br.csi.sistema_saude.repository.BancoMedicamentoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BancoMedicamentoService {

    private final BancoMedicamentoRepository bancoMedicamentoRepository;

    public BancoMedicamentoService(BancoMedicamentoRepository bancoMedicamentoRepository) {
        this.bancoMedicamentoRepository = bancoMedicamentoRepository;
    }

    public List<BancoMedicamentos> listarTodos() {
        return bancoMedicamentoRepository.findAll();
    }


    public void salvarNomeMedicamento(BancoMedicamentos bancoMedicamentos) {
        bancoMedicamentoRepository.save(bancoMedicamentos);
    }

    public void excluirNomeMedicamento(int codNomeMedicamento) {
        bancoMedicamentoRepository.deleteById(codNomeMedicamento);
    }

    public void atualizarNomeMedicamento(int codNomeMedicamento, String novoNome) {

        BancoMedicamentos nome = bancoMedicamentoRepository.findById(codNomeMedicamento)
                .orElseThrow(() -> new RuntimeException("Nome do medicamento não encontrado"));

        nome.setNome(novoNome);
        bancoMedicamentoRepository.save(nome);
    }

}
