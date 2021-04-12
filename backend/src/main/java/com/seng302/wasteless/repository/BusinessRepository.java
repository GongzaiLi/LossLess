package com.seng302.wasteless.repository;

import com.seng302.wasteless.model.Business;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface BusinessRepository extends JpaRepository<Business, Integer> {

    Business findFirstById(Integer id);

//    @Query( "select u from User u inner join u.roles r where r.role in :roles" )
//    List<User> findBySpecificRoles(@Param("roles") List<Role> roles);

    @Query(value = "Select * from business where id in (SELECT distinct business_id FROM Business B inner join BUSINESS_ADMINISTRATORS A where administrators_id = :user_id)", nativeQuery = true)
    List<Business> findBySpecificAdminId(@Param("user_id") Integer user_id);
}

