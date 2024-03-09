package ru.hse.client.utils;

public interface ExceptionSupplier<T> {
    T get() throws Exception;
}
