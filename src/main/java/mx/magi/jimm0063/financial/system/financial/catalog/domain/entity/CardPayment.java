package mx.magi.jimm0063.financial.system.financial.catalog.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "CARD_PAYMENT")
public class CardPayment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "CARD_PAYMENT_ID", nullable = false)
    private String cardPaymentId;

    @CreationTimestamp
    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;
}
