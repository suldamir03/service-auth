package nomadteam.auth.service;

import jakarta.security.auth.message.AuthException;
import nomadteam.auth.dto.AskDto;
import nomadteam.auth.dto.AuthenticationRequest;
import nomadteam.auth.dto.AuthenticationResponse;
import nomadteam.auth.dto.RegisterRequest;

public interface IAuthService {

    /**
     * @param request
     * Метод Аутентификации пользователя
     */

    AuthenticationResponse authenticate(AuthenticationRequest request);

    /**
     * @param request Метод Регистрации пользователя
     */

    AskDto register(RegisterRequest request);

    /**
     * @param token
     * Валидация токена
     */

    boolean validate(String token);

    AuthenticationResponse getAccessToken(String refreshToken);

    AuthenticationResponse refresh(String refreshToken) throws AuthException;
}
