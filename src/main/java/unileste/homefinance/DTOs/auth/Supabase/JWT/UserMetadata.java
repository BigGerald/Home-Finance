package unileste.homefinance.DTOs.auth.Supabase.JWT;

public class UserMetadata {
        private String displayName;
        private String email;
        private boolean emailVerified;
        private String firstName;
        private String lastName;
        private String phone;
        private boolean phoneVerified;
        private String role;
        private String sub;

        // getters e setters
        public String getDisplayName() { return displayName; }
        public void setDisplayName(String displayName) { this.displayName = displayName; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public boolean isEmailVerified() { return emailVerified; }
        public void setEmailVerified(boolean emailVerified) { this.emailVerified = emailVerified; }
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public boolean isPhoneVerified() { return phoneVerified; }
        public void setPhoneVerified(boolean phoneVerified) { this.phoneVerified = phoneVerified; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public String getSub() { return sub; }
        public void setSub(String sub) { this.sub = sub; }
    }