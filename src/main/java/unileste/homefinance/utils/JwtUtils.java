package unileste.homefinance.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import unileste.homefinance.exceptions.AuthException;

import java.util.Map;

@Component
public class JwtUtils {

    public Jwt getJwt() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            return jwtAuth.getToken();
        }

        throw new AuthException("Usuário não autenticado");
    }


    public String getUserId() {
        return getJwt().getSubject();
    }

    public String getEmail() {
        return getJwt().getClaim("email");
    }

    public String getSupabaseRole() {
        return getJwt().getClaim("role");
    }

    public Map<String, Object> getUserMetadata() {
        return getJwt().getClaim("user_metadata");
    }

    public String getUserRole() {
        Map<String, Object> metadata = getUserMetadata();

        if (metadata != null && metadata.get("role") != null) {
            return metadata.get("role").toString();
        }

        return "USER"; // default seguro
    }

    public String getDisplayName() {
        Map<String, Object> metadata = getUserMetadata();

        if (metadata != null && metadata.get("display_name") != null) {
            return metadata.get("display_name").toString();
        }

        return null;
    }

    public String getFirstName() {
        Map<String, Object> metadata = getUserMetadata();

        if (metadata != null && metadata.get("first_name") != null) {
            return metadata.get("first_name").toString();
        }

        return null;
    }

    public String getLastName() {
        Map<String, Object> metadata = getUserMetadata();

        if (metadata != null && metadata.get("last_name") != null) {
            return metadata.get("last_name").toString();
        }

        return null;
    }

    public Boolean isEmailVerified() {
        Map<String, Object> metadata = getUserMetadata();

        if (metadata != null && metadata.get("email_verified") != null) {
            return Boolean.valueOf(metadata.get("email_verified").toString());
        }

        return false;
    }

    public Boolean isPhoneVerified() {
        Map<String, Object> metadata = getUserMetadata();

        if (metadata != null && metadata.get("phone_verified") != null) {
            return Boolean.valueOf(metadata.get("phone_verified").toString());
        }

        return false;
    }
}