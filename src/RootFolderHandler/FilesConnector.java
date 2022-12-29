package RootFolderHandler;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
public class FilesConnector {
    public FilesConnector(String rootFolderPath) {
        Path pathDirectory = Paths.get(rootFolderPath);
        if (Files.notExists(pathDirectory)) {
            System.err.println("Возникли проблемы с путем коревой папки.");
        }


    }
}
