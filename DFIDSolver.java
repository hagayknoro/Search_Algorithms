import java.util.HashSet;
import java.util.Stack;

public class DFIDSolver extends GameSolver {
    public DFIDSolver(BoardState initial, BoardState target, boolean displayOpen) {
        super(initial, target, displayOpen);
    }

    @Override
    public void execute() {
        if (!validatePuzzle()) {
            solutionPath.append("no path\n");
            return;
        }

        if (initialState.equals(targetState)) {
            solutionPath.append("\n");
            return;
        }

        int currentDepth = 1;
        while (true) {
            HashSet<BoardState> visitedStates = new HashSet<>();
            ResultType result = performDepthSearch(initialState, currentDepth, visitedStates);

            if (!result.equals(ResultType.CONTINUE_DEEPER)) {
                if (result.equals(ResultType.PATH_NOT_FOUND)) {
                    solutionPath.append("no path\n");
                }
                break;
            }
            currentDepth++;
        }
    }

    private enum ResultType {
        SUCCESS,
        PATH_NOT_FOUND,
        CONTINUE_DEEPER
    }

    private ResultType performDepthSearch(BoardState currentState, int depthRemaining,
                                          HashSet<BoardState> visitedStates) {
        if (currentState.equals(targetState)) {
            totalCost = currentState.getPathCost(); // שינוי ל-getPathCost
            buildSolutionPath(currentState);
            return ResultType.SUCCESS;
        }

        if (depthRemaining == 0) {
            return ResultType.CONTINUE_DEEPER;
        }

        visitedStates.add(currentState);
        boolean shouldContinueDeeper = false;

        for (BoardState successor : currentState) { // שימוש באיטרטור המובנה
            if (visitedStates.contains(successor)) {
                continue;
            }

            successor.setPreviousState(currentState);
            exploredStates++;

            if (displayOpenList) {
                showExplorationStatus(visitedStates);
            }

            ResultType recursiveResult = performDepthSearch(successor, depthRemaining - 1, visitedStates);

            if (recursiveResult.equals(ResultType.SUCCESS)) {
                return ResultType.SUCCESS;
            }
            if (recursiveResult.equals(ResultType.CONTINUE_DEEPER)) {
                shouldContinueDeeper = true;
            }
        }

        visitedStates.remove(currentState);
        return shouldContinueDeeper ? ResultType.CONTINUE_DEEPER : ResultType.PATH_NOT_FOUND;
    }

    private void buildSolutionPath(BoardState goalState) {
        Stack<Move> movesStack = new Stack<>();
        BoardState currentState = goalState;

        while (currentState.getPreviousState() != null) {
            movesStack.push(currentState.getLastMove());
            currentState = currentState.getPreviousState();
        }

        while (!movesStack.isEmpty()) {
            Move currentMove = movesStack.pop();
            solutionPath.append(currentMove.toString());
            if (!movesStack.isEmpty()) {
                solutionPath.append("--");
            }
        }
        solutionPath.append("\n");
    }

    private void showExplorationStatus(HashSet<BoardState> visitedStates) {
        System.out.println("Exploration progress:");
        System.out.println("----------------------------------------");
        for (BoardState state : visitedStates) {
            state.displayBoard();
            System.out.println();
        }
        System.out.println("----------------------------------------");
    }
}
