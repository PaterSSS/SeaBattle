package org.example;

import org.example.consoleUI.Visualizer;
import org.example.field.GameField;
import org.example.gameAI.BotStructure.AI;
import org.example.gameAI.BotStructure.Action;
import org.example.gameAI.RandomBot;
import org.example.players.PossibleMoves;
import org.example.units.ProtoShip;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class GameLoop {
    private AI firstPlayer;
    private AI secondPlayer;
    private GameField field;

    public void initGame() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the path to the gameField:");
        String path = scanner.nextLine();
        field = new GameField(path);
        System.out.println("Enter 1 if you want random bot to play");
        int checkLevelOfBot = scanner.nextInt();
        Map<String, List<ProtoShip>> shipsOfPlayers = field.getShipsOfPlayers();

        if (checkLevelOfBot == 1) {
            firstPlayer = new RandomBot(field.getField(), shipsOfPlayers.get("first"), shipsOfPlayers.get("second"));
            secondPlayer = new RandomBot(field.getField(), shipsOfPlayers.get("second"), shipsOfPlayers.get("first"));
        } else {
//            firstPlayer = new SmartBot(field.getField(), shipsOfPlayers.get("first"), shipsOfPlayers.get("second"));
//            secondPlayer = new SmartBot(field.getField(), shipsOfPlayers.get("second"), shipsOfPlayers.get("first"));
        }
        playGame();
    }

    private void playGame() {
        //Если переменная true, то первый игрок делает ходы. Если false, то второй игрок ходит.
        boolean checkFirstPlayer = true;
        Visualizer visualizer = new Visualizer(field.getField());
        while (!firstPlayer.getYourAliveShips().isEmpty() && !secondPlayer.getYourAliveShips().isEmpty()) {
            List<String> listForActions = new ArrayList<>();
            AI currentPlayer = (checkFirstPlayer) ? firstPlayer : secondPlayer;
            StringBuilder builder = new StringBuilder((checkFirstPlayer)? "Player 1 ": "Player 2 ");
            int countOfAliveShips = currentPlayer.getYourAliveShips().size();
            AI playerToCheckShip = (checkFirstPlayer) ? secondPlayer : firstPlayer;
            for (int i = 0; i < countOfAliveShips; i++) {
                Action action = currentPlayer.makeMove(currentPlayer.getYourAliveShips().get(i), field.getFieldWidth(), field.getFieldHeight());
                ProtoShip currentShip = action.getShip();
                PossibleMoves move = action.getMove();
                StringBuilder sb = builder.append("did ").append(move).append(" ").append("for ship ").append(i + 1);
                switch (move) {
                    case MOVE_RIGHT -> field.moveShipRight(currentShip);
                    case MOVE_LEFT -> field.moveShipLeft(currentShip);
                    case MOVE_DOWN -> field.moveShipDown(currentShip);
                    case MOVE_UP -> field.moveShipUp(currentShip);
                    case CLOCKWISE_ROTATE -> field.clockwiseRotation(currentShip);
                    case COUNTERCLOCKWISE_ROTATE -> field.counterclockwiseRotation(currentShip);
                    case SHOOT -> field.shoot(currentShip, action.getPositionOfMove());
                    case NO_POSSIBLE_MOVES -> {}
                }
                listForActions.add(sb.toString());
                sb.replace(9, sb.length(), "");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            playerToCheckShip.checkHealthOfShip();
            checkFirstPlayer = !checkFirstPlayer;
            visualizer.printField(listForActions);
//            System.out.println("count of ships first " + firstPlayer.getYourAliveShips().size());
//            System.out.println("count of ships second " + secondPlayer.getYourAliveShips().size());
        }
        String template1 = "Congratulations";
        String template2 = "player won this match!!!";
        if (firstPlayer.getYourAliveShips().isEmpty()) {
            System.out.println(template1 + " second " + template2);
        } else {
            System.out.println(template1 + " first " + template2);
        }
    }
}
