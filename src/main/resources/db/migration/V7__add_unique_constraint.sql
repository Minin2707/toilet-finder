ALTER TABLE approvals
    ADD CONSTRAINT unique_user_toilet
        UNIQUE (toilet_id, user_id);