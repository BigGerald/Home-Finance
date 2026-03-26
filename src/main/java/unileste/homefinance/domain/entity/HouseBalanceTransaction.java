package unileste.homefinance.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import unileste.homefinance.domain.constants.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "house_balance_transactions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HouseBalanceTransaction {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "house_id", nullable = false)
    private House house;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    private String description;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}