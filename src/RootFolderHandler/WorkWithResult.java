package RootFolderHandler;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Класс, в котором осуществляется запись результата в один текстовый файл.
 */
public class WorkWithResult {
    /**
     * Происходит запись содержимого всех файлов в один текстовый файл AnswerFile.txt
     *
     * @param correctFilesIndexOrder корректный порядок индексов файлов из директории.
     * @param pathDirectory          директория с необходимыми файлами.
     * @param directoryFiles         все файлы в корневой папке.
     */
    public static void writeResultInFile(List<Integer> correctFilesIndexOrder, Path pathDirectory, List<Path> directoryFiles) {
        Path resultFilePath = Paths.get(pathDirectory.toString(), "AnswerFile.txt");
        try (FileWriter resultFile = new FileWriter(resultFilePath.toFile(), false)) {
            for (Integer index : correctFilesIndexOrder) {
                resultFile.write(Files.readString(directoryFiles.get(index)));
            }
        } catch (IOException e) {
            throw new RuntimeException("Произошла ошибка записи в результирующий файл.");
        }
    }
}
