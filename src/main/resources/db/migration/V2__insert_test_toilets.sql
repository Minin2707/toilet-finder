INSERT INTO toilets (
    id,
    title,
    description,
    location,
    address,
    status,
    created_at
)
VALUES (
           gen_random_uuid(),
           'Shopping mall restroom',
           'Free toilet on second floor',
           ST_SetSRID(ST_MakePoint(8.6821, 50.1109), 4326),
           'Frankfurt center',
           'APPROVED',
           NOW()
       );