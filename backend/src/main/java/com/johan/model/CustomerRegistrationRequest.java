package com.johan.model;

public record CustomerRegistrationRequest(String name, String email, Integer age, Gender gender) {
}
