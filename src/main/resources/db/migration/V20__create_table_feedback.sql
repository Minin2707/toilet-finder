CREATE TABLE user_messages
(
    id UUID PRIMARY KEY,

    user_id UUID NOT NULL,

    type VARCHAR(50) NOT NULL,

    message TEXT NOT NULL,

    created_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_feedback_user
        FOREIGN KEY (user_id)
            REFERENCES users(id)
);