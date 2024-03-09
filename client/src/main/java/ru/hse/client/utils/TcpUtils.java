package ru.hse.client.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

@RequiredArgsConstructor
@Component
public class TcpUtils {
    private final RetryExecutor executor;

    @Value("${client.attempts}")
    private final Integer attempts;

    @Value("${client.read.timeout}")
    private final Integer readTimeout;

    @Value("${client.write.delay}")
    private final Integer writeDelay;

    public boolean printServerResponse(InputStream is) {
        return executor.executeWithRetries(attempts, readTimeout,
                () -> {
                    ObjectInputStream ois = new ObjectInputStream(is);
                    String receivedData = (String) ois.readObject();
                    System.out.println(receivedData);
                    return true;
                }, "Could not get response from server.");
    }

    public boolean wroteSuccessfully(OutputStream out, String request) {
        return executor.executeWithRetries(attempts, writeDelay,
                () -> {
                    ObjectOutputStream oos = new ObjectOutputStream(out);
                    oos.writeObject(request);
                    return true;
                }, "Server is unreachable. Please, try again later." +
                        "If you want to try to reconnect, write your login.");
    }
}
