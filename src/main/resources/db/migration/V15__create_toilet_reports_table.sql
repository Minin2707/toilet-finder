CREATE TABLE toilet_reports (

                                id UUID PRIMARY KEY,

                                toilet_id UUID NOT NULL
                                    REFERENCES toilets(id),

                                user_id UUID NOT NULL
                                    REFERENCES users(id),

                                created_at TIMESTAMP NOT NULL,

                                CONSTRAINT unique_toilet_report
                                    UNIQUE (toilet_id, user_id)
);