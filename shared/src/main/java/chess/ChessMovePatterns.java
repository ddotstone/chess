package chess;

/**
 * A holder for chess move patterns
 * <p>
 */

public record ChessMovePatterns(int rowDiff, int colDiff, boolean canTake, boolean canRepeat,
                                boolean needTakeToMove) {
    public ChessMovePatterns(int rowDiff, int colDiff, boolean canTake, boolean canRepeat) {
        this(rowDiff, colDiff, canTake, canRepeat, false);
    }
}
