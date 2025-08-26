# Directory Structure Parser

## Core Implementation

**DirectoryStructure** - The primary domain model implementing the assignment’s requirement for a tree representation of the CSV file system.

- Direct node access by ID: O(1) lookups
- Parent-child mappings with sorted children for consistent alphabetical output
- Classification index for fast filtering
- Directory name index for quick path-based searches

**FileSystem** - The service layer that orchestrates the entire solution:

- Parses the CSV file via CsvParser
- Builds and manages the DirectoryStructure model
- Coordinates search operations through SearchEngine
- Formats output to match expected text files exactly

## Running the Solution

```bash
# Build and test
mvn clean install
mvn test

# Execute all assignment scenarios
mvn exec:java
```

## Test Validation

**FileSystemTest.java** validates each requirement against the provided expected output files:

- Loads `.txt` files from `src/test/resources/`
- Executes each search scenario and performs comparision

## Search Implementation

The SearchEngine supports all required functionality:

- Classification filters (Secret, Top Secret, Public)
- Directory-based searches including children
- Logical operations (AND, OR)
- Negation for exclusion criteria
- Size computation

Example for requirement 3f (non-public files in folder11):

```java
fileSystem.search(
new SearchCriteria()
.where(FilterType.DIRECTORY_NAME, "folder11")
.and(FilterType.CLASSIFICATION, "Public", true) // negated
);
```

## Project Structure

```
src/main/java/com/directorystructure/
├── model/ # Node entity, enums for Classification and FilterType
├── service/ # DirectoryStructure, CsvParser, SearchEngine, FileSystem
├── util/ # ResultFormatter for tree and list outputs
└── Main.java # Demonstrates all six requirements

src/test/
├── java/ # FileSystemTest with complete validation
└── resources/ # All provided expected output files
```

## Dependencies

- Java 17+ for modern language features
- Google Guava for efficient tree traversal
- JUnit 5 and AssertJ for comprehensive testing
