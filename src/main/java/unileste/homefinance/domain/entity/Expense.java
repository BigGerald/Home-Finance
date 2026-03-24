package unileste.homefinance.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import unileste.homefinance.domain.constants.ExpenseStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "expenses")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Expense {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "house_id", nullable = false)
    private House house;

    @Column(name = "creator_id", nullable = false)
    private UUID creatorId;

    @Column(name = "responsible_id")
    private UUID responsibleId;

    private String title;

    private String description;

    private BigDecimal amount;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    private ExpenseStatus status;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "expense", cascade = CascadeType.ALL,  orphanRemoval = true)
    private List<ExpenseSplit> splits;

    public void addSplits(List<String> userIds, BigDecimal totalAmount) {
        if (splits == null) {
            splits = new ArrayList<>();
        }
        for (String userId : userIds) {
            ExpenseSplit split = new ExpenseSplit();
            split.setExpense(this);
            split.setAmount(totalAmount.divide(BigDecimal.valueOf(userIds.size())));
            split.setUserId(UUID.fromString(userId));
            split.setStatus(ExpenseStatus.PENDING);
            splits.add(split);
        }
    }

    public void addSplit(ExpenseSplit split) {
        if (splits == null) {
            splits = new ArrayList<>();
        }
        split.setExpense(this);
        splits.add(split);
    }
}