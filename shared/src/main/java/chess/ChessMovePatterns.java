package chess;

/**
 * A holder for chess move patterns
 * <p>
 */

public record ChessMovePatterns(int row_diff, int col_diff, boolean canTake, boolean canRepeat,
                                boolean needTakeToMove) {
    public ChessMovePatterns(int row_diff, int col_diff, boolean canTake, boolean canRepeat) {
        this(row_diff, col_diff, canTake, canRepeat, false);
    }
}
