CREATE TABLE toilet_photos (

                               id UUID PRIMARY KEY,

                               toilet_id UUID NOT NULL
                                   REFERENCES toilets(id)
                                       ON DELETE CASCADE,

                               uploaded_by_user_id UUID NOT NULL
                                   REFERENCES users(id)
                                       ON DELETE CASCADE,

                               photo_url TEXT NOT NULL,

                               report_count INTEGER NOT NULL DEFAULT 0,

                               status VARCHAR(50) NOT NULL,

                               created_at TIMESTAMP NOT NULL
);

CREATE TABLE photo_reports (

                               id UUID PRIMARY KEY,

                               photo_id UUID NOT NULL
                                   REFERENCES toilet_photos(id)
                                       ON DELETE CASCADE,

                               user_id UUID NOT NULL
                                   REFERENCES users(id)
                                       ON DELETE CASCADE,

                               created_at TIMESTAMP NOT NULL
);