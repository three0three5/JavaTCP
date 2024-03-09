package ru.hse.vectorizer.command;

public interface ClientCommand {
    String process(String[] tokens, String login);
}
