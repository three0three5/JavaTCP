package ru.hse.vectorizer.command.clientImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.hse.vectorizer.command.ClientCommand;
import ru.hse.vectorizer.domain.SimpleVectorRepository;

@RequiredArgsConstructor
@Component
public class DeleteCommand implements ClientCommand {
    private final SimpleVectorRepository repository;
    @Override
    public String process(String[] tokens, String login) {
        if (!isSuitable(tokens)) {
            return null;
        }
        if (!repository.deleteByName(login, tokens[1])) {
            return "Vector " + tokens[1] + " doesn't exist or error has occurred while deleting.";
        }
        return "Deleted successfully.";
    }

    private static boolean isSuitable(String[] tokens) {
        return tokens.length == 2 && "delete".equalsIgnoreCase(tokens[0]);
    }
}
