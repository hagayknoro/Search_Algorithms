import java.util.*;

public class BreadthFirstSolver extends GameSolver {

    public BreadthFirstSolver(BoardState initial, BoardState target, boolean showOpenList) {
        super(initial, target, showOpenList);
    }

    @Override
    public void execute() {
        if (!validatePuzzle()) {
            solutionPath.append("no path\n");
            return;
        }

        if (initialState.equals(targetState)) {
            solutionPath.append("start is the goal\n");
            totalCost = 0;
            return;
        }

        Set<BoardState> visitedStates = new HashSet<>();
        Set<BoardState> pendingStates = new HashSet<>();
        Queue<BoardState> stateQueue = new LinkedList<>();

        exploredStates = 0;
        BoardState solution = null;

        stateQueue.add(initialState);
        pendingStates.add(initialState);

        while (!stateQueue.isEmpty()) {
            if (displayOpenList) {
                displayQueueContents(stateQueue);
            }

            BoardState currentState = stateQueue.poll();
            pendingStates.remove(currentState);
            visitedStates.add(currentState);

            for (BoardState nextState : currentState) {
                exploredStates++;

                if (!pendingStates.contains(nextState) && !visitedStates.contains(nextState)) {
                    nextState.setPreviousState(currentState);

                    if (nextState.equals(targetState)) {
                        solution = nextState;
                        break;
                    }

                    stateQueue.add(nextState);
                    pendingStates.add(nextState);
                }
            }

            if (solution != null) {
                break;
            }
        }

        if (solution == null) {
            solutionPath.append("no path\n");
        } else {
            totalCost = solution.getPathCost();
            reconstructSolution(solution);
        }
    }

    private void reconstructSolution(BoardState finalState) {
        Deque<Move> movesStack = new ArrayDeque<>();
        BoardState currentState = finalState;

        while (currentState.getPreviousState() != null) {
            movesStack.push(currentState.getLastMove());
            currentState = currentState.getPreviousState();
        }

        while (!movesStack.isEmpty()) {
            solutionPath.append(movesStack.pop().toString());
            solutionPath.append(movesStack.isEmpty() ? "\n" : "--");
        }
    }

    private void displayQueueContents(Queue<BoardState> queue) {
        System.out.println("\nCurrent Open List Status:");
        System.out.println("------------------------");
        for (BoardState state : queue) {
            state.displayBoard();
            System.out.println();
        }
        System.out.println("------------------------");
    }
}