package com.example.Kirana.repository;

import com.example.Kirana.model.KiranaUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KiranaUserRepository extends JpaRepository<KiranaUser, Integer> {

    Optional<KiranaUser> findByEmail(String email);

}
