package dev.yelpalekshitij;

import dev.yelpalekshitij.cli.ArgumentParser;
import dev.yelpalekshitij.exception.ServiceException;
import dev.yelpalekshitij.model.TransportType;
import dev.yelpalekshitij.service.DistanceService;
import dev.yelpalekshitij.service.EmissionCalculator;
import dev.yelpalekshitij.service.HttpService;
import dev.yelpalekshitij.service.OpenRouteServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.http.HttpClient;

public class Co2CaculatorApp {

    private static final Logger logger = LoggerFactory.getLogger(Co2CaculatorApp.class);

    private static final int EXIT_INVALID_ARGUMENT = 2;
    private static final int EXIT_SERVICE_FAILURE = 3;
    private static final int EXIT_UNEXPECTED = 1;

    public static void main(String[] args) {
        try {
            var params = ArgumentParser.parse(args);

            var start = params.get("start");
            var end = params.get("end");
            var transport = params.get("transportation-method");

            var type = TransportType.fromCli(transport);


            var httpService = new HttpService(HttpClient.newBuilder().build());
            var ors = new OpenRouteServiceClient(httpService, System.getenv("ORS_TOKEN"));
            var distanceService = new DistanceService(ors);
            var calculator = new EmissionCalculator(distanceService);

            var result = calculator.calculate(start, end, type);

            System.out.printf("Your trip caused %.1fkg of CO2-equivalent.%n", result);

        } catch (IllegalArgumentException e) {
            System.err.println("Argument error: " + e.getMessage());
            System.exit(EXIT_INVALID_ARGUMENT);
        } catch (ServiceException e) {
            System.err.println("Service error [" + e.getErrorCode() + "]: " + e.getMessage());
            System.exit(EXIT_SERVICE_FAILURE);
        } catch (Exception e) {
            logger.error("Unexpected error", e);
            System.err.println("Unexpected error occurred.");
            System.exit(EXIT_UNEXPECTED);
        }
    }
}
