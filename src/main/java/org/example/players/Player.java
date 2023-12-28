package org.example.players;

import org.example.units.ProtoShip;

import java.util.List;

public interface Player {
    List<ProtoShip> getAliveShips();
    void shipIsEliminated(ProtoShip ship);
    PossibleMoves makeMove();
}
