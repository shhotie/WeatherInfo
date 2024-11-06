package com.shhotie.service;

import com.shhotie.pojo.WeatherInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class OpenWeatherService {

    @Value("${openweather.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    // Endpoint for geocoding to get latitude and longitude
    private static final String GEOCODING_URL = "http://api.openweathermap.org/geo/1.0/zip";
    // Endpoint for weather data by latitude and longitude
    private static final String WEATHER_URL = "https://api.openweathermap.org/data/2.5/weather";

    // Method to get latitude and longitude from pincode
    public Optional<double[]> getLatLongFromPincode(String pincode) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(GEOCODING_URL)
                .queryParam("zip", pincode + ",IN") // "IN" for India, adjust as needed
                .queryParam("appid", apiKey);

        try {
            var response = restTemplate.getForObject(uriBuilder.toUriString(), Map.class);
            if (response != null && response.containsKey("lat") && response.containsKey("lon")) {
                double latitude = ((Number) response.get("lat")).doubleValue();
                double longitude = ((Number) response.get("lon")).doubleValue();
                return Optional.of(new double[]{latitude, longitude});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    // Method to get weather information by latitude, longitude, and date
    public Optional<WeatherInfo> getWeatherInfo(double latitude, double longitude, String date) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(WEATHER_URL)
                .queryParam("lat", latitude)
                .queryParam("lon", longitude)
                .queryParam("appid", apiKey)
                .queryParam("units", "metric"); // Optional: For temperature in Celsius

        try {
            var response = restTemplate.getForObject(uriBuilder.toUriString(), Map.class);
            if (response != null) {
                // Extract necessary fields from the response
                String description = ((Map<String, String>) ((List<?>) response.get("weather")).get(0)).get("description");
                double temp = ((Map<String, Number>) response.get("main")).get("temp").doubleValue();
                double humidity = ((Map<String, Number>) response.get("main")).get("humidity").doubleValue();

                WeatherInfo weatherInfo = new WeatherInfo();
                weatherInfo.setWeatherDescription(description);
                weatherInfo.setTemperature(temp);
                weatherInfo.setHumidity(humidity);
                weatherInfo.setForDate(date);

                return Optional.of(weatherInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
