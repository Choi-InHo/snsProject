package com.example.sis.repository;

import com.example.sis.model.entity.PostEntity;
import com.example.sis.model.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable; // 올바른 import 추가
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PostEntityRepository extends JpaRepository<PostEntity, Integer> {

    public Page<PostEntity> findAllByUser(UserEntity entity, Pageable pageable);
}
