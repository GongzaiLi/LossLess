package com.seng302.wasteless.repository;

import com.seng302.wasteless.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * AddressRepository is a repository interface for Address.
 * Used to declare accessors to JPA objects.
 */
@RepositoryRestResource
public interface AddressRepository extends JpaRepository<Address, Integer> {

    Address findAddressById(Integer id);
}
