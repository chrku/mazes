import algorithms.BinaryTree;
import algorithms.MazeGenerator;
import algorithms.Sidewinder;
import grid.Grid;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

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
        System.out.println(grid);

        return 0;
    }
}