import http from 'k6/http';
import { check } from 'k6';

export const options = {
    vus: 100,
    duration: '60s',
};

export default function () {

    const response = http.get(
        'http://localhost:8080/toilets/nearby'
        + '?lat=52.5200'
        + '&lon=13.4050'
        + '&radiusMeters=5000'
        + '&limit=50'
    );

    check(response, {
        'status is 200': (r) => r.status === 200,
    });
}