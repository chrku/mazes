package org.chrku.algorithms;

import org.chrku.grid.Cell;
import org.chrku.grid.Grid;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Sidewinder implements MazeGenerator {
    @Override
    public void generate(Grid grid) {
        Random rand = new Random();
        grid.rowIterator().forEachRemaining((List<Cell> row) -> {
            List<Cell> run = new ArrayList<>();

            for (Cell c : row) {
                run.add(c);

                boolean eastBoundary = c.getEast() == null;
                boolean northBoundary = c.getNorth() == null;

                boolean shouldClose = eastBoundary ||
                        (rand.nextBoolean() && !northBoundary);

                if (shouldClose && run.size() > 0) {
                    int index = rand.nextInt(run.size());
                    Cell toClose = run.get(index);
                    if (toClose.getNorth() != null) {
                        toClose.link(toClose.getNorth(), true);
                    }
                    run.clear();
                } else if (c.getEast() != null) {
                    c.link(c.getEast(), true);
                }
            }
        });
    }
}
