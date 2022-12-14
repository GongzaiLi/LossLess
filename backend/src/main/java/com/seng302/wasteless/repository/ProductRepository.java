package com.seng302.wasteless.repository;


import com.seng302.wasteless.model.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

/**
 * ProductRepository is a repository interface for Product.
 * Used to declare accessors to JPA objects.
 */
@RepositoryRestResource
public interface ProductRepository extends JpaRepository<Product, Integer> {

    Product findFirstById(String id);

    Product findFirstByDatabaseId(Long databaseId);

    List<Product> findAllByBusinessIdAndIdContainsAllIgnoreCase(Integer businessId, String productId, Pageable pageable);

    Integer countProductByBusinessIdAndIdContainsAllIgnoreCase(Integer businessId, String productId);

}
