package com.fastcampus.projectboardadmin.domain.converter;

import com.fastcampus.projectboardadmin.domain.constant.RoleType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;


// A type 의 entity attribute 를 -> B type 으로 DB column 저장
@Converter
public class RoleTypesConverter implements AttributeConverter<Set<RoleType>, String> {

    private static final String DELIMITER = ","; // 구분자??

    // Entity -> DB
    @Override
    public String convertToDatabaseColumn(Set<RoleType> attribute) {
        return attribute.stream()
                .map(RoleType::name)
                .sorted()
                .collect(Collectors.joining(DELIMITER));
    }

    // DB -> Entity
    @Override
    public Set<RoleType> convertToEntityAttribute(String dbData) {
        return Arrays.stream(dbData.split(DELIMITER))
                .map(RoleType::valueOf)
                .collect(Collectors.toSet());
    }
}
