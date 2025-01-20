package chess;

import java.util.Objects;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {

    private final int row;
    private final int col;

    public ChessPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * Return a new ChessPosition offset by given number of rows and columns.
     *
     * @param rows    Number of rows to offset
     * @param columns Number of columns to offset
     * @return New ChessPosition with given offset
     */
    public ChessPosition offsetBy(int rows, int columns) {
        return new ChessPosition(row + rows, col + columns);
    }

    @Override
    public boolean equals(Object other) {
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        ChessPosition o = (ChessPosition) other;
        return row == o.row && col == o.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    @Override
    public String toString() {
        return "(" + row + ", " + col + ")";
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return this.row;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {
        return this.col;
    }

    /**
     * @return whether the position is valid (i.e. on the chessboard)
     */
    public boolean isValid() {
        return 1 <= this.col && this.col <= 8 && 1 <= this.row && this.row <= 8;
    }
}
