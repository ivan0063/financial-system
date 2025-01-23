package mx.magi.jimm0063.financial.system.debt.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class DebtModel implements Serializable {
//    private String description;
//    private String monthPayment;
//    private String paymentMonthStatus;
    private String descripcion;
    private BigDecimal pagoRequerido;
    private BigDecimal montoOriginal;
    private String montoFinal;
}
