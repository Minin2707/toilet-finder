CREATE INDEX IF NOT EXISTS idx_toilet_photos_toilet_id
    ON toilet_photos(toilet_id);

CREATE INDEX IF NOT EXISTS idx_photo_reports_photo_id
    ON photo_reports(photo_id);

CREATE UNIQUE INDEX IF NOT EXISTS uq_photo_reports_photo_user
    ON photo_reports(photo_id, user_id);