package unileste.homefinance.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UserTypes {
     ADMIN("ADMIN"),
     USER("USER");
     private final String value;
}