public class SolverSelector {
    public static GameSolver createSolver(String solverType, BoardState initial, BoardState target, boolean displayOpen) {
        return switch (solverType) {
            case "BFS" -> new BreadthFirstSolver(initial, target, displayOpen);
            default -> throw new IllegalArgumentException("Unsupported solver type: " + solverType);
        };
    }
}