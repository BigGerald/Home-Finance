package unileste.homefinance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import unileste.homefinance.domain.constants.MemberStatus;
import unileste.homefinance.domain.entity.HouseMember;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface HouseMemberRepository extends JpaRepository<HouseMember, UUID> {

    boolean existsByUserIdAndStatus(UUID userID, MemberStatus status);
    Optional<HouseMember> findByUserIdAndStatus(UUID userID, MemberStatus status);
}
