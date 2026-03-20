package unileste.homefinance.DTOs.auth.Supabase.JWT;

import io.jsonwebtoken.Claims;

import java.util.List;
import java.util.Map;

public class JwtClaims {

    private String iss;
    private String sub;
    private String aud;
    private Long exp;
    private Long iat;
    private String email;
    private String phone;
    private AppMetadata appMetadata;
    private UserMetadata userMetadata;
    private String role;
    private String aal;
    private List<Amr> amr;
    private String sessionId;
    private boolean isAnonymous;

    public static JwtClaims fromClaims(Claims claims) {
        JwtClaims jwtClaims = new JwtClaims();
        jwtClaims.iss = claims.get("iss", String.class);
        jwtClaims.sub = claims.get("sub", String.class);
        jwtClaims.aud = claims.get("aud", String.class);
        jwtClaims.exp = claims.get("exp", Long.class);
        jwtClaims.iat = claims.get("iat", Long.class);
        jwtClaims.email = claims.get("email", String.class);
        jwtClaims.phone = claims.get("phone", String.class);
        jwtClaims.role = claims.get("role", String.class);
        jwtClaims.aal = claims.get("aal", String.class);
        jwtClaims.sessionId = claims.get("session_id", String.class);
        jwtClaims.isAnonymous = claims.get("is_anonymous", Boolean.class);

        // AppMetadata
        Map<String, Object> appMetadataMap = claims.get("app_metadata", Map.class);
        if (appMetadataMap != null) {
            AppMetadata appMetadata = new AppMetadata();
            appMetadata.setProvider((String) appMetadataMap.get("provider"));
            appMetadata.setProviders((List<String>) appMetadataMap.get("providers"));
            jwtClaims.appMetadata = appMetadata;
        }

        // UserMetadata
        Map<String, Object> userMetadataMap = claims.get("user_metadata", Map.class);
        if (userMetadataMap != null) {
            UserMetadata userMetadata = new UserMetadata();
            userMetadata.setDisplayName((String) userMetadataMap.get("display_name"));
            userMetadata.setEmail((String) userMetadataMap.get("email"));
            userMetadata.setEmailVerified(Boolean.TRUE.equals(userMetadataMap.get("email_verified")));
            userMetadata.setFirstName((String) userMetadataMap.get("first_name"));
            userMetadata.setLastName((String) userMetadataMap.get("last_name"));
            userMetadata.setPhone((String) userMetadataMap.get("phone"));
            userMetadata.setPhoneVerified(Boolean.TRUE.equals(userMetadataMap.get("phone_verified")));
            userMetadata.setRole((String) userMetadataMap.get("role"));
            userMetadata.setSub((String) userMetadataMap.get("sub"));
            jwtClaims.userMetadata = userMetadata;
        }

        // AMR (lista de maps)
        List<Map<String, Object>> amrList = claims.get("amr", List.class);
        if (amrList != null) {
            jwtClaims.amr = amrList.stream().map(amrMap -> {
                Amr amr = new Amr();
                amr.setMethod((String) amrMap.get("method"));
                Object ts = amrMap.get("timestamp");
                if (ts instanceof Integer) {
                    amr.setTimestamp(((Integer) ts).longValue());
                } else {
                    amr.setTimestamp((Long) ts);
                }
                return amr;
            }).toList();
        }

        return jwtClaims;
    }

    // getters e setters principais
    public String getIss() { return iss; }
    public String getSub() { return sub; }
    public String getAud() { return aud; }
    public Long getExp() { return exp; }
    public Long getIat() { return iat; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public AppMetadata getAppMetadata() { return appMetadata; }
    public UserMetadata getUserMetadata() { return userMetadata; }
    public String getRole() { return role; }
    public String getAal() { return aal; }
    public List<Amr> getAmr() { return amr; }
    public String getSessionId() { return sessionId; }
    public boolean isAnonymous() { return isAnonymous; }
}
