package org.chrku.algorithms;

import org.chrku.grid.Cell;
import org.chrku.grid.Grid;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class AldousBroder implements MazeGenerator {
    @Override
    public void generate(Grid grid) {
        int unvisited = grid.size() - 1;
        Cell current = grid.getRandomCell(ThreadLocalRandom.current());

        while (unvisited > 0) {
            Cell neighbour = current.neighbours()
                    .get(ThreadLocalRandom.current().nextInt(current.neighbours().size()));
            if (neighbour.getLinks().isEmpty()) {
                current.link(neighbour, true);
                --unvisited;
            }

            current = neighbour;
        }
    }
}
