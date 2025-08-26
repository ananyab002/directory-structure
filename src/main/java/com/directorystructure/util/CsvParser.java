package com.directorystructure.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.directorystructure.exceptions.DataParsingException;
import com.directorystructure.model.Node;
import com.directorystructure.model.NodeType;

/**
 * Parses CSV content into FileNode objects.
 */
public class CsvParser {

    public static List<Node> parse(String resourceName) {
        List<Node> nodes = new ArrayList<>();

        try (InputStream inputStream = CsvParser.class.getClassLoader().getResourceAsStream(resourceName)) {

            if (inputStream == null) {
                throw new DataParsingException("Resource not found: " + resourceName);
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                reader.lines()
                        .map(String::trim)
                        .filter(CsvParser::isValidLine)
                        .forEach(line -> nodes.add(parseLine(line)));

                if (nodes.isEmpty()) {
                    throw new DataParsingException("CSV file is empty or contains only header: " + resourceName);
                }

            } catch (IOException e) {
                throw new DataParsingException("Failed to read CSV file: " + resourceName, e);
            }

        } catch (IOException e) {
            throw new DataParsingException("Failed to load CSV resource: " + resourceName, e);
        }

        return nodes;
    }

    private static boolean isValidLine(String line) {
        return !line.isEmpty() &&
                !line.startsWith("#") &&
                !line.contains("id;");
    }

    private static Node parseLine(String line) {
        String[] parts = line.split(";", -1);
        if (parts.length < 7) {
            throw new DataParsingException(
                    "Invalid CSV format - expected 7 columns, got " + parts.length + " in line: " + line);
        }

        Long id = parseLong(parts[0]);
        Long parentId = parseLong(parts[1]);
        String name = parts[2].trim();
        NodeType type = NodeType.fromString(parts[3].trim());
        Long size = parseLong(parts[4]);
        String classification = parts[5].trim().isEmpty() ? "NA" : parts[5].trim();
        String checksum = parts[6].trim().isEmpty() ? null : parts[6].trim();

        if (id == null) {
            throw new DataParsingException("Missing or invalid ID in line: " + line);
        }
        if (name.isEmpty()) {
            throw new DataParsingException("Missing name in line: " + line);
        }
        if (type == null) {
            throw new DataParsingException("Invalid type in line: " + line);
        }

        return new Node(id, parentId, name, size, type, classification, checksum);
    }

    private static Long parseLong(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Long.valueOf(value.trim());
        } catch (NumberFormatException e) {
            throw new DataParsingException("Invalid number format: '" + value + "'", e);
        }
    }
}