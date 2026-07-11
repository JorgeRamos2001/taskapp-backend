package com.taskapp.boardtask.entity;

import com.taskapp.auth.entity.User;
import com.taskapp.board.entity.Board;
import com.taskapp.boardtask.entity.enums.BoardTaskPriority;
import com.taskapp.boardtask.entity.enums.BoardTaskState;
import com.taskapp.comment.entity.Comment;
import com.taskapp.subtask.entity.SubTask;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "board_tasks")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString(exclude = { "board", "assignee", "subTasks", "comments" })
@EqualsAndHashCode(of = "id")
@Builder
public class BoardTask {
    @Id
    @UuidGenerator
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id", nullable = false)
    private User assignee;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private BoardTaskPriority priority;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private BoardTaskState state;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "boardTask", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SubTask> subTasks = new ArrayList<>();

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();
}
