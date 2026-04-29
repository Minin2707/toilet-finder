CREATE TABLE approvals (
                           id UUID PRIMARY KEY,
                           toilet_id UUID NOT NULL REFERENCES toilets(id),
                           user_id UUID NOT NULL,
                           created_at TIMESTAMP NOT NULL
);