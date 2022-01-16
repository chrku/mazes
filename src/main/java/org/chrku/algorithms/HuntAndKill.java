package org.chrku.algorithms;

import org.chrku.grid.Cell;
import org.chrku.grid.Grid;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class HuntAndKill implements MazeGenerator {
    @Override
    public void generate(Grid grid) {
        Random rng = ThreadLocalRandom.current();
        Cell current = grid.getRandomCell(rng);

        while (current != null) {
            List<Cell> unvisitedNeighbours = current.neighbours().stream()
                    .filter((Cell c) -> c.getLinks().isEmpty())
                    .toList();

            if (!unvisitedNeighbours.isEmpty()) {
                Cell neighbour = unvisitedNeighbours.get(rng.nextInt(unvisitedNeighbours.size()));
                current.link(neighbour, true);
                current = neighbour;
            } else {
                current = null;

                for (var iterator = grid.cellIterator(); iterator.hasNext(); ) {
                    Cell cell = iterator.next();

                    List<Cell> visitedNeighbours = cell.neighbours().stream()
                            .filter((Cell c) -> !c.getLinks().isEmpty())
                            .toList();

                    if (cell.getLinks().isEmpty() && !visitedNeighbours.isEmpty()) {
                        current = cell;

                        Cell neighbour = visitedNeighbours.get(rng.nextInt(visitedNeighbours.size()));
                        current.link(neighbour, true);
                    }
                }
            }
        }
    }
}
