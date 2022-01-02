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
import java.nio.file.Paths;
import java.util.concurrent.Callable;

@Command(name = "generate_maze", mixinStandardHelpOptions = true,
        description = "Generates maze and outputs it.")
class Main implements Callable<Integer> {
    @Option(names = {"-a", "--algorithm"}, description = "Maze algorithm to choose from", required = true)
    private String algorithm = "BinaryTree";

    @Option(names = {"-r", "--numRows"}, description = "Number of rows for maze", required = true)
    private int numRows;

    @Option(names = {"-c", "--numCols"}, description = "Number of columns for maze", required = true)
    private int numCols;

    private enum OutputFormat {
        IMAGE, TEXT
    }

    @Option(names = {"-o", "--outputFormat"}, description = "Valid values: ${COMPLETION-CANDIDATES}",
            defaultValue = "IMAGE")
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
            case "BinaryTree" -> generator = new BinaryTree();
            case "Sidewinder" -> generator = new Sidewinder();
            default -> {
                System.out.println("Unsupported algorithm");
                return 1;
            }
        }

        generator.generate(grid);

        switch (outputFormat) {
            case IMAGE -> {
                if (outputPath.toString().isEmpty()) {
                    System.out.println("Need output path for image");
                } else {
                    try {
                        grid.writeImage(outputPath, 20, 2);
                    } catch (IOException e) {
                        System.out.println("Could not write to given path");
                        e.printStackTrace();
                    }
                }
            }
            case TEXT -> System.out.println(grid);
        }

        return 0;
    }
}