package com.example.sis.service;

import com.example.sis.exception.ErrorCode;
import com.example.sis.exception.SnsApplicationException;
import com.example.sis.model.User;
import com.example.sis.model.entity.UserEntity;
import com.example.sis.repository.UserEntityRepository;
import com.example.sis.util.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserEntityRepository userEntityRepository;
    private final BCryptPasswordEncoder encoder;

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.token.expired-time-ms}")
    private Long expiredTimeMs;

    //TODO: implement
    @Transactional // 예외가 발생하는 경우 같이 save 하지 못하도록 설정
    public User join(String userName, String password) {
        //회원가입하려는 userName으로 회원가입된 user가 있는지
        userEntityRepository.findByUserName(userName).ifPresent(it -> {
            throw new SnsApplicationException(ErrorCode.DUPLICATED_USER_NAME, String.format("%s is duplicated",userName));
        });
        //회원가입 진행 -> 유저 등록

        UserEntity userEntity = userEntityRepository.save(UserEntity.of(userName, encoder.encode(password)));
        return User.fromEntity(userEntity);
    }

    //TODO: implement
    //String으로 받는 이유는 인증시 문자열 토큰을 이용해서 인증하기 때문임
    public String login(String username, String password) {
        //회원 가입 여부 체크
        UserEntity userEntity = userEntityRepository.findByUserName(username).orElseThrow(()->new SnsApplicationException(ErrorCode.USER_NOT_FOUND,String.format("%s not founded", username)));

        //비밀번호 체크
        //TODO: 비밀번호가 그냥 평문으로 쳤을때 로그인이 되지 않음
        if (encoder.matches(password, userEntity.getPassword())) {
            throw new SnsApplicationException(ErrorCode.INVALID_PASSWORD);
        }
//        if (!userEntity.getPassword().equals(password)) {
//            throw new SnsApplicationException(ErrorCode.DUPLICATED_USER_NAME,"");
//        }

        //토큰 생성
        String token = JwtTokenUtils.doGenerateToken(username,secretKey,expiredTimeMs);

        return token;
    }

    public User loadUserByUsername(String userName) {
        return userEntityRepository.findByUserName(userName).map(User::fromEntity).orElseThrow(()->
                new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded",userName)));
    }
}
