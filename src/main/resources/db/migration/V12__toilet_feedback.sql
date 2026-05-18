CREATE TABLE toilet_feedback (

                                 toilet_id UUID NOT NULL,

                                 user_id UUID NOT NULL,

                                 feedback_type VARCHAR(50) NOT NULL,

                                 created_at TIMESTAMP NOT NULL,

                                 CONSTRAINT unique_feedback
                                     UNIQUE (
                                             toilet_id,
                                             user_id,
                                             feedback_type
                                         )
);