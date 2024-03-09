package ru.hse.vectorizer.tcp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.hse.vectorizer.utils.RetryExecutor;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

@Slf4j
@RequiredArgsConstructor
@Component
public class RequestReader {
    private final RetryExecutor executor;
    public String[] readInputStream(InputStream is, int attempt, int offset) throws IOException {
        ObjectInputStream ois = new ObjectInputStream(is);
        var ref = new Object() {
            String[] tokens;
        };
        if (!executor.executeWithRetries(attempt, offset,
                () -> {
                    String request = (String) ois.readObject();
                    if (request == null) {
                        log.warn("The request is empty");
                        ref.tokens = new String[]{};
                        return true;
                    }
                    log.info("Handling request: " + request);
                    ref.tokens = request.split(" ");
                    return true;
                }, "Couldn't read anything from client.")) {
            return new String[]{};
        }
        return ref.tokens;
    }
}
