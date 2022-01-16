package org.chrku;

import org.chrku.algorithms.*;
import org.chrku.grid.Grid;
import org.chrku.grid.LabeledGrid;
import org.chrku.solvers.DijkstraSolver;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;

@Command(name = "generate_maze", mixinStandardHelpOptions = true,
        description = "Generates maze and outputs it.")
class Main implements Callable<Integer> {
    private enum OutputFormat {
        image, text
    }

    private enum Algorithm {
        binary_tree, sidewinder, aldous_broder, wilson
    }

    private enum LabelColor {
        red, green, blue
    }

    @Option(names = {"-a", "--algorithm"}, description = "Maze algorithm to choose from. " +
            "Valid values: ${COMPLETION-CANDIDATES}", required = true)
    private Algorithm algorithm = Algorithm.binary_tree;

    @Option(names = {"-l", "--lineWidth"}, description = "Line width for image output", defaultValue = "2")
    private int lineWidth = 2;

    @Option(names = {"-cs", "--cellSize"}, description = "Cell size for image output", defaultValue = "20")
    private int cellSize = 20;

    @Option(names = {"-s", "--solve"}, description = "Shows shortest paths from arbitrary starting point")
    private boolean solve;

    @Option(names = {"-color", "--solveColor"}, description = "Which color to use for visualizing shortest paths",
            defaultValue = "red")
    private LabelColor color;

    @Option(names = {"-sr", "--startRow"}, description = "Starting row to use for shortest path visualization," +
            " -1 for random", defaultValue = "-1")
    private int startRow;

    @Option(names = {"-sc", "--startColumn"}, description = "Starting column to use for shortest path visualization," +
            " -1 for random", defaultValue = "-1")
    private int startColumn;

    @Option(names = {"-se", "--solveEnd"}, description = "Add an endpoint to the shortest path visualization",
            defaultValue = "false")
    private boolean solveEnd;

    @Option(names = {"-er", "--endRow"}, description = "Ending row to use for shortest path visualization," +
            " -1 for random", defaultValue = "-1")
    private int endRow;

    @Option(names = {"-ec", "--endColumn"}, description = "Ending column to use for shortest path visualization," +
            " -1 for random", defaultValue = "-1")
    private int endColumn;

    @Option(names = {"-h", "--numRows"}, description = "Number of rows for maze", required = true)
    private int numRows;

    @Option(names = {"-w", "--numCols"}, description = "Number of columns for maze", required = true)
    private int numCols;

    @Option(names = {"-o", "--outputFormat"}, description = "Output format of maze. " +
            "Valid values: ${COMPLETION-CANDIDATES}",
            defaultValue = "image")
    private OutputFormat outputFormat;

    @Option(names = {"-p", "--path"}, description = "Output path", defaultValue = "")
    private Path outputPath;

    // this example implements Callable, so parsing, error handling and handling user
    // requests for usage help or version help can be done with one line of code.
    public static void main(String... args) {
        int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() {
        // Create grid
        Grid grid = createGrid();

        // Output the grid to the chosen option
        outputGrid(grid);
        return 0;
    }

    private void outputGrid(Grid grid) {
        switch (outputFormat) {
            case image -> {
                if (outputPath.toString().isEmpty()) {
                    System.out.println("Need output path for image");
                } else {
                    try {
                        grid.writeImage(outputPath, cellSize, lineWidth);
                    } catch (IOException e) {
                        System.out.println("Could not write to given path");
                        e.printStackTrace();
                    }
                }
            }
            case text -> System.out.println(grid);
        }
    }

    private Grid createGrid() {
        Grid grid = null;

        if (solve) {
            grid = createShortestPathGrid(grid);
        } else {
            grid = new Grid(numRows, numCols);
            generateMaze(grid);
        }
        return grid;
    }

    private Grid createShortestPathGrid(Grid grid) {
        Color baseColor = Color.RED;

        switch (color) {
            case red -> baseColor = Color.RED;
            case green -> baseColor = Color.GREEN;
            case blue -> baseColor = Color.BLUE;
        }

        LabeledGrid labeledGrid = new LabeledGrid(numRows, numCols, baseColor, Color.YELLOW);

        if (startRow == -1) {
            startRow = ThreadLocalRandom.current().nextInt(numRows);
        }
        if (startColumn == -1) {
            startColumn = ThreadLocalRandom.current().nextInt(numCols);
        }
        if (endRow == -1) {
            endRow = ThreadLocalRandom.current().nextInt(numRows);
        }
        if (endColumn == -1) {
            endColumn = ThreadLocalRandom.current().nextInt(numCols);
        }

        generateMaze(labeledGrid);

        DijkstraSolver solver = new DijkstraSolver(labeledGrid, startRow, startColumn);
        solver.solve();
        labeledGrid.setLabels(solver.getDistances());
        if (solveEnd) {
            labeledGrid.setPath(solver.getPathTo(endRow, endColumn));
        }

        grid = labeledGrid;

        return grid;
    }

    private void generateMaze(Grid grid) {
        MazeGenerator generator = null;

        switch (algorithm) {
            case binary_tree -> generator = new BinaryTree();
            case sidewinder -> generator = new Sidewinder();
            case aldous_broder -> generator = new AldousBroder();
            case wilson ->  generator = new Wilson();
            default -> {
                System.out.println("Unsupported algorithm");
                System.exit(1);
            }
        }

        generator.generate(grid);
    }
}