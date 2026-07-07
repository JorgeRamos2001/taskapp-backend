CREATE TABLE users
(
    id         UUID PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    email      VARCHAR(100) NOT NULL,
    password   VARCHAR(255) NOT NULL,
    url_avatar VARCHAR(255),
    provider   VARCHAR(20)  NOT NULL,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT unq_user_email UNIQUE (email),
    CONSTRAINT ck_user_provider CHECK (provider IN ('GOOGLE', 'LOCAL'))
);

CREATE TABLE boards
(
    id          UUID PRIMARY KEY,
    owner_id    UUID         NOT NULL,
    title       VARCHAR(100) NOT NULL,
    description TEXT,
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_board_owner FOREIGN KEY (owner_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE board_members
(
    id         UUID PRIMARY KEY,
    board_id   UUID        NOT NULL,
    user_id    UUID        NOT NULL,
    role       VARCHAR(20) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_board_member_board FOREIGN KEY (board_id) REFERENCES boards (id) ON DELETE CASCADE,
    CONSTRAINT fk_board_member_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT unq_board_member_user_board UNIQUE (user_id, board_id),
    CONSTRAINT ck_board_member_role CHECK (role IN ('OWNER', 'ADMIN', 'MEMBER'))
);

CREATE TABLE board_tasks
(
    id          UUID PRIMARY KEY,
    board_id    UUID         NOT NULL,
    assignee_id UUID,
    title       VARCHAR(100) NOT NULL,
    description TEXT,
    priority    VARCHAR(20)  NOT NULL,
    state       VARCHAR(20)  NOT NULL,
    due_date    TIMESTAMPTZ,
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_board_task_board FOREIGN KEY (board_id) REFERENCES boards (id) ON DELETE CASCADE,
    CONSTRAINT fk_board_task_assignee FOREIGN KEY (assignee_id) REFERENCES users (id) ON DELETE SET NULL,
    CONSTRAINT ck_board_task_priority CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH', 'URGENT')),
    CONSTRAINT ck_board_task_state CHECK (state IN ('UNASSIGNED', 'IN_PROGRESS', 'DONE', 'OVERDUE'))
);

CREATE TABLE personal_tasks
(
    id          UUID PRIMARY KEY,
    owner_id    UUID         NOT NULL,
    title       VARCHAR(100) NOT NULL,
    description TEXT,
    priority    VARCHAR(20)  NOT NULL,
    state       VARCHAR(20)  NOT NULL,
    due_date    TIMESTAMPTZ,
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_personal_task_owner FOREIGN KEY (owner_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT ck_personal_task_priority CHECK (priority IN ('LOW', 'MID', 'HIGH', 'URGENT')),
    CONSTRAINT ck_personal_task_state CHECK (state IN ('IN_PROGRESS', 'DONE', 'OVERDUE'))
);

CREATE TABLE sub_tasks
(
    id           UUID PRIMARY KEY,
    task_id      UUID         NOT NULL,
    title        VARCHAR(100) NOT NULL,
    completed    BOOLEAN      NOT NULL DEFAULT FALSE,
    completed_at TIMESTAMPTZ,
    updated_at   TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_sub_task_task FOREIGN KEY (task_id) REFERENCES board_tasks (id) ON DELETE CASCADE
);

CREATE TABLE comments
(
    id         UUID PRIMARY KEY,
    task_id    UUID        NOT NULL,
    user_id    UUID        NOT NULL,
    content    TEXT        NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_comment_task FOREIGN KEY (task_id) REFERENCES board_tasks (id) ON DELETE CASCADE,
    CONSTRAINT fk_comment_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE refresh_tokens
(
    id         UUID PRIMARY KEY,
    user_id    UUID         NOT NULL,
    token      VARCHAR(255) NOT NULL,
    expires_at TIMESTAMPTZ  NOT NULL,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_refresh_token_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT unq_refresh_token_token UNIQUE (token)
);

-- Índices para optimización de consultas
CREATE INDEX idx_user_email ON users (email);
CREATE INDEX idx_board_member_board ON board_members (board_id);
CREATE INDEX idx_board_task_board ON board_tasks (board_id);
CREATE INDEX idx_board_task_assignee ON board_tasks (assignee_id);
CREATE INDEX idx_sub_task_task ON sub_tasks (task_id);
CREATE INDEX idx_comment_task ON comments (task_id);
CREATE INDEX idx_personal_task_owner ON personal_tasks (owner_id);