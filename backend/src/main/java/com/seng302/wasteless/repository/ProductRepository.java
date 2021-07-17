package com.seng302.wasteless.repository;


import com.seng302.wasteless.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

/**
 * ProductRepository is a repository interface for Product.
 * Used to declare accessors to JPA objects.
 */
@RepositoryRestResource
public interface ProductRepository extends JpaRepository<Product, Integer> {

    Product findFirstById(String id);

    List<Product> findAllByBusinessId(Integer id);

    @Query(value = "select * from Product where business_id = :businessId offset :offsetNumber rows fetch next :countNumber rows only", nativeQuery = true)
    List<Product> findCountProductsWithOffset(@Param("businessId") Integer businessId, @Param("offsetNumber") Integer offset, @Param("countNumber") Integer count);

    Integer countProductByBusinessId(Integer id);

}
