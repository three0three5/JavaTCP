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
class ProductCommandTest {
    @Mock
    SimpleVectorRepository repository;

    @InjectMocks
    ProductCommand command;

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
        tokens = new String[]{"product", "invalid", "tokens"};

        // when
        result = command.process(tokens, login);

        // then
        assertNull(result);
    }

    @Test
    void givenValidTokens_whenProcessAndVectorsExistAndDotProduct_thenReturnDotResult() {
        // given
        String[] tokens = new String[]{"product", "dot", "Vector1", "Vector2"};
        Vector vector1 = new Vector("Vector1", 1.0, 2.0, 3.0);
        Vector vector2 = new Vector("Vector2", 4.0, 5.0, 6.0);

        // when
        when(repository.findByName(login, "Vector1")).thenReturn(Optional.of(vector1));
        when(repository.findByName(login, "Vector2")).thenReturn(Optional.of(vector2));
        String result = command.process(tokens, login);

        // then
        assertEquals("Dot result is: 32.0", result);
    }

    @Test
    void givenValidTokens_whenProcessAndVectorsExistAndCrossProduct_thenReturnCrossResult() {
        // given
        String[] tokens = new String[]{"product", "cross", "Vector3", "Vector4"};
        Vector vector3 = new Vector("Vector3", 1.0, 2.0, 3.0);
        Vector vector4 = new Vector("Vector4", 4.0, 5.0, 6.0);

        // when
        when(repository.findByName(login, "Vector3")).thenReturn(Optional.of(vector3));
        when(repository.findByName(login, "Vector4")).thenReturn(Optional.of(vector4));
        String result = command.process(tokens, login);

        // then
        assertEquals("Vector result is: " + new Vector("", -3, 6, -3), result);
    }

    @Test
    void givenValidTokens_whenProcessAndVector1NotFound_thenReturnNotFoundMessage() {
        // given
        String[] tokens = new String[]{"product", "dot", "NonExistentVector", "Vector2"};

        // when
        when(repository.findByName(login, "NonExistentVector")).thenReturn(Optional.empty());
        String result = command.process(tokens, login);

        // then
        assertEquals("Vector NonExistentVector not found", result);
    }

    @Test
    void givenValidTokens_whenProcessAndVector2NotFound_thenReturnNotFoundMessage() {
        // given
        String[] tokens = new String[]{"product", "dot", "Vector1", "NonExistentVector"};

        // when
        Vector vector1 = new Vector("Vector1", 1.0, 2.0, 3.0);
        when(repository.findByName(login, "Vector1")).thenReturn(Optional.of(vector1));
        when(repository.findByName(login, "NonExistentVector")).thenReturn(Optional.empty());
        String result = command.process(tokens, login);

        // then
        assertEquals("Vector NonExistentVector not found", result);
    }

    @Test
    void givenValidTokens_whenProcessAndInvalidProductType_thenReturnNull() {
        // given
        String[] tokens = new String[]{"product", "invalid", "Vector1", "Vector2"};

        // when
        String result = command.process(tokens, login);

        // then
        assertNull(result);
    }
}
