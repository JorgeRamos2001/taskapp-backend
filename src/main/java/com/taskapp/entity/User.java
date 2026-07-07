package com.taskapp.entity;

import com.taskapp.entity.enums.UserProvider;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString(exclude = { "password", "boardMembers", "boardTasks", "comments", "personalTasks" })
@EqualsAndHashCode(of = "id")
@Builder
public class User {
    @Id
    @UuidGenerator
    private UUID id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(name = "url_avatar", length = 255)
    private String urlAvatar;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private UserProvider provider;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "user")
    @Builder.Default
    private List<BoardMember> boardMembers = new ArrayList<>();

    @OneToMany(mappedBy = "assignee")
    @Builder.Default
    private List<BoardTask> boardTasks = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "owner")
    @Builder.Default
    private List<PersonalTask> personalTasks = new ArrayList<>();
}
