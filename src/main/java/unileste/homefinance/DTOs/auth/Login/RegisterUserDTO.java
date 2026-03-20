package unileste.homefinance.DTOs.auth.Login;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
@AllArgsConstructor
public class RegisterUserDTO {
    private String firstName;
    private String lastName;
    private String displayName;
    private String email;
    private String password;
}
