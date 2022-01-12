package org.chrku;

import org.chrku.algorithms.BinaryTree;
import org.chrku.algorithms.MazeGenerator;
import org.chrku.algorithms.Sidewinder;
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
        binary_tree, sidewinder
    }

    private enum LabelColor {
        red, green, blue
    }

    @Option(names = {"-a", "--algorithm"}, description = "Maze algorithm to choose from. " +
            "Valid values: ${COMPLETION-CANDIDATES}", required = true)
    private final Algorithm algorithm = Algorithm.binary_tree;

    @Option(names = {"-l", "--lineWidth"}, description = "Line width for image output", defaultValue = "2")
    private final int lineWidth = 2;

    @Option(names = {"-cs", "--cellSize"}, description = "Cell size for image output", defaultValue = "20")
    private final int cellSize = 20;

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
        Grid grid;

        if (solve) {
            Color baseColor = Color.RED;

            switch (color) {
                case red -> baseColor = Color.RED;
                case green -> baseColor = Color.GREEN;
                case blue -> baseColor = Color.BLUE;
            }

            grid = new LabeledGrid(numRows, numCols, baseColor);
        } else {
            grid = new Grid(numRows, numCols);
        }

        MazeGenerator generator;

        switch (algorithm) {
            case binary_tree -> generator = new BinaryTree();
            case sidewinder -> generator = new Sidewinder();
            default -> {
                System.out.println("Unsupported algorithm");
                return 1;
            }
        }

        generator.generate(grid);

        if (solve) {
            if (startRow == -1) {
                startRow = ThreadLocalRandom.current().nextInt(numRows);
            }
            if (startColumn == -1) {
                startColumn = ThreadLocalRandom.current().nextInt(numCols);
            }

            DijkstraSolver solver = new DijkstraSolver(grid, startRow, startColumn);
            solver.solve();
            ((LabeledGrid) grid).setLabels(solver.getDistances());
        }

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

        return 0;
    }
}