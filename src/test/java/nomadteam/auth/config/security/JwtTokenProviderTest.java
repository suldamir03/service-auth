package nomadteam.auth.config.security;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import nomadteam.auth.persistence.entity.ERole;
import nomadteam.auth.persistence.entity.Role;
import nomadteam.auth.persistence.entity.UserCredentials;
import nomadteam.auth.persistence.entity.UserStatus;
import nomadteam.auth.persistence.repo.UserCredentialsRepository;
import nomadteam.auth.utils.EncodeUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
class JwtTokenProviderTest {

    @Mock
    JwtTokenProvider jwtTokenProvider;
    @Mock
    UserCredentialsRepository userCredentialsRepository;

    @Autowired
    PasswordEncoder passwordEncoder;
    EncodeUtil encodeUtil;

    @BeforeEach
    void setUp() {
        encodeUtil = new EncodeUtil(passwordEncoder);
    }


    @Test
    void itShouldAuthenticateUserAndReturnValidDataInTokens() {
        //given
        String username = "user123";
        String email = "suldamir@gmail.com";
        UserCredentials credentials;
        Role role = new Role(1L, ERole.ROLE_USER);
        List<Role> roles = List.of(role);

        jwtTokenProvider = new JwtTokenProvider(userCredentialsRepository);
        credentials = new UserCredentials(
                1L, username, email,
                encodeUtil.encodePassword("root"),
                UserStatus.ACTIVE, roles
        );

        //when
        when(userCredentialsRepository.findUserCredentialsByUsername(username))
                .thenReturn(credentials);
        String token = jwtTokenProvider.generateAccessToken(username, roles);

        //then
        assertThat(jwtTokenProvider.validateAccessToken(token)).isTrue();

        assertThat(rolesAreEquals(roles, role)).isTrue();

        assertThat(jwtTokenProvider.getUsername(token)).isEqualTo(username);

    }


    private boolean rolesAreEquals(List<Role> roles, Role role) {
        return roles.contains(role);
    }


    @Test
    public void testEncoder() {
        String root = encodeUtil.encodePassword("root");
        String newPassword = encodeUtil.encodePassword("root");
    }


}