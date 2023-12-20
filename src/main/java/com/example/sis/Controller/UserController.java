package com.example.sis.Controller;


import com.example.sis.Controller.request.UserJoinRequest;
import com.example.sis.Controller.request.UserLoginRequest;
import com.example.sis.Controller.response.Response;
import com.example.sis.Controller.response.UserJoinResponse;
import com.example.sis.Controller.response.UserLoginResponse;
import com.example.sis.model.User;
import com.example.sis.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

//    @PostMapping
//    public void login() {
//        userService.login();
//    }
}
