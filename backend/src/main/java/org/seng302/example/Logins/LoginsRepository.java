package org.seng302.example.Logins;

import org.seng302.example.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
interface LoginsRepository extends JpaRepository<Logins, Long> {

    Logins findByEmail(String email);
}
