package ru.hse.vectorizer.command.clientImpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.hse.vectorizer.domain.SimpleVectorRepository;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;
import static ru.hse.vectorizer.utils.Constants.LOGIN_SYMBOLS;

@ExtendWith(MockitoExtension.class)
class DeleteCommandTest {
    @Mock
    SimpleVectorRepository repository;

    @InjectMocks
    DeleteCommand command;

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
        tokens = new String[]{"delete", "extra", "tokens"};
        // when
        result = command.process(tokens, login);
        // then
        assertNull(result);
    }

    @Test
    void givenValidTokens_whenProcessAndDeletedSuccessfully_thenReturnSuccess() {
        // given
        String[] tokens = new String[]{"delete", "TestVector"};
        // when
        when(repository.deleteByName(login, "TestVector")).thenReturn(true);
        String result = command.process(tokens, login);
        // then
        assertEquals("Deleted successfully.", result);
    }

    @Test
    void givenValidTokens_whenProcessAndNotDeleted_thenReturnFailure() {
        // given
        String[] tokens = new String[]{"delete", "NonExistentVector"};
        // when
        when(repository.deleteByName(login, "NonExistentVector")).thenReturn(false);
        String result = command.process(tokens, login);
        // then
        assertEquals("Vector NonExistentVector doesn't exist or error has occurred while deleting.", result);
    }
}