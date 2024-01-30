package nomadteam.auth.service;

import java.util.List;
import nomadteam.auth.config.security.CustomAuthentication;
import nomadteam.auth.dto.PasswordUpdateDto;
import nomadteam.auth.dto.UserDto;
import nomadteam.auth.persistence.entity.User;


public interface IUserService {

    List<User> getUsernamesByIdList(List<Long> list);

    User findById(Long id);

    User updateUserInfo(Long id, UserDto userDto, CustomAuthentication authentication);

    void updatePassword(Long id, PasswordUpdateDto password, CustomAuthentication authentication);
}
