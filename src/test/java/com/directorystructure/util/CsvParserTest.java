package com.directorystructure.util;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.directorystructure.exceptions.DataParsingException;
import com.directorystructure.model.Node;

@DisplayName("CsvParser Logic Tests")
class CsvParserTest {

    @Test
    @DisplayName("Should handle empty optional fields gracefully")
    void shouldHandleEmptyOptionalFieldsGracefully() {
        List<Node> nodes = CsvParser.parse("test-csv/test-empty-fields.csv");

        assertThat(nodes).hasSize(6);

        Node fileWithoutClassification = nodes.stream()
                .filter(n -> n.getName().equals("file1"))
                .findFirst().orElse(null);
        assertThat(fileWithoutClassification).isNotNull();
        assertThat(fileWithoutClassification.getClassification()).isEqualTo("NA");
        assertThat(fileWithoutClassification.getSize()).isEqualTo(100);
    }

    @Test
    @DisplayName("Should throw exception for non-existent resource")
    void shouldThrowExceptionForNonExistentResource() {
        assertThatThrownBy(() -> CsvParser.parse("test-csv/non-existent-file.csv"))
                .isInstanceOf(DataParsingException.class)
                .hasMessage("Resource not found: test-csv/non-existent-file.csv");
    }

    @Test
    @DisplayName("Should handle empty file")
    void shouldHandleCompletelyEmptyFile() {
        assertThatThrownBy(() -> CsvParser.parse("test-csv/test-empty.csv"))
                .isInstanceOf(DataParsingException.class)
                .hasMessage("CSV file is empty or contains only header: test-csv/test-empty.csv");
    }

    @Test
    @DisplayName("Should handle file with only header row")
    void shouldHandleFileWithOnlyHeaderRow() {
        assertThatThrownBy(() -> CsvParser.parse("test-csv/test-header-only.csv"))
                .isInstanceOf(DataParsingException.class)
                .hasMessage("CSV file is empty or contains only header: test-csv/test-header-only.csv");
    }

    @Test
    @DisplayName("Should throw exception for insufficient columns")
    void shouldThrowExceptionForInsufficientColumns() {
        assertThatThrownBy(() -> CsvParser.parse("test-csv/test-insufficient-columns.csv"))
                .isInstanceOf(DataParsingException.class)
                .hasMessageContaining("Invalid CSV format - expected 7 columns");
    }

    @Test
    @DisplayName("Should throw exception for missing required ID")
    void shouldThrowExceptionForMissingRequiredId() {
        assertThatThrownBy(() -> CsvParser.parse("test-csv/test-missing-id.csv"))
                .isInstanceOf(DataParsingException.class)
                .hasMessageContaining("Missing or invalid ID");
    }

    @Test
    @DisplayName("Should throw exception for missing required name")
    void shouldThrowExceptionForMissingRequiredName() {
        assertThatThrownBy(() -> CsvParser.parse("test-csv/test-missing-name.csv"))
                .isInstanceOf(DataParsingException.class)
                .hasMessageContaining("Missing name");
    }

    @Test
    @DisplayName("Should throw exception for invalid type")
    void shouldThrowExceptionForInvalidType() {
        assertThatThrownBy(() -> CsvParser.parse("test-csv/test-invalid-type.csv"))
                .isInstanceOf(DataParsingException.class)
                .hasMessageContaining("Invalid node type: 'invalid-type'. Must be 'file' or 'directory'");
    }

    @Test
    @DisplayName("Should throw exception for invalid number format")
    void shouldThrowExceptionForInvalidNumberFormat() {
        assertThatThrownBy(() -> CsvParser.parse("test-csv/test-invalid-numbers.csv"))
                .isInstanceOf(DataParsingException.class)
                .hasMessageContaining("Invalid number format");
    }

}
