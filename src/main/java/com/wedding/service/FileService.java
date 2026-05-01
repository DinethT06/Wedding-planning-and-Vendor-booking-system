package com.wedding.service;

import javax.servlet.ServletContext;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class FileService {
    private final Path dataDirectory;

    public FileService(ServletContext servletContext) {
        this.dataDirectory = resolveDataDirectory(servletContext);
        ensureDataFiles();
    }

    private Path resolveDataDirectory(ServletContext servletContext) {
        List<Path> candidates = new ArrayList<>();

        candidates.add(Paths.get(System.getProperty("user.dir"), "data"));

        String realPath = servletContext.getRealPath("/");
        if (realPath != null) {
            Path webAppPath = Paths.get(realPath).toAbsolutePath();
            candidates.add(webAppPath.resolveSibling("data"));
            if (webAppPath.getParent() != null && webAppPath.getParent().getParent() != null) {
                candidates.add(webAppPath.getParent().getParent().resolve("data"));
            }
        }

        for (Path candidate : candidates) {
            if (Files.exists(candidate) || createDirectory(candidate)) {
                return candidate;
            }
        }

        throw new IllegalStateException("Unable to resolve data directory.");
    }

    private boolean createDirectory(Path path) {
        try {
            Files.createDirectories(path);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private void ensureDataFiles() {
        String[] fileNames = {
            "admins.txt",
            "vendors.txt",
            "customers.txt",
            "packages.txt",
            "bookings.txt",
            "reviews.txt"
        };

        for (String fileName : fileNames) {
            Path filePath = dataDirectory.resolve(fileName);
            if (!Files.exists(filePath)) {
                try {
                    Files.createFile(filePath);
                } catch (IOException e) {
                    throw new RuntimeException("Unable to create data file: " + fileName, e);
                }
            }
        }
    }

    public List<String> readAllLines(String fileName) {
        Path filePath = dataDirectory.resolve(fileName);
        try {
            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
            }
            return Files.readAllLines(filePath, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Unable to read file: " + fileName, e);
        }
    }

    public void writeAllLines(String fileName, List<String> lines) {
        Path filePath = dataDirectory.resolve(fileName);
        try {
            Files.write(filePath, lines, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Unable to write file: " + fileName, e);
        }
    }

    public void appendLine(String fileName, String line) {
        Path filePath = dataDirectory.resolve(fileName);
        try {
            Files.writeString(filePath, line + System.lineSeparator(), StandardCharsets.UTF_8, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException("Unable to append file: " + fileName, e);
        }
    }

    public Path getDataDirectory() {
        return dataDirectory;
    }
}
