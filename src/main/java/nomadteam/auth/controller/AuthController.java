package nomadteam.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.security.auth.message.AuthException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import nomadteam.auth.dto.AskDto;
import nomadteam.auth.dto.auth.AuthenticationRequest;
import nomadteam.auth.dto.auth.AuthenticationResponse;
import nomadteam.auth.dto.auth.RefreshJwtRequest;
import nomadteam.auth.dto.auth.RegisterRequest;
import nomadteam.auth.exception.BadRequestException;
import nomadteam.auth.service.IAuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@CrossOrigin("http://localhost:4200")
@Tag(name = "Контроллер авторизации", description = "Логика аутентификации и регистрации")
public class AuthController {

    IAuthService authService;

    @PostMapping("/registration")
    @Operation(summary = "Registration. Doesn't require  any token")
    public ResponseEntity<HttpStatus> register(@RequestBody RegisterRequest request) {
        log.info("Register user: {}", request);
        authService.register(request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


    @PostMapping(value = "/login", produces = "application/json")
    @Operation(summary = "Login. Doesn't require  any token")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest request) {
        log.info("Authentication request: {}", request);
        return ResponseEntity.ok(authService.authenticate(request));
    }

    @GetMapping("/validate")
    @Operation(summary = "Validate token")
    public ResponseEntity<AskDto> validate(@RequestParam("token") String token) {
        log.info("Validate token: {}", token);
        if (!authService.validate(token)) {
            throw new BadRequestException("Not valid token");
        }

        return ResponseEntity.ok(AskDto.makeDefault(true));
    }

    @PostMapping("/token")
    @Operation(summary = "Getting new access token via refresh token")
    public ResponseEntity<AuthenticationResponse> getNewAccessToken(@RequestBody RefreshJwtRequest request) {
        final AuthenticationResponse token = authService.getAccessToken(request.getRefreshToken());
        return ResponseEntity.ok(token);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Getting new refresh token via refresh token")
    public ResponseEntity<AuthenticationResponse> getNewRefreshToken(@RequestBody RefreshJwtRequest request)
            throws AuthException {
        final AuthenticationResponse token = authService.refresh(request.getRefreshToken());
        return ResponseEntity.ok(token);
    }
}
