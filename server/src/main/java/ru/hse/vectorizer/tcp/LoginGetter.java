package ru.hse.vectorizer.tcp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Optional;

import static ru.hse.vectorizer.utils.Constants.CORRECT_LOGIN;
import static ru.hse.vectorizer.utils.Constants.WRONG_LOGIN;

@Component
@Slf4j
@RequiredArgsConstructor
public class LoginGetter {
    private final RequestReader reader;
    private final ResponseCreator creator;
    @Value("${server.read_timeout}")
    private final Integer readTimeout;

    @Value("${server.write_delay}")
    private final Integer writeDelay;

    @Value("${server.attempts}")
    private final Integer attempts;
    public Optional<String> get(Socket sock) {
        InputStream is;
        OutputStream out;
        String[] result;
        try {
            is = sock.getInputStream();
            out = sock.getOutputStream();
            result = reader.readInputStream(is, attempts, readTimeout);
            if (result.length != 1 || !isAppropriate(result[0])) {
                creator.performResponse(out, WRONG_LOGIN, writeDelay, attempts);
                return Optional.empty();
            }
            creator.performResponse(out, CORRECT_LOGIN, writeDelay, attempts);
        } catch (IOException e) {
            log.error("Failed to fetch login: " + e.getMessage());
            return Optional.empty();
        }
        return Optional.of(result[0]);
    }

    private boolean isAppropriate(String login) {
        for (char i : login.toCharArray()) {
            if (!Character.isLetterOrDigit(i)) {
                return false;
            }
        }
        return login.length() <= 15 && login.length() >= 3;
    }
}
