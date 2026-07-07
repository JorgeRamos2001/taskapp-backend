package com.taskapp.entity;

import com.taskapp.entity.enums.BoardTaskPriority;
import com.taskapp.entity.enums.BoardTaskState;
import com.taskapp.entity.enums.PersonalTaskPriority;
import com.taskapp.entity.enums.PersonalTaskState;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "personal_tasks")
@AllArgsConstructor
@NoArgsConstructor
@Setter
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

    @Column(nullable = false, length = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private PersonalTaskPriority priority;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private PersonalTaskState state;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
}
