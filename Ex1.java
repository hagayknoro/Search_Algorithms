import java.io.*;
import java.util.*;

public class Ex1 {
    private static final String INPUT_FILENAME = "input.txt";
    private static final String OUTPUT_FILENAME = "output.txt";

    public static void main(String[] args) {
        Configuration config = readConfiguration(INPUT_FILENAME);
        executeGame(config);
    }

    private static void executeGame(Configuration config) {
        BoardState initialBoard = new BoardState(config.initialStateStr);
        BoardState targetBoard = new BoardState(config.goalStateStr);
        GameSolver solver = SolverSelector.createSolver(config.algorithmName, initialBoard, targetBoard, config.showOpenList);

        long beginTime = System.nanoTime();
        solver.execute();
        long completionTime = System.nanoTime();
        double totalTime = (completionTime - beginTime) / 1e9;

        writeResults(solver, totalTime, config.includeTime);
    }

    private static void writeResults(GameSolver solver, double totalTime, boolean includeTime) {
        try (BufferedWriter output = new BufferedWriter(new FileWriter(OUTPUT_FILENAME))) {
            output.write(solver.getSolutionPath());
            output.write("Num: " + solver.getExploredStates() + "\n");

            String costStr = solver.getSolutionPath().equals("no path\n") ? "inf" : String.valueOf(solver.getTotalCost());
            output.write("Cost: " + costStr + "\n");

            if (includeTime) {
                output.write(totalTime + " seconds\n");
            }
        } catch (IOException e) {
            System.err.println("Failed to write results: " + e.getMessage());
        }
    }

    private static Configuration readConfiguration(String filename) {
        Configuration config = new Configuration();
        List<String> fileContent = readFileLines(filename);

        config.algorithmName = fileContent.get(0);
        config.includeTime = fileContent.get(1).equals("with time");
        config.showOpenList = fileContent.get(2).equals("with open");

        int lineIdx = 3;
        StringBuilder initState = new StringBuilder();
        while (!fileContent.get(lineIdx).equals("Goal state:")) {
            initState.append(fileContent.get(lineIdx).replace(",", ""));
            lineIdx++;
        }
        config.initialStateStr = initState.toString();

        StringBuilder goalState = new StringBuilder();
        lineIdx++;
        while (lineIdx < fileContent.size()) {
            goalState.append(fileContent.get(lineIdx).replace(",", ""));
            lineIdx++;
        }
        config.goalStateStr = goalState.toString();

        return config;
    }

    private static List<String> readFileLines(String filename) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line.trim());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read input file", e);
        }
        return lines;
    }

    private static class Configuration {
        String algorithmName;
        boolean includeTime;
        boolean showOpenList;
        String initialStateStr;
        String goalStateStr;
    }
}