package chess;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {

    private final ChessPosition startPosition;
    private final ChessPosition endPosition;
    private final ChessPiece.PieceType promotionPiece;

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.promotionPiece = promotionPiece;
    }

    /**
     * Return all possible promotion variants (promotion to queen, bishop, rook, or knight) with a given starting and
     * ending position.
     */
    public static ArrayList<ChessMove> promotionMoves(ChessPosition startPosition, ChessPosition endPosition) {
        ArrayList<ChessMove> moves = new ArrayList<>(4);
        moves.add(new ChessMove(startPosition, endPosition, ChessPiece.PieceType.QUEEN));
        moves.add(new ChessMove(startPosition, endPosition, ChessPiece.PieceType.BISHOP));
        moves.add(new ChessMove(startPosition, endPosition, ChessPiece.PieceType.ROOK));
        moves.add(new ChessMove(startPosition, endPosition, ChessPiece.PieceType.KNIGHT));
        return moves;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        ChessMove o = (ChessMove) other;
        return startPosition.equals(o.startPosition) && endPosition.equals(o.endPosition)
                && promotionPiece == o.promotionPiece;
    }

    @Override
    public int hashCode() {
        return Objects.hash(startPosition, endPosition, promotionPiece);
    }

    @Override
    public String toString() {
        String string = startPosition.toString() + " to " + endPosition.toString();
        if (promotionPiece != null) {
            string += " [" + promotionPiece + "]";
        }
        return string;
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
        return startPosition;
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
        return endPosition;
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {
        return promotionPiece;
    }
}
