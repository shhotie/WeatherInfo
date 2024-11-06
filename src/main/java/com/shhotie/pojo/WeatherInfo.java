package com.shhotie.pojo;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class WeatherInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Pincode pincode;

    private String forDate;
    private String weatherDescription;
    private double temperature;
    private double humidity;
    private LocalDateTime lastUpdated; // New field to store the time of last update

}
