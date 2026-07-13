package com.taskapp.boardtask.entity;

import com.taskapp.auth.entity.User;
import com.taskapp.board.entity.Board;
import com.taskapp.boardtask.entity.enums.BoardTaskPriority;
import com.taskapp.boardtask.entity.enums.BoardTaskState;
import com.taskapp.comment.entity.Comment;
import com.taskapp.subtask.entity.SubTask;
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
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "board_tasks")
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id")
    private User assignee;

    @Setter
    @Column(nullable = false, length = 100)
    private String title;

    @Setter
    @Column(columnDefinition = "TEXT")
    private String description;

    @Setter
    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private BoardTaskPriority priority;

    @Setter
    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private BoardTaskState state;

    @Setter
    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "boardTask", cascade = CascadeType.ALL, orphanRemoval = true)
    @Fetch(FetchMode.SUBSELECT)
    @Builder.Default
    private List<SubTask> subTasks = new ArrayList<>();

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    @Fetch(FetchMode.SUBSELECT)
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();
}
