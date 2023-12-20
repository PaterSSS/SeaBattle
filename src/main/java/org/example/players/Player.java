package org.example.players;

import org.example.units.ProtoShip;

import java.util.List;

public interface Player {
    enum PossibleMoves {
        MOVE_RIGHT,
        MOVE_LEFT,
        MOVE_UP,
        MOVE_DOWN,
        SHOOT,
        CLOCKWISE_ROTATE,
        COUNTERCLOCKWISE_ROTATE
    }
    List<ProtoShip> getAliveShips();
    void shipIsEliminated(ProtoShip ship);
    PossibleMoves makeMove();
}
