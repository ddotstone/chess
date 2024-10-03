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

    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        throw new RuntimeException("Not implemented");
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
            try {
                this.makeMove(move);
            } catch (InvalidMoveException e) {
                return null;
            }
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
        if (start.getRow() > 8 || start.getRow() < 1 || start.getColumn() > 8 || start.getColumn() < 1) {
            throw new InvalidMoveException("Bad Start");
        }
        if (end.getRow() > 8 || end.getRow() < 1 || end.getColumn() > 8 || end.getColumn() < 1) {
            throw new InvalidMoveException("Bad End");
        }
        ChessPiece currPiece = board.getPiece(move.getStartPosition());
        if (currPiece == null) {
            throw new InvalidMoveException("Empty Space");
        }
        ChessPiece.PieceType endType = currPiece.getPieceType();
        if (move.getPromotionPiece() != null) {
            endType = move.getPromotionPiece();
        }
        board.addPiece(move.getStartPosition(), null);
        board.addPiece(move.getEndPosition(), new ChessPiece(currPiece.getTeamColor(), endType));
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
        do {
            row += colorMultiplier;
            col++;
        } while (row >= 1 && row <= 8 && col >= 1 && col <= 8 &&
                (board.getPiece(new ChessPosition(row, col)) == null));

        ChessPiece test = board.getPiece(new ChessPosition(row, col));

        if (row >= 1 && row <= 8 && col >= 1 && col <= 8) {
            ChessPiece endPiece = board.getPiece(new ChessPosition(row, col));
            if (endPiece.getTeamColor() != teamColor) {
                if (endPiece.getPieceType() == ChessPiece.PieceType.KING ||
                        endPiece.getPieceType() == ChessPiece.PieceType.QUEEN ||
                        endPiece.getPieceType() == ChessPiece.PieceType.PAWN ||
                        endPiece.getPieceType() == ChessPiece.PieceType.BISHOP) {
                    return true;
                }
            }
        }

        row = kingPosition.getRow();
        col = kingPosition.getColumn();

        do {
            row += colorMultiplier;
            col--;
        }
        while (row >= 1 && row <= 8 && col >= 1 && col <= 8 &&
                board.getPiece(new ChessPosition(row, col)) == null);

        if (row >= 1 && row <= 8 && col >= 1 && col <= 8) {
            ChessPiece endPiece = board.getPiece(new ChessPosition(row, col));
            if (endPiece.getTeamColor() != teamColor) {
                if (endPiece.getPieceType() == ChessPiece.PieceType.KING ||
                        endPiece.getPieceType() == ChessPiece.PieceType.QUEEN ||
                        endPiece.getPieceType() == ChessPiece.PieceType.PAWN ||
                        endPiece.getPieceType() == ChessPiece.PieceType.BISHOP) {
                    return true;
                }
            }
        }

        row = kingPosition.getRow();
        col = kingPosition.getColumn();
        do {
            row -= colorMultiplier;
            col++;
        } while (row >= 1 && row <= 8 && col >= 1 && col <= 8 &&
                board.getPiece(new ChessPosition(row, col)) == null);

        if (row >= 1 && row <= 8 && col >= 1 && col <= 8) {
            ChessPiece endPiece = board.getPiece(new ChessPosition(row, col));
            if (endPiece.getTeamColor() != teamColor) {
                if (endPiece.getPieceType() == ChessPiece.PieceType.KING ||
                        endPiece.getPieceType() == ChessPiece.PieceType.QUEEN ||
                        endPiece.getPieceType() == ChessPiece.PieceType.BISHOP) {
                    return true;
                }
            }
        }
        row = kingPosition.getRow();
        col = kingPosition.getColumn();
        do {
            row -= colorMultiplier;
            col--;
        } while (row >= 1 && row <= 8 && col >= 1 && col <= 8 &&
                board.getPiece(new ChessPosition(row, col)) == null);

        if (row >= 1 && row <= 8 && col >= 1 && col <= 8) {
            ChessPiece endPiece = board.getPiece(new ChessPosition(row, col));
            if (endPiece.getTeamColor() != teamColor) {
                if (endPiece.getPieceType() == ChessPiece.PieceType.KING ||
                        endPiece.getPieceType() == ChessPiece.PieceType.QUEEN ||
                        endPiece.getPieceType() == ChessPiece.PieceType.BISHOP) {
                    return true;
                }
            }
        }

        row = kingPosition.getRow();
        col = kingPosition.getRow();
        do {
            row += colorMultiplier;
        } while (row >= 1 && row <= 8 && col >= 1 && col <= 8 &&
                board.getPiece(new ChessPosition(row, col)) == null);

        if (row >= 1 && row <= 8 && col >= 1 && col <= 8) {
            ChessPiece endPiece = board.getPiece(new ChessPosition(row, col));
            if (endPiece.getTeamColor() != teamColor) {
                if (endPiece.getPieceType() == ChessPiece.PieceType.KING ||
                        endPiece.getPieceType() == ChessPiece.PieceType.QUEEN ||
                        endPiece.getPieceType() == ChessPiece.PieceType.ROOK) {
                    return true;
                }
            }
        }

        row = kingPosition.getRow();
        col = kingPosition.getRow();
        do {
            row -= colorMultiplier;
        } while (row >= 1 && row <= 8 && col >= 1 && col <= 8 &&
                board.getPiece(new ChessPosition(row, col)) == null);

        if (row >= 1 && row <= 8 && col >= 1 && col <= 8) {
            ChessPiece endPiece = board.getPiece(new ChessPosition(row, col));
            if (endPiece.getTeamColor() != teamColor) {
                if (endPiece.getPieceType() == ChessPiece.PieceType.KING ||
                        endPiece.getPieceType() == ChessPiece.PieceType.QUEEN ||
                        endPiece.getPieceType() == ChessPiece.PieceType.ROOK) {
                    return true;
                }
            }
        }

        row = kingPosition.getRow();
        col = kingPosition.getRow();
        do {
            col += 1;
        } while (row >= 1 && row <= 8 && col >= 1 && col <= 8 &&
                board.getPiece(new ChessPosition(row, col)) == null);

        if (row >= 1 && row <= 8 && col >= 1 && col <= 8) {
            ChessPiece endPiece = board.getPiece(new ChessPosition(row, col));
            if (endPiece.getTeamColor() != teamColor) {
                if (endPiece.getPieceType() == ChessPiece.PieceType.KING ||
                        endPiece.getPieceType() == ChessPiece.PieceType.QUEEN ||
                        endPiece.getPieceType() == ChessPiece.PieceType.ROOK) {
                    return true;
                }
            }
        }

        row = kingPosition.getRow();
        col = kingPosition.getRow();
        do {
            col -= 1;
        } while (row >= 1 && row <= 8 && col >= 1 && col <= 8 &&
                board.getPiece(new ChessPosition(row, col)) == null);

        if (row >= 1 && row <= 8 && col >= 1 && col <= 8) {
            ChessPiece endPiece = board.getPiece(new ChessPosition(row, col));
            if (endPiece.getTeamColor() != teamColor) {
                if (endPiece.getPieceType() == ChessPiece.PieceType.KING ||
                        endPiece.getPieceType() == ChessPiece.PieceType.QUEEN ||
                        endPiece.getPieceType() == ChessPiece.PieceType.ROOK) {
                    return true;
                }
            }
        }
        row = kingPosition.getRow();
        col = kingPosition.getRow();

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
        col = kingPosition.getRow();

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
        col = kingPosition.getRow();

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
        col = kingPosition.getRow();

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
        col = kingPosition.getRow();

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
        col = kingPosition.getRow();

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
        col = kingPosition.getRow();

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
        col = kingPosition.getRow();

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
        throw new RuntimeException("Not implemented");
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }
}
