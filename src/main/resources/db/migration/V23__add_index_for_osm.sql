CREATE UNIQUE INDEX idx_toilets_osm_id
    ON toilets(osm_id)
    WHERE osm_id IS NOT NULL;