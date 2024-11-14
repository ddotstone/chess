package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import static ui.EscapeSequences.*;

import java.util.ArrayList;

public class DisplayFunctions {
    public static String boardStringWhite(ChessBoard board) {
        return boardFancyString(board, ChessGame.TeamColor.WHITE);
    }

    public static String boardStringBlack(ChessBoard board) {
        return boardFancyString(board, ChessGame.TeamColor.BLACK);
    }

    private static String boardFancyString(ChessBoard board, ChessGame.TeamColor colorOrient) {
        String[] colDefines = new String[]{" A ", " B ", "  C  ", " D ", " E ", " F ", "  G  ", " H "};
        String[] rowDefines = new String[]{" 1 ", " 2 ", " 3 ", " 4 ", " 5 ", " 6 ", " 7 ", " 8 "};
        ArrayList<String> squares = new ArrayList<>();

        squares.add(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + EMPTY);
        for (int i = 0; i < colDefines.length; i++) {
            squares.add(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + colDefines[i]);
        }
        squares.add(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + EMPTY);
        boolean whiteRow = true;
        for (int i = rowDefines.length - 1; i >= 0; i--) {
            boolean whiteCol = whiteRow;
            whiteRow = !whiteRow;
            squares.add(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + rowDefines[i]);
            for (int j = 0; j < colDefines.length; j++) {
                String backColor = (whiteCol) ? SET_BG_COLOR_WHITE : SET_BG_COLOR_LIGHT_GREY;
                ChessPiece currPiece = board.getPiece(new ChessPosition(i + 1, j + 1));

                String piece;
                if (currPiece == null) {
                    piece = EMPTY;
                } else {
                    boolean isWhite = (currPiece.getTeamColor() == ChessGame.TeamColor.WHITE);
                    ChessPiece.PieceType type = currPiece.getPieceType();
                    switch (type) {
                        case KING:
                            piece = isWhite ? WHITE_KING : BLACK_KING;
                            break;
                        case QUEEN:
                            piece = isWhite ? WHITE_QUEEN : BLACK_QUEEN;
                            break;
                        case ROOK:
                            piece = isWhite ? WHITE_ROOK : BLACK_ROOK;
                            break;
                        case BISHOP:
                            piece = isWhite ? WHITE_BISHOP : BLACK_BISHOP;
                            break;
                        case KNIGHT:
                            piece = isWhite ? WHITE_KNIGHT : BLACK_KNIGHT;
                            break;
                        case PAWN:
                            piece = isWhite ? WHITE_PAWN : BLACK_PAWN;
                            break;
                        default:
                            piece = EMPTY;
                            break;

                    }
                }
                squares.add(backColor + SET_TEXT_COLOR_BLACK + piece);
                whiteCol = !whiteCol;
            }
            squares.add(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + rowDefines[i]);
        }

        squares.add(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + EMPTY);
        for (int i = 0; i < colDefines.length; i++) {
            squares.add(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + colDefines[i]);
        }
        squares.add(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + EMPTY);

        StringBuilder result = new StringBuilder("");
        result.append(SET_TEXT_BOLD);
        if (colorOrient == ChessGame.TeamColor.WHITE) {
            int added = 0;
            for (String square : squares) {
                result.append(square);
                added++;
                if (((added / 10) != 10) && (added % (colDefines.length + 2) == 0)) {
                    result.append(RESET_BG_COLOR + "\n");
                }

            }
        } else {
            int added = 0;
            for (int i = squares.size() - 1; i >= 0; i--) {
                String square = squares.get(i);
                result.append(square);
                added++;
                if (((added / 10) != 10) && (added % (colDefines.length + 2) == 0)) {
                    result.append(RESET_BG_COLOR + "\n");
                }

            }
        }
        result.append(RESET_BG_COLOR);
        return result.toString();
    }
}
