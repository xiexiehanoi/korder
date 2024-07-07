package com.hanghae.eventservice.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity( name="events")
@Table( name ="events")
public class EventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "name")
    private String name;

    @Column(nullable = true, name = "description")
    private String description;

    @Column(nullable = true, name = "place")
    private String place;

    @Column(nullable = false, name = "create_user_id")
    private Long userId;

    @Column(nullable = false, name = "date")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate date;

    @Column(nullable = false, name = "price")
    private BigDecimal price;

    @Column(nullable = false, name = "quantity")
    private int quantity;

    @Column(nullable = false, name = "status")
    @Builder.Default
    private String status="available";

    @Version
    @Column(nullable = false)
    @Builder.Default
    private Long version =0L;

    @Column(name = "created_at")
    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime deletedAt;

}
