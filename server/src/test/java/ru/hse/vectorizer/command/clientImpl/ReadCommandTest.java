package ru.hse.vectorizer.command.clientImpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.hse.vectorizer.domain.SimpleVectorRepository;
import ru.hse.vectorizer.domain.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;
import static ru.hse.vectorizer.utils.Constants.LOGIN_SYMBOLS;

@ExtendWith(MockitoExtension.class)
class ReadCommandTest {
    @Mock
    SimpleVectorRepository repository;

    @InjectMocks
    ReadCommand command;

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
    void givenValidTokens_whenProcessAndNoVectorsExist_thenReturnNoVectorsMessage() {
        // given
        String[] tokens = new String[]{"read"};

        // when
        when(repository.numberOfRows(login)).thenReturn(0);
        String result = command.process(tokens, login);

        // then
        assertEquals("No vectors were found", result);
    }

    @Test
    void givenValidTokens_whenProcessAndVectorsExist_thenReturnVectorList() {
        // given
        String[] tokens = new String[]{"read"};
        List<Vector> vectors = new ArrayList<>();
        vectors.add(new Vector("Vector1", 1.0, 2.0, 3.0));
        vectors.add(new Vector("Vector2", 4.0, 5.0, 6.0));

        // when
        when(repository.numberOfRows(login)).thenReturn(2);
        when(repository.findAll(login)).thenReturn(vectors);
        String result = command.process(tokens, login);

        // then
        StringBuilder expectedResult = new StringBuilder("Created vectors: ");
        vectors.forEach(vector -> {
            expectedResult.append("\n");
            expectedResult.append(vector);
        });
        assertEquals(expectedResult.toString(), result);
    }
}
