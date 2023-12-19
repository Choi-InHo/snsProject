package com.example.sis.Controller;


import com.example.sis.Controller.request.UserJoinRequest;
import com.example.sis.Controller.response.Response;
import com.example.sis.Controller.response.UserJoinResponse;
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
        User user = userService.join(request.getUserName(), request.getPasswoad());
        UserJoinResponse userJoinResponse = UserJoinResponse.fromUser(user);

        return Response.success(userJoinResponse);
    }

    @PostMapping
    public void login() {
        userService.login();
    }
}
