package com.dainam.D2.utils;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class StringUtils {

    public static String toPascalCase(String value, String delimiter) {
        return toPascalCase(value, delimiter, true);
    }

    public static String toPascalCase(String value, String delimiter, boolean persist) {
        if (delimiter == null) throw new ValidationException("Delimiter cannot be null");
        // Split value by delimiter
        String[] words;
        if (delimiter.isEmpty()) {
            words = new String[]{value};
        } else {
            words = value.split(delimiter);
        }

        // Use a StringBuilder for efficient string concatenation
        StringBuilder result = new StringBuilder();

        // Loop through each word
        for (String word : words) {
            if (word.isEmpty()) {
                continue;
            }
            // Capitalize the first letter and append the rest of the word
            result.append(Character.toUpperCase(word.charAt(0)));
            if (word.length() > 1) {
                if (!persist) result.append(word.substring(1).toLowerCase());
                else result.append(word.substring(1));
            }
        }

        return result.toString();
    }
    public static String toCamelCase(String value, String delimiter) {
        // Split value by delimiter
        String[] words = value.split(delimiter);

        // Use a StringBuilder for efficient string concatenation
        StringBuilder result = new StringBuilder();

        // Loop through each word
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            if (word.isEmpty()) {
                continue;
            }
            if (i == 0) {
                // For the first word, make the first letter lowercase
                result.append(word.toLowerCase());
            } else {
                // For subsequent words, capitalize the first letter and append the rest of the word
                result.append(Character.toUpperCase(word.charAt(0)));
                if (word.length() > 1) {
                    result.append(word.substring(1).toLowerCase());
                }
            }
        }

        return result.toString();
    }


    static public List<String> enumToNameList(Class<? extends Enum<?>> enumClass) {
        return Arrays.stream(Arrays.toString(enumClass.getEnumConstants())
                .replaceAll("^.|.$", "")
                .split(", ")).toList();
    }

    static public String join(String delimiter, Object ...objects){
        return String.join(delimiter, Arrays.stream(objects).map(Object::toString).toList());
    }

    static public String join(Object ...objects){
        return String.join(" ", Arrays.stream(objects).map(Object::toString).toList());
    }

}
