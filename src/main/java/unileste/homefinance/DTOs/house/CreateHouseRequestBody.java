package unileste.homefinance.DTOs.house;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateHouseRequestBody {
    private String name;

    public void validateCreateHouseRequestBody() {
        if (name == null) {
            throw new IllegalArgumentException("House name cannot be null");
        }
        if (name.isEmpty() || name.isBlank()) {
            throw new IllegalArgumentException("House name cannot be empty");
        }
    }
}
