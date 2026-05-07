CREATE TABLE auth_challenges (
                                 id UUID PRIMARY KEY,
                                 username VARCHAR(255) NOT NULL,
                                 challenge TEXT NOT NULL,
                                 challenge_type VARCHAR(50) NOT NULL,
                                 expires_at TIMESTAMP NOT NULL,
                                 created_at TIMESTAMP NOT NULL
);