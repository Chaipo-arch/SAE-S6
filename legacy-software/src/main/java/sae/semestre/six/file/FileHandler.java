package sae.semestre.six.file;

import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.util.function.Consumer;

@Service
public class FileHandler {

    public void writeToFile(String file,String content) {

        try (FileWriter fileWriter = new FileWriter(file, true)) {
            fileWriter.write(content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void writeToFile(String file, Consumer<FileWriter> onWrite) {
        try (FileWriter fileWriter = new FileWriter(file, true)) {
            onWrite.accept(fileWriter);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
