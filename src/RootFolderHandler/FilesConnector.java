package RootFolderHandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FilesConnector {
    private final Path pathDirectory;
    private final int countFiles;
    private List<Path> directoryFiles;
    private static final String regularExpressionForFilePath = "(require ‘)(.*?)(’)";
    public FilesConnector(String rootFolderPath) {
        pathDirectory = Paths.get(rootFolderPath);
        if (Files.notExists(pathDirectory)) {
            System.err.println("Возникли проблемы с путем корневой папки.");
        }
        try {
            directoryFiles = getFiles();
            countFiles = directoryFiles.size();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        List<List<Integer>> graphFilesPaths = new ArrayList<>();
        for (int way = 0; way < countFiles; ++way) {
            List<Path> pathToFile;
            try {
                pathToFile = findRequirePathsInFile(directoryFiles.get(way));
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }
            graphFilesPaths.add(new ArrayList<>());
            for (Path path:pathToFile) {
                graphFilesPaths.get(way).add(directoryFiles.indexOf(path));
            }
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
    private List<Path> findRequirePathsInFile(Path pathFileWithRequire) throws IOException {
        List<Path> allPathsInRequire = new ArrayList<>();
        String fileData = new String(Files.readAllBytes(pathFileWithRequire));
        Pattern p = Pattern.compile(regularExpressionForFilePath);
        Matcher matcher = p.matcher(fileData);

        while (matcher.find()) {
            Path desiredPathToRequireFile = Paths.get(String.valueOf(pathDirectory),matcher.group(2));
            if (Files.exists(desiredPathToRequireFile)) {
                allPathsInRequire.add(desiredPathToRequireFile);
            }
        }
        return allPathsInRequire;
    }

}
