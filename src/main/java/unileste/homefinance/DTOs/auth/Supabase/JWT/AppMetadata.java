package unileste.homefinance.DTOs.auth.Supabase.JWT;

import java.util.List;

public class AppMetadata {
        private String provider;
        private List<String> providers;

        // getters e setters
        public String getProvider() { return provider; }
        public void setProvider(String provider) { this.provider = provider; }
        public List<String> getProviders() { return providers; }
        public void setProviders(List<String> providers) { this.providers = providers; }
    }