package ru.hse.vectorizer.utils;

public interface ExceptionSupplier<T> {
    T get() throws Exception;
}
