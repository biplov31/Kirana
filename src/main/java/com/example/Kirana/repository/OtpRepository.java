package com.example.Kirana.repository;

import com.example.Kirana.model.KiranaOtp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<KiranaOtp, Integer> {

    Optional<KiranaOtp> findOtpByKiranaUserId(Integer userId);

}
