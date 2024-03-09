package ru.hse.vectorizer.tcp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.hse.vectorizer.command.CommandExecutor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@RequiredArgsConstructor
@Component
public class ClientHandler {
    private final RequestReader reader;
    private final ResponseCreator creator;
    private final CommandExecutor commandExecutor;
    private final LoginGetter loginGetter;

    @Value("${server.read_timeout}")
    private final Integer readTimeout;

    @Value("${server.write_delay}")
    private final Integer writeDelay;

    @Value("${server.attempts}")
    private final Integer attempts;

    private static final Map<Socket, String> clients = new ConcurrentHashMap<>();

    private static AtomicBoolean isRunning;

    public static void setIsRunning(AtomicBoolean other) {
        isRunning = other;
    }

    public void handleClient(Socket sock) {
        log.info("Trying to get login");
        Optional<String> optionalLogin = loginGetter.get(sock);
        if (optionalLogin.isEmpty()) {
            log.info("Could not get login from " + sock.getPort());
            closeEverything(sock);
            return;
        }
        String login = optionalLogin.get();
        clients.put(sock, login);
        log.info("New client: " + sock.getPort());
        try {
            sock.setSoTimeout(readTimeout);
        } catch (SocketException e) {
            log.error("Error with socket: " + e.getMessage());
            return;
        }
        perform(sock);
        closeEverything(sock);
    }

    private void perform(Socket sock) {
        while (isRunning.get()) {
            InputStream is;
            OutputStream out;
            try {
                is = sock.getInputStream();
                out = sock.getOutputStream();
            } catch (IOException e) {
                log.error("Failed to handle client: " + e.getMessage());
                break;
            }
            String[] tokens;
            try {
                tokens = reader.readInputStream(is, attempts, readTimeout);
            } catch (IOException e) {
                log.warn("Socket is dead: " + e.getMessage());
                break;
            }
            if (tokens.length == 0) {
                continue;
            }
            String response = commandExecutor.performCommand(tokens, clients.get(sock));
            if (response == null) {
                creator.performResponse(out, "Invalid command", writeDelay, attempts);
            }
            creator.performResponse(out, response, writeDelay, attempts);
        }
    }

    private void closeEverything(Socket sock) {
        OutputStream out = null;
        InputStream is = null;
        try {
            out = sock.getOutputStream();
            is = sock.getInputStream();
        } catch (IOException ignored) {
        }
        closeStreams(is, out);
        try {
            if (!sock.isClosed()) {
                sock.close();
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        clients.remove(sock);
    }

    private void closeStreams(InputStream is, OutputStream out) {
        try {
            if (out != null) {
                out.close();
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        try {
            if (is != null) {
                is.close();
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public void closeAllSockets() {
        for (Socket s : clients.keySet()) {
            try {
                s.close();
            } catch (IOException e) {
                log.error("Failed to close socket");
            }
        }
    }
}
