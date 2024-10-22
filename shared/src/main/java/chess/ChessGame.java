package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard board;
    private TeamColor teamTurn;

    public ChessGame(ChessGame another) {
        if (another == null) {
            return;
        }
        if (another.board == null) {
            this.board = null;
        } else {
            this.board = new ChessBoard(another.getBoard());
        }
        this.teamTurn = another.getTeamTurn();
        return;
    }

    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
        teamTurn = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece currPiece = board.getPiece(startPosition);
        if (currPiece == null) {
            return null;
        }

        Collection<ChessMove> allMoves = currPiece.pieceMoves(board, startPosition);
        Collection<ChessMove> validMoves = new ArrayList<ChessMove>();
        for (ChessMove move : allMoves) {
            ChessPiece endPiece = board.getPiece(move.getEndPosition());
            board.addPiece(move.getStartPosition(), null);
            board.addPiece(move.getEndPosition(), currPiece);

            if (!this.isInCheck(currPiece.getTeamColor())) {
                validMoves.add(move);
            }
            board.addPiece(move.getStartPosition(), currPiece);
            board.addPiece(move.getEndPosition(), endPiece);
        }
        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();
        ChessPiece currPiece = board.getPiece(move.getStartPosition());
        if (currPiece == null) {
            throw new InvalidMoveException("Empty Space");
        }
        if (currPiece.getTeamColor() != teamTurn) {
            throw new InvalidMoveException("Making move out of turn");
        }
        Collection<ChessMove> allMoves = validMoves(start);
        boolean legalMove = false;
        for (ChessMove currMove : allMoves) {
            if (currMove.equals(move)) {
                legalMove = true;
            }
        }

        if (!legalMove) {
            throw new InvalidMoveException("Not a valid move");
        }
        ChessPiece.PieceType endType = currPiece.getPieceType();
        if (move.getPromotionPiece() != null) {
            endType = move.getPromotionPiece();
        }

        board.addPiece(move.getStartPosition(), null);
        board.addPiece(move.getEndPosition(), new ChessPiece(currPiece.getTeamColor(), endType));
        teamTurn = (teamTurn == TeamColor.BLACK) ? TeamColor.WHITE : TeamColor.BLACK;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = null;
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition currPosition = new ChessPosition(row, col);
                ChessPiece currPiece = board.getPiece(currPosition);
                if (currPiece != null &&
                        currPiece.getPieceType() == ChessPiece.PieceType.KING &&
                        currPiece.getTeamColor() == teamColor) {
                    kingPosition = currPosition;
                    break;

                }
            }
        }
        if (kingPosition == null) {
            return false;
        }

        int colorMultiplier = (teamColor == TeamColor.BLACK) ? -1 : 1;

        int row = kingPosition.getRow();
        int col = kingPosition.getColumn();
        int iterations = 0;
        do {
            row += colorMultiplier;
            col++;
            iterations++;
        } while (row >= 1 && row <= 8 && col >= 1 && col <= 8 &&
                (board.getPiece(new ChessPosition(row, col)) == null));

        if (row >= 1 && row <= 8 && col >= 1 && col <= 8) {
            ChessPiece endPiece = board.getPiece(new ChessPosition(row, col));
            if (endPiece.getTeamColor() != teamColor) {
                if ((iterations == 1 && endPiece.getPieceType() == ChessPiece.PieceType.KING) ||
                        endPiece.getPieceType() == ChessPiece.PieceType.QUEEN ||
                        endPiece.getPieceType() == ChessPiece.PieceType.PAWN ||
                        endPiece.getPieceType() == ChessPiece.PieceType.BISHOP) {
                    return true;
                }
            }
        }

        row = kingPosition.getRow();
        col = kingPosition.getColumn();
        iterations = 0;

        do {
            row += colorMultiplier;
            col--;
            iterations++;
        }
        while (row >= 1 && row <= 8 && col >= 1 && col <= 8 &&
                board.getPiece(new ChessPosition(row, col)) == null);

        if (row >= 1 && row <= 8 && col >= 1 && col <= 8) {
            ChessPiece endPiece = board.getPiece(new ChessPosition(row, col));
            if (endPiece.getTeamColor() != teamColor) {
                if ((iterations == 1 && endPiece.getPieceType() == ChessPiece.PieceType.KING) ||
                        endPiece.getPieceType() == ChessPiece.PieceType.QUEEN ||
                        endPiece.getPieceType() == ChessPiece.PieceType.PAWN ||
                        endPiece.getPieceType() == ChessPiece.PieceType.BISHOP) {
                    return true;
                }
            }
        }

        row = kingPosition.getRow();
        col = kingPosition.getColumn();
        iterations = 0;
        do {
            row -= colorMultiplier;
            col++;
            iterations++;
        } while (row >= 1 && row <= 8 && col >= 1 && col <= 8 &&
                board.getPiece(new ChessPosition(row, col)) == null);

        if (row >= 1 && row <= 8 && col >= 1 && col <= 8) {
            ChessPiece endPiece = board.getPiece(new ChessPosition(row, col));
            if (endPiece.getTeamColor() != teamColor) {
                if ((iterations == 1 && endPiece.getPieceType() == ChessPiece.PieceType.KING) ||
                        endPiece.getPieceType() == ChessPiece.PieceType.QUEEN ||
                        endPiece.getPieceType() == ChessPiece.PieceType.BISHOP) {
                    return true;
                }
            }
        }
        row = kingPosition.getRow();
        col = kingPosition.getColumn();
        iterations = 0;
        do {
            row -= colorMultiplier;
            col--;
            iterations++;
        } while (row >= 1 && row <= 8 && col >= 1 && col <= 8 &&
                board.getPiece(new ChessPosition(row, col)) == null);

        if (row >= 1 && row <= 8 && col >= 1 && col <= 8) {
            ChessPiece endPiece = board.getPiece(new ChessPosition(row, col));
            if (endPiece.getTeamColor() != teamColor) {
                if ((iterations == 1 && endPiece.getPieceType() == ChessPiece.PieceType.KING) ||
                        endPiece.getPieceType() == ChessPiece.PieceType.QUEEN ||
                        endPiece.getPieceType() == ChessPiece.PieceType.BISHOP) {
                    return true;
                }
            }
        }

        row = kingPosition.getRow();
        col = kingPosition.getColumn();
        iterations = 0;
        do {
            row += colorMultiplier;
            iterations++;
        } while (row >= 1 && row <= 8 && col >= 1 && col <= 8 &&
                board.getPiece(new ChessPosition(row, col)) == null);

        if (row >= 1 && row <= 8 && col >= 1 && col <= 8) {
            ChessPiece endPiece = board.getPiece(new ChessPosition(row, col));
            if (endPiece.getTeamColor() != teamColor) {
                if ((iterations == 1 && endPiece.getPieceType() == ChessPiece.PieceType.KING) ||
                        endPiece.getPieceType() == ChessPiece.PieceType.QUEEN ||
                        endPiece.getPieceType() == ChessPiece.PieceType.ROOK) {
                    return true;
                }
            }
        }

        row = kingPosition.getRow();
        col = kingPosition.getColumn();
        iterations = 0;
        do {
            row -= colorMultiplier;
            iterations++;
        } while (row >= 1 && row <= 8 && col >= 1 && col <= 8 &&
                board.getPiece(new ChessPosition(row, col)) == null);

        if (row >= 1 && row <= 8 && col >= 1 && col <= 8) {
            ChessPiece endPiece = board.getPiece(new ChessPosition(row, col));
            if (endPiece.getTeamColor() != teamColor) {
                if ((iterations == 1 && endPiece.getPieceType() == ChessPiece.PieceType.KING) ||
                        endPiece.getPieceType() == ChessPiece.PieceType.QUEEN ||
                        endPiece.getPieceType() == ChessPiece.PieceType.ROOK) {
                    return true;
                }
            }
        }

        row = kingPosition.getRow();
        col = kingPosition.getColumn();
        iterations = 0;
        do {
            col += 1;
            iterations++;
        } while (row >= 1 && row <= 8 && col >= 1 && col <= 8 &&
                board.getPiece(new ChessPosition(row, col)) == null);

        if (row >= 1 && row <= 8 && col >= 1 && col <= 8) {
            ChessPiece endPiece = board.getPiece(new ChessPosition(row, col));
            if (endPiece.getTeamColor() != teamColor) {
                if ((iterations == 1 && endPiece.getPieceType() == ChessPiece.PieceType.KING) ||
                        endPiece.getPieceType() == ChessPiece.PieceType.QUEEN ||
                        endPiece.getPieceType() == ChessPiece.PieceType.ROOK) {
                    return true;
                }
            }
        }

        row = kingPosition.getRow();
        col = kingPosition.getColumn();
        iterations = 0;
        do {
            col -= 1;
            iterations++;
        } while (row >= 1 && row <= 8 && col >= 1 && col <= 8 &&
                board.getPiece(new ChessPosition(row, col)) == null);

        if (row >= 1 && row <= 8 && col >= 1 && col <= 8) {
            ChessPiece endPiece = board.getPiece(new ChessPosition(row, col));
            if (endPiece.getTeamColor() != teamColor) {
                if ((iterations == 1 && endPiece.getPieceType() == ChessPiece.PieceType.KING) ||
                        endPiece.getPieceType() == ChessPiece.PieceType.QUEEN ||
                        endPiece.getPieceType() == ChessPiece.PieceType.ROOK) {
                    return true;
                }
            }
        }
        row = kingPosition.getRow();
        col = kingPosition.getColumn();

        row += 2;
        col += 1;
        if (row >= 1 && row <= 8 && col >= 1 && col <= 8) {
            ChessPiece endPiece = board.getPiece(new ChessPosition(row, col));
            if (endPiece != null && endPiece.getTeamColor() != teamColor) {
                if (endPiece.getPieceType() == ChessPiece.PieceType.KNIGHT) {
                    return true;
                }
            }
        }
        row = kingPosition.getRow();
        col = kingPosition.getColumn();

        row += 1;
        col += 2;
        if (row >= 1 && row <= 8 && col >= 1 && col <= 8) {
            ChessPiece endPiece = board.getPiece(new ChessPosition(row, col));
            if (endPiece != null && endPiece.getTeamColor() != teamColor) {
                if (endPiece.getPieceType() == ChessPiece.PieceType.KNIGHT) {
                    return true;
                }
            }
        }
        row = kingPosition.getRow();
        col = kingPosition.getColumn();

        row -= 2;
        col += 1;
        if (row >= 1 && row <= 8 && col >= 1 && col <= 8) {
            ChessPiece endPiece = board.getPiece(new ChessPosition(row, col));
            if (endPiece != null && endPiece.getTeamColor() != teamColor) {
                if (endPiece.getPieceType() == ChessPiece.PieceType.KNIGHT) {
                    return true;
                }
            }
        }
        row = kingPosition.getRow();
        col = kingPosition.getColumn();

        row -= 1;
        col += 2;
        if (row >= 1 && row <= 8 && col >= 1 && col <= 8) {
            ChessPiece endPiece = board.getPiece(new ChessPosition(row, col));
            if (endPiece != null && endPiece.getTeamColor() != teamColor) {
                if (endPiece.getPieceType() == ChessPiece.PieceType.KNIGHT) {
                    return true;
                }
            }
        }
        row = kingPosition.getRow();
        col = kingPosition.getColumn();

        row += 2;
        col -= 1;
        if (row >= 1 && row <= 8 && col >= 1 && col <= 8) {
            ChessPiece endPiece = board.getPiece(new ChessPosition(row, col));
            if (endPiece != null && endPiece.getTeamColor() != teamColor) {
                if (endPiece.getPieceType() == ChessPiece.PieceType.KNIGHT) {
                    return true;
                }
            }
        }
        row = kingPosition.getRow();
        col = kingPosition.getColumn();

        row += 1;
        col -= 2;
        if (row >= 1 && row <= 8 && col >= 1 && col <= 8) {
            ChessPiece endPiece = board.getPiece(new ChessPosition(row, col));
            if (endPiece != null && endPiece.getTeamColor() != teamColor) {
                if (endPiece.getPieceType() == ChessPiece.PieceType.KNIGHT) {
                    return true;
                }
            }
        }

        row = kingPosition.getRow();
        col = kingPosition.getColumn();

        row -= 2;
        col -= 1;
        if (row >= 1 && row <= 8 && col >= 1 && col <= 8) {
            ChessPiece endPiece = board.getPiece(new ChessPosition(row, col));
            if (endPiece != null && endPiece.getTeamColor() != teamColor) {
                if (endPiece.getPieceType() == ChessPiece.PieceType.KNIGHT) {
                    return true;
                }
            }
        }

        row = kingPosition.getRow();
        col = kingPosition.getColumn();

        row -= 1;
        col -= 2;
        if (row >= 1 && row <= 8 && col >= 1 && col <= 8) {
            ChessPiece endPiece = board.getPiece(new ChessPosition(row, col));
            if (endPiece != null && endPiece.getTeamColor() != teamColor) {
                if (endPiece.getPieceType() == ChessPiece.PieceType.KNIGHT) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            return false;
        }
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPiece currPiece = this.board.getPiece(new ChessPosition(row, col));
                if (currPiece != null && currPiece.getTeamColor() == teamColor) {
                    if (validMoves(new ChessPosition(row, col)).size() > 0) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return false;
        }
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPiece currPiece = this.board.getPiece(new ChessPosition(row, col));
                if (currPiece != null && currPiece.getTeamColor() == teamColor) {
                    if (validMoves(new ChessPosition(row, col)).size() > 0) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = new ChessBoard(board);
    }

    @Override
    public String toString() {
        String print = String.format("    %s    \n%s\n", teamTurn.toString(), board.toString());
        return print;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }
}
