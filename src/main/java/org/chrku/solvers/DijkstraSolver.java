package org.chrku.solvers;

import org.chrku.grid.Cell;
import org.chrku.grid.Grid;

import java.util.*;

public class DijkstraSolver {
    private final List<List<Double>> distances;
    private final Grid grid;
    private final int startRow;
    private final int startColumn;

    public DijkstraSolver(Grid grid, int startRow, int startColumn) {
        this.grid = grid;
        this.startRow = startRow;
        this.startColumn = startColumn;
        this.distances = new ArrayList<>();

        for (int i = 0; i < grid.rows(); ++i) {
            distances.add(new ArrayList<>());
            for (int j = 0; j < grid.columns(); ++j) {
                distances.get(i).add(Double.POSITIVE_INFINITY);
            }
        }

        distances.get(startRow).set(startColumn, 0.0);
    }

    public void solve() {
        PriorityQueue<DijkstraNode> queue = new PriorityQueue<>();
        Set<Cell> visited = new HashSet<>();

        Cell initialCell = grid.getCell(startRow, startColumn);
        queue.add(new DijkstraNode(initialCell, 0.0));

        while (!queue.isEmpty()) {
            DijkstraNode currentNode = queue.poll();
            Cell currentCell = currentNode.cell();

            if (!visited.contains(currentCell)) {
                for (Cell neighbour : currentCell.getLinks()) {
                    double cost = currentNode.cost() + 1;
                    if (cost < distances.get(neighbour.getRow()).get(neighbour.getColumn())) {
                        distances.get(neighbour.getRow()).set(neighbour.getColumn(), cost);
                        queue.add(new DijkstraNode(neighbour, cost));
                    }
                }

                visited.add(currentCell);
            }
        }
    }

    private record DijkstraNode(Cell cell, double cost) implements Comparable<DijkstraNode> {
        @Override
        public int compareTo(DijkstraNode o) {
            return Double.compare(cost, o.cost());
        }
    }
}
