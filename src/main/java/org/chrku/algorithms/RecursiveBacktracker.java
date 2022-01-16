package org.chrku.algorithms;

import org.chrku.grid.Cell;
import org.chrku.grid.Grid;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class RecursiveBacktracker implements MazeGenerator {

    public void generateHelper(Cell current, Random rng) {
        while (true) {
            List<Cell> neighbours = current.neighbours().stream()
                    .filter((Cell c) -> c.getLinks().isEmpty())
                    .toList();
            if (neighbours.isEmpty()) {
                return;
            } else {
                Cell neighbour = neighbours.get(rng.nextInt(neighbours.size()));
                current.link(neighbour, true);
                generateHelper(neighbour, rng);
            }
        }
    }

    @Override
    public void generate(Grid grid) {
        Random rng = ThreadLocalRandom.current();
        generateHelper(grid.getRandomCell(rng), rng);
    }
}
