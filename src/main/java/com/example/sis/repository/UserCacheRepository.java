package com.example.sis.repository;

import com.example.sis.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.time.Duration;
import java.util.Optional;

@Repository
@Slf4j
@RequiredArgsConstructor
//redis로 캐싱을 할때는 expired를 지정해주는것이 좋다.

public class UserCacheRepository {

    private final RedisTemplate<String, User> userRedisTemplate;
    //TTL을 설정해주기 위한 변수
    //이 시간이 지나면 캐시에서 메모리가 사라짐
    private final static Duration USER_CACHE_TTL = Duration.ofDays(3);

    public void setUser(User user) {
        userRedisTemplate.opsForValue().set(user.getUsername(),user,USER_CACHE_TTL);
    }

    public Optional<User> getUser(String userName) {
        User user = userRedisTemplate.opsForValue().get(getKey(userName));
        return Optional.ofNullable(user);
    }

    private String getKey(String userName) {
        return "USER: " + userName;
    }
}
