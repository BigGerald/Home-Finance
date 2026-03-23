package unileste.homefinance.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import unileste.homefinance.domain.constants.MemberRole;
import unileste.homefinance.domain.constants.MemberStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "house_members")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HouseMember {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "house_id", nullable = false)
    private House house;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    private MemberRole role;

    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    @Column(name = "joined_at")
    private LocalDateTime joinedAt;

    @Column(name = "left_at")
    private LocalDateTime leftAt;
}