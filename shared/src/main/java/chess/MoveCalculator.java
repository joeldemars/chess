package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.NoSuchElementException;

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
     * Given an array of potential end positions, return a collection of moves corresponding to each one that is valid.
     * A potential end position is valid if it exists on the board and is not occupied by a member of the same team.
     *
     * @return Collection of moves corresponding to valid end positions
     */
    private static Collection<ChessMove> validatePotentialEndPositions(
            ChessPosition[] potentialPositions, ChessBoard board, ChessGame.TeamColor teamColor, ChessPosition start) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        for (ChessPosition position : potentialPositions) {
            if (position.isValid()) {
                ChessPiece piece = board.getPiece(position);
                if (piece == null || piece.getTeamColor() != teamColor) {
                    moves.add(new ChessMove(start, position, null));
                }
            }
        }
        return moves;
    }

    /**
     * Calculates all possible special moves (i.e. castling or en passant) for a given piece in a given game
     *
     * @return Collection of valid moves
     */
    public static Collection<ChessMove> specialMoves(ChessGame game, ChessPiece piece, ChessPosition position) {
        if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            return enPassantMoves(game, piece, position);
        } else if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            return castlingMoves(game, piece, position);
        } else {
            return new ArrayList<>();
        }
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

        return validatePotentialEndPositions(potentialPositions, board, piece.getTeamColor(), myPosition);
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

        return validatePotentialEndPositions(potentialPositions, board, piece.getTeamColor(), myPosition);
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

    /**
     * Return all possible en passant moves for a pawn in a given game at a given position
     */
    private static ArrayList<ChessMove> enPassantMoves(ChessGame game, ChessPiece piece, ChessPosition position) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        ChessMove lastMove;

        try {
            lastMove = game.getHistory().getLast();
        } catch (NoSuchElementException e) {
            return moves;
        }

        ChessGame.TeamColor teamColor = piece.getTeamColor();
        int direction = teamColor == ChessGame.TeamColor.WHITE ? 1 : -1;
        ChessPosition opponentPosition = lastMove.getEndPosition();
        ChessMove pawnInitialMove = new ChessMove(opponentPosition.offsetBy(2 * direction, 0),
                opponentPosition, null);
        ChessPiece opponentPiece = game.getBoard().getPiece(opponentPosition);
        ChessMove enPassantMove = new ChessMove(position, opponentPosition.offsetBy(direction, 0), null);

        if (opponentPiece.getPieceType() == ChessPiece.PieceType.PAWN
                && Math.abs(position.getColumn() - opponentPosition.getColumn()) == 1
                && lastMove.equals(pawnInitialMove)
                && !game.after(enPassantMove).isInCheck(teamColor)
        ) {
            moves.add(enPassantMove);
        }
        return moves;
    }

    /**
     * Returns whether the piece at a given position has moved in the history of a given game
     */
    private static boolean hasMoved(ChessGame game, ChessPosition position) {
        return game.getHistory().stream().anyMatch((move) -> move.getEndPosition().equals(position));
    }

    /**
     * Return all possible castling moves for a king in a given game at a given position
     */
    private static ArrayList<ChessMove> castlingMoves(ChessGame game, ChessPiece king, ChessPosition kingPosition) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        ChessGame.TeamColor teamColor = king.getTeamColor();
        int row = teamColor == ChessGame.TeamColor.WHITE ? 1 : 8;
        if (hasMoved(game, kingPosition) || game.isInCheck(teamColor)) {
            return moves;
        }

        ChessPosition oneLeftOfKing = kingPosition.offsetBy(0, -1);
        ChessPosition twoLeftOfKing = kingPosition.offsetBy(0, -2);
        ChessPosition leftCorner = new ChessPosition(row, 1);
        ChessPiece leftRook = game.getBoard().getPiece(leftCorner);
        boolean canCastleLeft = leftRook != null
                && leftRook.getPieceType() == ChessPiece.PieceType.ROOK
                && !hasMoved(game, leftCorner)
                && game.getBoard().getPiece(oneLeftOfKing) == null
                && !game.after(new ChessMove(kingPosition, oneLeftOfKing, null)).isInCheck(teamColor)
                && game.getBoard().getPiece(twoLeftOfKing) == null
                && !game.after(new ChessMove(kingPosition, twoLeftOfKing, null)).isInCheck(teamColor);
        if (canCastleLeft) {
            moves.add(new ChessMove(kingPosition, twoLeftOfKing, null));
        }

        ChessPosition oneRightOfKing = kingPosition.offsetBy(0, 1);
        ChessPosition twoRightOfKing = kingPosition.offsetBy(0, 2);
        ChessPosition rightCorner = new ChessPosition(row, 8);
        ChessPiece rightRook = game.getBoard().getPiece(rightCorner);
        boolean canCastleRight = rightRook != null
                && rightRook.getPieceType() == ChessPiece.PieceType.ROOK
                && !hasMoved(game, rightCorner)
                && game.getBoard().getPiece(oneRightOfKing) == null
                && !game.after(new ChessMove(kingPosition, oneRightOfKing, null)).isInCheck(teamColor)
                && game.getBoard().getPiece(twoRightOfKing) == null
                && !game.after(new ChessMove(kingPosition, twoRightOfKing, null)).isInCheck(teamColor);
        if (canCastleRight) {
            moves.add(new ChessMove(kingPosition, twoRightOfKing, null));
        }

        return moves;
    }
}
