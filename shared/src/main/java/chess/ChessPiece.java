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
        return "ChessPiece{ COLOR:" + teamColor.toString() + ", TYPE:" + pieceType.toString() + '}';
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return this.teamColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return this.pieceType;
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
            case PieceType.PAWN:
                possibleMoves = populateMovesPawn(board, myPosition);
                break;
            case PieceType.QUEEN:
                possibleMoves = populateMovesQueen(board, myPosition);
                break;
            case PieceType.ROOK:
                possibleMoves = populateMovesRook(board, myPosition);
                break;
            default:
                possibleMoves = new ArrayList<ChessMove>();
                break;
        }
        return possibleMoves;
    }

    /**
     * finds all moves for a bishop
     *
     * @param board      current board find simple moves from
     * @param myPosition position on board piece is located
     * @return a collection of possible ChessMoves
     */
    public Collection<ChessMove> populateMovesBishop(ChessBoard board, ChessPosition myPosition) {
        ChessMovePatterns[] bishopPatterns = new ChessMovePatterns[4];
        bishopPatterns[0] = new ChessMovePatterns(1, 1, true, true);
        bishopPatterns[1] = new ChessMovePatterns(1, -1, true, true);
        bishopPatterns[2] = new ChessMovePatterns(-1, 1, true, true);
        bishopPatterns[3] = new ChessMovePatterns(-1, -1, true, true);
        return populateSimpleMoves(board, myPosition, bishopPatterns);
    }

    /**
     * Finds all moves for a King
     *
     * @param board      current board find simple moves from
     * @param myPosition position on board piece is located
     * @return a collection of possible ChessMoves
     */
    public Collection<ChessMove> populateMovesKing(ChessBoard board, ChessPosition myPosition) {
        ChessMovePatterns[] kingPatterns = new ChessMovePatterns[8];
        kingPatterns[0] = new ChessMovePatterns(1, 1, true, false);
        kingPatterns[1] = new ChessMovePatterns(1, 0, true, false);
        kingPatterns[2] = new ChessMovePatterns(0, 1, true, false);
        kingPatterns[3] = new ChessMovePatterns(1, -1, true, false);
        kingPatterns[4] = new ChessMovePatterns(-1, 1, true, false);
        kingPatterns[5] = new ChessMovePatterns(-1, -1, true, false);
        kingPatterns[6] = new ChessMovePatterns(-1, 0, true, false);
        kingPatterns[7] = new ChessMovePatterns(0, -1, true, false);
        return populateSimpleMoves(board, myPosition, kingPatterns);
    }

    /**
     * Finds all moves for a Knight
     *
     * @param board      current board find simple moves from
     * @param myPosition position on board piece is located
     * @return a collection of possible ChessMoves
     */
    public Collection<ChessMove> populateMovesKnight(ChessBoard board, ChessPosition myPosition) {
        ChessMovePatterns[] knightPatterns = new ChessMovePatterns[8];
        knightPatterns[0] = new ChessMovePatterns(1, 2, true, false);
        knightPatterns[1] = new ChessMovePatterns(2, 1, true, false);
        knightPatterns[2] = new ChessMovePatterns(-1, 2, true, false);
        knightPatterns[3] = new ChessMovePatterns(-2, 1, true, false);
        knightPatterns[4] = new ChessMovePatterns(1, -2, true, false);
        knightPatterns[5] = new ChessMovePatterns(2, -1, true, false);
        knightPatterns[6] = new ChessMovePatterns(-1, -2, true, false);
        knightPatterns[7] = new ChessMovePatterns(-2, -1, true, false);
        return populateSimpleMoves(board, myPosition, knightPatterns);
    }

    /**
     * Finds all moves for Pawns
     *
     * @param board      current board find simple moves from
     * @param myPosition position on board piece is located
     * @return a collection of possible ChessMoves
     */
    public Collection<ChessMove> populateMovesPawn(ChessBoard board, ChessPosition myPosition) {
        ChessMovePatterns[] pawnPatterns = new ChessMovePatterns[3];
        pawnPatterns[0] = new ChessMovePatterns(1, 0, false, false);
        pawnPatterns[1] = new ChessMovePatterns(1, 1, true, false, true);
        pawnPatterns[2] = new ChessMovePatterns(1, -1, true, false, true);

        Collection<ChessMove> simpleMoves = populateSimpleMoves(board, myPosition, pawnPatterns);

        // Nonstandard moves
        int promotionRow = (this.teamColor == ChessGame.TeamColor.WHITE) ? 8 : 1;
        Collection<ChessMove> possibleMoves = new ArrayList<ChessMove>();
        for (ChessMove move : simpleMoves) {
            if (move.getEndPosition().getRow() == promotionRow) {
                possibleMoves.add(new ChessMove(move, PieceType.ROOK));
                possibleMoves.add(new ChessMove(move, PieceType.BISHOP));
                possibleMoves.add(new ChessMove(move, PieceType.KNIGHT));
                possibleMoves.add(new ChessMove(move, PieceType.QUEEN));
            } else {
                possibleMoves.add(move);
            }
        }
        int homeRow = (this.teamColor == ChessGame.TeamColor.WHITE) ? 2 : 7;
        int rowMultiplier = (this.teamColor == ChessGame.TeamColor.WHITE) ? 1 : -1;
        if (myPosition.getRow() == homeRow) {
            ChessPiece ahead1 = board.getPiece(new ChessPosition(myPosition.getRow() + rowMultiplier,
                    myPosition.getColumn()));
            ChessPosition ahead2Square = new ChessPosition(myPosition.getRow() + 2 * rowMultiplier,
                    myPosition.getColumn());
            ChessPiece ahead_2 = board.getPiece(ahead2Square);
            if (ahead1 == null && ahead_2 == null) {
                possibleMoves.add(new ChessMove(myPosition, ahead2Square, null));
            }
        }

        return possibleMoves;
    }

    /**
     * Finds all possible moves for a queen
     *
     * @param board      current board find simple moves from
     * @param myPosition position on board piece is located
     * @return a collection of possible ChessMoves
     */
    public Collection<ChessMove> populateMovesQueen(ChessBoard board, ChessPosition myPosition) {
        ChessMovePatterns[] queenPatterns = new ChessMovePatterns[8];
        queenPatterns[0] = new ChessMovePatterns(1, 1, true, true);
        queenPatterns[1] = new ChessMovePatterns(1, 0, true, true);
        queenPatterns[2] = new ChessMovePatterns(0, 1, true, true);
        queenPatterns[3] = new ChessMovePatterns(1, -1, true, true);
        queenPatterns[4] = new ChessMovePatterns(-1, 1, true, true);
        queenPatterns[5] = new ChessMovePatterns(-1, -1, true, true);
        queenPatterns[6] = new ChessMovePatterns(-1, 0, true, true);
        queenPatterns[7] = new ChessMovePatterns(0, -1, true, true);
        return populateSimpleMoves(board, myPosition, queenPatterns);
    }

    /**
     * Finds all possible moves for a rook
     *
     * @param board      current board find simple moves from
     * @param myPosition position on board piece is located
     * @return a collection of possible ChessMoves
     */
    public Collection<ChessMove> populateMovesRook(ChessBoard board, ChessPosition myPosition) {
        ChessMovePatterns[] rookPatterns = new ChessMovePatterns[4];
        rookPatterns[0] = new ChessMovePatterns(1, 0, true, true);
        rookPatterns[1] = new ChessMovePatterns(0, 1, true, true);
        rookPatterns[2] = new ChessMovePatterns(-1, 0, true, true);
        rookPatterns[3] = new ChessMovePatterns(0, -1, true, true);
        return populateSimpleMoves(board, myPosition, rookPatterns);
    }

    /**
     * Finds all moves for a colleciton of Move Patterns
     *
     * @param board        current board find simple moves from
     * @param myPosition   position on board piece is located
     * @param movePatterns an array of possible simple move patters to test
     * @return a collection of possible ChessMoves
     */
    public Collection<ChessMove> populateSimpleMoves(ChessBoard board, ChessPosition myPosition, ChessMovePatterns[] movePatterns) {
        int colorMultiplier = (this.teamColor == ChessGame.TeamColor.BLACK) ? -1 : 1;

        Collection<ChessMove> allMoves = new ArrayList<ChessMove>();
        for (ChessMovePatterns movePattern : movePatterns) {
            int currRow = myPosition.getRow();
            int currCol = myPosition.getColumn();
            ChessPiece pieceInSquare;
            do {
                currRow += colorMultiplier * movePattern.row_diff();
                currCol += colorMultiplier * movePattern.col_diff();

                if (currRow > ChessBoard.N_ROWS || currCol > ChessBoard.N_COLS) {
                    break;
                }
                if (currRow <= 0 || currCol <= 0) {
                    break;
                }
                pieceInSquare = board.getPiece(new ChessPosition(currRow, currCol));
                ChessPosition dest = new ChessPosition(currRow, currCol);
                if (pieceInSquare != null) {
                    if ((pieceInSquare.teamColor != this.teamColor) &&
                            (pieceInSquare.pieceType != PieceType.KING) &&
                            movePattern.canTake()) {
                        allMoves.add(new ChessMove(myPosition, dest, null));
                    }
                } else if (!movePattern.needTakeToMove()) {
                    allMoves.add(new ChessMove(myPosition, dest, null));
                }

            } while (movePattern.canRepeat() && (board.getPiece(new ChessPosition(currRow, currCol))) == null);
        }
        return allMoves;
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
