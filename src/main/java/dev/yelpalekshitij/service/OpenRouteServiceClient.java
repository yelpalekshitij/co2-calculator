package dev.yelpalekshitij.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.yelpalekshitij.exception.ErrorCode;
import dev.yelpalekshitij.exception.ServiceException;
import dev.yelpalekshitij.model.Coordinates;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class OpenRouteServiceClient {

    private static final String GEOCODE_URL = "https://api.openrouteservice.org/geocode/search";
    private static final String MATRIX_URL = "https://api.openrouteservice.org/v2/matrix/driving-car";

    private final HttpService httpService;
    private final String apiKey;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public OpenRouteServiceClient(HttpService httpService, String apiKey) {
        this.httpService = httpService;
        if (apiKey == null || apiKey.isEmpty()) {
            throw new ServiceException(ErrorCode.CONFIGURATION_ERROR, "ORS_TOKEN environment variable is not set");
        }
        this.apiKey = apiKey;
    }

    public Coordinates geocode(String city) {
        try {
            String url = GEOCODE_URL +
                    "?api_key=" + URLEncoder.encode(apiKey, StandardCharsets.UTF_8) +
                    "&text=" + URLEncoder.encode(city, StandardCharsets.UTF_8) +
                    "&layers=locality";

            var response = httpService.get(url, Map.of());
            JsonNode root = objectMapper.readTree(response.body());
            JsonNode features = root.path("features");
            if (!features.isArray() || features.isEmpty()) {
                throw new ServiceException(ErrorCode.API_FAILURE, "City not found: " + city);
            }
            JsonNode coords = features.get(0).path("geometry").path("coordinates");
            double lon = coords.get(0).asDouble();
            double lat = coords.get(1).asDouble();
            return new Coordinates(lat, lon);
        } catch (Exception e) {
            throw new ServiceException(ErrorCode.API_FAILURE, "Failed to geocode city " + city + ": " + e.getMessage());
        }
    }

    public double getDistanceKm(Coordinates from, Coordinates to) {
        try {
            // OpenRouteService matrix endpoint expects JSON body
            String body = objectMapper.writeValueAsString(Map.of(
                    "locations", new double[][]{
                            {from.longitude(), from.latitude()},
                            {to.longitude(), to.latitude()}
                    },
                    "metrics", new String[]{"distance"},
                    "units", "km"
            ));

            var response = httpService.post(
                    MATRIX_URL,
                    Map.of("Authorization", apiKey, "Content-Type", "application/json"),
                    body
            );

            JsonNode root = objectMapper.readTree(response.body());
            JsonNode distances = root.path("distances");
            if (!distances.isArray() || distances.size() < 2) {
                throw new ServiceException(ErrorCode.API_FAILURE, "Invalid distance matrix");
            }
            return distances.get(0).get(1).asDouble();
        } catch (Exception e) {
            throw new ServiceException(ErrorCode.API_FAILURE, "Failed to get distance: " + e.getMessage());
        }
    }
}
