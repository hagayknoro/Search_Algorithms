import java.util.Arrays;

public class Move {
    private final int[] source = new int[2];
    private final int[] target = new int[2];
    private final int gemType;

    public Move(int srcRow, int srcCol, int destRow, int destCol, int gem) {
        validateGemType(gem);
        source[0] = srcRow;
        source[1] = srcCol;
        target[0] = destRow;
        target[1] = destCol;
        gemType = gem;
    }

    private void validateGemType(int gem) {
        if (gem < 2 || gem > 4) {
            throw new IllegalArgumentException("Invalid gem type");
        }
    }

    public int getGemType() {
        return gemType;
    }

    public int[] getTarget() {
        return target.clone();
    }

    public int[] getSource() {
        return source.clone();
    }

    public int getCost() {
        return switch (gemType) {
            case 2 -> 3;  // Emerald
            case 3 -> 10; // Ruby
            case 4 -> 1;  // Sapphire
            default -> 0;
        };
    }

    @Override
    public String toString() {
        String gemSymbol = switch (gemType) {
            case 2 -> "G";
            case 3 -> "R";
            case 4 -> "B";
            default -> "";
        };
        return String.format("(%d,%d):%s:(%d,%d)",
                source[0] + 1, source[1] + 1, gemSymbol, target[0] + 1, target[1] + 1);
    }

    public boolean isReverse(Move other) {
        if (other == null) return false;
        return Arrays.equals(this.source, other.target) &&
                Arrays.equals(this.target, other.source) &&
                this.gemType == other.gemType;
    }
}