package ru.hse.vectorizer.command.clientImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.hse.vectorizer.command.ClientCommand;
import ru.hse.vectorizer.domain.SimpleVectorRepository;
import ru.hse.vectorizer.domain.Vector;

@Component
@RequiredArgsConstructor
public class ReadPageCommand implements ClientCommand {
    private final SimpleVectorRepository repository;
    @Override
    public String process(String[] tokens, String login) {
        if (!isSuitable(tokens)) {
            return null;
        }
        int pageSize = Integer.parseInt(tokens[1]);
        int pageNum = Integer.parseInt(tokens[2]);
        Iterable<Vector> result = repository.findPage(login, pageSize, pageNum);
        StringBuilder resultString = new StringBuilder();
        for (Vector v : result) {
            resultString.append(v.toString());
            resultString.append("\n");
        }
        if (resultString.isEmpty()) {
            int numberOfRows = repository.numberOfRows(login);
            return "Page is empty. Check the entered data: " +
                    "the first 2 numbers must be natural numbers. Also, " +
                    "there are " + numberOfRows + " vectors in repository, so" +
                    " these two quantities must take into account this limitation.";
        }
        return resultString.toString();
    }

    private boolean isSuitable(String[] tokens) {
        return tokens.length == 3 && "read".equalsIgnoreCase(tokens[0])
                && isNumeric(tokens[1]) && isNumeric(tokens[2]);
    }

    private boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }
}
