package nomadteam.auth.config.cloud;

import nomadteam.auth.dto.UsernameDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "blog")
public interface BlogServiceFeignExchanger {

    @PostMapping("/feign/register/user")
    UsernameDto sendRegisteredUser(UsernameDto usernameDto);
}
