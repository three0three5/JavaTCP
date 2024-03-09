package ru.hse.vectorizer.tcp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.hse.vectorizer.utils.RetryExecutor;

import java.io.ObjectOutputStream;
import java.io.OutputStream;

@Slf4j
@RequiredArgsConstructor
@Component
public class ResponseCreator {
    private final RetryExecutor retryExecutor;
    public void performResponse(OutputStream out, String response, int delay, int attempts) {
        retryExecutor.executeWithRetries(attempts, delay,
                () -> {
                    ObjectOutputStream oos = new ObjectOutputStream(out);
                    oos.writeObject(response + "\n");
                    return true;
                }, "Couldn't write response.");
    }
}
