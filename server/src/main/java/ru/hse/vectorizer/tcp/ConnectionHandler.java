package ru.hse.vectorizer.tcp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConnectionHandler {
    private final ClientHandler clientHandler;

    @Value("${server.port}")
    private final Integer serverPort;

    @Value("${server.pool.size}")
    private final Integer connectionPoolSize;

    public void handleConnections(AtomicBoolean isRunning) {
        ClientHandler.setIsRunning(isRunning);
        ExecutorService connectionPool = Executors.newFixedThreadPool(connectionPoolSize);
        try (ServerSocket serv = new ServerSocket(serverPort)) {
            log.info("Server started listening at port: " + serv.getLocalPort());
            serv.setSoTimeout(10);
            while (isRunning.get()) {
                try {
                    Socket sock = serv.accept();
                    connectionPool.execute(() -> clientHandler.handleClient(sock));
                } catch (SocketTimeoutException ignored) {
                } catch (IOException e) {
                    log.error("Failed to accept connection: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            log.error("Exception: " + e.getMessage());
        } finally {
            log.info("Closing sockets...");
            closeAllSockets();
        }
        connectionPool.shutdown();
    }

    private void closeAllSockets() {
        clientHandler.closeAllSockets();
    }
}
