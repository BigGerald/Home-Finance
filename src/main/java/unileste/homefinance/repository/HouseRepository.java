package unileste.homefinance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import unileste.homefinance.domain.entity.House;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface HouseRepository extends JpaRepository<House, UUID> {
    boolean existsByInviteCode(String inviteCode);
    Optional<House> findById(UUID id);
    Optional<House> findByInviteCode(String inviteCode);
    Optional<House> findByAdminId(UUID adminId);
}
