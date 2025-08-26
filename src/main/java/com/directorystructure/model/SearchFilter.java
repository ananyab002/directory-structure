package com.directorystructure.model;

import com.directorystructure.exceptions.ValidationException;
import com.directorystructure.model.enums.FilterType;

public class SearchFilter {
    private final FilterType type;
    private final String value;
    private final boolean negate;

    public SearchFilter(FilterType type, String value, boolean negate) {
        if (type == null) {
            throw new ValidationException("FilterType cannot be null");
        }
        if (value == null) {
            throw new ValidationException("Filter value cannot be null");
        }
        this.type = type;
        this.value = value;
        this.negate = negate;
    }

    public FilterType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public boolean isNegated() {
        return negate;
    }
}
