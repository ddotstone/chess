package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private final ChessGame.TeamColor teamColor;
    private ChessPiece.PieceType pieceType;


    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.teamColor = pieceColor;
        this.pieceType = type;
    }

    @Override
    public int hashCode() {
        return 71 * (this.teamColor.hashCode() + this.pieceType.hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || this.getClass() != obj.getClass()) return false;
        ChessPiece that = (ChessPiece) obj;
        return ((this.teamColor == that.teamColor) && (this.pieceType == that.pieceType));
    }

    @Override
    public String toString() {
        return "ChessPiece{" + teamColor.toString() + ", " + pieceType.toString() + '}';
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        throw new RuntimeException("Not implemented");
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> possibleMoves;
        switch (this.pieceType) {
            case PieceType.BISHOP:
                possibleMoves = populateMovesBishop(board, myPosition);
                break;
            case PieceType.KING:
                possibleMoves = populateMovesKing(board, myPosition);
                break;
            case PieceType.KNIGHT:
                possibleMoves = populateMovesKnight(board, myPosition);
                break;
            default:
                possibleMoves = new ArrayList<ChessMove>();
                break;
        }
        return possibleMoves;
    }

    public Collection<ChessMove> populateMovesBishop(ChessBoard board, ChessPosition myPosition) {
        ChessMovePatterns[] bishop_patterns = new ChessMovePatterns[4];
        bishop_patterns[0] = new ChessMovePatterns(1, 1, true, true);
        bishop_patterns[1] = new ChessMovePatterns(1, -1, true, true);
        bishop_patterns[2] = new ChessMovePatterns(-1, 1, true, true);
        bishop_patterns[3] = new ChessMovePatterns(-1, -1, true, true);
        return populateSimpleMoves(board, myPosition, bishop_patterns);
    }

    public Collection<ChessMove> populateMovesKing(ChessBoard board, ChessPosition myPosition) {
        ChessMovePatterns[] king_patterns = new ChessMovePatterns[8];
        king_patterns[0] = new ChessMovePatterns(1, 1, true, false);
        king_patterns[1] = new ChessMovePatterns(1, 0, true, false);
        king_patterns[2] = new ChessMovePatterns(0, 1, true, false);
        king_patterns[3] = new ChessMovePatterns(1, -1, true, false);
        king_patterns[4] = new ChessMovePatterns(-1, 1, true, false);
        king_patterns[5] = new ChessMovePatterns(-1, -1, true, false);
        king_patterns[6] = new ChessMovePatterns(-1, 0, true, false);
        king_patterns[7] = new ChessMovePatterns(0, -1, true, false);
        return populateSimpleMoves(board, myPosition, king_patterns);
    }

    public Collection<ChessMove> populateMovesKnight(ChessBoard board, ChessPosition myPosition) {
        ChessMovePatterns[] knight_patterns = new ChessMovePatterns[8];
        knight_patterns[0] = new ChessMovePatterns(1, 2, true, false);
        knight_patterns[1] = new ChessMovePatterns(2, 1, true, false);
        knight_patterns[2] = new ChessMovePatterns(-1, 2, true, false);
        knight_patterns[3] = new ChessMovePatterns(-2, 1, true, false);
        knight_patterns[4] = new ChessMovePatterns(1, -2, true, false);
        knight_patterns[5] = new ChessMovePatterns(2, -1, true, false);
        knight_patterns[6] = new ChessMovePatterns(-1, -2, true, false);
        knight_patterns[7] = new ChessMovePatterns(-2, -1, true, false);
        return populateSimpleMoves(board, myPosition, knight_patterns);
    }

    public Collection<ChessMove> populateSimpleMoves(ChessBoard board, ChessPosition myPosition, ChessMovePatterns[] movePatterns) {
        int color_multiplier = (this.teamColor == ChessGame.TeamColor.BLACK) ? -1 : 1;

        Collection<ChessMove> all_moves = new ArrayList<ChessMove>();
        for (ChessMovePatterns movePattern : movePatterns) {
            int curr_row = myPosition.getRow();
            int curr_col = myPosition.getColumn();
            ChessPiece pieceInSquare;
            do {
                curr_row += color_multiplier * movePattern.row_diff;
                curr_col += color_multiplier * movePattern.col_diff;

                if (curr_row > ChessBoard.N_ROWS || curr_col > ChessBoard.N_COLS) {
                    break;
                }
                if (curr_row <= 0 || curr_col <= 0) {
                    break;
                }
                pieceInSquare = board.getPiece(new ChessPosition(curr_row, curr_col));
                ChessPosition dest = new ChessPosition(curr_row, curr_col);
                if (pieceInSquare != null) {
                    if ((pieceInSquare.teamColor != this.teamColor) &&
                            (pieceInSquare.pieceType != PieceType.KING) &&
                            movePattern.canTake) {
                        all_moves.add(new ChessMove(myPosition, dest, null));
                    }
                } else if (!movePattern.needTakeToMove) {
                    all_moves.add(new ChessMove(myPosition, dest, null));
                }

            } while (movePattern.canRepeat && (board.getPiece(new ChessPosition(curr_row, curr_col))) == null);
        }
        return all_moves;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }


}
