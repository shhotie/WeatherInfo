package com.shhotie.controller;

import com.shhotie.pojo.WeatherInfo;
import com.shhotie.service.WeatherService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {
    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    // Endpoint to get weather information by pincode and date
    @GetMapping
    public ResponseEntity<WeatherInfo> getWeatherByPincodeAndDate(
            @RequestParam String pincode,
            @RequestParam String forDate) {

        // Call the service to get weather information
        WeatherInfo weatherInfo = weatherService.getWeatherByPincodeAndDate(pincode, forDate);

        // Return the weather information
        return ResponseEntity.ok(weatherInfo);
    }
}

