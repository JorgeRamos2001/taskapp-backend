package com.taskapp.auth.entity;

import com.taskapp.auth.entity.enums.UserProvider;
import com.taskapp.board.entity.BoardMember;
import com.taskapp.boardtask.entity.BoardTask;
import com.taskapp.comment.entity.Comment;
import com.taskapp.personaltask.entity.PersonalTask;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString(exclude = { "password", "boardMembers", "boardTasks", "comments", "personalTasks" })
@EqualsAndHashCode(of = "id")
@Builder
public class User implements UserDetails {
    @Id
    @UuidGenerator
    private UUID id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 255)
    @Setter
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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("USER"));
    }

    @Override
    public String getUsername() {
        return email;
    }
}
