package dev.yelpalekshitij.service;

import dev.yelpalekshitij.model.TransportType;

public class EmissionCalculator {

    private final DistanceService distanceService;

    public EmissionCalculator(DistanceService distanceService) {
        this.distanceService = distanceService;
    }

    public double calculate(String startCity, String endCity, TransportType type) {
        double distanceKm = distanceService.getDistance(endCity, startCity);
        return type.calculateKg(distanceKm);
    }
}

