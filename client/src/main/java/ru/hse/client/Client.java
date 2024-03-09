package ru.hse.client;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.hse.client.app.ClientRunner;

@RequiredArgsConstructor
@SpringBootApplication
public class Client implements CommandLineRunner {
    private final ClientRunner runner;
    public static void main(String[] args) {
        SpringApplication.run(Client.class, args);
    }

    @Override
    public void run(String... args) {
        runner.run();
    }
}
