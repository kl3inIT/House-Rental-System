package com.rental.houserental.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 500)
    private String message;

    @Column(nullable = false, length = 100)
    private String type; // e.g. upgrade-request-approved, upgrade-request-rejected

    @Column(nullable = false)
    private boolean isRead = false;

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
}