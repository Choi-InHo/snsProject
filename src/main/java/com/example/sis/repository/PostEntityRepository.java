package com.example.sis.repository;

import com.example.sis.model.entity.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;

@Repository
public interface PostEntityRepository extends JpaRepository<PostEntity, Integer> {
}
