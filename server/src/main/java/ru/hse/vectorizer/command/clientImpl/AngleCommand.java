package ru.hse.vectorizer.command.clientImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.hse.vectorizer.command.ClientCommand;
import ru.hse.vectorizer.domain.SimpleVectorRepository;
import ru.hse.vectorizer.domain.Vector;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class AngleCommand implements ClientCommand {
    private final SimpleVectorRepository repository;

    @Override
    public String process(String[] tokens, String login) {
        if (!isSuitable(tokens)) {
            return null;
        }
        Optional<Vector> first = repository.findByName(login, tokens[1]);
        Optional<Vector> second = repository.findByName(login, tokens[2]);
        if (first.isEmpty()) {
            return "Vector " + tokens[1] + " not found.";
        }
        if (second.isEmpty()) {
            return "Vector " + tokens[2] + " not found.";
        }
        return "Angle (in radians) equals: " + first.get().getAngle(second.get());
    }

    private boolean isSuitable(String[] tokens) {
        return tokens.length == 3 && "angle".equalsIgnoreCase(tokens[0]);
    }
}
