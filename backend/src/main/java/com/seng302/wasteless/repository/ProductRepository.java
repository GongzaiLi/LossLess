package com.seng302.wasteless.repository;

import com.seng302.wasteless.model.Business;
import com.seng302.wasteless.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface ProductRepository extends JpaRepository<Product, Integer> {

    Product findFirstById(String id);
    List<Product> findAllByBusinessId(Integer id);
}
