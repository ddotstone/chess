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
        this.board = new ChessPiece[N_ROWS][];
        for (int row = 0; row < N_ROWS; row++) {
            ChessPiece[] currRow = copy.board[row];
            System.arraycopy(currRow, 0, this.board[row], 0, N_COLS);
        }
    }

    @Override
    public int hashCode() {
        int hash_code = 0;
        for (int row = 0; row < N_ROWS; row++) {
            for (int col = 0; col < N_COLS; col++) {
                int hash_piece = this.board[row][col] == null ? 0 : this.board[row][col].hashCode();
                hash_code += hash_piece;
            }
        }
        return 71 * hash_code;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || this.getClass() != obj.getClass()) return false;
        ChessBoard that = (ChessBoard) obj;
        for (int row = 0; row < N_ROWS; row++) {
            for (int col = 0; col < N_COLS; col++) {
                boolean is_equal = this.board[row][col] == null ?
                        that.board[row][col] == null :
                        this.board[row][col].equals(that.board[row][col]);
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
        StringBuilder string_board = new StringBuilder();
        for (int row = N_ROWS - 1; row >= 0; row--) {
            string_board.append('|');
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
                string_board.append(piece);
                string_board.append('|');
            }
            string_board.append("\n");
        }
        return string_board.toString();
    }
}
