package dev.yelpalekshitij.util;

import dev.yelpalekshitij.exception.ErrorCode;
import dev.yelpalekshitij.exception.ServiceException;

public class RetryExecutor {

    public static <T> T execute(
            int maxAttempts,
            long initialDelayMs,
            Retryable<T> action
    ) {
        long delay = initialDelayMs;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                return action.run();
            } catch (ServiceException e) {
                if (attempt == maxAttempts) {
                    throw new ServiceException(ErrorCode.RETRY_EXHAUSTED, e.getMessage());
                }

                sleep(delay);
                delay *= 2;
            }
        }
        throw new IllegalStateException();
    }

    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @FunctionalInterface
    public interface Retryable<T> {
        T run();
    }
}
