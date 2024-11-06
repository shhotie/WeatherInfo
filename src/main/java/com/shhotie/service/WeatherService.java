package com.shhotie.service;

import com.shhotie.pojo.Pincode;
import com.shhotie.pojo.WeatherInfo;
import com.shhotie.repositories.PincodeRepository;
import com.shhotie.repositories.WeatherInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class WeatherService {

    private final PincodeRepository pincodeRepository;
    private final WeatherInfoRepository weatherInfoRepository;
    private final OpenWeatherService openWeatherService;

    @Autowired
    public WeatherService(PincodeRepository pincodeRepository, WeatherInfoRepository weatherInfoRepository, OpenWeatherService openWeatherService) {
        this.pincodeRepository = pincodeRepository;
        this.weatherInfoRepository = weatherInfoRepository;
        this.openWeatherService = openWeatherService;
    }

    public WeatherInfo getWeatherByPincodeAndDate(String pincode, String forDate) {
        Optional<Pincode> pincodeOptional = pincodeRepository.findByPincode(pincode);

        if (pincodeOptional.isPresent()) {
            Pincode savedPincode = pincodeOptional.get();
            Optional<WeatherInfo> weatherInfoOptional = weatherInfoRepository.findByPincodeAndForDate(savedPincode, forDate);

            if (weatherInfoOptional.isPresent()) {
                WeatherInfo existingWeatherInfo = weatherInfoOptional.get();

                // Check if data is still valid (for this example, data is valid if it's from the same day)
                LocalDate lastUpdatedDate = existingWeatherInfo.getLastUpdated().toLocalDate();
                LocalDate requestDate = LocalDate.parse(forDate);

                if (!lastUpdatedDate.isBefore(requestDate)) {
                    return existingWeatherInfo; // Return cached data if it's valid
                }
            }
        }

        // If no valid data is found, fetch new data and update the database
        Pincode pincodeEntity = pincodeOptional.orElseGet(() -> {
            double[] latLong = openWeatherService.getLatLongFromPincode(pincode)
                    .orElseThrow(() -> new RuntimeException("Could not retrieve lat/long for pincode: " + pincode));
            Pincode newPincode = new Pincode();
            newPincode.setPincode(pincode);
            newPincode.setLatitude(latLong[0]);
            newPincode.setLongitude(latLong[1]);
            return pincodeRepository.save(newPincode);
        });

        double latitude = pincodeEntity.getLatitude();
        double longitude = pincodeEntity.getLongitude();

        WeatherInfo newWeatherInfo = openWeatherService.getWeatherInfo(latitude, longitude, forDate)
                .orElseThrow(() -> new RuntimeException("Could not retrieve weather info for pincode: " + pincode));

        newWeatherInfo.setPincode(pincodeEntity);
        newWeatherInfo.setForDate(forDate);
        newWeatherInfo.setLastUpdated(LocalDateTime.now()); // Update lastUpdated time

        return weatherInfoRepository.save(newWeatherInfo);
    }
}

