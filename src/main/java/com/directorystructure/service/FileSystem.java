package com.directorystructure.service;

import java.util.List;
import java.util.Set;

import com.directorystructure.model.DirectoryStructure;
import com.directorystructure.model.Node;
import com.directorystructure.util.CsvParser;
import com.directorystructure.util.ResultFormatter;

public class FileSystem {
    private final DirectoryStructure repository;
    private final SearchEngine searchEngine;

    public FileSystem() {
        this.repository = new DirectoryStructure();
        this.searchEngine = new SearchEngine(repository);
    }

    public void loadFromCsv(String resourceName) {
        List<Node> nodes = CsvParser.parse(resourceName);
        for (Node node : nodes) {
            repository.addNode(node);
        }
        repository.computeAllSizes();
    }

    public String buildTree() {
        return ResultFormatter.formatTree(repository);
    }

    public String search(SearchCriteria criteria) {
        if (criteria == null || criteria.getFilters().isEmpty()) {
            return "";
        }

        Set<Node> results = searchEngine.execute(criteria);

        if (criteria.shouldComputeSize()) {
            long totalSize = results.stream()
                    .filter(node -> !node.isDirectory())
                    .filter(node -> node.getSize() != null)
                    .mapToLong(Node::getSize)
                    .sum();
            return String.valueOf(totalSize);

        }

        return ResultFormatter.formatSearchResults(results);
    }
}