package com.directorystructure;

import com.directorystructure.model.enums.FilterType;
import com.directorystructure.service.FileSystem;
import com.directorystructure.service.SearchCriteria;

public class Main {
    public static void main(String[] args) {

        FileSystem fileSystem = new FileSystem();

        // Initialize directory structure model from CSV
        fileSystem.loadFromCsv("directory-structure.csv");

        System.out.println("3a) === FILE SYSTEM TREE ===");
        String formattedTree = fileSystem.buildTree();
        System.out.println(formattedTree);

        System.out.println("\n3b) Secret files:");
        SearchCriteria secretFilesQuery = new SearchCriteria().where(FilterType.CLASSIFICATION, "Secret");
        System.out.println(fileSystem.search(secretFilesQuery));

        System.out.println("\n3c) Top Secret files:");
        SearchCriteria topSecretFilesQuery = new SearchCriteria().where(FilterType.CLASSIFICATION, "Top secret");
        System.out.println(fileSystem.search(topSecretFilesQuery));

        System.out.println("\n3d) Files in folder11 and other than Public:");
        SearchCriteria publicFolder11Query = new SearchCriteria()
                .where(FilterType.DIRECTORY_NAME, "folder11")
                .and(FilterType.CLASSIFICATION, "Public", true);
        System.out.println(fileSystem.search(publicFolder11Query));

        System.out.println("\n3e) Secret OR Top Secret:");
        SearchCriteria secretOrTopSecretQuery = new SearchCriteria()
                .where(FilterType.CLASSIFICATION, "Secret")
                .or(FilterType.CLASSIFICATION, "Top secret");
        System.out.println(fileSystem.search(secretOrTopSecretQuery));

        System.out.println("\n3f) Size of Public files:");
        SearchCriteria publicFilesSizeQuery = new SearchCriteria()
                .where(FilterType.CLASSIFICATION, "Public")
                .size();
        System.out.println(fileSystem.search(publicFilesSizeQuery));
    }
}