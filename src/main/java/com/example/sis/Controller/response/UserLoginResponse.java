package com.example.sis.Controller.response;

import com.example.sis.model.User;
import com.example.sis.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserLoginResponse {

    private String token;
}
