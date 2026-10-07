package edu.sfsu.csc413.chess.model;

import java.util.ArrayList;
import java.util.List;

public class Board {

    private final Piece[][] squares;

    public Board() {
        squares = new Piece[Position.BOARD_SIZE][Position.BOARD_SIZE];
    }

    public Piece pieceAt(Position position) {
        return squares[position.file()][position.rank()];
    }

    public boolean isEmpty(Position position) {
        return pieceAt(position) == null;
    }

    public void place(Position position, Piece piece) {
        squares[position.file()][position.rank()] = piece;
    }

    public void apply(Move move) {
        place(move.from(), null);
        Piece pieceToPlace = move.isPromotion()
                ? createPromoted(move.promotesTo(), move.moved().color())
                : move.moved();
        place(move.to(), pieceToPlace);
    }

    public void undo(Move move) {
        place(move.from(), move.moved());
        place(move.to(), move.captured());
    }

    /**
     * Builds a promoted piece without making the model package depend on the
     * factory package. The cost is duplicating the four promotion constructors
     * that PieceFactory already knows about.
     */
    private Piece createPromoted(PieceType type, Color color) {
        return switch (type) {
            case QUEEN -> new Queen(color);
            case ROOK -> new Rook(color);
            case BISHOP -> new Bishop(color);
            case KNIGHT -> new Knight(color);
            case PAWN, KING -> throw new IllegalArgumentException(
                    "Invalid promotion type: " + type);
        };
    }

    public List<Position> positionsOf(Color color) {
        List<Position> positions = new ArrayList<>();
        for (int file = 0; file < Position.BOARD_SIZE; file++) {
            for (int rank = 0; rank < Position.BOARD_SIZE; rank++) {
                Piece piece = squares[file][rank];
                if (piece != null && piece.color() == color) {
                    positions.add(new Position(file, rank));
                }
            }
        }
        return positions;
    }

    @Override
    public String toString() {
        StringBuilder fen = new StringBuilder();
        for (int rank = Position.BOARD_SIZE - 1; rank >= 0; rank--) {
            if (rank < Position.BOARD_SIZE - 1) {
                fen.append('/');
            }

            int emptySquares = 0;
            for (int file = 0; file < Position.BOARD_SIZE; file++) {
                Piece piece = squares[file][rank];
                if (piece == null) {
                    emptySquares++;
                } else {
                    if (emptySquares > 0) {
                        fen.append(emptySquares);
                        emptySquares = 0;
                    }
                    fen.append(piece.symbol());
                }
            }
            if (emptySquares > 0) {
                fen.append(emptySquares);
            }
        }
        return fen.toString();
    }
}
