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

    public ChessBoard(ChessBoard copy) {
        if (copy.board == null) {
            this.board = null;
        }
        this.board = new ChessPiece[N_ROWS][];
        for (int row = 0; row < N_ROWS; row++) {
            this.board[row] = copy.board[row].clone();
        }
    }

    public static boolean isOnBoard(int row, int col) {
        return ((row >= 1) && (col >= 1) && (row <= 8) && (col <= 8));
    }

    @Override
    public int hashCode() {
        int hashCode = 0;
        for (int row = 0; row < N_ROWS; row++) {
            for (int col = 0; col < N_COLS; col++) {
                int hashPiece = this.board[row][col] == null ? 0 : this.board[row][col].hashCode();
                hashCode += hashPiece;
            }
        }
        return 71 * hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) obj;
        for (int row = 0; row < N_ROWS; row++) {
            for (int col = 0; col < N_COLS; col++) {
                boolean isEqual = this.board[row][col] == null ?
                        that.board[row][col] == null :
                        this.board[row][col].equals(that.board[row][col]);
                if (!isEqual) {
                    return false;
                }
            }
        }
        return true;
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
            for (int col = 0; col < board.length; col++) {
                if (row == 0 || row == 7) {
                    ChessGame.TeamColor teamColor = (row == 0) ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;
                    board[row][0] = new ChessPiece(teamColor, ChessPiece.PieceType.ROOK);
                    board[row][1] = new ChessPiece(teamColor, ChessPiece.PieceType.KNIGHT);
                    board[row][2] = new ChessPiece(teamColor, ChessPiece.PieceType.BISHOP);
                    board[row][3] = new ChessPiece(teamColor, ChessPiece.PieceType.QUEEN);
                    board[row][4] = new ChessPiece(teamColor, ChessPiece.PieceType.KING);
                    board[row][5] = new ChessPiece(teamColor, ChessPiece.PieceType.BISHOP);
                    board[row][6] = new ChessPiece(teamColor, ChessPiece.PieceType.KNIGHT);
                    board[row][7] = new ChessPiece(teamColor, ChessPiece.PieceType.ROOK);
                    break;
                } else if (row == 1 || row == 6) {
                    ChessGame.TeamColor teamColor = (row == 1) ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;
                    board[row][col] = new ChessPiece(teamColor, ChessPiece.PieceType.PAWN);
                } else {
                    board[row][col] = null;
                }
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBoard = new StringBuilder();
        for (int row = N_ROWS - 1; row >= 0; row--) {
            stringBoard.append('|');
            for (int col = 0; col < N_COLS; col++) {
                String piece;
                if (this.board[row][col] == null) {
                    piece = " ";
                } else {
                    if (this.board[row][col].getPieceType() == ChessPiece.PieceType.KNIGHT) {
                        piece = "n";

                    } else {
                        piece = this.board[row][col].getPieceType().toString().substring(0, 1);
                    }

                    if (this.board[row][col].getTeamColor() == ChessGame.TeamColor.WHITE) {
                        piece = piece.toUpperCase();
                    } else {
                        piece = piece.toLowerCase();
                    }
                }
                stringBoard.append(piece);
                stringBoard.append('|');
            }
            stringBoard.append("\n");
        }
        return stringBoard.toString();
    }
}
