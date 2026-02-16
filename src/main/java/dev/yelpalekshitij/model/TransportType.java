package dev.yelpalekshitij.model;

public enum TransportType {

    DIESEL_CAR_SMALL(142),
    PETROL_CAR_SMALL(154),
    ELECTRIC_CAR_SMALL(50),
    DIESEL_CAR_MEDIUM(171),
    PETROL_CAR_MEDIUM(192),
    ELECTRIC_CAR_MEDIUM(58),
    DIESEL_CAR_LARGE(209),
    PETROL_CAR_LARGE(282),
    ELECTRIC_CAR_LARGE(73),
    BUS_DEFAULT(27),
    TRAIN_DEFAULT(6);

    private final int gramsPerKm;

    TransportType(int gramsPerKm) {
        this.gramsPerKm = gramsPerKm;
    }

    public double calculateKg(double distanceKm) {
        return (distanceKm * gramsPerKm) / 1000.0;
    }

    public static TransportType fromCli(String input) {
        return valueOf(input.toUpperCase().replace("-", "_"));
    }
}
