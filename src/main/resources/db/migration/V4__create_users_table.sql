CREATE TABLE users (
                       id UUID PRIMARY KEY,
                       username VARCHAR(255) UNIQUE NOT NULL,
                       credential_id TEXT UNIQUE NOT NULL,
                       public_key TEXT NOT NULL,
                       created_at TIMESTAMP NOT NULL
);