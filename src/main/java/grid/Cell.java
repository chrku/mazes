package grid;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Cell {
    private final int row;
    private final int column;

    private Cell north;
    private Cell south;
    private Cell east;
    private Cell west;

    private final Set<Cell> links;

    public Cell(int row, int column) {
        this.row = row;
        this.column = column;
        this.north = null;
        this.south = null;
        this.east = null;
        this.west = null;
        this.links = new HashSet<>();
    }

    public Cell(int row, int column, Cell north, Cell south, Cell east, Cell west) {
        this.row = row;
        this.column = column;
        this.north = north;
        this.south = south;
        this.east = east;
        this.west = west;
        this.links = new HashSet<>();
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public Cell getNorth() {
        return north;
    }

    public Cell getSouth() {
        return south;
    }

    public Cell getEast() {
        return east;
    }

    public Cell getWest() {
        return west;
    }

    public Set<Cell> getLinks() {
        return links;
    }

    public void setNorth(Cell north) {
        this.north = north;
    }

    public void setSouth(Cell south) {
        this.south = south;
    }

    public void setEast(Cell east) {
        this.east = east;
    }

    public void setWest(Cell west) {
        this.west = west;
    }

    public void link(Cell cell, boolean bidir) {
        links.add(cell);
        if (bidir) {
            cell.link(this, false);
        }
    }

    public void unlink(Cell cell, boolean bidir) {
        links.remove(cell);
        if (bidir) {
            cell.unlink(this, false);
        }
    }

    public boolean isLinked(Cell cell) {
        return links.contains(cell);
    }

    public List<Cell> neighbours() {
        ArrayList<Cell> neighbours = new ArrayList<>();
        if (north != null) { neighbours.add(north); }
        if (south != null) { neighbours.add(south); }
        if (west != null) { neighbours.add(west); }
        if (east != null) { neighbours.add(east); }
        return neighbours;
    }
}
