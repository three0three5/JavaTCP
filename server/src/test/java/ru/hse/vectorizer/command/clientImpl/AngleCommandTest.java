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
class AngleCommandTest {
    @Mock
    SimpleVectorRepository repository;

    @InjectMocks
    AngleCommand command;

    String login = new Random().ints(10, 0, LOGIN_SYMBOLS.length())
            .mapToObj(index -> LOGIN_SYMBOLS.charAt(index % LOGIN_SYMBOLS.length()))
            .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
            .toString();


    @Test
    void givenInvalidTokens_whenProcess_thenReturnNull() {
        // given
        String[] tokens = new String[]{"qe", "f", "a"};
        // when
        String result = command.process(tokens, login);
        // then
        assertNull(result);

        // given
        tokens = new String[]{"angle", "f"};
        // when
        result = command.process(tokens, login);
        // then
        assertNull(result);
    }

    @Test
    void givenNonExistingFirstVector_whenProcess_thenReturnVectorNotFound() {
        // given
        String first = "first";
        String second = "second";
        when(repository.findByName(login, first)).thenReturn(Optional.empty());
        when(repository.findByName(login, second)).thenReturn(Optional.empty());
        // when
        String result = command.process(new String[]{"angle", first, second}, login);
        // then
        assertEquals("Vector " + first + " not found.", result);
    }

    @Test
    void givenNonExistingSecondVector_whenProcess_thenReturnVectorNotFound() {
        // given
        String first = "first";
        String second = "second";
        Vector firstVec = new Vector(first, 0, 1, 2);
        when(repository.findByName(login, first)).thenReturn(Optional.of(firstVec));
        when(repository.findByName(login, second)).thenReturn(Optional.empty());
        // when
        String result = command.process(new String[]{"angle", first, second}, login);
        // then
        assertEquals("Vector " + second + " not found.", result);
    }

    @Test
    void givenExistingVectors_whenProcess_thenReturnAngle() {
        // given
        String first = "first";
        String second = "second";
        Vector firstVec = new Vector(first, 5, 1, 2);
        Vector secondVec = new Vector(second, -3, 1, -2);
        when(repository.findByName(login, first)).thenReturn(Optional.of(firstVec));
        when(repository.findByName(login, second)).thenReturn(Optional.of(secondVec));
        // when
        String result = command.process(new String[]{"angle", first, second}, login);
        // then
        assertEquals("Angle (in radians) equals: 2.6431122164523604", result);
    }
}