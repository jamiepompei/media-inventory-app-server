package com.inventory.app.server.config.converter;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class StringListConverterTest {

    private final StringListConverter underTest = new StringListConverter();

    @Test
    public void testConvertToDatabaseColumn_WithValidList() {
        // GIVEN
        List<String> input = Arrays.asList("Author1", "Author2", "Author3");

        // WHEN
        String result = underTest.convertToDatabaseColumn(input);

        // THEN
        assertEquals("Author1;Author2;Author3", result);
    }

    @Test
    public void testConvertToDatabaseColumn_WithEmptyList() {
        // GIVEN
        List<String> input = Collections.emptyList();

        // WHEN
        String result = underTest.convertToDatabaseColumn(input);

        // THEN
        assertEquals("", result);
    }

    @Test
    public void testConvertToDatabaseColumn_WithNullList() {
        // GIVEN
        List<String> input = null;

        // WHEN
        String result = underTest.convertToDatabaseColumn(input);

        // THEN
        assertEquals("", result);
    }

    @Test
    public void testConvertToEntityAttribute_WithValidString() {
        // GIVEN
        String input = "Author1;Author2;Author3";

        // WHEN
        List<String> result = underTest.convertToEntityAttribute(input);

        // THEN
        assertEquals(Arrays.asList("Author1", "Author2", "Author3"), result);
    }

    @Test
    public void testConvertToEntityAttribute_WithNullString() {
        // GIVEN
        String input = null;

        // WHEN
        List<String> result = underTest.convertToEntityAttribute(input);

        // THEN
        assertEquals(Collections.emptyList(), result);
    }
}