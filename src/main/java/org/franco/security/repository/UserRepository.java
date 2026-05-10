package org.franco.security.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;
import org.franco.security.entity.AppUser;

@ApplicationScoped
public class UserRepository implements PanacheRepositoryBase<AppUser, Long> {

    public Optional<AppUser> findByEmail(String email) {
        return find("email", email.toLowerCase()).firstResultOptional();
    }

    public boolean emailExists(String email) {
        return count("email", email.toLowerCase()) > 0;
    }
}
