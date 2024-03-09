package ru.hse.client.app;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.hse.client.utils.RetryExecutor;
import ru.hse.client.utils.TcpUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

import static ru.hse.client.utils.Constants.GREETING;
import static ru.hse.client.utils.Constants.HELP;

@RequiredArgsConstructor
@Service
public class ClientRunner implements Runnable {
    private final RetryExecutor retryExecutor;
    private final TcpUtils utils;
    private Socket clientSocket;

    @Value("${client.attempts}")
    private final Integer attempts;

    @Value("${client.read.timeout}")
    private final Integer readTimeout;

    @Value("${client.write.delay}")
    private final Integer writeDelay;

    @Value("${server.address}")
    private final String serverAddress;

    @Value("${server.port}")
    private final Integer serverPort;

    @Override
    public void run() {
        System.out.println(GREETING);
        System.out.println(HELP);
        handleConnection();
    }

    private void handleConnection() {
        Scanner sc = new Scanner(System.in);
        InputStream is = null;
        OutputStream out = null;
        while (true) {
            String request = sc.nextLine();
            if ("exit".equalsIgnoreCase(request)) {
                System.out.println("Shutting down...");
                break;
            }
            if (!updateSocket(serverAddress, serverPort)) {
                System.out.println("Could not reconnect to the server. Try again later.");
                continue;
            }
            try {
                is = clientSocket.getInputStream();
                out = clientSocket.getOutputStream();
                if (!utils.wroteSuccessfully(out, request)) {
                    clientSocket = null;
                    continue;
                }
                if (!utils.printServerResponse(is)) {
                    clientSocket = null;
                }
            } catch (IOException e) {
                System.out.println("Error: " + e.getMessage());
                clientSocket = null;
                try {
                    Thread.sleep(writeDelay);
                } catch (InterruptedException ex) {
                    System.out.println("Error: " + ex.getMessage());
                    break;
                }
            }
        }
        closeEverything(is, out, clientSocket);
    }

    private void closeEverything(InputStream is, OutputStream out, Socket clientSocket) {
        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {
                System.out.println("Error: Could not close input stream.");
            }
        }
        if (out != null) {
            try {
                out.close();
            } catch (IOException e) {
                System.out.println("Error: Could not close output stream.");
            }
        }
        if (clientSocket != null) {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.out.println("Error: Could not close socket.");
            }
        }
    }

    private boolean updateSocket(String serverAddress, int serverPort) {
        if (clientSocket != null) {
            return true;
        }
        if (!retryExecutor.executeWithRetries(attempts, writeDelay,
                () -> {
                    clientSocket = new Socket(serverAddress, serverPort);
                    clientSocket.setSoTimeout(readTimeout);
                    return true;
                }, "Could not update socket")) {
            clientSocket = null;
            return false;
        }
        return true;
    }
}
