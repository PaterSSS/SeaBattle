package org.example.gameAI;

import org.example.field.fieldStructure.Position;
import org.example.field.fieldStructure.Structure;
import org.example.gameAI.BotStructure.AI;
import org.example.gameAI.BotStructure.Action;
import org.example.players.PossibleMoves;
import org.example.units.OrientationOfShip;
import org.example.units.ProtoShip;

import java.util.List;

//public class SmartBot extends AI {
//    public SmartBot(Structure field, List<ProtoShip> yourShips, List<ProtoShip> enemyShips) {
//        super(field, yourShips, enemyShips);
//    }
//
//    @Override
//    public Action makeMove(ProtoShip ship, int width, int height) {
//        calculateAllPossibleMoves(ship);
//        Action preferAction = null;
//        for (Action action : listOfMoves) {
//            if (action.getMove() == PossibleMoves.SHOOT) {
//                preferAction = action;
//            }
//        }
//        if (preferAction != null) {
//            return preferAction;
//        } else {
//            Position posOfShip = ship.getHeadOfShip();
//            if (posOfShip.getX() < width/2 &&  posOfShip.getY() < height/2) {
//                if (ship.getOrientation() == OrientationOfShip.HORIZONTAL) {
//                    preferAction = ()
//                }
//            }
//        }
//    }
//}
