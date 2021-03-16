package com.seng302.wasteless.Business;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface BusinessRepository extends JpaRepository<Business, Integer> {
}

