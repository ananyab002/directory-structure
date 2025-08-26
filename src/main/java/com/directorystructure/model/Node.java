package com.directorystructure.model;

public class Node {
    private final Long id;
    private final Long parentId;
    private final String name;
    private Long size;
    private final NodeType type;
    private final String classification;
    private final String checksum;

    public Node(Long id, Long parentId, String name, Long size,
            NodeType type, String classification, String checksum) {
        this.id = id;
        this.parentId = parentId;
        this.name = name;
        this.size = size;
        this.type = type;
        this.classification = classification;
        this.checksum = checksum;
    }

    public boolean isDirectory() {
        return type == NodeType.DIRECTORY;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Long getId() {
        return id;
    }

    public Long getParentId() {
        return parentId;
    }

    public String getName() {
        return name;
    }

    public Long getSize() {
        return size;
    }

    public NodeType getType() {
        return type;
    }

    public String getClassification() {
        return classification;
    }

    public String getChecksum() {
        return checksum;
    }
}