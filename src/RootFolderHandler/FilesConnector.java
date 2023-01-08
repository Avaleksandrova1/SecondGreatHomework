package RootFolderHandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Класс, в котором выполняется поиск файлов дирекории, создает граф из списка путей,
 * выполняется построение зависимостей между файлолами с помощью наличия "require".
 */
public class FilesConnector {

    private final Path pathDirectory; // директория с необходимыми файлами.
    private final int countFiles; // количество файлов, найденных в директории.
    private List<List<Integer>> graphFilesPaths; // Представление путей файлов в виде графа.

    private final List<Path> directoryFiles; // список файлов в каталогах и подкаьалогах, найденных в корневой папке.

    private static final String REGULAR_EXPRESSION_FOR_FILE_PATH = "(require ‘)(.*?)(’)"; // Регулярное выражения для поиска в файлах "require".

    /**
     * Выполняет поиск в каталогах и подкаталогах всех файлов в заданной корневой папке,
     * проверяет наличие циклической зависимости, выводит в консоль список для конктреного файла, необходимых файлов.
     * @param rootFolderPath Корневая папка, необходимая для поиска файлов.
     */

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
        makeGraphFromList();
        List<Integer> cyclicDependenciesFiles = WorkWithFileDependencies.findFileDependencies(countFiles, graphFilesPaths);

        // Проверка на наличие циклической зависимости
        if (!cyclicDependenciesFiles.isEmpty()) {
            StringJoiner errorCycleDependence = new StringJoiner("\n");
            errorCycleDependence.add("Найден цикл в зависимостях, из-за чего невозможно соединить файлы. Цикл файлов:");
            cyclicDependenciesFiles.forEach((i) -> errorCycleDependence.add(directoryFiles.get(i).toString()));
            throw new RuntimeException(errorCycleDependence.toString());
        }
        WorkWithResult.writeResultInFile(SortingGraph.startSortingGraph(countFiles, graphFilesPaths), pathDirectory,directoryFiles );

        // Вывод в консоль список файлов
        for (Path path : directoryFiles) {
            try {
                System.out.printf("Необходимо для файла %s :\n", path.toString());
                findRequirePathsInFile(path).forEach(System.out::println);
                System.out.println("\n");

            } catch (IOException e) {
                throw new RuntimeException("Возникли проблемы с отрытием файла.");
            }
        }
    }

    /** Формируется граф из списка путей файлов корневой папки.
     * Граф создается таким образом, что каждое число является
     * индексом пути файла из  полученного списка directoryFiles.
     */
    private void makeGraphFromList() {
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
    }

    /**
     * Происходит поиск файлов в корневой папке.
     * @return                  Список, содержащий пути до всех найденных файлов.
     * @throws IOException      в случае, если происходит ошибка при попытке поиска файлоа.
     */
    private List<Path> getFiles() throws IOException {
        List<Path> listFilePaths;
        try (Stream<Path> walk = Files.walk(pathDirectory)) {
            listFilePaths = walk.filter(Files::isRegularFile).collect(Collectors.toList());
        } catch (IOException e) {
            throw new IOException("Произошла ошибка при попытке поиска файлов в корневой папке.");
        }
        return listFilePaths;
    }

    /**
     * Осуществляет обработку и проверку наличия файлов с "require".
     * @param pathFileWithRequire   путь к файлу для поиска "require".
     * @return                      Список путей к необходимым файлам корневой папки.
     * @throws IOException          если происходит ошибка открытия файла.
     */
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


}
