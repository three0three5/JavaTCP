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
import static org.mockito.Mockito.when;
import static ru.hse.vectorizer.utils.Constants.LOGIN_SYMBOLS;

@ExtendWith(MockitoExtension.class)
class RangeCommandTest {
    @Mock
    SimpleVectorRepository repository;

    @InjectMocks
    RangeCommand command;

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
    }

    @Test
    void givenValidTokens_whenProcessAndVectorExists_thenReturnRangeResult() {
        // given
        String[] tokens = new String[]{"range", "Vector1"};
        Vector vector1 = new Vector("Vector1", 1.0, 2.0, 3.0);

        // when
        when(repository.findByName(login, "Vector1")).thenReturn(Optional.of(vector1));
        String result = command.process(tokens, login);

        // then=
        assertEquals("Range of vector is: 3.7416573867739413", result);
    }

    @Test
    void givenValidTokens_whenProcessAndVectorNotFound_thenReturnNotFoundMessage() {
        // given
        String[] tokens = new String[]{"range", "NonExistentVector"};

        // when
        when(repository.findByName(login, "NonExistentVector")).thenReturn(Optional.empty());
        String result = command.process(tokens, login);

        // then
        assertEquals("Vector NonExistentVector not found.", result);
    }
}
