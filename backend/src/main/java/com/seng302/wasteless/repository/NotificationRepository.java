package com.seng302.wasteless.repository;

import com.seng302.wasteless.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;


@RepositoryRestResource
public interface NotificationRepository extends JpaRepository<Notification, Integer>, JpaSpecificationExecutor<Notification> {
//    /**JpaRepository<Listing, Integer>, JpaSpecificationExecutor<Listing>
//     * Returns all notifications that belong to the current user and have the given archived state.
//     * The notifications are sorted by newest first, except that starred notifications always appear
//     * first.
//     * @param userId The id of the current user.
//     * @param archived Whether archived or non-archived notifications should be retrieved
//     * @return A (possibly empty) list of all notifications that belong to the current user.
//     */
//    List<Notification> findByUserIdAndArchivedOrderByStarredDescCreatedDesc(Integer userId, Boolean archived);

}
