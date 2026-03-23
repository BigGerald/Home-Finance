package unileste.homefinance.domain.constants;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ExpenseStatus {

    PENDING("PENDING"),
    PAID("PAID");

    private final String value;

    ExpenseStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static ExpenseStatus fromValue(String value) {
        for (ExpenseStatus status : ExpenseStatus.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid ExpenseStatus: " + value);
    }
}