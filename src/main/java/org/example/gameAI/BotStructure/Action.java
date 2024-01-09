package org.example.gameAI.BotStructure;

import org.example.field.fieldStructure.Position;
import org.example.players.PossibleMoves;
import org.example.units.ProtoShip;

public class Action {
    private PossibleMoves move;
    private Position positionOfMove;
    private ProtoShip ship;
    public Action(PossibleMoves move, Position position, ProtoShip ship) {
        this.move = move;
        this.positionOfMove = position;
        this.ship = ship;
    }
    public Action(PossibleMoves move, ProtoShip ship) {
        this.move = move;
        this.ship = ship;
    }

    public PossibleMoves getMove() {
        return move;
    }

    public Position getPositionOfMove() {
        return positionOfMove;
    }
    public ProtoShip getShip() {
        return ship;
    }
}
