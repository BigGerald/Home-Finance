package unileste.homefinance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import unileste.homefinance.domain.entity.ExpenseSplit;

import java.util.Optional;
import java.util.UUID;

public interface ExpenseSplitRepository extends JpaRepository<ExpenseSplit, UUID> {
    Optional<ExpenseSplit> findById(UUID uuid);
}
