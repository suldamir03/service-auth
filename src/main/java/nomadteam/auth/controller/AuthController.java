package nomadteam.auth.controller;

import jakarta.security.auth.message.AuthException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import nomadteam.auth.dto.AskDto;
import nomadteam.auth.dto.AuthenticationRequest;
import nomadteam.auth.dto.AuthenticationResponse;
import nomadteam.auth.dto.RefreshJwtRequest;
import nomadteam.auth.dto.RegisterRequest;
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
public class AuthController {

    IAuthService authService;

    @PostMapping("/registration")
    public ResponseEntity<HttpStatus> register(@RequestBody RegisterRequest request) {
        log.info("Register user: {}", request);
        authService.register(request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


    @PostMapping(value = "/login", produces = "application/json")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest request) {
        log.info("Authentication request: {}", request);
        return ResponseEntity.ok(authService.authenticate(request));
    }

    @GetMapping("/validate")
    public ResponseEntity<AskDto> validate(@RequestParam("token") String token) {
        log.info("Validate token: {}", token);
        if (!authService.validate(token)) {
            throw new BadRequestException("Not valid token");
        }

        return ResponseEntity.ok(AskDto.makeDefault(true));
    }

    @PostMapping("/token")
    public ResponseEntity<AuthenticationResponse> getNewAccessToken(@RequestBody RefreshJwtRequest request) {
        final AuthenticationResponse token = authService.getAccessToken(request.getRefreshToken());
        return ResponseEntity.ok(token);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthenticationResponse> getNewRefreshToken(@RequestBody RefreshJwtRequest request)
            throws AuthException {
        final AuthenticationResponse token = authService.refresh(request.getRefreshToken());
        return ResponseEntity.ok(token);
    }
}
