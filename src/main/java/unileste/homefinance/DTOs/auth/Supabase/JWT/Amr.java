package unileste.homefinance.DTOs.auth.Supabase.JWT;

public class Amr {
        private String method;
        private Long timestamp;

        // getters e setters
        public String getMethod() { return method; }
        public void setMethod(String method) { this.method = method; }
        public Long getTimestamp() { return timestamp; }
        public void setTimestamp(Long timestamp) { this.timestamp = timestamp; }
    }