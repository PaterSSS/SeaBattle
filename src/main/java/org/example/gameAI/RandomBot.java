package org.example.gameAI;

import org.example.field.fieldStructure.Structure;
import org.example.gameAI.BotStructure.AI;
import org.example.gameAI.BotStructure.Action;
import org.example.players.PossibleMoves;
import org.example.units.ProtoShip;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class RandomBot extends AI {
    Random random;

    public RandomBot(Structure field, List<ProtoShip> yourShips, List<ProtoShip> enemyShips) {
        super(field, yourShips, enemyShips);
        random = new Random();
    }

    @Override
    public Action makeMove(ProtoShip ship, int width, int heigth) {
        calculateAllPossibleMoves(ship);
        if (listOfMoves.isEmpty()) {
            return new Action(PossibleMoves.NO_POSSIBLE_MOVES, ship);
        }
        Action preferAction = null;
        for (Action action : listOfMoves) {
            if (action.getMove() == PossibleMoves.SHOOT) {
                preferAction = action;
            }
        }
        if (preferAction != null) {
            return preferAction;
        }
        Collections.shuffle(listOfMoves);
        return listOfMoves.get(0);
    }
}

