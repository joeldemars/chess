package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor teamTurn;
    private ChessBoard board;
    private ArrayList<ChessMove> history;
    private boolean isOver;

    public ChessGame() {
        teamTurn = TeamColor.WHITE;
        board = new ChessBoard();
        board.resetBoard();
        history = new ArrayList<>();
        isOver = false;
    }

    public ChessGame(ChessGame other) {
        teamTurn = other.teamTurn;
        board = new ChessBoard(other.board);
        history = new ArrayList<>(other.history);
        isOver = other.isOver;
    }

    /**
     * Construct a new ChessGame object corresponding to the current game with the given move applied
     *
     * @param move Move to apply
     * @return new ChessGame object after applying move
     */
    public ChessGame after(ChessMove move) {
        ChessGame newGame = new ChessGame(this);
        newGame.board.makeMove(move);
        newGame.history.add(move);
        return newGame;
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
     * Gets potential moves for a piece at the given location (including moves that place or leave the king in check)
     *
     * @param startPosition the piece to get potential moves for
     * @return Set of potential moves for requested piece
     */
    public Collection<ChessMove> potentialMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) {
            return new ArrayList<>(0);
        }
        return piece.pieceMoves(board, startPosition);
    }

    /**
     * Get a valid moves for a piece at the given location (not including moves that place or leave the king in check)
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) {
            return null;
        }
        TeamColor color = piece.getTeamColor();
        ArrayList<ChessMove> validMoves = potentialMoves(startPosition).stream().filter(
                (move) -> !this.after(move).isInCheck(color)
        ).collect(Collectors.toCollection(ArrayList::new));
        validMoves.addAll(MoveCalculator.specialMoves(this, piece, startPosition));
        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        if (piece == null
                || piece.getTeamColor() != teamTurn
                || !validMoves(move.getStartPosition()).contains(move)
        ) {
            throw new InvalidMoveException();
        }
        board.makeMove(move);
        teamTurn = teamTurn == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE;
        history.add(move);
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = board.kingPosition(teamColor);
        return board.opponentPositions(teamColor).stream()
                .map(this::potentialMoves)
                .anyMatch((moves) ->
                        moves.stream().anyMatch(
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
        return isInCheck(teamColor)
                && board.teamPositions(teamColor).stream().map(this::validMoves).allMatch(Collection::isEmpty);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return !isInCheck(teamColor)
                && board.teamPositions(teamColor).stream().map(this::validMoves).allMatch(Collection::isEmpty);
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    public ArrayList<ChessMove> getHistory() {
        return history;
    }

    public boolean getIsOver() {
        return isOver;
    }

    public void setIsOver(boolean isOver) {
        this.isOver = isOver;
    }
}
