package nomadteam.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.List;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import nomadteam.auth.config.security.CustomAuthentication;
import nomadteam.auth.dto.PasswordUpdateDto;
import nomadteam.auth.dto.UserDto;
import nomadteam.auth.dto.UsernameDto;
import nomadteam.auth.dto.mapper.UserMapper;
import nomadteam.auth.service.IUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@CrossOrigin("http://localhost:4200")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UsersController {

    IUserService userService;

    @GetMapping("/list")
    @Operation(summary = "Получение списка логинов по ID(для бэка)")
    public ResponseEntity<List<UsernameDto>> getUsernameByIdList(@RequestParam("ids") List<Long> list) {
        log.info("Income request to ids: {}", list);
        return ResponseEntity.ok()
                .body(
                        userService.getUsernamesByIdList(list)
                                .stream()
                                .map(UserMapper::toUsernameDto)
                                .toList()
                );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получение данных пользователей по ID")
    public ResponseEntity<UserDto> getUserInfo(@PathVariable Long id) {
        return ResponseEntity.ok(UserMapper.toDto(userService.findById(id)));
    }


    @PatchMapping("/{id}")
    @SecurityRequirement(name = "JWT")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Обновление пользователя", description = "Требует логина")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @RequestBody UserDto userDto,
            @NonNull CustomAuthentication authentication) {
        log.info("Request for updating user");
        return ResponseEntity.ok(UserMapper.toDto(userService.updateUserInfo(id, userDto, authentication)));
    }

    @PatchMapping("/password/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Обновление пароля пользователя", description = "Требует логина")
    public ResponseEntity<?> updatePassword(@PathVariable Long id, @RequestBody PasswordUpdateDto password,
            @NonNull CustomAuthentication authentication) {
        log.info("Request for updating user password");
        userService.updatePassword(id, password, authentication);
        return ResponseEntity.ok().build();
    }

}