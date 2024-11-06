package com.shhotie.repositories;

import com.shhotie.pojo.Pincode;
import com.shhotie.pojo.WeatherInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WeatherInfoRepository extends JpaRepository<WeatherInfo, Long> {
    Optional<WeatherInfo> findByPincodeAndForDate(Pincode pincode, String forDate);
}