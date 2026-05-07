CREATE INDEX toilets_location_idx
    ON toilets
    USING GIST(location);