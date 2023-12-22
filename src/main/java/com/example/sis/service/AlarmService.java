package com.example.sis.service;




import com.example.sis.exception.ErrorCode;
import com.example.sis.exception.SnsApplicationException;
import com.example.sis.model.AlarmArgs;
import com.example.sis.model.AlarmNoti;
import com.example.sis.model.AlarmType;
import com.example.sis.model.entity.AlarmEntity;
import com.example.sis.model.entity.UserEntity;
import com.example.sis.repository.AlarmEntityRepository;
import com.example.sis.repository.EmitterRepository;
import com.example.sis.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlarmService {

    private final static String ALARM_NAME = "alarm";

    private final AlarmEntityRepository alarmEntityRepository;
    private final EmitterRepository emitterRepository;
    private final UserEntityRepository userEntityRepository;
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;


    //알람이 발생했을때 알람이 발생했다고 API에 보내주는 메소드
    public void send(Integer alarmId, Integer userId) {
        emitterRepository.get(userId).ifPresentOrElse(sseEmitter -> {
            try {
                sseEmitter.send(SseEmitter.event().id(alarmId.toString()).name(ALARM_NAME).data("new alarm"));
            } catch (IOException e) {
                emitterRepository.delete(userId);
                throw new SnsApplicationException(ErrorCode.NOTIFICATION_CONNECT_ERROR);
            }
        }, () ->log.info("no"));
    }


    public SseEmitter connectNotification(Integer userId) {
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
        emitterRepository.save(userId, emitter);
        emitter.onCompletion(() -> emitterRepository.delete(userId));
        emitter.onTimeout(() -> emitterRepository.delete(userId));

        try {
            log.info("send");
            emitter.send(SseEmitter.event()
                    .id("id")
                    .name(ALARM_NAME)
                    .data("connect completed"));
        } catch (IOException exception) {
            throw new SnsApplicationException(ErrorCode.NOTIFICATION_CONNECT_ERROR);
        }
        return emitter;
    }

}