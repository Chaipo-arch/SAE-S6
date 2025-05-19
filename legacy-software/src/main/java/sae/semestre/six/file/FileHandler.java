package sae.semestre.six.file;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.function.Consumer;

@Service
public class FileHandler {

    public void writeToFile(String file, String content) {
        try {
            createFileAndParentDirectories(file);

            // Write contents to file
            FileWriter fileWriter = new FileWriter(file, true);
            fileWriter.write(content);
            fileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException("File" + file + " cannot be opened", e);
        }
    }

    public void writeToFile(String file, Consumer<FileWriter> onWrite) {
        try (FileWriter fileWriter = new FileWriter(file, true)) {
            onWrite.accept(fileWriter);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void createFileAndParentDirectories(String filepath) throws IOException {
        File f = new File(filepath);
        createFileAndParentDirectories(f);
    }

    private void createFileAndParentDirectories(File f) throws IOException {
        // Create parent directories
        Files.createDirectories(f.toPath().getParent());

        // Create file
        if (!f.exists()) {
            Files.createFile(f.toPath());
        }
    }
}
