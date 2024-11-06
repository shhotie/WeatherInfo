package com.shhotie.repositories;

import com.shhotie.pojo.Pincode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PincodeRepository extends JpaRepository<Pincode, Long> {
    Optional<Pincode> findByPincode(String pincode);
}
