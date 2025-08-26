package com.directorystructure.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.directorystructure.exceptions.ValidationException;
import com.google.common.graph.Traverser;

public class DirectoryStructure {

    private Long rootId = null;
    private final Map<Long, Node> nodes = new HashMap<>();
    private final Map<Long, TreeSet<Long>> childrenIdMap = new HashMap<>();
    private final Map<String, Set<Long>> classificationIndex = new HashMap<>();
    private final Map<String, Long> directoryNameIndex = new HashMap<>();

    private final Traverser<Long> treeTraverser = Traverser
            .forTree(nodeId -> childrenIdMap.getOrDefault(nodeId, new TreeSet<>()));

    public void addNode(Node node) {
        if (node == null) {
            throw new ValidationException("Node cannot be null");
        }
        if (node.getId() == null) {
            throw new ValidationException("Node ID cannot be null");
        }
        if (node.getParentId() == null && rootId != null) {
            throw new ValidationException("Multiple root nodes detected");
        }

        nodes.put(node.getId(), node);

        if (node.getParentId() != null) {
            Node parent = nodes.get(node.getParentId());
            if (parent != null && !parent.isDirectory()) {
                throw new ValidationException("Only directories can have children");
            }
            childrenIdMap.computeIfAbsent(node.getParentId(), k -> createSortedChildSet())
                    .add(node.getId());
        } else {
            rootId = node.getId();
        }

        if (node.getClassification() != null) {
            classificationIndex.computeIfAbsent(node.getClassification(), k -> new HashSet<>())
                    .add(node.getId());
        }
        if (node.isDirectory()) {
            directoryNameIndex.put(node.getName(), node.getId());
        }
    }

    private TreeSet<Long> createSortedChildSet() {
        return new TreeSet<>((id1, id2) -> {
            Node n1 = nodes.get(id1);
            Node n2 = nodes.get(id2);
            return (n1 == null || n2 == null) ? 0 : n1.getName().compareTo(n2.getName());
        });
    }

    public Long computeSubtreeSize(Long nodeId) {
        long totalSize = 0;

        for (Long id : treeTraverser.depthFirstPostOrder(nodeId)) {
            Node node = nodes.get(id);
            if (node == null)
                continue;

            if (node.isDirectory()) {
                long dirSize = 0;
                Set<Long> children = childrenIdMap.get(id);
                if (children != null) {
                    for (Long childId : children) {
                        Node child = nodes.get(childId);
                        if (child != null && child.getSize() != null) {
                            dirSize += child.getSize();
                        }
                    }
                }
                node.setSize(dirSize);
            }

            if (node.getSize() != null) {
                totalSize = node.getSize();
            }
        }

        return totalSize;
    }

    public void computeAllSizes() {
        if (rootId != null) {
            computeSubtreeSize(rootId);
        }
    }

    public Long getRootId() {
        return rootId;
    }

    public Node getNode(Long id) {
        return nodes.get(id);
    }

    public Set<Long> getAllNodeIds() {
        return new HashSet<>(nodes.keySet());
    }

    public Set<Node> getNodesByIds(Set<Long> ids) {
        return ids.stream()
                .map(nodes::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    public Set<Long> getChildren(Long parentId) {
        return childrenIdMap.getOrDefault(parentId, new TreeSet<>());
    }

    public Iterable<Long> getTreeTraversal(Long rootId) {
        return treeTraverser.depthFirstPreOrder(rootId);
    }

    public Set<Long> getNodesByClassification(String classification) {
        return classificationIndex.getOrDefault(classification, Collections.emptySet());
    }

    public Long getDirectoryIdByName(String name) {
        return directoryNameIndex.get(name);
    }
}
