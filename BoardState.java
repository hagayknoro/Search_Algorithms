import java.util.*;

public class BoardState implements Iterable<BoardState> {
    private static final int EMPTY = 0;
    private static final int WALL = 1;
    private static final int EMERALD = 2;
    private static final int RUBY = 3;
    private static final int SAPPHIRE = 4;

    private final int[][] board = new int[3][3];
    private Move previousMove = null;
    private BoardState parentState = null;
    private int pathCost;
    private int estimatedCost = -1;
    private int sequence;
    public boolean marked = false;

    public BoardState(String boardStr) {
        validateInput(boardStr);
        initializeBoard(boardStr);
    }

    private void validateInput(String boardStr) {
        if (boardStr.length() != 9) {
            throw new IllegalArgumentException("Invalid board string length");
        }
    }

    private void initializeBoard(String boardStr) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                board[row][col] = convertSymbolToValue(boardStr.charAt(row * 3 + col));
            }
        }
    }



    public int estimateCost(BoardState target) {
        if (estimatedCost >= 0) return estimatedCost;

        int total = 0;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (board[row][col] != target.board[row][col]) {
                    total += getGemValue(board[row][col]);
                }
            }
        }
        estimatedCost = total;
        return total;
    }

    private void executeMove(Move move) {
        int[] src = move.getSource();
        int[] tgt = move.getTarget();
        board[src[0]][src[1]] = EMPTY;
        board[tgt[0]][tgt[1]] = move.getGemType();
    }

    private BoardState(int[][] sourceBoard, Move moveApplied) {
        duplicateBoard(sourceBoard);
        previousMove = moveApplied;
        executeMove(moveApplied);
    }

    private int getGemValue(int gemType) {
        return switch (gemType) {
            case EMERALD -> 3;
            case RUBY -> 10;
            case SAPPHIRE -> 1;
            default -> 0;
        };
    }

    public int getPathCost() {
        return pathCost;
    }

    public int getTotalCost(BoardState target) {
        return estimateCost(target) + getPathCost();
    }

    public void displayBoard() {
        for (int row = 0; row < 3; row++) {
            System.out.print("[");
            for (int col = 0; col < 3; col++) {
                System.out.print(convertValueToSymbol(board[row][col]) +
                        (col < 2 ? "," : "]"));
            }
            System.out.println();
        }
    }

    private void duplicateBoard(int[][] source) {
        for (int i = 0; i < 3; i++) {
            System.arraycopy(source[i], 0, board[i], 0, 3);
        }
    }



    private int convertSymbolToValue(char symbol) {
        return switch (symbol) {
            case 'R' -> RUBY;
            case 'B' -> SAPPHIRE;
            case 'G' -> EMERALD;
            case '_' -> EMPTY;
            case 'X' -> WALL;
            default -> throw new IllegalArgumentException("Invalid symbol: " + symbol);
        };
    }

    private char convertValueToSymbol(int value) {
        return switch (value) {
            case RUBY -> 'R';
            case SAPPHIRE -> 'B';
            case EMERALD -> 'G';
            case EMPTY -> '_';
            case WALL -> 'X';
            default -> '?';
        };
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof BoardState other)) return false;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (this.board[i][j] != other.board[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

    // Getters and setters with renamed methods
    public int[][] getBoardMatrix() {
        int[][] copy = new int[3][3];
        duplicateBoard(this.board, copy);
        return copy;
    }

    public Move getLastMove() {
        return previousMove;
    }

    public BoardState getPreviousState() {
        return parentState;
    }

    public void setPreviousState(BoardState parent) {
        this.parentState = parent;
    }

    public void setSequence(int seq) {
        this.sequence = seq;
    }

    public int getSequence() {
        return sequence;
    }

    private void duplicateBoard(int[][] src, int[][] dst) {
        for (int i = 0; i < 3; i++) {
            System.arraycopy(src[i], 0, dst[i], 0, 3);
        }
    }

    @Override
    public Iterator<BoardState> iterator() {
        return new BoardStateIterator();
    }

    private class BoardStateIterator implements Iterator<BoardState> {
        private int row = 0;
        private int col = 0;
        private int direction = 0;
        private BoardState nextState = null;
        private boolean nextFound = false;

        @Override
        public boolean hasNext() {
            if (!nextFound) {
                findNextState();
            }
            return nextFound;
        }

        @Override
        public BoardState next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            nextFound = false;
            return nextState;
        }

        private void findNextState() {
            while (row < 3) {
                if (board[row][col] > 1) {
                    while (direction < 4) {
                        int[] adjacentCell = findAdjacentCell(row, col, direction);
                        if (adjacentCell != null && board[adjacentCell[0]][adjacentCell[1]] == EMPTY) {
                            Move potentialMove = new Move(row, col, adjacentCell[0], adjacentCell[1], board[row][col]);
                            if (previousMove == null || !previousMove.isReverse(potentialMove)) {
                                nextState = new BoardState(board, potentialMove);
                                nextState.pathCost = pathCost + potentialMove.getCost();
                                direction++;
                                nextFound = true;
                                return;
                            }
                        }
                        direction++;
                    }
                }
                moveToNextPosition();
            }
            nextState = null;
            nextFound = false;
        }

        private void moveToNextPosition() {
            direction = 0;
            col = (col + 1) % 3;
            if (col == 0) {
                row++;
            }
        }

        private int[] findAdjacentCell(int x, int y, int dir) {
            return switch (dir) {
                case 0 -> new int[]{x, (y + 2) % 3};
                case 1 -> new int[]{x, (y + 1) % 3};
                case 2 -> new int[]{(x + 2) % 3, y};
                case 3 -> new int[]{(x + 1) % 3, y};
                default -> null;
            };
        }
    }
}
