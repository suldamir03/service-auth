package nomadteam.auth.service.impl;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import nomadteam.auth.config.security.CustomAuthentication;
import nomadteam.auth.dto.PasswordUpdateDto;
import nomadteam.auth.dto.UserDto;
import nomadteam.auth.dto.mapper.UserMapper;
import nomadteam.auth.exception.BadRequestException;
import nomadteam.auth.persistence.entity.User;
import nomadteam.auth.persistence.repo.UserRepository;
import nomadteam.auth.service.IUserService;
import nomadteam.auth.utils.EncodeUtil;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final EncodeUtil encodeUtil;

    @Override
    public List<User> getUsernamesByIdList(List<Long> list) {
        return userRepository.findByIdIn(list);
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public User updateUserInfo(Long id, UserDto userDto, @NonNull CustomAuthentication authentication) {
        if (Objects.isNull(userDto)
                || Objects.isNull(userDto.getUsername())
                || Objects.isNull(userDto.getEmail())) {
            throw new BadRequestException("One of main fields is null");
        }
        Optional<User> dbUser = userRepository.findById(id);

        if (dbUser.isEmpty()) {
            throw new BadRequestException(String.format("User with id %s not found", id));
        }

        if (!authentication.getId().equals(id)) {
            throw new BadRequestException(
                    String.format("Id from request path: %s is not equal", id));
        }

        User user = UserMapper.toEntity(userDto);
        user.setId(id);
        return userRepository.save(user);
    }

    @Override
    public void updatePassword(Long id, PasswordUpdateDto passwordDto, CustomAuthentication authentication) {
        if (!Objects.equals(authentication.getId(), id)) {
            throw new BadRequestException(
                    String.format("User id: %s don't match token id: %s", id, authentication.getId()));
        }

        if (passwordDto.isEmpty()) {
            throw new BadRequestException("Passwords cannot be null");
        }
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(String.format("User with id %s not found", id)));

        System.out.println(
                "encodeUtil.matchEncodedData(encodeUtil.encodePassword(passwordDto.getOldPassword()), user.getPassword()) "
                        + encodeUtil.matchEncodedData(encodeUtil.encodePassword(passwordDto.getOldPassword()),
                        user.getPassword()));
        System.out.println("user.getPassword() = " + user.getPassword());
        System.out.println("passwordDto = " + passwordDto.getOldPassword());
        System.out.println("encodeUtil = " + encodeUtil.encodePassword(passwordDto.getOldPassword()));
        if (encodeUtil.matchEncodedData(passwordDto.getOldPassword(), user.getPassword())) {
            user.setPassword(encodeUtil.encodePassword(passwordDto.getNewPassword()));
            userRepository.save(user);
        } else {
            throw new BadRequestException("Old password != current password");
        }
    }
}
