package chess;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    public static final int N_ROWS = 8;
    public static final int N_COLS = 8;
    private ChessPiece[][] board;

    public ChessBoard() {
        board = new ChessPiece[N_ROWS][N_COLS];
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        if (position.getRow() <= N_ROWS && position.getColumn() <= N_COLS) {
            return board[position.getRow() - 1][position.getColumn() - 1];
        }
        return null;
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        for (int row = 0; row < board.length; row++) {
            if (row == 0 || row == 7) {
                ChessGame.TeamColor teamColor = (row == 0) ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;
                board[row][0] = new ChessPiece(teamColor, ChessPiece.PieceType.ROOK);
                board[row][1] = new ChessPiece(teamColor, ChessPiece.PieceType.KNIGHT);
                board[row][2] = new ChessPiece(teamColor, ChessPiece.PieceType.BISHOP);
                board[row][3] = new ChessPiece(teamColor, ChessPiece.PieceType.KING);
                board[row][4] = new ChessPiece(teamColor, ChessPiece.PieceType.QUEEN);
                board[row][5] = new ChessPiece(teamColor, ChessPiece.PieceType.BISHOP);
                board[row][6] = new ChessPiece(teamColor, ChessPiece.PieceType.KNIGHT);
                board[row][7] = new ChessPiece(teamColor, ChessPiece.PieceType.ROOK);
            } else if ((row == 1) || (row == 6)) {
                ChessGame.TeamColor teamColor = (row == 1) ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;
                for (ChessPiece space : board[row]) {
                    space = new ChessPiece(teamColor, ChessPiece.PieceType.PAWN);
                }
            } else {
                for (ChessPiece space : board[row]) {
                    space = null;
                }
            }

        }
    }
}
