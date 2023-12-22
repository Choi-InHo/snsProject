package com.example.sis.service;

import com.example.sis.exception.ErrorCode;
import com.example.sis.exception.SnsApplicationException;
import com.example.sis.model.AlarmArgs;
import com.example.sis.model.AlarmType;
import com.example.sis.model.Comment;
import com.example.sis.model.Post;
import com.example.sis.model.entity.*;
import com.example.sis.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.IsNewStrategy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostEntityRepository postEntityRepository;
    private final UserEntityRepository userEntityRepository;
    private final LikeEntityRepository likeEntityRepository;
    private final CommentEntityRepository commentEntityRepository;
    private final AlarmEntityRepository alarmEntityRepository;
    private final AlarmService alarmService;

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

    @Transactional
    public void delete(String userName, Integer postId) {
        UserEntity userEntity = userEntityRepository.findByUserName(userName).orElseThrow(()->
                new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s is not founded",userName)));

        // post exit
        PostEntity postEntity =  postEntityRepository.findById(postId).orElseThrow(()->
                new SnsApplicationException(ErrorCode.POST_NOT_FOUND, String.format("%s not founded", postId)));
        if(postEntity.getUser() != userEntity){
            throw new SnsApplicationException(ErrorCode.INVALID_PERMISSION, String.format("%s has no permission", userName));
        }

        postEntityRepository.delete(postEntity);

    }

    public Page<Post> list(Pageable pageable) {
        return postEntityRepository.findAll(pageable).map(Post::fromEntity);
    }

    public Page<Post> my(String userName, Pageable pageable) {

        UserEntity userEntity = userEntityRepository.findByUserName(userName).orElseThrow(()->
                new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s is not founded",userName)));

        return postEntityRepository.findAllByUser(userEntity, pageable).map(Post::fromEntity);
    }

    @Transactional
    public void like(Integer postId, String userName) {
        UserEntity userEntity = userEntityRepository.findByUserName(userName).orElseThrow(()->
                new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s is not founded",userName)));

        // post exit
        PostEntity postEntity =  postEntityRepository.findById(postId).orElseThrow(()->
                new SnsApplicationException(ErrorCode.POST_NOT_FOUND, String.format("%s not founded", postId)));

        //한 번만 좋아요를 누를 수 있도록 설정
        likeEntityRepository.findByUserAndPost(userEntity, postEntity).ifPresent(it -> {
            throw new SnsApplicationException(ErrorCode.ALREADY_LIKED_POST, String.format("userName % is already like post %d", userName, postId));
        });

        //like save
        likeEntityRepository.save(LikeEntity.of(userEntity,postEntity));

        AlarmEntity alarmEntity = alarmEntityRepository.save(AlarmEntity.of(AlarmType.NEW_LIKE_ON_POST, new AlarmArgs(userEntity.getId(), postEntity.getId()), postEntity.getUser()));

        //        TODO: implements
        alarmService.send(alarmEntity.getId(), postEntity.getUser().getId());
//        alarmProducer.send(new AlarmEvent(AlarmType.NEW_LIKE_ON_POST, new AlarmArgs(userEntity.getId(), postId), postEntity.getUser().getId()));
    }

    @Transactional
    public int likeCount(Integer postId) {
        // post exit
        PostEntity postEntity =  postEntityRepository.findById(postId).orElseThrow(()->
                new SnsApplicationException(ErrorCode.POST_NOT_FOUND, String.format("%s not founded", postId)));

        //count like
//        List<LikeEntity> likeEntities =  likeEntityRepository.findAllByPost(postEntity);
//
//        return likeEntities.size();
        return likeEntityRepository.countByPost(postEntity);
    }

    @Transactional
    public void comment(Integer postId, String comment,  String userName) {
        UserEntity userEntity = userEntityRepository.findByUserName(userName).orElseThrow(()->
                new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s is not founded",userName)));

        // post exit
        PostEntity postEntity =  postEntityRepository.findById(postId).orElseThrow(()->
                new SnsApplicationException(ErrorCode.POST_NOT_FOUND, String.format("%s not founded", postId)));
        //comment save
        commentEntityRepository.save(CommentEntity.of(userEntity, postEntity,comment));

        AlarmEntity alarmEntity = alarmEntityRepository.save(AlarmEntity.of(AlarmType.NEW_COMMENT_ON_POST, new AlarmArgs(userEntity.getId(), postEntity.getId()), postEntity.getUser()));
//        TODO: implements
        alarmService.send(alarmEntity.getId(), postEntity.getUser().getId());
    }

    public Page<Comment> getComments(Integer postId, Pageable pageable){

        PostEntity postEntity =  postEntityRepository.findById(postId).orElseThrow(()->
                new SnsApplicationException(ErrorCode.POST_NOT_FOUND, String.format("%s not founded", postId)));
        //comment save
        return commentEntityRepository.findAllByPost(postEntity, pageable).map(Comment::fromEntity);

    }

    private PostEntity getPostOrException(Integer postId) {
        return postEntityRepository.findById(postId).orElseThrow(()->
                new SnsApplicationException(ErrorCode.POST_NOT_FOUND, String.format("%s not founded", postId)));

    }

    private UserEntity getUserOrException(String userName) {
        return userEntityRepository.findByUserName(userName).orElseThrow(()->
                new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s is not founded",userName)));

    }
}
