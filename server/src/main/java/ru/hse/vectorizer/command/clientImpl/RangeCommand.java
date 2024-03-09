package ru.hse.vectorizer.command.clientImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.hse.vectorizer.command.ClientCommand;
import ru.hse.vectorizer.domain.SimpleVectorRepository;
import ru.hse.vectorizer.domain.Vector;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class RangeCommand implements ClientCommand {
    private final SimpleVectorRepository repository;

    @Override
    public String process(String[] tokens, String login) {
        if (!isSuitable(tokens)) {
            return null;
        }
        Optional<Vector> v = repository.findByName(login, tokens[1]);
        return v.map(vector -> "Range of vector is: " + vector.getLength())
                .orElseGet(() -> "Vector " + tokens[1] + " not found.");
    }

    private boolean isSuitable(String[] tokens) {
        return tokens.length == 2 && "range".equalsIgnoreCase(tokens[0]);
    }
}
