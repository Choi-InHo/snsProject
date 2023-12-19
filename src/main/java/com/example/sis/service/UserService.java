package com.example.sis.service;

import com.example.sis.exception.SnsApplicationException;
import com.example.sis.model.User;
import com.example.sis.model.entity.UserEntity;
import com.example.sis.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserEntityRepository userEntityRepository;

    //TODO: implement
    public User join(String userName, String password) {
        //회원가입하려는 userName으로 회원가입된 user가 있는지
        userEntityRepository.findByUserName(userName).ifPresent(it -> {
            throw new SnsApplicationException();
        });
        //회원가입 진행 -> 유저 등록

        UserEntity userEntity = userEntityRepository.save(UserEntity.of(userName,password));
        return User.fromEntity(userEntity);
    }
    //TODO: implement
    //String으로 받는 이유는 인증시 문자열 토큰을 이용해서 인증하기 때문임
    public String login(String username, String password) {
        return "";
    }
}
