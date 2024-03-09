package ru.hse.vectorizer.command.clientImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.hse.vectorizer.command.ClientCommand;
import ru.hse.vectorizer.domain.SimpleVectorRepository;
import ru.hse.vectorizer.domain.Vector;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class ProductTripleCommand implements ClientCommand {
    private final SimpleVectorRepository repository;

    @Override
    public String process(String[] tokens, String login) {
        if (!isSuitable(tokens)) {
            return null;
        }
        Optional<Vector> first = repository.findByName(login, tokens[2]);
        Optional<Vector> second = repository.findByName(login, tokens[3]);
        Optional<Vector> third = repository.findByName(login, tokens[4]);
        if (first.isEmpty()) {
            return "Vector " + tokens[2] + " not found";
        }
        if (second.isEmpty()) {
            return "Vector " + tokens[3] + " not found";
        }
        if (third.isEmpty()) {
            return "Vector " + tokens[4] + " not found.";
        }
        return "Result is " + first.get().dotProduct(
                second.get().mulByVector(third.get())
        );
    }

    private boolean isSuitable(String[] tokens) {
        return tokens.length == 5 && "product".equalsIgnoreCase(tokens[0]) &&
                "triple".equalsIgnoreCase(tokens[1]);
    }
}
