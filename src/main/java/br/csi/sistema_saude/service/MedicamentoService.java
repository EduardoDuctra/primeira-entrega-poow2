package br.csi.sistema_saude.service;

import br.csi.sistema_saude.model.Dados;
import br.csi.sistema_saude.model.Medicamento;
import br.csi.sistema_saude.repository.MedicamentoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedicamentoService {

    private final MedicamentoRepository medicamentoRepository;

    public MedicamentoService(MedicamentoRepository medicamentoRepository) {
        this.medicamentoRepository = medicamentoRepository;
    }

    public void salvarMedicamento (Medicamento medicamento) {
        medicamentoRepository.save(medicamento);
    }
    public List<Medicamento> buscarMedicamentos() {
        return medicamentoRepository.findAll();
    }

    public Medicamento buscarMedicamento(Integer codMedicamento) {
        return medicamentoRepository.findById(codMedicamento).get();
    }

    public List<Medicamento> buscarMedicamentoUsuario(Integer codUsuario) {
        return medicamentoRepository.findByUsuario_CodUsuario(codUsuario);
    }

    public void excluirMedicamento(Integer codMedicamento) {
        medicamentoRepository.deleteById(codMedicamento);
    }

    public void atualizarMedicamento (Medicamento medicamento) {
        Medicamento m = this.medicamentoRepository.getReferenceById(medicamento.getCodMedicamento());
        m.setNomeMedicamento(medicamento.getNomeMedicamento());
        m.setDoseDiaria(medicamento.getDoseDiaria());
        m.setDuracaoTratamento(medicamento.getDuracaoTratamento());
        m.setDataInicio(medicamento.getDataInicio());

        this.medicamentoRepository.save(m);
    }
}
