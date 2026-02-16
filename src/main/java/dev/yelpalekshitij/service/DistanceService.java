package dev.yelpalekshitij.service;

import dev.yelpalekshitij.model.Coordinates;

public class DistanceService {

    private final OpenRouteServiceClient orsClient;

    public DistanceService(OpenRouteServiceClient orsClient) {
        this.orsClient = orsClient;
    }

    public double getDistance(String startCity, String endCity) {
        Coordinates from = orsClient.geocode(startCity);
        Coordinates to = orsClient.geocode(endCity);
        return orsClient.getDistanceKm(from, to);
    }
}

