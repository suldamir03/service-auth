package nomadteam.auth.service.impl;

import java.util.Optional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import nomadteam.auth.config.security.CustomUserDetails;
import nomadteam.auth.persistence.entity.UserCredentials;
import nomadteam.auth.persistence.repo.UserCredentialsRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserDetailsServiceImpl implements UserDetailsService {

    UserCredentialsRepository userCredentialsRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final Optional<UserCredentials> credentials = userCredentialsRepository.findByUsername(username);

        if (credentials.isEmpty()) {
            throw new UsernameNotFoundException(String.format("Invalid username: %s", credentials));
        }

        return CustomUserDetails.builder()
                .username(credentials.get().getUsername())
                .password(credentials.get().getPassword())
                .roles(credentials.get().getRoles())
                .build();
    }
}
