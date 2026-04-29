CREATE EXTENSION IF NOT EXISTS postgis;

CREATE TABLE toilets (
                         id UUID PRIMARY KEY,
                         title VARCHAR(255) NOT NULL,
                         description TEXT,
                         location GEOGRAPHY(Point, 4326) NOT NULL,
                         address VARCHAR(500),
                         status VARCHAR(50) NOT NULL,
                         created_at TIMESTAMP NOT NULL
);