import java.util.HashSet;
import java.util.Stack;

public class IDAstarSolver extends GameSolver {
    private static final int INFINITY = Integer.MAX_VALUE;
    private final Stack<BoardState> searchStack;
    private final HashSet<BoardState> visitedStates;
    private int currentThreshold;
    private int nextThreshold;

    public IDAstarSolver(BoardState initial, BoardState target, boolean showOpenList) {
        super(initial, target, showOpenList);
        this.searchStack = new Stack<>();
        this.visitedStates = new HashSet<>();
    }

    @Override
    public void execute() {
        if (!validateInitialConditions()) {
            return;
        }

        performIterativeDeepeningSearch();
    }

    private boolean validateInitialConditions() {
        if (!validatePuzzle()) {
            solutionPath.append("no path\n");
            return false;
        }

        if (initialState.equals(targetState)) {
            solutionPath.append("\n");
            return false;
        }

        return true;
    }

    private void performIterativeDeepeningSearch() {
        initializeSearch();

        while (currentThreshold != INFINITY) {
            prepareNextIteration();
            boolean solutionFound = executeSearchIteration();

            if (solutionFound) {
                return;
            }

            currentThreshold = nextThreshold;
        }

        handleNoSolutionFound();
    }

    private void initializeSearch() {
        currentThreshold = initialState.estimateCost(targetState);
        nextThreshold = INFINITY;
    }

    private void prepareNextIteration() {
        clearSearchStructures();
        initialState.marked = false;
        nextThreshold = INFINITY;
    }

    private void clearSearchStructures() {
        searchStack.clear();
        visitedStates.clear();
        searchStack.push(initialState);
        visitedStates.add(initialState);
    }

    private boolean executeSearchIteration() {
        while (!searchStack.isEmpty()) {
            handleOpenListDisplay();

            BoardState currentState = searchStack.peek();

            if (currentState.marked) {
                processMarkedState(currentState);
                continue;
            }

            if (exploreCurrentState(currentState)) {
                return true;
            }
        }
        return false;
    }

    private void handleOpenListDisplay() {
        if (displayOpenList) {
            displayCurrentOpenList();
        }
    }

    private void processMarkedState(BoardState state) {
        searchStack.pop();
        visitedStates.remove(state);
    }

    private boolean exploreCurrentState(BoardState current) {
        current.marked = true;

        for (BoardState successor : current) {
            exploredStates++;

            int successorCost = successor.getTotalCost(targetState);
            if (successorCost > currentThreshold) {
                nextThreshold = Math.min(nextThreshold, successorCost);
                continue;
            }

            if (handleDuplicateState(successor)) {
                continue;
            }

            if (successor.equals(targetState)) {
                return handleSolutionFound(successor);
            }

            addToSearchStructures(successor);
        }

        return false;
    }

    private void addToSearchStructures(BoardState state) {
        searchStack.push(state);
        visitedStates.add(state);
    }

    private boolean handleDuplicateState(BoardState newState) {
        if (!visitedStates.contains(newState)) {
            return false;
        }

        BoardState existingState = findStateInStack(newState);
        if (existingState == null) {
            return true;
        }

        if (existingState.marked) {
            return true;
        }

        if (existingState.getTotalCost(targetState) > newState.getTotalCost(targetState)) {
            searchStack.remove(existingState);
            visitedStates.remove(newState);
            return false;
        }

        return true;
    }

    private BoardState findStateInStack(BoardState target) {
        for (BoardState state : searchStack) {
            if (state.equals(target)) {
                return state;
            }
        }
        return null;
    }

    private boolean handleSolutionFound(BoardState goalState) {
        totalCost = goalState.getPathCost();
        constructSolutionPath(goalState);
        return true;
    }

    private void constructSolutionPath(BoardState goalState) {
        for (BoardState state : searchStack) {
            if (state.marked && state.getLastMove() != null) {
                solutionPath.append(state.getLastMove().toString()).append("--");
            }
        }
        solutionPath.append(goalState.getLastMove().toString()).append("\n");
    }

    private void displayCurrentOpenList() {
        System.out.println("Open List Status:");
        System.out.println("----------------------------------------");
        searchStack.stream()
                .filter(state -> !state.marked)
                .forEach(state -> {
                    state.displayBoard();
                    System.out.println();
                });
        System.out.println("----------------------------------------");
    }

    private void handleNoSolutionFound() {
        solutionPath.append("no path\n");
    }
}