import java.util.*;

public class AStarSolver extends GameSolver {
    private final NodeComparator evaluator;

    public AStarSolver(BoardState initial, BoardState target, boolean displayProgress) {
        super(initial, target, displayProgress);
        this.evaluator = new NodeComparator();
    }

    private class NodeComparator implements Comparator<BoardState> {
        @Override
        public int compare(BoardState first, BoardState second) {
            int costComparison = Integer.compare(
                    first.getTotalCost(targetState),
                    second.getTotalCost(targetState)
            );

            return costComparison != 0
                    ? costComparison
                    : Integer.compare(first.getSequence(), second.getSequence());
        }
    }

    @Override
    public void execute() {
        if (!isValidConfiguration()) {
            return;
        }

        BoardState solution = performSearch();
        generateOutput(solution);
    }

    private boolean isValidConfiguration() {
        if (!validatePuzzle()) {
            solutionPath.append("no path\n");
            return false;
        }

        if (initialState.equals(targetState)) {
            solutionPath.append("start is the goal\n");
            totalCost = 0;
            return false;
        }

        return true;
    }

    private BoardState performSearch() {
        Set<BoardState> visitedNodes = new HashSet<>();
        Set<BoardState> pendingNodes = new HashSet<>();
        Queue<BoardState> priorityQueue = new PriorityQueue<>(evaluator);

        initializeSearch(pendingNodes, priorityQueue);

        while (!priorityQueue.isEmpty()) {
            if (displayOpenList) {
                visualizeSearchProgress(priorityQueue);
            }

            BoardState currentNode = priorityQueue.poll();
            pendingNodes.remove(currentNode);

            if (currentNode.equals(targetState)) {
                return currentNode;
            }

            visitedNodes.add(currentNode);
            processNeighbors(currentNode, visitedNodes, pendingNodes, priorityQueue);
        }

        return null;
    }

    private void initializeSearch(Set<BoardState> pending, Queue<BoardState> queue) {
        exploredStates = 0;
        queue.add(initialState);
        pending.add(initialState);
    }

    private void processNeighbors(
            BoardState current,
            Set<BoardState> visited,
            Set<BoardState> pending,
            Queue<BoardState> queue) {

        for (BoardState neighbor : current) {
            exploredStates++;
            neighbor.setSequence(exploredStates);

            if (isNewState(neighbor, visited, pending)) {
                addNewState(neighbor, current, pending, queue);
            } else if (pending.contains(neighbor)) {
                updateExistingState(neighbor, current, queue);
            }
        }
    }

    private boolean isNewState(BoardState node, Set<BoardState> visited, Set<BoardState> pending) {
        return !visited.contains(node) && !pending.contains(node);
    }

    private void addNewState(
            BoardState node,
            BoardState parent,
            Set<BoardState> pending,
            Queue<BoardState> queue) {

        node.setPreviousState(parent);
        queue.add(node);
        pending.add(node);
    }

    private void updateExistingState(BoardState newVersion, BoardState parent, Queue<BoardState> queue) {
        for (BoardState existing : queue) {
            if (existing.equals(newVersion) &&
                    existing.getTotalCost(targetState) > newVersion.getTotalCost(targetState)) {

                queue.remove(existing);
                queue.add(newVersion);
                newVersion.setPreviousState(parent);
                break;
            }
        }
    }

    private void generateOutput(BoardState solution) {
        if (solution == null) {
            solutionPath.append("no path\n");
            return;
        }

        totalCost = solution.getPathCost();
        reconstructPath(solution);
    }

    private void reconstructPath(BoardState endState) {
        Deque<Move> pathStack = new ArrayDeque<>();
        BoardState current = endState;

        while (current.getPreviousState() != null) {
            pathStack.push(current.getLastMove());
            current = current.getPreviousState();
        }

        buildPathString(pathStack);
    }

    private void buildPathString(Deque<Move> pathStack) {
        while (!pathStack.isEmpty()) {
            solutionPath.append(pathStack.pop().toString());
            if (!pathStack.isEmpty()) {
                solutionPath.append("--");
            }
        }
        solutionPath.append("\n");
    }

    private void visualizeSearchProgress(Queue<BoardState> queue) {
        System.out.println("\nCurrent Search Progress");
        System.out.println("======================");
        for (BoardState node : queue) {
            node.displayBoard();
            System.out.println();
        }
        System.out.println("======================");
    }
}