package algorithms;

import grid.Cell;
import grid.Grid;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BinaryTree implements MazeGenerator {
    @Override
    public void generate(Grid grid) {
        Random rand = new Random();
        grid.cellIterator().forEachRemaining((Cell c) -> {
            List<Cell> neighbours = new ArrayList<>();

            if (c.getNorth() != null) {
                neighbours.add(c.getNorth());
            }
            if (c.getEast() != null) {
                neighbours.add(c.getEast());
            }

            if (neighbours.size() > 0) {
                int index = rand.nextInt(neighbours.size());
                c.link(neighbours.get(index), true);
            }
        });
    }
}
