package com.SRDCHW2.repository;


import com.SRDCHW2.models.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUsername(String username);
    List<User> findByAdmin(boolean admin);

    @Transactional
    void deleteByUsername(String username);

    @Query(value = "SELECT * FROM users u WHERE " +
            "(:field = 'username' AND LOWER(u.username) LIKE LOWER(CONCAT('%', :value, '%'))) OR " +
            "(:field = 'name' AND LOWER(u.name) LIKE LOWER(CONCAT('%', :value, '%'))) OR " +
            "(:field = 'surname' AND LOWER(u.surname) LIKE LOWER(CONCAT('%', :value, '%'))) OR " +
            "(:field = 'email' AND LOWER(u.email) LIKE LOWER(CONCAT('%', :value, '%'))) OR " +
            "(:field = 'address' AND LOWER(u.address) LIKE LOWER(CONCAT('%', :value, '%'))) OR " +
            "(:field = 'gender' AND LOWER(u.gender) = LOWER(:value)) OR " +
            "(:field = 'birthdate' AND CAST(u.birthdate AS CHAR) LIKE CONCAT('%', :value, '%')) OR " +
            "(:field = 'admin' AND ((u.admin = TRUE AND LOWER(:value) = 'admin') OR (u.admin = FALSE AND LOWER(:value) = 'user')))",
            countQuery = "SELECT count(*) FROM users u WHERE " +
                    "(:field = 'username' AND LOWER(u.username) LIKE LOWER(CONCAT('%', :value, '%'))) OR " +
                    "(:field = 'name' AND LOWER(u.name) LIKE LOWER(CONCAT('%', :value, '%'))) OR " +
                    "(:field = 'surname' AND LOWER(u.surname) LIKE LOWER(CONCAT('%', :value, '%'))) OR " +
                    "(:field = 'email' AND LOWER(u.email) LIKE LOWER(CONCAT('%', :value, '%'))) OR " +
                    "(:field = 'address' AND LOWER(u.address) LIKE LOWER(CONCAT('%', :value, '%'))) OR " +
                    "(:field = 'gender' AND LOWER(u.gender) = LOWER(:value)) OR " +
                    "(:field = 'birthdate' AND CAST(u.birthdate AS CHAR) LIKE CONCAT('%', :value, '%')) OR " +
                    "(:field = 'admin' AND ((u.admin = TRUE AND LOWER(:value) = 'admin') OR (u.admin = FALSE AND LOWER(:value) = 'user')))",
            nativeQuery = true)
    Page<User> findUsersByFieldAndValue(
            @Param("field") String field,
            @Param("value") String value,
            Pageable pageable
    );
}
