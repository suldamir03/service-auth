package nomadteam.auth.service.impl;

import io.jsonwebtoken.Claims;
import jakarta.security.auth.message.AuthException;
import jakarta.ws.rs.NotFoundException;
import java.util.Collections;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import nomadteam.auth.config.cloud.BlogServiceFeignExchanger;
import nomadteam.auth.config.security.JwtTokenProvider;
import nomadteam.auth.dto.auth.AuthenticationRequest;
import nomadteam.auth.dto.auth.AuthenticationResponse;
import nomadteam.auth.dto.auth.RegisterRequest;
import nomadteam.auth.dto.mapper.UserMapper;
import nomadteam.auth.exception.BadRequestException;
import nomadteam.auth.persistence.entity.ERole;
import nomadteam.auth.persistence.entity.UserCredentials;
import nomadteam.auth.persistence.entity.UserStatus;
import nomadteam.auth.persistence.repo.RoleRepository;
import nomadteam.auth.persistence.repo.UserCredentialsRepository;
import nomadteam.auth.service.IAuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthServiceImpl implements IAuthService {

    JwtTokenProvider jwtProvider;
    PasswordEncoder passwordEncoder;
    AuthenticationManager authenticationManager;
    UserCredentialsRepository userCredentialsRepository;
    RoleRepository roleRepository;
    BlogServiceFeignExchanger exchanger;

    /**
     * @param request Метод Регистрации пользователя
     */
    @Override
    public void register(RegisterRequest request) {

        if (request.getUsername().isBlank() || request.getEmail().isBlank() ||
                request.getPassword().isBlank()) {
            throw new BadRequestException("Not all fields are filled in");
        }

        if (userCredentialsRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Account with this username already exists");
        }

        if (userCredentialsRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException(
                    String.format("Account with email %s already exists", request.getEmail()));
        }

        request.setPassword(passwordEncoder.encode(request.getPassword()));
        log.info("Save user: {}", request);
        final UserCredentials savedUser = userCredentialsRepository.saveAndFlush(
                UserCredentials.builder()
                        .email(request.getEmail())
                        .username(request.getUsername())
                        .password(request.getPassword())
                        .roles(Collections.singletonList(
                                        roleRepository.findRoleByName(ERole.ROLE_USER)
                                                .orElseThrow(
                                                        () -> new RuntimeException("Role cannot be null")
                                                )
                                )
                        )
                        .status(UserStatus.ACTIVE)
                        .build()
        );

        exchanger.sendRegisteredUser(UserMapper.toUsernameDto(savedUser));
    }

    /**
     * @param request Метод Аутентификации пользователя
     */
    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        if (!userCredentialsRepository.existsByUsername(request.getUsername())) {
            throw new NotFoundException(
                    String.format("User with login %s not found", request.getUsername())
            );
        }

        Authentication authenticate;
        try {
            authenticate = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword())
            );
        } catch (Exception e) {
            throw new BadRequestException("Bad Credentials");
        }

        var accessToken = jwtProvider.generateAccessToken(request.getUsername(), authenticate.getAuthorities());
        var refreshToken = jwtProvider.generateRefreshToken(request.getUsername());
        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public boolean validate(String token) {
        return jwtProvider.validateAccessToken(token);
    }

    @Override
    public AuthenticationResponse getAccessToken(String refreshToken) {
        if (jwtProvider.validateRefreshToken(refreshToken)) {
            final Claims claims = jwtProvider.getRefreshClaims(refreshToken);
            final String username = claims.getSubject();
            UserCredentials user = userCredentialsRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException(String.format(
                                    "User with username %s not found",
                                    username)
                            )
                    );
            final String accessToken = jwtProvider.generateAccessToken(user.getUsername(), user.getRoles());
            return new AuthenticationResponse(accessToken, refreshToken);
        }
        return new AuthenticationResponse();
    }


    @Override
    public AuthenticationResponse refresh(String refreshToken) throws AuthException {
        if (jwtProvider.validateRefreshToken(refreshToken)) {

            final Claims claims = jwtProvider.getRefreshClaims(refreshToken);
            final String username = claims.getSubject();

            UserCredentials user = userCredentialsRepository.findByUsername(
                    username
            ).orElseThrow(() -> new RuntimeException(String.format(
                            "User with username %s not found",
                            username)
                    )
            );

            final String accessToken = jwtProvider.generateAccessToken(username, user.getRoles());
            final String newRefreshToken = jwtProvider.generateRefreshToken(username);

            return new AuthenticationResponse(accessToken, newRefreshToken);
        }
        throw new AuthException("Невалидный JWT токен");
    }
}
