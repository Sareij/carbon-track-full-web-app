package com.carbontrack.repository;

import com.carbontrack.model.ActivityLog;
import com.carbontrack.model.Category;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * Read queries for the logs list. "join fetch" loads each log's emission factor in the SAME query
 * (otherwise Hibernate would fire one extra query per row: the "N+1 problem").
 * Declared as a marker Repository so it exposes ONLY these two methods.
 */
public interface ActivityLogSearchRepository extends Repository<ActivityLog, Long> {

    @Query("select a from ActivityLog a join fetch a.emissionFactor " +
           "where a.user.username = :username and a.logDate between :from and :to " +
           "order by a.logDate desc, a.id desc")
    List<ActivityLog> findForUser(@Param("username") String username,
                                  @Param("from") LocalDate from,
                                  @Param("to") LocalDate to);

    @Query("select a from ActivityLog a join fetch a.emissionFactor " +
           "where a.user.username = :username and a.category = :category " +
           "and a.logDate between :from and :to " +
           "order by a.logDate desc, a.id desc")
    List<ActivityLog> findForUserAndCategory(@Param("username") String username,
                                             @Param("category") Category category,
                                             @Param("from") LocalDate from,
                                             @Param("to") LocalDate to);
}
