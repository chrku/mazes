package org.chrku;

import org.chrku.algorithms.BinaryTree;
import org.chrku.algorithms.MazeGenerator;
import org.chrku.algorithms.Sidewinder;
import org.chrku.grid.Grid;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.Callable;

@Command(name = "generate_maze", mixinStandardHelpOptions = true,
        description = "Generates maze and outputs it.")
class Main implements Callable<Integer> {

    private enum OutputFormat {
        image, text
    }

    private enum Algorithm {
        binary_tree, sidewinder
    }

    @Option(names = {"-a", "--algorithm"}, description = "Maze algorithm to choose from. " +
            "Valid values: ${COMPLETION-CANDIDATES}", required = true)
    private Algorithm algorithm = Algorithm.binary_tree;

    @Option(names = {"-r", "--numRows"}, description = "Number of rows for maze", required = true)
    private int numRows;

    @Option(names = {"-c", "--numCols"}, description = "Number of columns for maze", required = true)
    private int numCols;

    @Option(names = {"-l", "--lineWidth"}, description = "Line width for image output", defaultValue = "2")
    private int lineWidth = 2;

    @Option(names = {"-s", "--cellSize"}, description = "Cell size for image output", defaultValue = "20")
    private int cellSize = 20;

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
        Grid grid = new Grid(numRows, numCols);
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