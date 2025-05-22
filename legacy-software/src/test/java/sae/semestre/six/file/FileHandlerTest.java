package sae.semestre.six.file;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;

public class FileHandlerTest {

    @Test
    void verifyStringIsWrittenInFile() throws IOException {
        FileHandler fileHandler = new FileHandler();
        File file = new File("RandomDFOKJSOPMFHSOFHDJDFHjdlsdkhgsd.txt");
        file.createNewFile();

        FileReader fileReader = new FileReader(file);

        assertEquals(-1, fileReader.read(), "Le fichier créé n'est pas vide.");

        fileHandler.writeToFile("RandomDFOKJSOPMFHSOFHDJDFHjdlsdkhgsd.txt","test");

        BufferedReader bufferedReader = new BufferedReader(fileReader);
        assertEquals("test", bufferedReader.readLine(), "Le contenu du ficher n'a pas été créé.");

        bufferedReader.close();
        fileReader.close();
        if(file.exists()) {
            boolean deleted = file.delete();
            assertTrue(!file.exists(),"Le fichier n'a pas été supprimé.");
        }
    }
}