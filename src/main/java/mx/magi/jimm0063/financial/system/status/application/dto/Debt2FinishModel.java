package mx.magi.jimm0063.financial.system.status.application.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Builder
@Setter @Getter
public class Debt2FinishModel implements Serializable {
    private String name;
    private String currentInstallment;
    private Double monthAmount;

    public void creteCurrentInstallment(Integer monthsFinanced, Integer monthsPaid) {
        this.currentInstallment = String.format("%d de %d", monthsPaid, monthsFinanced);
    }
}
