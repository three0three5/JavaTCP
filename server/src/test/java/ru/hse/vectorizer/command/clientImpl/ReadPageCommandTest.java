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
class ReadPageCommandTest {
    @Mock
    SimpleVectorRepository repository;

    @InjectMocks
    ReadPageCommand command;

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
    void givenValidTokens_whenProcessAndInvalidPageSize_thenReturnNull() {
        // given
        String[] tokens = new String[]{"read", "invalid", "2"};

        // when
        String result = command.process(tokens, login);

        // then
        assertNull(result);
    }

    @Test
    void givenValidTokens_whenProcessAndInvalidPageNum_thenReturnNull() {
        // given
        String[] tokens = new String[]{"read", "2", "invalid"};

        // when
        String result = command.process(tokens, login);

        // then
        assertNull(result);
    }

    @Test
    void givenValidTokens_whenProcessAndEmptyPage_thenReturnEmptyPageMessage() {
        // given
        String[] tokens = new String[]{"read", "2", "4"};
        List<Vector> vectors = new ArrayList<>();

        // when
        when(repository.findPage(login, 2, 4)).thenReturn(vectors);
        when(repository.numberOfRows(login)).thenReturn(5);
        String result = command.process(tokens, login);

        // then
        assertEquals("Page is empty. Check the entered data: the first 2 numbers must be natural numbers. Also, there are 5 vectors in repository, so these two quantities must take into account this limitation.", result);
    }

    @Test
    void givenValidTokens_whenProcessAndVectorsExistInPage_thenReturnVectorsInPage() {
        // given
        String[] tokens = new String[]{"read", "2", "1"};
        List<Vector> vectors = new ArrayList<>();
        vectors.add(new Vector("Vector1", 1.0, 2.0, 3.0));
        vectors.add(new Vector("Vector2", 4.0, 5.0, 6.0));

        // when
        when(repository.findPage(login, 2, 1)).thenReturn(vectors);

        // then
        String result = command.process(tokens, login);
        StringBuilder expectedResult = new StringBuilder();
        vectors.forEach(vector -> {
            expectedResult.append(vector.toString());
            expectedResult.append("\n");
        });
        assertEquals(expectedResult.toString(), result);
    }

    @Test
    void givenValidTokens_whenProcessAndPageSizeExceedsRepositorySize_thenReturnAllVectorsInRepository() {
        // given
        String[] tokens = new String[]{"read", "10", "1"};
        List<Vector> vectors = new ArrayList<>();
        vectors.add(new Vector("Vector1", 1.0, 2.0, 3.0));
        vectors.add(new Vector("Vector2", 4.0, 5.0, 6.0));

        // when
        when(repository.findPage(login, 10, 1)).thenReturn(vectors);

        // then
        String result = command.process(tokens, login);
        StringBuilder expectedResult = new StringBuilder();
        vectors.forEach(vector -> {
            expectedResult.append(vector.toString());
            expectedResult.append("\n");
        });
        assertEquals(expectedResult.toString(), result);
    }
}
