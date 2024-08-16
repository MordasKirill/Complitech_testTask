package com.complitech.demo.repository;

import com.complitech.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByLogin(String login);

    @Modifying
    @Transactional
    @Query(value = "call delete_users_in_range(:start_id, :end_id)", nativeQuery = true)
    void deleteUsersInRange(@Param("start_id") Long startId, @Param("end_id") Long endId);

}
