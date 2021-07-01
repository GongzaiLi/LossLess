package com.seng302.wasteless.repository;

import com.seng302.wasteless.model.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * ProductImageRepository is a repository interface for ProductImage.
 * Used to declare accessors to JPA objects.
 */
@RepositoryRestResource
public interface ProductImageRepository extends JpaRepository<ProductImage, Integer> {

    ProductImage findFirstById(Integer id);
}