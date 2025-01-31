package xyz.vann.chat_guy;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class FileService {
    private final Logger logger = LoggerFactory.getLogger(FileService.class);

    public void moveFileFromDocsToProcessed(String fileName){
        String sourceFilePath = Paths.get(getDocsDirPath(), fileName).toString();
        String destFilePath = Paths.get(getProcessedDirPath(), fileName).toString();
        moveFile(sourceFilePath, destFilePath);
    }
    public List<String> getDocsDirFileNames(){
        return getFileName(getDocsDirPath());
    }

    public Resource getResource(String fileName){
        return new FileSystemResource(getDocsDirPath() + File.separator + fileName);
    }
    private List<String> getFileName(String dirPath){
        try (Stream<Path> files = Files.list(Paths.get(dirPath))){
            return files
                    .filter(Files::isRegularFile)
                    .map(path -> path.getFileName().toString())
                    .collect(Collectors.toList());
        } catch (IOException e){
            System.out.println("Error: " + e.getMessage());
            return List.of();
        }
    }

    private void moveFile(String sourcePath, String destinationPath) {
        try {
            Path source = Paths.get(sourcePath);
            Path destination = Paths.get(destinationPath);

            // Create destination directory if it doesn't exist
            Files.createDirectories(destination.getParent());

            // Move file
            Files.move(source, destination, StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException e) {
            logger.error("Error moving file from {} to {}", sourcePath, destinationPath, e);
        }
    }

    private String getDocsDirPath(){
        Path path = Paths.get("src", "main", "resources", "docs");
        return path.toFile().getAbsolutePath();
    }

    private String getProcessedDirPath(){
        Path path = Paths.get("src", "main", "resources", "processed");
        return path.toFile().getAbsolutePath();
    }
}
