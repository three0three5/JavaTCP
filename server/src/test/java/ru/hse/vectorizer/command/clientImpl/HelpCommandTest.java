package ru.hse.vectorizer.command.clientImpl;

import org.junit.jupiter.api.Test;
import ru.hse.vectorizer.utils.Constants;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static ru.hse.vectorizer.utils.Constants.LOGIN_SYMBOLS;

class HelpCommandTest {
    HelpCommand command = new HelpCommand();

    String login = new Random().ints(10, 0, LOGIN_SYMBOLS.length())
            .mapToObj(index -> LOGIN_SYMBOLS.charAt(index % LOGIN_SYMBOLS.length()))
            .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
            .toString();

    @Test
    void givenHelpToken_whenProcess_thenReturnHelpMessage() {
        // given
        String[] tokens = new String[]{"help"};

        // when
        String result = command.process(tokens, login);

        // then
        assertEquals(Constants.CLIENT_HELP, result);
    }

    @Test
    void givenNonHelpToken_whenProcess_thenReturnNull() {
        // given
        String[] tokens = new String[]{"not-help"};

        // when
        String result = command.process(tokens, login);

        // then
        assertNull(result);
    }
}