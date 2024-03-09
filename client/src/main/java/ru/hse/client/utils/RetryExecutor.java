package ru.hse.client.utils;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.SocketTimeoutException;

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
                System.out.println("Timeout reached...");
            } catch (IOException e) {
                System.out.println("Error while executing action: " + e.getMessage());
                try {
                    Thread.sleep(offset);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                throw new RuntimeException(e);
            }
            currentAttempt++;
        }
        System.out.println(message);
        return false;
    }
}
