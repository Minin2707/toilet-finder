import http from 'k6/http';

export const options = {
    vus: 100,
    duration: '60s',
};

export default function () {

    http.get(
        'http://localhost:8080/api/toilets/nearby'
        + '?lat=52.5200'
        + '&lon=13.4050'
        + '&radiusMeters=5000'
        + '&limit=50'
    );
}