package ru.hse.vectorizer.domain;

import java.util.Optional;

public interface SimpleVectorRepository {
    Optional<Vector> findByName(String login, String token);
    boolean deleteByName(String login, String name);

    Optional<Vector> save(String login, Vector vector);

    int numberOfRows(String login);

    Iterable<Vector> findAll(String login);
    Iterable<Vector> findPage(String login, int pageSize, int pageNumber);
}
