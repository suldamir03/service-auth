package nomadteam.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PasswordUpdateDto {

    @JsonProperty("new_password")
    String newPassword;

    @JsonProperty("old_password")
    String oldPassword;

    public boolean isEmpty() {
        return oldPassword.isEmpty() || newPassword.isEmpty();
    }
}
