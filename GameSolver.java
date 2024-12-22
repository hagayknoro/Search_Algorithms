import java.util.Arrays;

public abstract class GameSolver {
    protected boolean displayOpenList;
    protected BoardState initialState;
    protected BoardState targetState;
    protected int exploredStates;
    protected int totalCost;
    protected StringBuilder solutionPath = new StringBuilder();

    public GameSolver(BoardState start, BoardState target, boolean showOpen) {
        this.initialState = start;
        this.targetState = target;
        this.displayOpenList = showOpen;
    }

    public int getTotalCost() {
        return totalCost;
    }

    public int getExploredStates() {
        return exploredStates;
    }

    public String getSolutionPath() {
        return solutionPath.toString();
    }

    protected boolean validatePuzzle() {
        int[][] initial = initialState.getBoardMatrix();
        int[][] target = targetState.getBoardMatrix();
        int[] initialGems = new int[3];
        int[] targetGems = new int[3];

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if ((initial[i][j] == 1) != (target[i][j] == 1)) {
                    return false;
                }
                countGems(initial[i][j], initialGems);
                countGems(target[i][j], targetGems);
            }
        }
        return Arrays.equals(initialGems, targetGems);
    }

    private void countGems(int cell, int[] counts) {
        if (cell >= 2 && cell <= 4) {
            counts[cell - 2]++;
        }
    }

    public abstract void execute();
}
