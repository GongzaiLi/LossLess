package com.seng302.wasteless.repository;

import com.seng302.wasteless.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * ImageRepository is a repository interface for Image.
 * Used to declare accessors to JPA objects.
 */
@RepositoryRestResource
public interface ImageRepository extends JpaRepository<Image, Integer> {

    Image findFirstById(Integer id);
}