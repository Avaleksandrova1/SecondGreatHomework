package RootFolderHandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FilesConnector {
    private final Path pathDirectory;

    public FilesConnector(String rootFolderPath) {
        pathDirectory = Paths.get(rootFolderPath);
        if (Files.notExists(pathDirectory)) {
            System.err.println("Возникли проблемы с путем корневой папки.");
        }
        try {
            List<Path> directoryFiles = getFiles();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }

    }
    private List<Path> getFiles() throws IOException {
        List<Path> listFilePaths;
        try (Stream<Path> walk = Files.walk(pathDirectory)) {
            listFilePaths = walk.filter(Files::isRegularFile).collect(Collectors.toList());
        } catch (IOException e) {
            throw new IOException("Произошла ошибка при попытке поиска файлов в корневой папке.");
        }
        return listFilePaths;
    }

}
