package com.directorystructure.service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.directorystructure.exceptions.ValidationException;
import com.directorystructure.model.DirectoryStructure;
import com.directorystructure.model.Node;
import com.directorystructure.model.SearchFilter;
import com.directorystructure.model.enums.LogicalOperator;

public class SearchEngine {

    private final DirectoryStructure directoryModel;

    public SearchEngine(DirectoryStructure directoryModel) {
        if (directoryModel == null) {
            throw new ValidationException("DirectoryStructure directoryModel cannot be null");
        }
        this.directoryModel = directoryModel;
    }

    public Set<Node> execute(SearchCriteria criteria) {
        if (criteria == null || criteria.getFilters().isEmpty()) {
            return Collections.emptySet();
        }

        List<SearchFilter> filters = criteria.getFilters();
        List<LogicalOperator> operators = criteria.getOperators();

        Set<Long> resultIds = applyFilter(filters.get(0));

        for (int i = 1; i < filters.size(); i++) {
            Set<Long> filterResult = applyFilter(filters.get(i));
            LogicalOperator operator = operators.get(i - 1);

            if (operator == LogicalOperator.AND) {
                resultIds.retainAll(filterResult);
            } else {
                resultIds.addAll(filterResult);
            }
        }

        return directoryModel.getNodesByIds(resultIds);
    }

    private Set<Long> applyFilter(SearchFilter filter) {
        return switch (filter.getType()) {
            case CLASSIFICATION -> findByClassification(filter.getValue(), filter.isNegated());
            case DIRECTORY_NAME -> findByDirectory(filter.getValue(), filter.isNegated());
            default -> Collections.emptySet();
        };
    }

    private Set<Long> findByClassification(String classification, boolean negate) {
        Set<Long> result = new HashSet<>();
        Set<Long> indexed = directoryModel.getNodesByClassification(classification);

        if (negate) {
            result.addAll(directoryModel.getAllNodeIds());
            result.removeAll(indexed);
        } else {
            result.addAll(indexed);
        }

        return result;
    }

    private Set<Long> findByDirectory(String directoryName, boolean negate) {
        Set<Long> result = new HashSet<>();
        Long dirId = directoryModel.getDirectoryIdByName(directoryName);

        if (dirId != null) {
            Set<Long> descendants = new HashSet<>();
            directoryModel.getTreeTraversal(dirId).forEach(descendants::add);

            if (negate) {
                result.addAll(directoryModel.getAllNodeIds());
                result.removeAll(descendants);
            } else {
                result.addAll(descendants);
            }
        } else if (negate) {
            result.addAll(directoryModel.getAllNodeIds());
        }

        return result;
    }
}
