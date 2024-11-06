package com.example.filmtinder.utils;


import jakarta.persistence.AttributeConverter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SetToStringConverter implements AttributeConverter<Set<String>, String> {

    @Override
    public String convertToDatabaseColumn(Set<String> attribute) {
        return attribute == null ? null : String.join(",", attribute);
    }

    @Override
    public Set<String> convertToEntityAttribute(String dbData) {
        return dbData == null || dbData.isEmpty()
                ? new HashSet<>()
                : new HashSet<>(List.of(dbData.split(",")));
    }
}
