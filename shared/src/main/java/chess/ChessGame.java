package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor teamTurn;
    private ChessBoard board = new ChessBoard();
    private final HashSet<ChessPosition> whitePositions = new HashSet<>(16);
    private final HashSet<ChessPosition> blackPositions = new HashSet<>(16);

    public ChessGame() {
        teamTurn = TeamColor.WHITE;
        board.resetBoard();
        for (int i = 1; i <= 8; i++) {
            whitePositions.add(new ChessPosition(1, i));
            whitePositions.add(new ChessPosition(2, i));
            blackPositions.add(new ChessPosition(7, i));
            blackPositions.add(new ChessPosition(8, i));
        }
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
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * @return All positions occupied by the current team.
     */
    private HashSet<ChessPosition> teamPositions(TeamColor teamColor) {
        if (teamColor == TeamColor.WHITE) {
            return whitePositions;
        } else {
            return blackPositions;
        }
    }

    /**
     * @return All positions occupied by the opposing team.
     */
    private HashSet<ChessPosition> opponentPositions(TeamColor teamColor) {
        if (teamColor == TeamColor.WHITE) {
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
    private ChessPosition getKingPosition(TeamColor teamColor) {
        HashSet<ChessPosition> teamPositions = teamPositions(teamColor);

        for (ChessPosition position : teamPositions) {
            if (board.getPiece(position) != null
                    && board.getPiece(position).getPieceType() == ChessPiece.PieceType.KING) {
                return position;
            }
        }

        return null;
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) {
            return null;
        } else {
            return piece.pieceMoves(board, startPosition);
        }
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = getKingPosition(teamColor);
        return opponentPositions(teamColor).parallelStream()
                .map(this::validMoves)
                .anyMatch((moves) ->
                        moves != null && moves.stream().anyMatch(
                                (move) -> move.getEndPosition().equals(kingPosition)
                        )
                );
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;

        whitePositions.clear();
        blackPositions.clear();
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);
                if (piece != null) {
                    if (piece.getTeamColor() == TeamColor.WHITE) {
                        whitePositions.add(position);
                    } else {
                        blackPositions.add(position);
                    }
                }
            }
        }
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}
