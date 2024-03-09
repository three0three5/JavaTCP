package ru.hse.vectorizer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Component
public class CommandExecutor {
    private final List<ClientCommand> commands;
    public String performCommand(String[] tokens, String login) {
        for (var c : commands) {
            String result = c.process(tokens, login);
            if (result != null) {
                return result;
            }
        }
        return "Invalid command.";
    }
}
