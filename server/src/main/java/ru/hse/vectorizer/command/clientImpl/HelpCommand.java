package ru.hse.vectorizer.command.clientImpl;

import org.springframework.stereotype.Component;
import ru.hse.vectorizer.command.ClientCommand;

import static ru.hse.vectorizer.utils.Constants.CLIENT_HELP;

@Component
public class HelpCommand implements ClientCommand {
    @Override
    public String process(String[] tokens, String login) {
        if ("help".equalsIgnoreCase(tokens[0])) {
            return CLIENT_HELP;
        }
        return null;
    }
}
