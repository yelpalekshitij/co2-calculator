package dev.yelpalekshitij.service;

import dev.yelpalekshitij.exception.ErrorCode;
import dev.yelpalekshitij.exception.ServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class HttpServiceTest {

    private final HttpClient mockClient = mock(HttpClient.class);
    private final HttpService httpService = new HttpService(mockClient);
    HttpResponse<String> mockResponse = mock(HttpResponse.class);

    @BeforeEach
    void setUp() {

    }

    @Test
    void testPostRetriesAndReturnsResponse() throws Exception {
        // given
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn("{\"ok\":true}");
        // simulate failures first, then success
        when(mockClient.send(any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
                .thenThrow(new RuntimeException("fail1"))
                .thenThrow(new RuntimeException("fail2"))
                .thenReturn(mockResponse);

        // when
        HttpResponse<String> response = httpService.post("http://google.com", Map.of(), "{\"test\":1}");

        // then
        assertEquals("{\"ok\":true}", response.body());
        // verify send called 3 times (retry)
        verify(mockClient, times(3)).send(any(), any());
    }

    @Test
    void testHttpFailureThrows() throws Exception {
        // given
        when(mockResponse.statusCode()).thenReturn(500);
        when(mockClient.send(any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
                .thenReturn(mockResponse);

        // when
        ServiceException ex = assertThrows(ServiceException.class,
                () -> httpService.post("http://google.com", Map.of(), "{}"));

        // then
        assertEquals(ErrorCode.RETRY_EXHAUSTED, ex.getErrorCode());
        assertEquals("HTTP 500", ex.getMessage());
    }
}
