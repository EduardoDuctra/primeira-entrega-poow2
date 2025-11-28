package br.csi.sistema_saude.model.DTO;

import br.csi.sistema_saude.model.RecolhimentoFarmacia;
import br.csi.sistema_saude.model.StatusRecolhimento;

public record RecolhimentoFarmaciaDTO(
        int codRecolhimento,
        String emailCliente,
        String nomeMedicamento,
        StatusRecolhimento status
) {
    public RecolhimentoFarmaciaDTO(RecolhimentoFarmacia rf) {
        this(
                rf.getRecolhimento().getCodRecolhimento(),
                rf.getRecolhimento().getEmailCliente(),
                rf.getRecolhimento().getMedicamento().getBancoMedicamentos().getNome(),
                rf.getStatus()
        );
    }
}
