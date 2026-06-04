package com.toiletfinder.toilet_finder;

import org.junit.jupiter.api.Test;

import org.testcontainers.containers.PostgreSQLContainer;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PostgresContainerTest {

    @Test
    void shouldStartPostgresContainer() {

        try (
                PostgreSQLContainer<?> postgres =
                        new PostgreSQLContainer<>(
                                "postgres:17"
                        )
        ) {

            postgres.start();

            assertTrue(
                    postgres.isRunning()
            );

            System.out.println(
                    postgres.getJdbcUrl()
            );
        }
    }
}