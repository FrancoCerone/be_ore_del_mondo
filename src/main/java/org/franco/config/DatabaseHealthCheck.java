package org.franco.config;

import jakarta.enterprise.context.ApplicationScoped;
import javax.sql.DataSource;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

@Readiness
@ApplicationScoped
public class DatabaseHealthCheck implements HealthCheck {

    private final DataSource dataSource;

    public DatabaseHealthCheck(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public HealthCheckResponse call() {
        try (var connection = dataSource.getConnection()) {
            return HealthCheckResponse.named("database").status(connection.isValid(2)).build();
        } catch (Exception exception) {
            return HealthCheckResponse.named("database").down().build();
        }
    }
}
