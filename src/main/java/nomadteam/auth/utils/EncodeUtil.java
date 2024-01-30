package nomadteam.auth.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EncodeUtil {

    private final PasswordEncoder passwordEncoder;

    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    public boolean matchEncodedData(String newPassword, String oldPassword) {
        return passwordEncoder.matches(newPassword, oldPassword);
    }
}