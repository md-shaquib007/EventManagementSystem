package com.ceoms.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "budgets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false, unique = true)
    private Event event;

    @Column(name = "estimated_amount", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal estimatedAmount = BigDecimal.ZERO;

    @Column(name = "approved_amount", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal approvedAmount = BigDecimal.ZERO;

    @Column(name = "actual_spending", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal actualSpending = BigDecimal.ZERO;

    @Column(name = "approved", nullable = false)
    @Builder.Default
    private Boolean approved = false;

    @Column(name = "approval_comment", columnDefinition = "TEXT")
    private String approvalComment;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public BigDecimal getRemainingBudget() {
        BigDecimal approved = approvedAmount != null ? approvedAmount : BigDecimal.ZERO;
        BigDecimal spent = actualSpending != null ? actualSpending : BigDecimal.ZERO;
        return approved.subtract(spent);
    }

    public BigDecimal getUtilizationPercentage() {
        if (approvedAmount == null || approvedAmount.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal spent = actualSpending != null ? actualSpending : BigDecimal.ZERO;
        return spent.multiply(BigDecimal.valueOf(100)).divide(approvedAmount, 2, java.math.RoundingMode.HALF_UP);
    }
}
