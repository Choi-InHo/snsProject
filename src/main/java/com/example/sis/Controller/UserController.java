package com.example.sis.Controller;


import com.example.sis.Controller.request.UserJoinRequest;
import com.example.sis.Controller.request.UserLoginRequest;
import com.example.sis.Controller.response.AlarmResponse;
import com.example.sis.Controller.response.Response;
import com.example.sis.Controller.response.UserJoinResponse;
import com.example.sis.Controller.response.UserLoginResponse;
import com.example.sis.model.User;
import com.example.sis.service.AlarmService;
import com.example.sis.service.UserService;
import com.example.sis.util.ClassUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@Slf4j
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {


    private final UserService userService;
    private final AlarmService alarmService;

    @PostMapping("/join")
    public Response<UserJoinResponse> join(@RequestBody UserJoinRequest request) {
        return Response.success(UserJoinResponse.fromUser(userService.join(request.getName(), request.getPassword())));
    }

    @PostMapping("/login")
    public Response<UserLoginResponse> login(@RequestBody UserLoginRequest request) {
        String token = userService.login(request.getName(), request.getPassword());
        return Response.success(new UserLoginResponse(token));
    }

    @GetMapping("/alarm")
    public Response<Page<AlarmResponse>> alarm(Pageable pageable, Authentication authentication) {
        return Response.success(userService.alarmList(authentication.getName(), pageable).map(AlarmResponse::fromAlarm));
    }

    @GetMapping(value = "/alarm/subscribe")
    public SseEmitter subscribe(Authentication authentication) {
        log.info("subscribe");
        User user = ClassUtils.getSafeCastInstance(authentication.getPrincipal(), User.class);
        return alarmService.connectNotification(user.getId());
    }


//    @PostMapping
//    public void login() {
//        userService.login();
//    }
}
