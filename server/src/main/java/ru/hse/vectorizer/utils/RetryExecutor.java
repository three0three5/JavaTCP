package ru.hse.vectorizer.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.SocketTimeoutException;

@Slf4j
@Component
public class RetryExecutor {
    public boolean executeWithRetries(int attempts, int offset, ExceptionSupplier<Boolean> action, String message) {
        int currentAttempt = 0;
        while (currentAttempt < attempts) {
            try {
                if (action.get()) {
                    return true;
                }
            } catch (SocketTimeoutException e) {
                log.info("Timeout reached...");
            } catch (IOException e) {
                log.warn("Error while executing action: " + e.getMessage());
                try {
                    Thread.sleep(offset);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            } catch (Exception e) {
                log.error(e.getMessage());
                throw new RuntimeException(e);
            }
            currentAttempt++;
        }
        log.warn(message);
        return false;
    }
}
