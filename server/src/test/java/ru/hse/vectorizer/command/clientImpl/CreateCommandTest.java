package ru.hse.vectorizer.command.clientImpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.hse.vectorizer.domain.SimpleVectorRepository;
import ru.hse.vectorizer.domain.Vector;

import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static ru.hse.vectorizer.utils.Constants.LOGIN_SYMBOLS;


@ExtendWith(MockitoExtension.class)
class CreateCommandTest {
    @Mock
    SimpleVectorRepository repository;

    @InjectMocks
    CreateCommand command;

    String login = new Random().ints(10, 0, LOGIN_SYMBOLS.length())
            .mapToObj(index -> LOGIN_SYMBOLS.charAt(index % LOGIN_SYMBOLS.length()))
            .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
            .toString();


    @Test
    void givenNotSuitableTokens_whenProcess_thenReturnNull() {
        // given
        String[] tokens = new String[]{"invalid", "tokens"};
        // when
        String result = command.process(tokens, login);
        // then
        assertNull(result);

        // given
        tokens = new String[]{"create", "tokens", "1", "2.0", "o"};
        // when
        result = command.process(tokens, login);
        // then
        assertNull(result);
    }

    @Test
    void givenValidTokens_whenProcessAndSaved_thenReturnSuccess() {
        // given
        String[] tokens = new String[]{"create", "Testttt", "-5.5", "0", "2"};
        // when
        Vector inserted = new Vector("Testttt", -5.5, 0, 2);
        when(repository.save(login, inserted)).thenReturn(Optional.of(inserted));
        String result = command.process(tokens, login);
        // then
        assertEquals("Vector has been saved", result);
    }

    @Test
    void givenValidTokens_whenProcessAndNotSaved_thenReturnFailure() {
        // given
        String[] tokens = new String[]{"create", "vEctor", "123", "-130.3", "14"};
        // when
        when(repository.save(eq(login), any(Vector.class))).thenReturn(Optional.empty());
        String result = command.process(tokens, login);
        // then
        assertEquals("Vector has not been saved.", result);
    }
}