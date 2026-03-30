package unileste.homefinance.exceptions;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import lombok.Getter;

@Getter
public class SupabaseException extends RuntimeException {

    private Integer code;

    public SupabaseException(FeignException ex) {
        super(extractMessage(ex));
        this.code = extractCode(ex);
    }

    private static String extractMessage(FeignException ex) {
        try {
            String json = extractJson(ex);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(json);

            // Supabase retorna array
            return node.get(0).get("msg").asText();

        } catch (Exception e) {
            return "Unexpected Supabase error";
        }
    }

    private static Integer extractCode(FeignException ex) {
        try {
            String json = extractJson(ex);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(json);

            return node.get(0).get("code").asInt();

        } catch (Exception e) {
            return ex.status(); // fallback
        }
    }

    private static String extractJson(FeignException ex) {
        String message = ex.getMessage();

        int start = message.indexOf("[{");
        int end = message.lastIndexOf("}]");

        if (start != -1 && end != -1) {
            return message.substring(start, end + 2);
        }

        throw new RuntimeException("Invalid Supabase error format");
    }
}