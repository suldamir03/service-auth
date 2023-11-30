package nomadteam.auth.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public enum ERole {

    ROLE_USER("ROLE_USER"),
    ROLE_SUPER_ADMIN("ROLE_SUPER_ADMIN"),
    ROLE_ADMIN("ROLE_ADMIN"),
    ROLE_COMPANY_ADMIN("ROLE_COMPANY_ADMIN"),
    ROLE_COMPANY_MODERATOR("ROLE_COMPANY_MODERATOR");

    private final String authority;
}
