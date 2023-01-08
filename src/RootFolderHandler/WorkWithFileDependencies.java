package RootFolderHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс для работы с зависимостями файлов, в том числе и с обработкой циклических зависимостей.
 */
public class WorkWithFileDependencies {
    private static int firstIndexOfCycleGraph; // Индекс первой вершины графа
    private static int lastIndexOfCycleGraph;  // Индекс последней вершины графа

    /**
     * Рассматривает зависимости между файлами с использованием графа.
     * @param countPathsFiles   количество файлов в директории.
     * @param graphFilesPaths   количество файлов в директории.
     * @return                  Список индексов путей зависимостей файлов.
     */
    public static List<Integer> findFileDependencies(int countPathsFiles, List<List<Integer>> graphFilesPaths) {
        int[] metNodes = new int[countPathsFiles];
        int[] arrayOfNodesForCycle = new int[countPathsFiles];
        firstIndexOfCycleGraph = -1;
        for (int i = 0; i < countPathsFiles; ++i) {
            metNodes[i] = 0;
            arrayOfNodesForCycle[i] = -1;
        }
        for (int curNode = 0; curNode < countPathsFiles; ++curNode) {
            if (isCyclicalDependence(curNode, metNodes, arrayOfNodesForCycle, graphFilesPaths)) {
                break;
            }
        }
        if (firstIndexOfCycleGraph != -1) {
            List<Integer> listOfIndex = new ArrayList<>();
            listOfIndex.add(countPathsFiles);
            for (int i = lastIndexOfCycleGraph; i != countPathsFiles; i = arrayOfNodesForCycle[i]) {
                listOfIndex.add(i);
            }
            listOfIndex.add(countPathsFiles);
            return listOfIndex;
        }
        return new ArrayList<>();
    }

    /**
     * Осуществляется проверка на наличие циклической зависимости в графе.
     *
     * @param currentNode          текущий узел вершины.
     * @param metNodes             список встреченных узлы.
     * @param arrayOfNodesForCycle вспомогательный список вершин.
     * @param graphFilesPaths      граф путей файлов директории.
     * @return                     TRUE, если есть циклическая зависимость, иначе - FALSE.
     */
    private static boolean isCyclicalDependence(int currentNode, int[] metNodes, int[] arrayOfNodesForCycle,
                                                List<List<Integer>> graphFilesPaths) {
        metNodes[currentNode] = 1;
        for (int i = 0; i < graphFilesPaths.get(currentNode).size(); ++i) {
            int nextNode = graphFilesPaths.get(currentNode).get(i);
            if (metNodes[nextNode] == 0) {
                arrayOfNodesForCycle[nextNode] = currentNode;
                if (isCyclicalDependence(nextNode, metNodes, arrayOfNodesForCycle, graphFilesPaths)) {
                    return true;
                }
            } else {
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
