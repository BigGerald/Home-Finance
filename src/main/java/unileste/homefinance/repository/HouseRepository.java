package unileste.homefinance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import unileste.homefinance.domain.entity.House;

import java.util.UUID;

@Repository
public interface HouseRepository extends JpaRepository<House, UUID> {
    public boolean existsByInviteCode(String inviteCode);
}
