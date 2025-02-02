package chess;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private final ChessPiece[][] grid;
    private final HashSet<ChessPosition> whitePositions;
    private final HashSet<ChessPosition> blackPositions;

    public ChessBoard() {
        grid = new ChessPiece[8][8];
        whitePositions = new HashSet<>(16);
        blackPositions = new HashSet<>(16);
    }

    public ChessBoard(ChessBoard other) {
        grid = other.grid.clone();
        for (int i = 0; i < 8; i++) {
            grid[i] = other.grid[i].clone();
        }
        whitePositions = new HashSet<>(other.whitePositions);
        blackPositions = new HashSet<>(other.blackPositions);
    }

    public ChessBoard after(ChessMove move) {
        ChessBoard newBoard = new ChessBoard(this);
        newBoard.makeMove(move);
        return newBoard;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(grid, that.grid);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(grid);
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        grid[position.getRow() - 1][position.getColumn() - 1] = piece;
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            whitePositions.add(position);
        } else {
            blackPositions.add(position);
        }
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return grid[position.getRow() - 1][position.getColumn() - 1];
    }

    /**
     * Apply a move to the chessboard.
     * WARNING: Performs no checks for move validity.
     *
     * @param move Move to apply
     */
    public void makeMove(ChessMove move) {
        ChessPosition start = move.getStartPosition();
        ChessPiece piece = getPiece(start);
        ChessPosition end = move.getEndPosition();
        ChessPiece opponentPiece = getPiece(end);

        grid[start.getRow() - 1][start.getColumn() - 1] = null;
        grid[end.getRow() - 1][end.getColumn() - 1] = piece;

        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            whitePositions.remove(start);
            whitePositions.add(end);
            if (opponentPiece != null) {
                blackPositions.remove(end);
            }
        } else {
            blackPositions.remove(start);
            blackPositions.add(end);
            if (opponentPiece != null) {
                whitePositions.remove(end);
            }
        }
    }

    /**
     * @return All positions occupied by the current team.
     */
    public HashSet<ChessPosition> teamPositions(ChessGame.TeamColor teamColor) {
        if (teamColor == ChessGame.TeamColor.WHITE) {
            return whitePositions;
        } else {
            return blackPositions;
        }
    }

    /**
     * @return All positions occupied by the opposing team.
     */
    public HashSet<ChessPosition> opponentPositions(ChessGame.TeamColor teamColor) {
        if (teamColor == ChessGame.TeamColor.WHITE) {
            return blackPositions;
        } else {
            return whitePositions;
        }
    }

    /**
     * Return the position of the given team's king
     *
     * @param teamColor Team color
     * @return Position of king, or null if none
     */
    public ChessPosition kingPosition(ChessGame.TeamColor teamColor) {
        return teamPositions(teamColor).stream().dropWhile(
                (position) -> getPiece(position).getPieceType() != ChessPiece.PieceType.KING
        ).findFirst().orElse(null);
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        ChessPiece[] whiteRow = {
                new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK),
                new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT),
                new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP),
                new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN),
                new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING),
                new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP),
                new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT),
                new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK),
        };

        ChessPiece[] blackRow = {
                new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK),
                new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT),
                new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP),
                new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN),
                new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING),
                new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP),
                new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT),
                new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK),
        };

        grid[0] = whiteRow;
        Arrays.fill(grid[1], new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
        for (int i = 2; i < 6; i++) {
            Arrays.fill(grid[i], null);
        }
        Arrays.fill(grid[6], new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
        grid[7] = blackRow;

        whitePositions.clear();
        blackPositions.clear();
        for (int i = 1; i <= 8; i++) {
            whitePositions.add(new ChessPosition(1, i));
            whitePositions.add(new ChessPosition(2, i));
            blackPositions.add(new ChessPosition(7, i));
            blackPositions.add(new ChessPosition(8, i));
        }
    }
}
