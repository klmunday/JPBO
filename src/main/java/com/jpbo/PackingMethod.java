package com.jpbo;

public enum PackingMethod {
    UNCOMPRESSED(0),
    COMPRESSED(1131442803),
    ENCRYPTED(1164862322),
    PRODUCT(1449489011);

    private int value;

    PackingMethod(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static PackingMethod fromValue(int value) {
        for (PackingMethod method : values())
            if (method.getValue() == value)
                return method;
        return null;
    }
}
