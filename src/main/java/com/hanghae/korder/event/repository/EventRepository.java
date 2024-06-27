package com.hanghae.korder.event.repository;

import com.hanghae.korder.event.entity.EventEntity;
import com.hanghae.korder.event.repository.querydsl.EventRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<EventEntity, Long>, EventRepositoryCustom {


    Optional<EventEntity> findByIdAndCreatedBy(Long id, String createdBy);

    List<EventEntity> findAll();
}