package com.directorystructure.service;

import java.util.ArrayList;
import java.util.List;

import com.directorystructure.exceptions.ValidationException;
import com.directorystructure.model.SearchFilter;
import com.directorystructure.model.enums.FilterType;
import com.directorystructure.model.enums.LogicalOperator;

public class SearchCriteria {
    private final List<SearchFilter> filters = new ArrayList<>();
    private final List<LogicalOperator> operators = new ArrayList<>();
    private boolean computeSize = false;
    
    public SearchCriteria where(FilterType type, String value, boolean negate) {
        if (type == null) {
            throw new ValidationException("FilterType cannot be null");
        }
        if (value == null) {
            throw new ValidationException("Search value cannot be null");
        }
        filters.add(new SearchFilter(type, value, negate));
        return this;
    }
    
    public SearchCriteria where(FilterType type, String value) {
        return where(type, value, false);
    }
    
    public SearchCriteria and(FilterType type, String value, boolean negate) {
        operators.add(LogicalOperator.AND);
        return where(type, value, negate);
    }
    
    public SearchCriteria and(FilterType type, String value) {
        return and(type, value, false);
    }
    
    public SearchCriteria or(FilterType type, String value, boolean negate) {
        operators.add(LogicalOperator.OR);
        return where(type, value, negate);
    }
    
    public SearchCriteria or(FilterType type, String value) {
        return or(type, value, false);
    }

    public SearchCriteria size() {
        this.computeSize = true;
        return this;
    }
    
    public List<SearchFilter> getFilters() { return filters; }
    public List<LogicalOperator> getOperators() { return operators; }
    public boolean shouldComputeSize() { return computeSize; }
}