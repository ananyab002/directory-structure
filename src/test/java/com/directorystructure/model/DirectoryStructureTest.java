package com.directorystructure.model;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.directorystructure.exceptions.ValidationException;

@DisplayName("DirectoryStructure Core Logic Tests")
class DirectoryStructureTest {

    private DirectoryStructure directoryStructure;

    @BeforeEach
    void setUp() {
        directoryStructure = new DirectoryStructure();
    }

    @Test
    @DisplayName("Should build tree with correct parent-child relationships and alphabetical sorting")
    void shouldBuildTreeWithCorrectRelationshipsAndSorting() {
        Node root = new Node(1L, null, "root", null, NodeType.DIRECTORY, null, null);
        Node childZ = new Node(2L, 1L, "zzz.txt", 100L, NodeType.FILE, "Public", "hash1");
        Node childA = new Node(3L, 1L, "aaa.txt", 200L, NodeType.FILE, "Secret", "hash2");
        Node subdir = new Node(4L, 1L, "middleware", null, NodeType.DIRECTORY, null, null);

        directoryStructure.addNode(root);
        directoryStructure.addNode(childZ);
        directoryStructure.addNode(childA);
        directoryStructure.addNode(subdir);

        assertThat(directoryStructure.getRootId()).isEqualTo(1);
        assertThat(directoryStructure.getChildren(1L)).containsExactly(3L, 4L, 2L);
        assertThat(directoryStructure.getChildren(4L)).isEmpty();
    }

    @Test
    @DisplayName("Should calculate directory sizes through nested hierarchy")
    void shouldCalculateDirectorySizes() {
        Node root = new Node(1L, null, "root", null, NodeType.DIRECTORY, null, null);
        Node docs = new Node(2L, 1L, "docs", null, NodeType.DIRECTORY, null, null);
        Node src = new Node(3L, 1L, "src", null, NodeType.DIRECTORY, null, null);
        Node nested = new Node(4L, 2L, "nested", null, NodeType.DIRECTORY, null, null);
        Node file1 = new Node(5L, 4L, "deep.txt", 50L, NodeType.FILE, "Secret", "hash1");
        Node file2 = new Node(6L, 3L, "main.java", 150L, NodeType.FILE, "Public", "hash2");
        Node file3 = new Node(7L, 1L, "readme.md", 25L, NodeType.FILE, "Public", "hash3");

        directoryStructure.addNode(root);
        directoryStructure.addNode(docs);
        directoryStructure.addNode(src);
        directoryStructure.addNode(nested);
        directoryStructure.addNode(file1);
        directoryStructure.addNode(file2);
        directoryStructure.addNode(file3);

        directoryStructure.computeAllSizes();

        assertThat(directoryStructure.getNode(4L).getSize()).isEqualTo(50);
        assertThat(directoryStructure.getNode(2L).getSize()).isEqualTo(50);
        assertThat(directoryStructure.getNode(3L).getSize()).isEqualTo(150);
        assertThat(directoryStructure.getNode(1L).getSize()).isEqualTo(225);
    }

    @Test
    @DisplayName("Should index nodes by classification and handle multiple classifications")
    void shouldIndexNodesByClassification() {
        Node root = new Node(1L, null, "root", null, NodeType.DIRECTORY, null, null);
        Node secretFile1 = new Node(2L, 1L, "classified1.txt", 100L, NodeType.FILE, "Secret", "hash1");
        Node secretFile2 = new Node(3L, 1L, "classified2.txt", 200L, NodeType.FILE, "Secret", "hash2");
        Node publicFile = new Node(4L, 1L, "public.txt", 50L, NodeType.FILE, "Public", "hash3");
        Node topSecretFile = new Node(5L, 1L, "topsecret.txt", 75L, NodeType.FILE, "Top secret", "hash4");
        Node unclassifiedFile = new Node(6L, 1L, "unclassified.txt", 25L, NodeType.FILE, null, "hash5");

        directoryStructure.addNode(root);
        directoryStructure.addNode(secretFile1);
        directoryStructure.addNode(secretFile2);
        directoryStructure.addNode(publicFile);
        directoryStructure.addNode(topSecretFile);
        directoryStructure.addNode(unclassifiedFile);

        assertThat(directoryStructure.getNodesByClassification("Secret"))
                .containsExactlyInAnyOrder(2L, 3L);
        assertThat(directoryStructure.getNodesByClassification("Public"))
                .containsExactly(4L);
        assertThat(directoryStructure.getNodesByClassification("Top secret"))
                .containsExactly(5L);
        assertThat(directoryStructure.getNodesByClassification("Nonexistent"))
                .isEmpty();
        assertThat(directoryStructure.getNodesByClassification(null))
                .isEmpty();
    }

    @Test
    @DisplayName("Should index directories by name and perform depth-first pre-order traversal")
    void shouldIndexDirectoriesAndTraverse() {
        Node root = new Node(1L, null, "project", null, NodeType.DIRECTORY, null, null);
        Node src = new Node(2L, 1L, "src", null, NodeType.DIRECTORY, null, null);
        Node test = new Node(3L, 1L, "test", null, NodeType.DIRECTORY, null, null);
        Node main = new Node(4L, 2L, "main", null, NodeType.DIRECTORY, null, null);
        Node file1 = new Node(5L, 4L, "App.java", 200L, NodeType.FILE, "Public", "hash1");
        Node file2 = new Node(6L, 3L, "AppTest.java", 150L, NodeType.FILE, "Public", "hash2");

        directoryStructure.addNode(root);
        directoryStructure.addNode(src);
        directoryStructure.addNode(test);
        directoryStructure.addNode(main);
        directoryStructure.addNode(file1);
        directoryStructure.addNode(file2);

        assertThat(directoryStructure.getDirectoryIdByName("project")).isEqualTo(1);
        assertThat(directoryStructure.getDirectoryIdByName("src")).isEqualTo(2);
        assertThat(directoryStructure.getDirectoryIdByName("main")).isEqualTo(4);
        assertThat(directoryStructure.getDirectoryIdByName("nonexistent")).isNull();

        Iterable<Long> traversal = directoryStructure.getTreeTraversal(1L);
        assertThat(traversal).containsExactly(1L, 2L, 4L, 5L, 3L, 6L);
    }

    @Test
    @DisplayName("Should throw ValidationException for invalid inputs")
    void shouldThrowValidationExceptionForInvalidInputs() {
        assertThatThrownBy(() -> directoryStructure.addNode(null))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Node cannot be null");

        Node nodeWithNullId = new Node(null, null, "invalid", null, NodeType.DIRECTORY, null, null);
        assertThatThrownBy(() -> directoryStructure.addNode(nodeWithNullId))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Node ID cannot be null");
    }

    @Test
    @DisplayName("Should throw ValidationException when files try to have children")
    void shouldThrowExceptionWhenFilesHaveChildren() {
        Node root = new Node(1L, null, "root", null, NodeType.DIRECTORY, null, null);
        Node file = new Node(2L, 1L, "document.txt", 100L, NodeType.FILE, "Public", "hash1");
        Node impossibleChild = new Node(3L, 2L, "child_of_file.txt", 50L, NodeType.FILE, "Secret", "hash2");

        directoryStructure.addNode(root);
        directoryStructure.addNode(file);

        assertThatThrownBy(() -> directoryStructure.addNode(impossibleChild))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Only directories can have children");
    }

    @Test
    @DisplayName("Should throw ValidationException when multiple root nodes are added")
    void shouldThrowExceptionForMultipleRoots() {
        Node firstRoot = new Node(1L, null, "first_root", null, NodeType.DIRECTORY, null, null);
        Node secondRoot = new Node(2L, null, "second_root", null, NodeType.DIRECTORY, null, null);

        directoryStructure.addNode(firstRoot);
        assertThat(directoryStructure.getRootId()).isEqualTo(1);

        // Should throw ValidationException when trying to add second root
        assertThatThrownBy(() -> directoryStructure.addNode(secondRoot))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Multiple root nodes detected");
    }

    @Test
    @DisplayName("Should allow directories to have children")
    void shouldAllowDirectoriesToHaveChildren() {
        Node root = new Node(1L, null, "root", null, NodeType.DIRECTORY, null, null);
        Node subdir = new Node(2L, 1L, "subdir", null, NodeType.DIRECTORY, null, null);
        Node file1 = new Node(3L, 1L, "file1.txt", 100L, NodeType.FILE, "Public", "hash1");
        Node file2 = new Node(4L, 2L, "file2.txt", 150L, NodeType.FILE, "Secret", "hash2");

        assertThatCode(() -> {
            directoryStructure.addNode(root);
            directoryStructure.addNode(subdir);
            directoryStructure.addNode(file1);
            directoryStructure.addNode(file2);
        }).doesNotThrowAnyException();

        assertThat(directoryStructure.getChildren(1L)).containsExactlyInAnyOrder(2L, 3L);
        assertThat(directoryStructure.getChildren(2L)).containsExactly(4L);
    }

    @Test
    @DisplayName("Should handle edge cases for empty structure and single nodes")
    void shouldHandleEdgeCases() {
        assertThat(directoryStructure.getRootId()).isNull();
        assertThat(directoryStructure.getAllNodeIds()).isEmpty();
        assertThat(directoryStructure.getChildren(999L)).isEmpty();

        Node singleRoot = new Node(42L, null, "isolated", null, NodeType.DIRECTORY, "Top secret", null);
        directoryStructure.addNode(singleRoot);

        assertThat(directoryStructure.getRootId()).isEqualTo(42);
        assertThat(directoryStructure.getChildren(42L)).isEmpty();
        assertThat(directoryStructure.getAllNodeIds()).containsExactly(42L);
    }

    @Test
    @DisplayName("Should test getNodesByIds functionality")
    void shouldTestGetNodesByIds() {
        Node root = new Node(1L, null, "root", null, NodeType.DIRECTORY, null, null);
        Node file1 = new Node(2L, 1L, "file1.txt", 100L, NodeType.FILE, "Public", "hash1");
        Node file2 = new Node(3L, 1L, "file2.txt", 200L, NodeType.FILE, "Secret", "hash2");

        directoryStructure.addNode(root);
        directoryStructure.addNode(file1);
        directoryStructure.addNode(file2);

        Set<Long> requestedIds = Set.of(1L, 2L, 999L);
        Set<Node> nodes = directoryStructure.getNodesByIds(requestedIds);

        assertThat(nodes).hasSize(2);
        assertThat(nodes.stream().map(Node::getId))
                .containsExactlyInAnyOrder(1L, 2L);
    }
}