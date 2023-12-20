package com.example.sis.service;

import com.example.sis.exception.ErrorCode;
import com.example.sis.exception.SnsApplicationException;
import com.example.sis.model.Post;
import com.example.sis.model.entity.PostEntity;
import com.example.sis.model.entity.UserEntity;
import com.example.sis.repository.PostEntityRepository;
import com.example.sis.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.support.IsNewStrategy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostEntityRepository postEntityRepository;
    private final UserEntityRepository userEntityRepository;

    @Transactional
    public void create(String title, String body, String userName) {
        //user find
        UserEntity user = userEntityRepository.findByUserName(userName).orElseThrow(()-> new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded",userName)));

        //post new
        PostEntity saved = postEntityRepository.save(PostEntity.of(title, body, user));
    }

    @Transactional
    public Post modify(String title, String body, String userName, Integer postId) {
        UserEntity userEntity = userEntityRepository.findByUserName(userName).orElseThrow(()->
                new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s is not founded",userName)));

        // post exit
        PostEntity postEntity =  postEntityRepository.findById(postId).orElseThrow(()->
                new SnsApplicationException(ErrorCode.POST_NOT_FOUND, String.format("%s not founded", postId)));
        if(postEntity.getUser() != userEntity){
            throw new SnsApplicationException(ErrorCode.INVALID_PERMISSION, String.format("%s has no permission", userName));
        }

        postEntity.setTitle(title);
        postEntity.setBody(body);

        return Post.fromEntity(postEntityRepository.saveAndFlush(postEntity));
    }
}