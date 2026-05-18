ALTER TABLE toilets
    ADD COLUMN confirmation_count INT DEFAULT 0;

ALTER TABLE toilets
    ADD COLUMN last_confirmed_at TIMESTAMP;