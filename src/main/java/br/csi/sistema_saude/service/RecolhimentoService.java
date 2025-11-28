package br.csi.sistema_saude.service;

import br.csi.sistema_saude.model.DTO.RecolhimentoDTO;
import br.csi.sistema_saude.model.DTO.RecolhimentoFarmaciaDTO;
import br.csi.sistema_saude.model.Farmacia;
import br.csi.sistema_saude.model.Recolhimento;
import br.csi.sistema_saude.model.RecolhimentoFarmacia;
import br.csi.sistema_saude.model.StatusRecolhimento;
import br.csi.sistema_saude.repository.RecolhimentoFarmaciaRepository;
import br.csi.sistema_saude.repository.RecolhimentoRepository;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class RecolhimentoService {

    private final RecolhimentoRepository recolhimentoRepository;
    private final RecolhimentoFarmaciaRepository recolhimentoFarmaciaRepository;

    public RecolhimentoService(RecolhimentoRepository recolhimentoRepository, RecolhimentoFarmaciaRepository recolhimentoFarmaciaRepository) {
        this.recolhimentoRepository = recolhimentoRepository;
        this.recolhimentoFarmaciaRepository = recolhimentoFarmaciaRepository;
    }

    public void salvarRecolhimento(Recolhimento recolhimento) {


        recolhimentoRepository.save(recolhimento);
    }



    public List<RecolhimentoDTO> listarTodasSolicitacoes() {
        List<RecolhimentoDTO> dtoList = new ArrayList<>();


        List<Recolhimento> recolhimentos = recolhimentoRepository.findAll();


        for (Recolhimento recolhimento : recolhimentos) {
            dtoList.add(new RecolhimentoDTO(recolhimento));
        }

        return dtoList;
    }


    public List<RecolhimentoFarmaciaDTO> listarPendentesPorFarmacia(int codFarmacia) {

        List<RecolhimentoFarmacia> lista = recolhimentoFarmaciaRepository.findByFarmacia_CodFarmacia(codFarmacia);

        List<RecolhimentoFarmaciaDTO> dtoList = new ArrayList<>();

        for (RecolhimentoFarmacia rf : lista) {
            dtoList.add(new RecolhimentoFarmaciaDTO(rf));
        }

        return dtoList;
    }

    public void atualizarStatusRecolhimento(int codRecolhimento, Farmacia farmacia, StatusRecolhimento novoStatus) {

        Optional<RecolhimentoFarmacia> optional = recolhimentoFarmaciaRepository
                .findByRecolhimento_CodRecolhimentoAndFarmacia_CodFarmacia(codRecolhimento, farmacia.getCodFarmacia());

        RecolhimentoFarmacia recolhimentoFarmacia;
        if (optional.isPresent()) {
            recolhimentoFarmacia = optional.get();
        } else {

            Recolhimento recolhimento = recolhimentoRepository.findById(codRecolhimento)
                    .orElseThrow(() -> new NoSuchElementException("Recolhimento não encontrado"));

            recolhimentoFarmacia = new RecolhimentoFarmacia();
            recolhimentoFarmacia.setRecolhimento(recolhimento);
            recolhimentoFarmacia.setFarmacia(farmacia);
        }


        recolhimentoFarmacia.setStatus(novoStatus);

        recolhimentoFarmaciaRepository.save(recolhimentoFarmacia);
    }
}



