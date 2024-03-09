package ru.hse.vectorizer.app;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import ru.hse.vectorizer.tcp.ConnectionHandler;

import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

import static ru.hse.vectorizer.utils.Constants.GREETING;
import static ru.hse.vectorizer.utils.Constants.HELP;

@Slf4j
@Service
@RequiredArgsConstructor
@Profile("!test")
public class ServerRunner implements CommandLineRunner {
    private final ConnectionHandler handler;
    private final AtomicBoolean isRunning = new AtomicBoolean(true);

    @Override
    public void run(String... args) {
        (new Thread(this::handleClients)).start();
        handleCommands();
    }

    private void handleCommands() {
        System.out.println(GREETING);
        System.out.println(HELP);
        Scanner sc = new Scanner(System.in);
        while (true) {
            String str = sc.nextLine();
            if (isExitRequest(str)) {
                performExitRequest();
                break;
            }
        }
    }

    private void handleClients() {
        handler.handleConnections(isRunning);
    }

    private void performExitRequest() {
        log.info("Shutting down...");
        isRunning.set(false);
    }
    private static boolean isExitRequest(String token) {
        return "exit".equalsIgnoreCase(token);
    }
}
