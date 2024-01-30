package nomadteam.auth.service.impl;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import nomadteam.auth.config.security.CustomAuthentication;
import nomadteam.auth.config.security.CustomUserDetails;
import nomadteam.auth.dto.PasswordUpdateDto;
import nomadteam.auth.exception.BadRequestException;
import nomadteam.auth.persistence.entity.ERole;
import nomadteam.auth.persistence.entity.Role;
import nomadteam.auth.persistence.entity.User;
import nomadteam.auth.persistence.entity.UserStatus;
import nomadteam.auth.persistence.repo.UserRepository;
import nomadteam.auth.service.IUserService;
import nomadteam.auth.utils.EncodeUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserServiceImplTest {

    @Mock
    UserRepository repository;
    @Mock
    IUserService service;

    @Autowired
    EncodeUtil encodeUtil;
    Long id = 1L;
    String oldPassword = "root";
    String newPassword = "root123";
    String username = "user";
    CustomUserDetails userDetails;
    CustomAuthentication authentication;
    User user = new User();


    @BeforeEach
    void setUp() {
        //given
        service = new UserServiceImpl(repository, encodeUtil);
        userDetails = CustomUserDetails.builder()
                .username(username)
                .password("")
                .roles(List.of(new Role(1L, ERole.ROLE_USER)))
                .build();

        authentication = new CustomAuthentication(
                userDetails, "",
                userDetails.getAuthorities(),
                id
        );

        user.setPassword(encodeUtil.encodePassword(oldPassword));
        user.setUsername(username);
        user.setId(id);
        user.setStatus(UserStatus.ACTIVE);
    }

    @Test
    void itShouldUpdatePasswordWithoutErrors() {
        //when
        when(repository.findById(id))
                .thenReturn(Optional.of(user));

        service.updatePassword(
                id,
                PasswordUpdateDto.builder()
                        .newPassword(newPassword)
                        .oldPassword(oldPassword)
                        .build(),
                authentication
        );

        //then
        verify(repository).save(user);
    }


    @Test
    void itShouldThrowBadRequestExceptionBecausePasswordsDifferent() {
        //given
        user.setPassword(encodeUtil.encodePassword("root324324"));

        when(repository.findById(id))
                .thenReturn(Optional.of(user));

        assertThrows(
                BadRequestException.class, () -> service.updatePassword(
                        id,
                        PasswordUpdateDto.builder()
                                .newPassword(newPassword)
                                .oldPassword(oldPassword)
                                .build(),
                        authentication
                )
        );
    }


    @Test
    void itShouldThrowExceptionBecauseOfDifferentIds() {
        when(repository.findById(id))
                .thenReturn(Optional.of(user));

        assertThrows(
                BadRequestException.class, () -> service.updatePassword(
                        2L,
                        PasswordUpdateDto.builder()
                                .newPassword(newPassword)
                                .oldPassword(oldPassword)
                                .build(),
                        authentication
                )
        );
    }
}