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
    private int countFiles;
    private List<List<Integer>> graphFilesPaths;

    private List<Path> directoryFiles;
    private static final String REGULAR_EXPRESSION_FOR_FILE_PATH = "(require ‘)(.*?)(’)";
    private int firstIndexOfCycleGraph;
    private int lastIndexOfCycleGraph;

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
        graphFilesPaths = new ArrayList<>();
        for (int way = 0; way < countFiles; ++way) {
            List<Path> pathToFile;
            try {
                pathToFile = findRequirePathsInFile(directoryFiles.get(way));
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }
            graphFilesPaths.add(new ArrayList<>());
            for (Path path : pathToFile) {
                graphFilesPaths.get(way).add(directoryFiles.indexOf(path));
            }
        }
        List<Integer> cyclicDependenciesFiles = findFileDependencies(countFiles);
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
        Pattern p = Pattern.compile(REGULAR_EXPRESSION_FOR_FILE_PATH);
        Matcher matcher = p.matcher(fileData);

        while (matcher.find()) {
            Path desiredPathToRequireFile = Paths.get(String.valueOf(pathDirectory), matcher.group(2));
            if (Files.exists(desiredPathToRequireFile)) {
                allPathsInRequire.add(desiredPathToRequireFile);
            }
        }
        return allPathsInRequire;
    }

    public List<Integer> findFileDependencies(int countPathsFiles) {
        int[] metNodes = new int[countPathsFiles];
        int[] arrayOfNodesForCycle = new int[countPathsFiles];
        firstIndexOfCycleGraph = -1;
        for (int i = 0; i < countPathsFiles; ++i) {
            metNodes[i] = 0;
            arrayOfNodesForCycle[i] = -1;
        }
        for (int curNode = 0; curNode < countPathsFiles; ++curNode) {
            if (isCyclicalDependence(curNode, metNodes, arrayOfNodesForCycle)) {
                break;
            }
        }
        if (firstIndexOfCycleGraph != -1) {
            List<Integer> listOfIndex = new ArrayList<>();
            listOfIndex.add(firstIndexOfCycleGraph);
            for (int i = lastIndexOfCycleGraph; i != firstIndexOfCycleGraph; i = arrayOfNodesForCycle[i]) {
                listOfIndex.add(i);
            }
            listOfIndex.add(firstIndexOfCycleGraph);
            return listOfIndex;
        }
        return new ArrayList<>();
    }

    private boolean isCyclicalDependence(int currentNode, int[] metNodes, int[] arrayOfNodesForCycle) {
        metNodes[currentNode] = 1;
        for (int i = 0; i < graphFilesPaths.get(currentNode).size(); ++i) {
            int nextNode = graphFilesPaths.get(currentNode).get(i);
            if (metNodes[nextNode]== 0) {
                arrayOfNodesForCycle[nextNode] = currentNode;
                if (isCyclicalDependence(nextNode, metNodes, arrayOfNodesForCycle)) {
                    return true;
                }
            }else {
                if (metNodes[nextNode] == 1) {
                    firstIndexOfCycleGraph = nextNode;
                    lastIndexOfCycleGraph = currentNode;
                    return true;
                }
            }
        }
        metNodes[currentNode] = 2;
        return false;
    }

}
