package ru.hse.vectorizer.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


class VectorTest {
    @Test
    void mulByConst() {
        // given
        Vector a = new Vector("", 1, 2, 3);
        // when
        a = a.mulByConst(10);
        // then
        Vector valid = new Vector("", 10, 20, 30);
        assertEquals(valid.getX(), a.getX());
        assertEquals(valid.getY(), a.getY());
        assertEquals(valid.getZ(), a.getZ());

        // given
        a = new Vector("", 1, 2, 3);
        // when
        a = a.mulByConst(0);
        // then
        valid = new Vector("", 0, 0, 0);
        assertEquals(valid.getX(), a.getX());
        assertEquals(valid.getY(), a.getY());
        assertEquals(valid.getZ(), a.getZ());
    }

    @Test
    void addVector() {
        // given
        Vector a = new Vector("", 1, 2, 3);
        Vector b = new Vector("", -1, -2, -3);
        // when
        a = a.addVector(b);
        // then
        Vector valid = new Vector("", 0, 0, 0);
        assertEquals(valid.getX(), a.getX());
        assertEquals(valid.getY(), a.getY());
        assertEquals(valid.getZ(), a.getZ());
    }

    @Test
    void subVector() {
        // given
        Vector a = new Vector("", 100, -100, 0);
        Vector b = new Vector("", -1, -2, -3);
        // when
        a = a.subVector(b);
        // then
        Vector valid = new Vector("", 101, -98, 3);
        assertEquals(valid.getX(), a.getX());
        assertEquals(valid.getY(), a.getY());
        assertEquals(valid.getZ(), a.getZ());
    }

    @Test
    void mulByVector() {
        // given
        Vector a = new Vector("", 1, 2, 3);
        Vector b = new Vector("", 4, 5, 6);
        // when
        a = a.mulByVector(b);
        // then
        Vector valid = new Vector("", -3, 6, -3);
        assertEquals(valid.getX(), a.getX());
        assertEquals(valid.getY(), a.getY());
        assertEquals(valid.getZ(), a.getZ());
    }

    @Test
    void getLength() {
        // given
        Vector a = new Vector("", 10, -5, 3);
        // when
        double result = a.getLength();
        // then
        assertEquals(11.5758369028, result, 1e-5);
    }

    @Test
    void dotProduct() {
        // given
        Vector a = new Vector("", 3, 4, -9);
        Vector b = new Vector("", 10, 14, 12);
        // when
        double result = a.dotProduct(b);
        // then
        assertEquals(-22, result, 1e-5);
    }

    @Test
    void getAngle() {
        // given
        Vector a = new Vector("", 5, 2, 8);
        Vector b = new Vector("", -5, -2, -8);
        // when
        double result = a.getAngle(b);
        // then
        assertEquals(3.141592653, result, 1e-5);

        // given
        a = new Vector("", 2, -10, 4);
        b = new Vector("", 1, -5, 2);
        // when
        result = a.getAngle(b);
        // then
        assertEquals(0, result, 1e-5);
    }
}