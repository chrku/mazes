package grid;

import java.util.*;
import java.util.function.Consumer;

public class Grid {
    private final int numRows;
    private final int numColumns;

    private List<List<Cell>> cells;

    public Grid(int numRows, int numColumns) {
        this.numRows = numRows;
        this.numColumns = numColumns;
        prepareGrid();
    }

    public int size() {
        return numRows * numColumns;
    }

    public Cell getCell(int row, int col) {
        if (row < 0 || col < 0 || row >= numRows || col >= numColumns) {
            return null;
        }

        return cells.get(row).get(col);
    }

    public List<Cell> getRow(int row) {
        if (row < 0 || row >= numRows) {
            return null;
        }

        return cells.get(row);
    }

    public Cell getRandomCell(Random rng) {
        int row = rng.nextInt(numRows);
        int col = rng.nextInt(numColumns);

        return cells.get(row).get(col);
    }

    private void prepareGrid() {
        for (int i = 0; i < numRows; i++) {
            cells.add(new ArrayList<>());
            for (int j = 0; j < numColumns; j++) {
                cells.get(i).add(new Cell(i, j));
            }
        }

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numColumns; j++) {
                Cell current = cells.get(i).get(j);

                if (i - 1 >= 0) { current.setNorth(cells.get(i - 1).get(j)); }
                if (i + 1 < numRows) { current.setNorth(cells.get(i + 1).get(j)); }
                if (j - 1 >= 0) { current.setNorth(cells.get(i).get(j - 1)); }
                if (j + 1 < numColumns) { current.setNorth(cells.get(i).get(j + 1)); }
            }
        }
    }

    public class CellIterator implements Iterator<Cell> {
        private int curRow;
        private int curCol;

        public CellIterator() {
            this.curRow = 0;
            this.curCol = 0;
        }

        @Override
        public boolean hasNext() {
            return this.curRow < Grid.this.numRows;
        }

        @Override
        public Cell next() {
            var retVal = Grid.this.getCell(curRow, curCol);

            ++this.curCol;
            if (this.curCol >= Grid.this.numColumns) {
                this.curCol = 0;
                ++this.curRow;
            }

            return retVal;
        }

        @Override
        public void forEachRemaining(Consumer<? super Cell> action) {
            while (hasNext()) {
                action.accept(next());
            }
        }
    }

    public class RowIterator implements Iterator<List<Cell>> {
        private int curRow;

        public RowIterator() {
            this.curRow = 0;
        }

        @Override
        public boolean hasNext() {
            return this.curRow < Grid.this.numRows;
        }

        @Override
        public List<Cell> next() {
            var ret = Grid.this.getRow(curRow);
            ++curRow;
            return ret;
        }

        @Override
        public void forEachRemaining(Consumer<? super List<Cell>> action) {
            while (hasNext()) {
                action.accept(next());
            }
        }
    }

    public RowIterator rowIterator() {
        return new RowIterator();
    }

    public CellIterator CellIterator() {
        return new CellIterator();
    }
}
