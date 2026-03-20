package unileste.homefinance.config;


import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class SupabaseJwtConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {

        Map<String, Object> metadata = jwt.getClaim("user_metadata");

        String role = extractRole(metadata);

        Collection<SimpleGrantedAuthority> authorities = List.of(
            new SimpleGrantedAuthority("ROLE_" + role.toUpperCase())
        );

        return new JwtAuthenticationToken(jwt, authorities);
    }

    private String extractRole(Map<String, Object> metadata) {
        if (metadata == null) {
            return "USER";
        }

        Object roleObj = metadata.get("role");

        if (roleObj == null) {
            return "USER";
        }

        return roleObj.toString();
    }
}