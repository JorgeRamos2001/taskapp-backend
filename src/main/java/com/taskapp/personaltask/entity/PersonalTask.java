package com.taskapp.personaltask.entity;

import com.taskapp.auth.entity.User;
import com.taskapp.personaltask.entity.enums.PersonalTaskPriority;
import com.taskapp.personaltask.entity.enums.PersonalTaskState;
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
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "personal_tasks")
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString(exclude = { "owner" })
@EqualsAndHashCode(of = "id")
@Builder
public class PersonalTask {
    @Id
    @UuidGenerator
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Setter
    @Column(nullable = false, length = 100)
    private String title;

    @Setter
    @Column(columnDefinition = "TEXT")
    private String description;

    @Setter
    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private PersonalTaskPriority priority;

    @Setter
    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private PersonalTaskState state;

    @Setter
    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
}
