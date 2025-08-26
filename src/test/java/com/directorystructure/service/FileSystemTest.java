package com.directorystructure.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.directorystructure.model.enums.FilterType;

@DisplayName("FileSystem Integration Tests")
class FileSystemTest {

    private FileSystem fileSystem;

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() {
        fileSystem = new FileSystem();
        fileSystem.loadFromCsv("directory-structure.csv");
    }

    @Test
    @DisplayName("3a) Should load CSV and creates a string containing an indented tree structure that is equal to tree.txt")
    void shouldLoadCsvAndBuildCompleteTreeStructure() throws IOException {
        String actualTree = fileSystem.buildTree();

        try (InputStream is = getClass().getClassLoader().getResourceAsStream("tree.txt")) {
            assertThat(is).isNotNull();
            String expectedTree = new String(is.readAllBytes(), StandardCharsets.UTF_8);

            actualTree = actualTree.replace("\r\n", "\n").trim();
            expectedTree = expectedTree.replace("\r\n", "\n").trim();

            assertThat(actualTree).isEqualTo(expectedTree);
        }
    }

    @Test
    @DisplayName("3b) Creates a string containing file nodes with classification Top secret that is equal to top-secret.txt")
    void shouldExecute3bTopSecretFiles() throws IOException {
        String result = fileSystem.search(
                new SearchCriteria().where(FilterType.CLASSIFICATION, "Top secret"));

        try (InputStream is = getClass().getClassLoader().getResourceAsStream("top-secret.txt")) {
            assertThat(is).isNotNull();
            String expectedTree = new String(is.readAllBytes(), StandardCharsets.UTF_8);

            result = result.replace("\r\n", "\n").trim();
            expectedTree = expectedTree.replace("\r\n", "\n").trim();

            assertThat(result).isEqualTo(expectedTree);
        }
    }

    @Test
    @DisplayName("3c) Creates a string containing file nodes with classification secret that is equal to secret.txt")
    void shouldExecute3cSecretFiles() throws IOException {

        String result = fileSystem.search(
                new SearchCriteria().where(FilterType.CLASSIFICATION, "Secret"));

        try (InputStream is = getClass().getClassLoader().getResourceAsStream("secret.txt")) {
            assertThat(is).isNotNull();
            String expectedTree = new String(is.readAllBytes(), StandardCharsets.UTF_8);

            result = result.replace("\r\n", "\n").trim();
            expectedTree = expectedTree.replace("\r\n", "\n").trim();

            assertThat(result).isEqualTo(expectedTree);
        }
    }

    @Test
    @DisplayName("3d) Creates a string containing file nodes with classification secret that is equal to secret.txt")
    void shouldExecute3dSecretOrTopSecretFiles() throws IOException {

        String result = fileSystem.search(
                new SearchCriteria().where(FilterType.CLASSIFICATION, "Secret")
                        .or(FilterType.CLASSIFICATION, "Top secret"));

        try (InputStream is = getClass().getClassLoader().getResourceAsStream("secret-or-top-secret.txt")) {
            assertThat(is).isNotNull();
            String expectedTree = new String(is.readAllBytes(), StandardCharsets.UTF_8);

            result = result.replace("\r\n", "\n").trim();
            expectedTree = expectedTree.replace("\r\n", "\n").trim();

            assertThat(result).isEqualTo(expectedTree);
        }
    }

    @Test
    @DisplayName("3e) Calculates the sum of size for all file nodes with classification Public")
    void shouldExecute3eSizeCalculation() {

        String result = fileSystem.search(
                new SearchCriteria()
                        .where(FilterType.CLASSIFICATION, "Public")
                        .size());

        assertThat(result).matches("\\d+");
        int totalSize = Integer.parseInt(result);
        assertThat(totalSize).isEqualTo(120);
    }

    @Test
    @DisplayName("3f) Creates a string containing all file nodes under \"folder11\" with classification other than Public")
    void shouldExecuteMainJavaQuery3dFilesInFolder11AndNotPublic() throws IOException {

        String result = fileSystem.search(
                new SearchCriteria()
                        .where(FilterType.DIRECTORY_NAME, "folder11")
                        .and(FilterType.CLASSIFICATION, "Public", true));
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("non-public-folder11.txt")) {
            assertThat(is).isNotNull();
            String expectedTree = new String(is.readAllBytes(), StandardCharsets.UTF_8);

            result = result.replace("\r\n", "\n").trim();
            expectedTree = expectedTree.replace("\r\n", "\n").trim();

            assertThat(result).isEqualTo(expectedTree);
        }

    }

    /* Testing the search functionality */
    @Test
    @DisplayName("Should find files by classification")
    void shouldFindFilesByClassification() {
        String result = fileSystem.search(
                new SearchCriteria().where(FilterType.CLASSIFICATION, "Public"));

        String expected = String.join(System.lineSeparator(),
                "name = file5, type = File, size = 50, classification = Public, checksum = 42",
                "name = file7, type = File, size = 70, classification = Public, checksum = 42");

        assertThat(result).isEqualToIgnoringNewLines(expected);
    }

    @Test
    @DisplayName("Should find files in directory tree")
    void shouldFindFilesInDirectoryTree() {
        String result = fileSystem.search(new SearchCriteria().where(FilterType.DIRECTORY_NAME, "folder11"));

        String expected = String.join(System.lineSeparator(),
                "name = file1, type = File, size = 10, classification = Secret, checksum = 42",
                "name = file5, type = File, size = 50, classification = Public, checksum = 42",
                "name = file6, type = File, size = 60, classification = Secret, checksum = 42",
                "name = file7, type = File, size = 70, classification = Public, checksum = 42",
                "name = file8, type = File, size = 80, classification = Secret, checksum = 42",
                "name = file9, type = File, size = 90, classification = Top secret, checksum = 42");

        assertThat(result).isEqualToIgnoringNewLines(expected);
    }

    @Test
    @DisplayName("Should find files size in directory tree")
    void shouldFindFilesSizeInDirectoryTree() {
        String result = fileSystem.search(new SearchCriteria().where(FilterType.DIRECTORY_NAME, "folder10").size());

        assertThat(result).matches("\\d+");
        int totalSize = Integer.parseInt(result);
        assertThat(totalSize).isEqualTo(170);
    }

    @Test
    @DisplayName("Should find files size in directory tree")
    void shouldFindFilesSizeInDirectoryTreeWithNegation() {
        String result = fileSystem
                .search(new SearchCriteria().where(FilterType.DIRECTORY_NAME, "folder3", true).size());

        assertThat(result).matches("\\d+");
        int totalSize = Integer.parseInt(result);
        assertThat(totalSize).isEqualTo(210);
    }

    @Test
    @DisplayName("Should perform AND logic with negation and size correctly")
    void shouldPerformAndLogicCorrectly() {
        String result = fileSystem.search(
                new SearchCriteria()
                        .where(FilterType.DIRECTORY_NAME, "folder11")
                        .and(FilterType.CLASSIFICATION, "Secret", true)
                        .size());

        assertThat(result).matches("\\d+");
        int totalSize = Integer.parseInt(result);
        assertThat(totalSize).isEqualTo(210);
    }

}