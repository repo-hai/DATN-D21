package com.DATN.Bej.repository;

import com.DATN.Bej.entity.identity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByPhoneNumber(String phoneNumber);
    Optional<User> findByPhoneNumber(String phoneNumber);
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);

    List<User> findDistinctByRoles_NameIn(List<String> roleNames);

}

