package org.chrku.algorithms;

import org.chrku.grid.Cell;
import org.chrku.grid.Grid;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Wilson implements MazeGenerator {
    @Override
    public void generate(Grid grid) {
        List<Cell> unvisited = new ArrayList<>();
        grid.cellIterator().forEachRemaining(unvisited::add);
        Random rng = ThreadLocalRandom.current();

        int index = rng.nextInt(unvisited.size());
        unvisited.remove(index);

        while (!unvisited.isEmpty()) {
            index = rng.nextInt(unvisited.size());
            Cell cell = unvisited.get(index);

            List<Cell> path = new ArrayList<>();
            path.add(cell);

            while (unvisited.contains(cell)) {
                cell = cell.neighbours().get(rng.nextInt(cell.neighbours().size()));
                int position = path.indexOf(cell);
                if (position == -1) {
                    path.add(cell);
                } else {
                    path = path.subList(0, position + 1);
                }
            }

            for (int i = 0; i < path.size() - 1; ++i) {
                path.get(i).link(path.get(i + 1), true);
                unvisited.remove(path.get(i));
            }
        }
    }
}
