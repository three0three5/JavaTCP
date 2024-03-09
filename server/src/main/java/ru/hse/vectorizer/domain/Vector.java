package ru.hse.vectorizer.domain;

import lombok.Data;


@Data
public class Vector {
    private final String name;
    private final double x;
    private final double y;
    private final double z;
    public Vector mulByConst(int c) {
        return new Vector("", x * c, y * c, z * c);
    }

    public Vector addVector(Vector other) {
        return new Vector("", x + other.x, y + other.y, z + other.z);
    }

    public Vector subVector(Vector other) {
        return new Vector("", x - other.x, y - other.y, z - other.z);
    }

    public Vector mulByVector(Vector other) {
        return new Vector("",
                y * other.z - z * other.y,
                z * other.x - x * other.z,
                x * other.y - y * other.x
        );
    }

    public double getLength() {
        return Math.sqrt(x * x + y * y + z * z);
    }

    public double dotProduct(Vector other) {
        return x * other.x + y * other.y + z * other.z;
    }

    public double getAngle(Vector other) {
        return Math.acos(
                (dotProduct(other)) / (getLength() * other.getLength())
        );
    }

    @Override
    public String toString() {
        String result = "(%f, %f, %f)".formatted(x, y, z);
        if (name != null) {
            result = name + ": " + result;
        }
        return result;
    }
}
