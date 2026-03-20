package unileste.homefinance.DTOs.auth.PasswordRecover;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RecoverPasswordRequest {
    private String email;
    private String redirectUrl;
}