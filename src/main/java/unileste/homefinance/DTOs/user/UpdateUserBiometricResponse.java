package unileste.homefinance.DTOs.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserBiometricResponse {
    private String message;
    private boolean isEnabled;
}
