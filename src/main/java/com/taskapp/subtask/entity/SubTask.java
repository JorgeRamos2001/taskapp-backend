package com.taskapp.subtask.entity;

import com.taskapp.boardtask.entity.BoardTask;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "sub_tasks")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString(exclude = { "boardTask" })
@EqualsAndHashCode(of = "id")
@Builder
public class SubTask {
    @Id
    @UuidGenerator
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private BoardTask boardTask;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false)
    private Boolean completed;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
}
