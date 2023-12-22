package com.example.sis.Controller;


import com.example.sis.Controller.request.UserJoinRequest;
import com.example.sis.Controller.request.UserLoginRequest;
import com.example.sis.Controller.response.AlarmResponse;
import com.example.sis.Controller.response.Response;
import com.example.sis.Controller.response.UserJoinResponse;
import com.example.sis.Controller.response.UserLoginResponse;
import com.example.sis.model.User;
import com.example.sis.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {


    private final UserService userService;

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


//    @PostMapping
//    public void login() {
//        userService.login();
//    }
}
