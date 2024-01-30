package nomadteam.auth.service;

import jakarta.security.auth.message.AuthException;
import nomadteam.auth.dto.auth.AuthenticationRequest;
import nomadteam.auth.dto.auth.AuthenticationResponse;
import nomadteam.auth.dto.auth.RegisterRequest;

public interface IAuthService {

    /**
     * @param request Метод Аутентификации пользователя
     */

    AuthenticationResponse authenticate(AuthenticationRequest request);

    /**
     * @param request Метод Регистрации пользователя
     */

    void register(RegisterRequest request);

    /**
     * Метод проверяющий access токен
     */
    boolean validate(String token);

    /**
     * Получение accessToken-a по refresh токену
     */
    AuthenticationResponse getAccessToken(String refreshToken);

    /**
     * Получение refreshToken-a по refresh токену
     */
    AuthenticationResponse refresh(String refreshToken) throws AuthException;
}
