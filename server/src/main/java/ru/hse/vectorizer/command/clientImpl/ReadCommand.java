package ru.hse.vectorizer.command.clientImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.hse.vectorizer.command.ClientCommand;
import ru.hse.vectorizer.domain.SimpleVectorRepository;
import ru.hse.vectorizer.domain.Vector;

@RequiredArgsConstructor
@Component
public class ReadCommand implements ClientCommand {
    private final SimpleVectorRepository repository;
    @Override
    public String process(String[] tokens, String login) {
        if (!isSuitable(tokens)) {
            return null;
        }
        if (repository.numberOfRows(login) == 0) {
            return "No vectors were found";
        }
        StringBuilder res = new StringBuilder("Created vectors: ");
        repository.findAll(login).forEach((Vector v) -> {res.append("\n"); res.append(v);});
        return res.toString();
    }

    private boolean isSuitable(String[] tokens) {
        return tokens.length == 1 && "read".equalsIgnoreCase(tokens[0]);
    }
}
