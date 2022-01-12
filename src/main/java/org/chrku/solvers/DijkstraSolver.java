package org.chrku.solvers;

import org.chrku.grid.Cell;
import org.chrku.grid.Grid;

import java.util.*;

public class DijkstraSolver {
    private final List<List<Double>> distances;
    private final List<List<DijkstraNode>> nodes;

    private final Grid grid;

    private final int startRow;
    private final int startColumn;

    public DijkstraSolver(Grid grid, int startRow, int startColumn) {
        this.grid = grid;
        this.startRow = startRow;
        this.startColumn = startColumn;

        this.distances = new ArrayList<>();
        this.nodes = new ArrayList<>();

        for (int i = 0; i < grid.rows(); ++i) {
            distances.add(new ArrayList<>());
            nodes.add(new ArrayList<>());
            for (int j = 0; j < grid.columns(); ++j) {
                distances.get(i).add(Double.POSITIVE_INFINITY);
                nodes.get(i).add(null);
            }
        }

        distances.get(startRow).set(startColumn, 0.0);
    }

    public List<List<Double>> getDistances() {
        return distances;
    }

    public Set<Cell> getPathTo(int endRow, int endColumn) {
        DijkstraNode endNode = nodes.get(endRow).get(endColumn);
        if (endNode == null) {
            return Collections.emptySet();
        }

        Set<Cell> path = new HashSet<>();

        while (endNode.predecessor() != null) {
            path.add(endNode.cell());
            endNode = endNode.predecessor();
        }
        path.add(endNode.cell());

        return path;
    }

    public void solve() {
        PriorityQueue<DijkstraNode> queue = new PriorityQueue<>();
        Set<Cell> visited = new HashSet<>();

        Cell initialCell = grid.getCell(startRow, startColumn);
        var startNode = new DijkstraNode(initialCell, 0.0, null);
        queue.add(startNode);
        nodes.get(startRow).set(startColumn, startNode);

        while (!queue.isEmpty()) {
            DijkstraNode currentNode = queue.poll();
            Cell currentCell = currentNode.cell();

            if (!visited.contains(currentCell)) {
                for (Cell neighbour : currentCell.getLinks()) {
                    double cost = currentNode.cost() + 1;
                    if (cost < distances.get(neighbour.getRow()).get(neighbour.getColumn())) {
                        var newNode = new DijkstraNode(neighbour, cost, currentNode);
                        distances.get(neighbour.getRow()).set(neighbour.getColumn(), cost);
                        nodes.get(neighbour.getRow()).set(neighbour.getColumn(), newNode);
                        queue.add(newNode);
                    }
                }

                visited.add(currentCell);
            }
        }
    }

    private record DijkstraNode(Cell cell, double cost, DijkstraNode predecessor) implements Comparable<DijkstraNode> {
        @Override
        public int compareTo(DijkstraNode o) {
            return Double.compare(cost, o.cost());
        }
    }
}
