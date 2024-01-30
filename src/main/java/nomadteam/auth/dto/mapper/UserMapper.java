package nomadteam.auth.dto.mapper;

import java.util.Objects;
import nomadteam.auth.dto.UserDto;
import nomadteam.auth.dto.UsernameDto;
import nomadteam.auth.persistence.entity.User;

public class UserMapper {

    public static UserDto toDto(User user) {
        if (Objects.isNull(user)) {
            return null;
        }
        return UserDto.builder()
                .id(user.getId())
                .status(user.getStatus())
                .email(user.getEmail())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
    }

    //todo: передача пароля
    public static User toEntity(UserDto dto) {
        User user = new User();
        user.setId(dto.getId());
        user.setEmail(dto.getEmail());
        user.setUsername(dto.getUsername());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        return user;
    }


    public static UsernameDto toUsernameDto(User user) {
        return UsernameDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .build();
    }
}