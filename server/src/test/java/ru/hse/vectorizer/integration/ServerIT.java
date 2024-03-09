package ru.hse.vectorizer.integration;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.hse.vectorizer.Server;
import ru.hse.vectorizer.app.ServerRunner;
import ru.hse.vectorizer.domain.JdbcConfiguration;
import ru.hse.vectorizer.domain.JdbcVectorRepository;
import ru.hse.vectorizer.domain.SimpleVectorRepository;
import ru.hse.vectorizer.domain.Vector;
import ru.hse.vectorizer.integration.domain.TestJdbcConfiguration;
import ru.hse.vectorizer.tcp.ConnectionHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.hse.vectorizer.utils.Constants.LOGIN_SYMBOLS;

@IntegrationTest
@Slf4j
@SpringBootTest(classes = {TestJdbcConfiguration.class,
        Server.class,
        JdbcVectorRepository.class})
public class ServerIT {
    @MockBean
    private JdbcConfiguration configuration;

    @Value("${server.port}")
    int port;

    @Value("${server.address}")
    String address;

    Thread app;
    ServerRunner runner;

    @Autowired
    ConnectionHandler handler;

    @Autowired
    SimpleVectorRepository repository;

    String login = new Random().ints(10, 0, LOGIN_SYMBOLS.length())
            .mapToObj(index -> LOGIN_SYMBOLS.charAt(index % LOGIN_SYMBOLS.length()))
            .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
            .toString();

    @BeforeEach
    void init() throws InterruptedException {
        // Приходится ждать, чтобы успеть освободить порт
        Thread.sleep(500);
        runner = new ServerRunner(handler);
        app = new Thread(() -> runner.run());
        app.start();
    }

    @AfterEach
    void cleanUp() {
        app.interrupt();
    }

    @Test
    void givenEmptyRepository_whenClientReads_thenReturnEmpty() throws IOException, ClassNotFoundException {
        String serverResponse;
        try (Socket clientSocket = new Socket(address, port)) {
            OutputStream outs = clientSocket.getOutputStream();
            InputStream ins = clientSocket.getInputStream();
            ObjectOutputStream out = new ObjectOutputStream(outs);
            out.writeObject(login);
            ObjectInputStream in = new ObjectInputStream(ins);
            String ignored = (String) in.readObject();
            // when
            out = new ObjectOutputStream(outs);
            out.writeObject("read");
            in = new ObjectInputStream(ins);
            serverResponse = (String) in.readObject();
        }
        // then
        assertNotNull(serverResponse);
        assertEquals("No vectors were found\n", serverResponse);
    }

    @Test
    void givenEmptyRepository_whenClientCreatesVector_thenVectorSaved() throws IOException, ClassNotFoundException {
        String serverResponse;
        try (Socket clientSocket = new Socket(address, port)) {
            OutputStream outs = clientSocket.getOutputStream();
            InputStream ins = clientSocket.getInputStream();
            ObjectOutputStream out = new ObjectOutputStream(outs);
            out.writeObject(login);
            ObjectInputStream in = new ObjectInputStream(ins);
            String ignored = (String) in.readObject();
            // when
            out = new ObjectOutputStream(outs);
            out.writeObject("create test 1 2 3");
            in = new ObjectInputStream(ins);
            serverResponse = (String) in.readObject();
        }
        // then
        assertNotNull(serverResponse);
        Optional<Vector> saved = repository.findByName(login, "test");
        assertTrue(saved.isPresent());
        assertEquals(1, saved.get().getX());
        assertEquals(2, saved.get().getY());
        assertEquals(3, saved.get().getZ());
    }

    @Test
    void givenExistingVectors_whenClientUpdatesVector_thenUpdated() throws IOException, ClassNotFoundException {
        repository.save(login, new Vector("test", 9, 2, 5));

        String serverResponse;
        try (Socket clientSocket = new Socket(address, port)) {
            OutputStream outs = clientSocket.getOutputStream();
            InputStream ins = clientSocket.getInputStream();
            ObjectOutputStream out = new ObjectOutputStream(outs);
            out.writeObject(login);
            ObjectInputStream in = new ObjectInputStream(ins);
            String ignored = (String) in.readObject();
            // when
            out = new ObjectOutputStream(outs);
            out.writeObject("create test 2 5 -7");
            in = new ObjectInputStream(ins);
            serverResponse = (String) in.readObject();
        }
        // then
        assertNotNull(serverResponse);
        Optional<Vector> saved = repository.findByName(login, "test");
        assertTrue(saved.isPresent());
        assertEquals(2, saved.get().getX());
        assertEquals(5, saved.get().getY());
        assertEquals(-7, saved.get().getZ());
    }
}
