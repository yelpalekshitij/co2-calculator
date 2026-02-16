package dev.yelpalekshitij.integrationTests;

import dev.yelpalekshitij.exception.ServiceException;
import dev.yelpalekshitij.model.TransportType;
import dev.yelpalekshitij.service.DistanceService;
import dev.yelpalekshitij.service.EmissionCalculator;
import dev.yelpalekshitij.service.HttpService;
import dev.yelpalekshitij.service.OpenRouteServiceClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.net.http.HttpResponse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class Co2CalculatorTest {
    private final HttpService httpService = mock(HttpService.class);
    private final OpenRouteServiceClient orsClient = new OpenRouteServiceClient(httpService, "token");
    private final DistanceService distanceService = new DistanceService(orsClient);
    private final EmissionCalculator calculator = new EmissionCalculator(distanceService);
    HttpResponse<String> mockGetResponse = mock(HttpResponse.class);
    HttpResponse<String> mockPostResponse = mock(HttpResponse.class);

    String getResponse = """
                {
                  "features": [
                    {
                      "geometry": {
                        "coordinates": [123.45, 456.78]
                      }
                    }
                  ]
                }
                """;
    String postResponse = """
                {
                  "distances": [[0, 100000], [100000, 0]]
                }
                """;

    @BeforeEach
    void setup() {
        when(httpService.get(anyString(), any())).thenReturn(mockGetResponse);
        when(httpService.post(anyString(), any(), any())).thenReturn(mockPostResponse);
        when(mockGetResponse.body()).thenReturn(getResponse);
        when(mockPostResponse.body()).thenReturn(postResponse);
    }

    @Test
    void testEmissionCalculationDieselCarMedium() throws ServiceException {
        // when
        double co2 = calculator.calculate("Berlin", "Hamburg", TransportType.DIESEL_CAR_MEDIUM);

        // then: distance 100000 km * 171 g/km = 17100000 g ≈ 17100.0 kg
        assertEquals(17100.0, co2);
    }

    @Test
    void testEmissionCalculationElectricCarSmall() throws ServiceException {
        // when
        double co2 = calculator.calculate("CityA", "CityB", TransportType.ELECTRIC_CAR_SMALL);

        // then: 100000 km * 50 g/km = 5000000 g = 5000.0 kg
        assertEquals(5000.0, co2);
    }

    @Test
    void testDistanceServiceThrowsOnInvalidJson() {
        // given: invalid ORS JSON
        when(mockPostResponse.body()).thenReturn("invalid json");

        // when and then
        assertThrows(ServiceException.class, () -> distanceService.getDistance("Berlin", "Hamburg"));

        verify(httpService, times(1)).post(anyString(), any(), any());
    }

    @Test
    void testOrsClientThrowsOnInvalidJson() {
        // given: invalid ORS JSON
        when(mockGetResponse.body()).thenReturn("invalid json");

        // when and then
        assertThrows(ServiceException.class, () -> orsClient.geocode("Berlin"));

        verify(httpService, times(1)).get(anyString(), any());
    }

    @Test
    void testEmissionCalculatorThrowsForUnknownVehicle() throws ServiceException {
        // when and then: unknown vehicle
        assertThrows(IllegalArgumentException.class,
                () -> calculator.calculate("Berlin", "Hamburg", TransportType.valueOf("flying-car")));
    }
}
