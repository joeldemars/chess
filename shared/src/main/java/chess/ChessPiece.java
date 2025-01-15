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

    private final ChessGame.TeamColor pieceColor;
    private final ChessPiece.PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
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

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return switch (type) {
            case KING -> kingMoves(board, myPosition);
            case QUEEN -> queenMoves(board, myPosition);
            case BISHOP -> bishopMoves(board, myPosition);
            case KNIGHT -> knightMoves(board, myPosition);
            case ROOK -> rookMoves(board, myPosition);
            case PAWN -> pawnMoves(board, myPosition);
        };
    }

    /**
     * Get all possible moves along a straight line (i.e. for queens, bishops, and rooks), given the row and column
     * offsets that define the line (e.g. rowOffset = 1 and columnOffset = 1 to get possible moves NE of a piece)
     */
    private ArrayList<ChessMove> movesAlong(ChessBoard board, ChessPosition myPosition, int rowOffset, int columnOffset) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        ChessPosition newPosition = new ChessPosition(myPosition.getRow() + rowOffset, myPosition.getColumn() + columnOffset);

        while (newPosition.isValid()) {
            ChessPiece piece = board.getPiece(newPosition);
            if (piece == null) {
                moves.add(new ChessMove(myPosition, newPosition, null));
                newPosition = new ChessPosition(newPosition.getRow() + rowOffset, newPosition.getColumn() + columnOffset);
            } else {
                if (piece.pieceColor != this.pieceColor) {
                    moves.add(new ChessMove(myPosition, newPosition, null));
                }
                break;
            }
        }

        return moves;
    }

    private Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        ChessPosition[] potentialPositions = {
                new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 1),
                new ChessPosition(myPosition.getRow(), myPosition.getColumn() - 1),
                new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 1),
                new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn()),
                new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn()),
                new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 1),
                new ChessPosition(myPosition.getRow(), myPosition.getColumn() + 1),
                new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1),
        };

        for (ChessPosition position : potentialPositions) {
            if (position.isValid() && (board.getPiece(position) == null || board.getPiece(position).pieceColor != pieceColor)) {
                moves.add(new ChessMove(myPosition, position, null));
            }
        }

        return moves;
    }

    private Collection<ChessMove> queenMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = bishopMoves(board, myPosition);
        moves.addAll(rookMoves(board, myPosition));
        return moves;
    }

    private Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> moves = movesAlong(board, myPosition, 1, 1);
        moves.addAll(movesAlong(board, myPosition, 1, -1));
        moves.addAll(movesAlong(board, myPosition, -1, 1));
        moves.addAll(movesAlong(board, myPosition, -1, -1));
        return moves;
    }

    private Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        ChessPosition[] potentialPositions = {
                new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn() - 1),
                new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn() + 1),
                new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 2),
                new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 2),
                new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 2),
                new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 2),
                new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn() + 1),
                new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn() - 1),
        };

        for (ChessPosition position : potentialPositions) {
            if (position.isValid() && (board.getPiece(position) == null || board.getPiece(position).pieceColor != pieceColor)) {
                moves.add(new ChessMove(myPosition, position, null));
            }
        }

        return moves;
    }

    private Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> moves = movesAlong(board, myPosition, 0, 1);
        moves.addAll(movesAlong(board, myPosition, 0, -1));
        moves.addAll(movesAlong(board, myPosition, 1, 0));
        moves.addAll(movesAlong(board, myPosition, -1, 0));
        return moves;
    }

    private Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        int direction = pieceColor == ChessGame.TeamColor.WHITE ? 1 : -1;
        boolean initialMove = myPosition.getRow() == (pieceColor == ChessGame.TeamColor.WHITE ? 2 : 7);
        boolean promotionMove = myPosition.getRow() == (pieceColor == ChessGame.TeamColor.WHITE ? 7 : 2);
        ChessPosition singleSquare = new ChessPosition(myPosition.getRow() + direction, myPosition.getColumn());
        ChessPosition doubleSquare = new ChessPosition(myPosition.getRow() + 2 * direction, myPosition.getColumn());
        ChessPosition leftCapture = new ChessPosition(myPosition.getRow() + direction, myPosition.getColumn() - 1);
        ChessPosition rightCapture = new ChessPosition(myPosition.getRow() + direction, myPosition.getColumn() + 1);

        if (board.getPiece(singleSquare) == null) {
            if (promotionMove) {
                moves.add(new ChessMove(myPosition, singleSquare, PieceType.QUEEN));
                moves.add(new ChessMove(myPosition, singleSquare, PieceType.BISHOP));
                moves.add(new ChessMove(myPosition, singleSquare, PieceType.ROOK));
                moves.add(new ChessMove(myPosition, singleSquare, PieceType.KNIGHT));
            } else {
                moves.add(new ChessMove(myPosition, singleSquare, null));
                if (initialMove && board.getPiece(doubleSquare) == null) {
                    moves.add(new ChessMove(myPosition, doubleSquare, null));
                }
            }
        }
        if (leftCapture.isValid() && board.getPiece(leftCapture) != null && board.getPiece(leftCapture).pieceColor != pieceColor) {
            if (promotionMove) {
                moves.add(new ChessMove(myPosition, leftCapture, PieceType.QUEEN));
                moves.add(new ChessMove(myPosition, leftCapture, PieceType.BISHOP));
                moves.add(new ChessMove(myPosition, leftCapture, PieceType.ROOK));
                moves.add(new ChessMove(myPosition, leftCapture, PieceType.KNIGHT));
            } else {
                moves.add(new ChessMove(myPosition, leftCapture, null));
            }
        }
        if (rightCapture.isValid() && board.getPiece(rightCapture) != null && board.getPiece(rightCapture).pieceColor != pieceColor) {
            if (promotionMove) {
                moves.add(new ChessMove(myPosition, rightCapture, PieceType.QUEEN));
                moves.add(new ChessMove(myPosition, rightCapture, PieceType.BISHOP));
                moves.add(new ChessMove(myPosition, rightCapture, PieceType.ROOK));
                moves.add(new ChessMove(myPosition, rightCapture, PieceType.KNIGHT));
            } else {
                moves.add(new ChessMove(myPosition, rightCapture, null));
            }
        }

        return moves;
    }
}
