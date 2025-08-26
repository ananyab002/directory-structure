package com.directorystructure.model;

import com.directorystructure.exceptions.DataParsingException;

public enum NodeType {
    FILE("file"),
    DIRECTORY("directory");

    private final String value;

    NodeType(String value) {
        this.value = value;
    }

    public static NodeType fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new DataParsingException("Node type cannot be null or empty");
        }

        String trimmed = value.trim().toLowerCase();
        switch (trimmed) {
            case "file" -> {
                return FILE;
            }
            case "directory" -> {
                return DIRECTORY;
            }
            default ->
                throw new DataParsingException("Invalid node type: '" + value + "'. Must be 'file' or 'directory'");
        }
    }

    public String getValue() {
        return value;
    }
}
