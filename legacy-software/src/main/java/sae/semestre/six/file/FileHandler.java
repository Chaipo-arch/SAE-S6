package sae.semestre.six.file;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;

/**
 * Gère les échanges avec les fichiers
 */
@Service
public class FileHandler {

    /**
     * Ecrit les données vers un fichier
     * @param file le chemin absolu du fichier
     * @param content le contenu à écrire
     */
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

    /**
     * Ecrit un contenu vers un fichier via une fonction dédiée
     * @param file le chemin absolu du fichier à écrire
     * @param onWrite la fonction d'écriture dans le fichier
     */
    public void writeToFile(String file, Consumer<FileWriter> onWrite) {
        try {
            createFileAndParentDirectories(file);
            FileWriter fileWriter = new FileWriter(file, true);
            onWrite.accept(fileWriter);
            fileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Lit des contenus depuis un fichier
     * @param file le chemin absolu du fichier à lire
     * @return le contenu du fichier sous forme de chaîne de caractères
     */
    public String readFromFile(String file) {
        try {
            return Files.readString(Path.of(file));
        } catch (IOException e) {
            throw new RuntimeException("Cannot read file " + file, e);
        }
    }

    /**
     * Crée un fichier et ses dossiers parents, le cas échéant
     * @param filepath le chemin absolu du fichier à créer
     * @throws IOException s'il est impossible de créer les dossiers
     */
    private void createFileAndParentDirectories(String filepath) throws IOException {
        File f = new File(filepath);
        createFileAndParentDirectories(f);
    }

    /**
     * Crée un fichier et ses dossiers parents, le cas échéant
     * @param f le fichier à créer
     * @throws IOException s'il est impossible de créer les dossiers
     */
    private void createFileAndParentDirectories(File f) throws IOException {
        // Create parent directories
        Files.createDirectories(f.toPath().getParent());

        // Create file
        if (!f.exists()) {
            Files.createFile(f.toPath());
        }
    }
}
