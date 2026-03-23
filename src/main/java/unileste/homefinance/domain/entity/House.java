package unileste.homefinance.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "houses")
@Getter
@Setter
public class House {

    @Id
    @GeneratedValue
    private UUID id;

    private String name;

    @Column(name = "invite_code", unique = true, nullable = false)
    private String inviteCode;

    private BigDecimal balance;

    @Column(name = "admin_id", nullable = false)
    private UUID adminId;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "house", cascade = CascadeType.ALL, orphanRemoval = true,  fetch = FetchType.LAZY)
    private List<HouseMember> members;

    @OneToMany(mappedBy = "house")
    private List<Expense> expenses;

    public void addMember(HouseMember member) {
        member.setHouse(this);
        if(this.members == null) {
            this.members = new ArrayList<>();
        }
        members.add(member);
    }
}