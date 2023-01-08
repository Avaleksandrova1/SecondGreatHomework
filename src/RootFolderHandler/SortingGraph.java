package RootFolderHandler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Класс, в котором выполняется топологическая сортировка графа.
 */

public class SortingGraph {

    /**
     * Выполняется подготовительная часть топологической сортировки.
     *
     * @param countFilesPaths   количество файлов в директории.
     * @param graphFilesPaths   количество файлов в директории.
     * @return                  отсортированная последовательность номеров списка путей.
     */
    public static List<Integer> startSortingGraph(int countFilesPaths, List<List<Integer>> graphFilesPaths) {
        List<Integer> resultSortedSequence = new ArrayList<>();
        boolean[] visitedNodes = new boolean[countFilesPaths];
        for (int curNode = 0; curNode < countFilesPaths; curNode++) {
            visitedNodes[curNode] = false;
        }

        for (int curNode = 0; curNode < countFilesPaths; curNode++) {
            if (!visitedNodes[curNode]) {
                topologicalSort(curNode, visitedNodes, resultSortedSequence, graphFilesPaths);
            }
        }
        return resultSortedSequence;
    }

    /**
     * Рекурсивная топологическая сортировка графа.
     *
     * @param currentNode     текущая вершина из списка путей.
     * @param visitedNodes    вспомогательный массив для анализирования результата использования вершины.
     * @param resultSequence  список для результата отсортированной последовательнсти.
     * @param graphFilesPaths количество файлов в директории.
     */
    private static void topologicalSort(int currentNode, boolean[] visitedNodes, List<Integer> resultSequence, List<List<Integer>> graphFilesPaths) {
        int nextNodeForCheckIndex;

        Iterator<Integer> graphIteratorOfNodes = graphFilesPaths.get(currentNode).iterator();
        visitedNodes[currentNode] = true;
        while (graphIteratorOfNodes.hasNext()) {
            nextNodeForCheckIndex = graphIteratorOfNodes.next();
            if (!visitedNodes[nextNodeForCheckIndex])
                topologicalSort(nextNodeForCheckIndex, visitedNodes, resultSequence, graphFilesPaths);
        }
        resultSequence.add(currentNode);
    }

}
