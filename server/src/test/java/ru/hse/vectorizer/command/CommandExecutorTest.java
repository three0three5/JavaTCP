package ru.hse.vectorizer.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.hse.vectorizer.utils.Constants.LOGIN_SYMBOLS;

@ExtendWith(MockitoExtension.class)
class CommandExecutorTest {
    @Mock
    ClientCommand firstCommand;

    @Mock
    ClientCommand secondCommand;

    CommandExecutor executor;

    String login = new Random().ints(10, 0, LOGIN_SYMBOLS.length())
            .mapToObj(index -> LOGIN_SYMBOLS.charAt(index % LOGIN_SYMBOLS.length()))
            .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
            .toString();

    @BeforeEach
    void init() {
        executor = new CommandExecutor(List.of(firstCommand, secondCommand));
    }

    @Test
    void whenPerformCommand_thenInvokeProcessForEachCommand() {
        when(firstCommand.process(any(String[].class), eq(login))).thenReturn(null);
        when(secondCommand.process(any(String[].class), eq(login))).thenReturn(null);
        // when
        executor.performCommand(new String[]{}, login);
        // then
        verify(firstCommand).process(any(String[].class), eq(login));
        verify(secondCommand).process(any(String[].class), eq(login));
    }

    @Test
    void givenNotSuitableTokens_whenPerformCommand_thenReturnInvalidCommandString() {
        // given
        when(firstCommand.process(any(String[].class), eq(login))).thenReturn(null);
        when(secondCommand.process(any(String[].class), eq(login))).thenReturn(null);
        // when
        String result = executor.performCommand(new String[]{"random", "tokens"}, login);
        // then
        assertEquals(result, "Invalid command."); // Команда не найдена
    }

    @Test
    void givenSuitableCommand_whenPerformCommand_thenReturnProcessed() {
        // given
        when(firstCommand.process(any(String[].class), eq(login))).thenReturn(null);
        when(secondCommand.process(eq(new String[]{"random", "tokens"}), eq(login))).thenReturn("result");
        // when
        String result = executor.performCommand(new String[]{"random", "tokens"}, login);
        // then
        assertEquals(result, "result");
    }
}