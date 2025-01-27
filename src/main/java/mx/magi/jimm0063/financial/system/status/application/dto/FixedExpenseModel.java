package mx.magi.jimm0063.financial.system.status.application.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class FixedExpenseModel {
    private Double costAmount;
    private String name;
}
