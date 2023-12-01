package nomadteam.auth.config.security;

import java.util.Collection;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

@Getter
@Setter
@ToString
public class CustomAuthentication extends UsernamePasswordAuthenticationToken {

    Long id;

    public CustomAuthentication(Object principal, Object credentials,
            Collection<? extends GrantedAuthority> authorities, Long id) {
        super(principal, credentials, authorities);
        this.id = id;
    }
}
