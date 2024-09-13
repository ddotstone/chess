package chess;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove extends Object {

    private final ChessPosition startPosition;
    private final ChessPosition endPosition;
    private final ChessPiece.PieceType promotionPiece;

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.promotionPiece = promotionPiece;
    }

    public ChessMove(ChessMove copy, ChessPiece.PieceType promotionPiece) {
        this(copy.startPosition, copy.endPosition, promotionPiece);
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
        return this.startPosition;
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
        return this.endPosition;
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {
        return this.promotionPiece;
    }

    @Override
    public int hashCode() {
        int promotionCode = (promotionPiece == null) ? 9 : promotionPiece.ordinal();
        return (startPosition.hashCode() + endPosition.hashCode() + promotionCode) * 71;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ChessMove that = (ChessMove) obj;
        return ((that.startPosition.equals(this.startPosition)) &&
                (that.endPosition.equals(this.endPosition)) &&
                (this.promotionPiece == null ? that.promotionPiece == null : this.promotionPiece.equals(that.promotionPiece)));
    }

    @Override
    public String toString() {
        String promotionPieceString = (this.promotionPiece == null) ? "" : promotionPiece.toString();
        return ("ChessMove{" + startPosition.toString() + ", " + endPosition.toString() + ", " + promotionPieceString + "}");
    }


}
