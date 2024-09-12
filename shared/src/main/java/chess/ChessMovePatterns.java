package chess;

/**
 * A holder for chess move patterns
 * <p>
 */

public class ChessMovePatterns {
    public final int row_diff;
    public final int col_diff;
    public final boolean canTake;
    public final boolean canRepeat;
    public final boolean needTakeToMove;

    public ChessMovePatterns(int row_diff, int col_diff, boolean canTake, boolean canRepeat, boolean needTakeToMove) {
        this.row_diff = row_diff;
        this.col_diff = col_diff;
        this.canTake = canTake;
        this.canRepeat = canRepeat;
        this.needTakeToMove = needTakeToMove;
    }

    public ChessMovePatterns(int row_diff, int col_diff, boolean canTake, boolean canRepeat) {
        this.row_diff = row_diff;
        this.col_diff = col_diff;
        this.canTake = canTake;
        this.canRepeat = canRepeat;
        this.needTakeToMove = false;
    }
}
