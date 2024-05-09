package com.backend.devx.member.role;

public enum Gender {
    MALE("남성"),
    FEMALE("여성"),
    OTHER("다른");

    private final String value;

    Gender(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
