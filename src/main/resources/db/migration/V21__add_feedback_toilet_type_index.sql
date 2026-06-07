CREATE INDEX idx_feedback_toilet_type
    ON toilet_feedback (
                        toilet_id,
                        feedback_type
        );