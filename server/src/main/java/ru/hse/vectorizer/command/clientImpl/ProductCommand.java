package ru.hse.vectorizer.command.clientImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.hse.vectorizer.command.ClientCommand;
import ru.hse.vectorizer.domain.SimpleVectorRepository;
import ru.hse.vectorizer.domain.Vector;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class ProductCommand implements ClientCommand {
    private final SimpleVectorRepository repository;

    @Override
    public String process(String[] tokens, String login) {
        if (!isSuitable(tokens)) {
            return null;
        }
        Optional<Vector> first = repository.findByName(login, tokens[2]);
        Optional<Vector> second = repository.findByName(login, tokens[3]);
        if (first.isEmpty()) {
            return "Vector " + tokens[2] + " not found";
        }
        if (second.isEmpty()) {
            return "Vector " + tokens[3] + " not found";
        }
        if ("dot".equalsIgnoreCase(tokens[1])) {
            return "Dot result is: " + first.get().dotProduct(second.get());
        } else {
            return "Vector result is: " + first.get().mulByVector(second.get());
        }
    }

    private boolean isSuitable(String[] tokens) {
        return tokens.length == 4 && "product".equalsIgnoreCase(tokens[0]) &&
                ("dot".equalsIgnoreCase(tokens[1]) || "cross".equalsIgnoreCase(tokens[1]));
    }
}
