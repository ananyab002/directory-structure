package com.directorystructure.util;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.directorystructure.model.DirectoryStructure;
import com.directorystructure.model.Node;

public class ResultFormatter {

    public static String formatTree(DirectoryStructure repository) {

        Long rootId = repository.getRootId();
        if (rootId == null) {
            return "No file system structure found";
        }

        StringBuilder sb = new StringBuilder();
        Map<Long, String> indentMap = new HashMap<>();
        indentMap.put(rootId, "");

        for (Long nodeId : repository.getTreeTraversal(rootId)) {
            Node node = repository.getNode(nodeId);
            if (node == null)
                continue;

            String indent = indentMap.getOrDefault(nodeId, "");
            sb.append(indent).append(formatNode(node)).append("\n");

            String childIndent = indent + " ";
            for (Long childId : repository.getChildren(nodeId)) {
                indentMap.put(childId, childIndent);
            }
        }

        return sb.toString();
    }

    public static String formatSearchResults(Set<Node> nodes) {
        return nodes.stream()
                .filter(node -> !node.isDirectory())
                .sorted(Comparator.comparing(Node::getName))
                .map(ResultFormatter::formatNode)
                .collect(Collectors.joining("\n"));
    }

    private static String formatNode(Node node) {
        if (node == null)
            return "";

        StringBuilder sb = new StringBuilder()
                .append("name = ").append(node.getName())
                .append(", type = ").append(node.isDirectory() ? "Directory" : "File");

        if (node.getSize() != null) {
            sb.append(", size = ").append(node.getSize());
        }

        if (!node.isDirectory()) {
            if (node.getClassification() != null) {
                sb.append(", classification = ").append(node.getClassification());
            }
            if (node.getChecksum() != null) {
                sb.append(", checksum = ").append(node.getChecksum());
            }
        }

        return sb.toString();
    }
}
