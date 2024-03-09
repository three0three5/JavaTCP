package ru.hse.vectorizer.command.clientImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.hse.vectorizer.command.ClientCommand;
import ru.hse.vectorizer.domain.SimpleVectorRepository;
import ru.hse.vectorizer.domain.Vector;

@RequiredArgsConstructor
@Component
public class CreateCommand implements ClientCommand {
    private final SimpleVectorRepository repository;

    @Override
    public String process(String[] tokens, String login) {
        if (!isSuitable(tokens)) {
            return null;
        }
        Vector inserted = new Vector(tokens[1],
                Double.parseDouble(tokens[2]), Double.parseDouble(tokens[3]), Double.parseDouble(tokens[4]));
        if (repository.save(login, inserted).isPresent()) {
            return "Vector has been saved";
        }
        return "Vector has not been saved.";
    }

    private boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

    private boolean isSuitable(String[] tokens) {
        return tokens.length == 5 && "create".equalsIgnoreCase(tokens[0]) &&
                isNumeric(tokens[2]) && isNumeric(tokens[3]) && isNumeric(tokens[4]);
    }
}
