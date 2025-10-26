package br.csi.sistema_saude.service;

import br.csi.sistema_saude.model.Medicamento;
import br.csi.sistema_saude.repository.MedicamentoRepository;
import br.csi.sistema_saude.repository.BancoMedicamentoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedicamentoService {

    private final MedicamentoRepository medicamentoRepository;
    private final BancoMedicamentoRepository bancoMedicamentoRepository;
    private final UsuarioService usuarioService;

    public MedicamentoService(MedicamentoRepository medicamentoRepository, BancoMedicamentoRepository bancoMedicamentoRepository, UsuarioService usuarioService) {
        this.medicamentoRepository = medicamentoRepository;
        this.bancoMedicamentoRepository = bancoMedicamentoRepository;
        this.usuarioService = usuarioService;
    }

    public void salvarMedicamento(Medicamento medicamento) {

        medicamentoRepository.save(medicamento);
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


    public Medicamento atualizarMedicamento(Medicamento medicamentoAtualizado) {

        // Busca o medicamento original do banco
        Medicamento medicamentoExistente = medicamentoRepository.findById(medicamentoAtualizado.getCodMedicamento())
                .orElseThrow(() -> new RuntimeException("Medicamento não encontrado"));


        medicamentoExistente.setDataInicio(medicamentoAtualizado.getDataInicio());
        medicamentoExistente.setDoseDiaria(medicamentoAtualizado.getDoseDiaria());
        medicamentoExistente.setDuracaoTratamento(medicamentoAtualizado.getDuracaoTratamento());


        return medicamentoRepository.save(medicamentoExistente);
    }
}
