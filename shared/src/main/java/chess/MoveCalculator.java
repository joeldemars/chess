package chess;

import java.util.ArrayList;
import java.util.Collection;

public class MoveCalculator {
    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPiece piece, ChessPosition position) {
        return switch (piece.getPieceType()) {
            case KING -> kingMoves(board, piece, position);
            case QUEEN -> queenMoves(board, piece, position);
            case BISHOP -> bishopMoves(board, piece, position);
            case KNIGHT -> knightMoves(board, piece, position);
            case ROOK -> rookMoves(board, piece, position);
            case PAWN -> pawnMoves(board, piece, position);
        };
    }

    /**
     * Get all possible moves along a straight line (i.e. for queens, bishops, and rooks), given the row and column
     * offsets that define the line (e.g. rowOffset = 1 and columnOffset = 1 to get possible moves NE of a piece).
     */
    private static ArrayList<ChessMove> movesAlong(ChessBoard board, ChessPiece piece, ChessPosition myPosition,
                                                   int rowOffset, int columnOffset) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        ChessPosition newPosition = myPosition.offsetBy(rowOffset, columnOffset);

        while (newPosition.isValid()) {
            ChessPiece otherPiece = board.getPiece(newPosition);
            if (otherPiece == null) {
                moves.add(new ChessMove(myPosition, newPosition, null));
                newPosition = newPosition.offsetBy(rowOffset, columnOffset);
            } else {
                if (otherPiece.getTeamColor() != piece.getTeamColor()) {
                    moves.add(new ChessMove(myPosition, newPosition, null));
                }
                break;
            }
        }

        return moves;
    }

    /**
     * Return all possible moves for a king on a given board in a given position.
     */
    private static Collection<ChessMove> kingMoves(ChessBoard board, ChessPiece piece, ChessPosition myPosition) {
        ArrayList<ChessMove> moves = new ArrayList<>(8);
        ChessPosition[] potentialPositions = {
                myPosition.offsetBy(-1, -1),
                myPosition.offsetBy(-1, 0),
                myPosition.offsetBy(-1, 1),
                myPosition.offsetBy(0, -1),
                myPosition.offsetBy(0, 1),
                myPosition.offsetBy(1, -1),
                myPosition.offsetBy(1, 0),
                myPosition.offsetBy(1, 1),
        };

        for (ChessPosition position : potentialPositions) {
            if (position.isValid()) {
                ChessPiece other = board.getPiece(position);
                if (other == null || other.getTeamColor() != piece.getTeamColor()) {
                    moves.add(new ChessMove(myPosition, position, null));
                }
            }
        }

        return moves;
    }

    /**
     * Return all possible moves for a queen on a given board in a given position.
     */
    private static Collection<ChessMove> queenMoves(ChessBoard board, ChessPiece piece, ChessPosition myPosition) {
        Collection<ChessMove> moves = bishopMoves(board, piece, myPosition);
        moves.addAll(rookMoves(board, piece, myPosition));
        return moves;
    }

    /**
     * Return all possible moves for a bishop on a given board in a given position.
     */
    private static Collection<ChessMove> bishopMoves(ChessBoard board, ChessPiece piece, ChessPosition myPosition) {
        ArrayList<ChessMove> moves = movesAlong(board, piece, myPosition, 1, 1);
        moves.addAll(movesAlong(board, piece, myPosition, 1, -1));
        moves.addAll(movesAlong(board, piece, myPosition, -1, 1));
        moves.addAll(movesAlong(board, piece, myPosition, -1, -1));
        return moves;
    }

    /**
     * Return all possible moves for a knight on a given board in a given position.
     */
    private static Collection<ChessMove> knightMoves(ChessBoard board, ChessPiece piece, ChessPosition myPosition) {
        ArrayList<ChessMove> moves = new ArrayList<>(8);
        ChessPosition[] potentialPositions = {
                myPosition.offsetBy(-2, -1),
                myPosition.offsetBy(-2, 1),
                myPosition.offsetBy(-1, 2),
                myPosition.offsetBy(-1, -2),
                myPosition.offsetBy(1, -2),
                myPosition.offsetBy(1, 2),
                myPosition.offsetBy(2, -1),
                myPosition.offsetBy(2, 1),
        };

        for (ChessPosition position : potentialPositions) {
            if (position.isValid()) {
                ChessPiece other = board.getPiece(position);
                if (other == null || other.getTeamColor() != piece.getTeamColor()) {
                    moves.add(new ChessMove(myPosition, position, null));
                }
            }
        }

        return moves;
    }

    /**
     * Return all possible moves for a rook on a given board in a given position.
     */
    private static Collection<ChessMove> rookMoves(ChessBoard board, ChessPiece piece, ChessPosition myPosition) {
        ArrayList<ChessMove> moves = movesAlong(board, piece, myPosition, 0, 1);
        moves.addAll(movesAlong(board, piece, myPosition, 0, -1));
        moves.addAll(movesAlong(board, piece, myPosition, 1, 0));
        moves.addAll(movesAlong(board, piece, myPosition, -1, 0));
        return moves;
    }

    /**
     * Return all possible moves for a pawn on a given board in a given position.
     */
    private static Collection<ChessMove> pawnMoves(ChessBoard board, ChessPiece piece, ChessPosition myPosition) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        int offset = piece.getTeamColor() == ChessGame.TeamColor.WHITE ? 1 : -1;
        boolean initialMove = myPosition.getRow() == (piece.getTeamColor() == ChessGame.TeamColor.WHITE ? 2 : 7);
        boolean promotionMove = myPosition.getRow() == (piece.getTeamColor() == ChessGame.TeamColor.WHITE ? 7 : 2);
        ChessPosition singleSquare = myPosition.offsetBy(offset, 0);
        ChessPosition doubleSquare = myPosition.offsetBy(2 * offset, 0);
        ChessPosition leftCapture = myPosition.offsetBy(offset, -1);
        ChessPosition rightCapture = myPosition.offsetBy(offset, 1);

        if (board.getPiece(singleSquare) == null) {
            if (promotionMove) {
                moves.addAll(ChessMove.promotionMoves(myPosition, singleSquare));
            } else {
                moves.add(new ChessMove(myPosition, singleSquare, null));
                if (initialMove && board.getPiece(doubleSquare) == null) {
                    moves.add(new ChessMove(myPosition, doubleSquare, null));
                }
            }
        }

        if (leftCapture.isValid()
                && board.getPiece(leftCapture) != null
                && board.getPiece(leftCapture).getTeamColor() != piece.getTeamColor()) {
            if (promotionMove) {
                moves.addAll(ChessMove.promotionMoves(myPosition, leftCapture));
            } else {
                moves.add(new ChessMove(myPosition, leftCapture, null));
            }
        }

        if (rightCapture.isValid()
                && board.getPiece(rightCapture) != null
                && board.getPiece(rightCapture).getTeamColor() != piece.getTeamColor()) {
            if (promotionMove) {
                moves.addAll(ChessMove.promotionMoves(myPosition, rightCapture));
            } else {
                moves.add(new ChessMove(myPosition, rightCapture, null));
            }
        }

        return moves;
    }
}
