package org.example.field;

import org.example.consoleUI.Visualizer;
import org.example.field.fieldStructure.*;
import org.example.units.OrientationOfShip;
import org.example.units.ProtoShip;
import org.example.units.Ship;

import java.util.Random;
import java.util.function.UnaryOperator;

public class GameField {
    private Structure field;
    private final int fieldWidth;
    private final int fieldHeight;
    private final Random random = new Random();

    public GameField(int width, int height) {
        this.fieldHeight = height;
        this.fieldWidth = width;
        FieldBuilder builder = new FieldBuilder(20, 10);
        builder.createEmptyField();
        field = builder.getField();
    }
    public boolean addShip(ProtoShip ship) {
        Position headOfShip = ship.getHeadOfShip();
        int length = ship.getSizeOfShip();
        OrientationOfShip orientation = ship.getOrientation();
        UnaryOperator<Node> way = (orientation == OrientationOfShip.VERTICAL) ? Node::getDown : Node::getLeft;

        if(!isSpaceAvailableForShip(headOfShip, orientation, length, way)) {
            return false;
        }
        Node head = field.getCellByPosition(headOfShip);
        for (int i = 0; i < length; i++) {
            head.getCell().shipSailTo(ship);
            head = way.apply(head);
        }
        return true;
    }
    private boolean isSpaceAvailableForShip(Position position, OrientationOfShip orientation,
                                            int lengthOfShip, UnaryOperator<Node> wayOfChecking) {
        int x = position.getX();
        int y = position.getY();

        if (y <= 0 || y >= fieldHeight - 1 || x <= 0 || x >= fieldWidth - 1) {
            return false;
        }
        if (orientation == OrientationOfShip.VERTICAL && ((y + lengthOfShip) >= fieldHeight)) {
            return false;
        }
        if (orientation == OrientationOfShip.HORIZONTAL && ((x - lengthOfShip) <= 0)) {
            return false;
        }

        Node headOfShip = field.getCellByPosition(position);
        Node nearLine, shipLine, farLine;

        if (orientation == OrientationOfShip.HORIZONTAL || orientation == OrientationOfShip.SINGLE_DECK) {
            shipLine = headOfShip.getRight();
            nearLine = shipLine.getUp();
            farLine = shipLine.getDown();
        } else {
            shipLine = headOfShip.getUp();
            nearLine = shipLine.getLeft();
            farLine = shipLine.getRight();
        }

        for (int i = 0; i <= lengthOfShip; i++) {
            if (nearLine.getCell().getTypeOfCell() == TypeOfCell.PART_OF_SHIP ||
                    farLine.getCell().getTypeOfCell() == TypeOfCell.PART_OF_SHIP) {
                return false;
            }
            if ((shipLine.getCell().getTypeOfCell() == TypeOfCell.BARRIER && i != 0) ||
                    shipLine.getCell().getTypeOfCell() == TypeOfCell.PART_OF_SHIP) {
                return false;
            }
            nearLine = wayOfChecking.apply(nearLine);
            shipLine = wayOfChecking.apply(shipLine);
            farLine = wayOfChecking.apply(farLine);
        }
        return true;
    }
    public boolean moveShipRight(ProtoShip ship) {
        OrientationOfShip orientation = ship.getOrientation();
        if (orientation == OrientationOfShip.VERTICAL) {
            return false;
        }
        Position shipPos = ship.getHeadOfShip();
        Node head = field.getCellByPosition(shipPos);
        if (!isHeadOfShipInProperDirection(ship, Node::getRight, head)) {
            return false;
        }
        if (checkCollision(ship, Node::getRight)) {
            return true;
        }
        head.getRight().getCell().shipSailTo(ship);
        for (int i = 0; i < ship.getSizeOfShip() - 1; i++) {
            head = head.getLeft();
        }

        head.getCell().shipSailedAway();
        shipPos.setX(shipPos.getX() + 1);
        return true;
    }
    private boolean isHeadOfShipInProperDirection(ProtoShip ship, UnaryOperator<Node> iterateWay, Node head) {
        Node cellForTest = iterateWay.apply(head);
        return !(cellForTest.getCell().getShip() == ship);
    }
    private boolean checkCollision(ProtoShip ship, UnaryOperator<Node> wayOfIterating) {
        Node head = field.getCellByPosition(ship.getHeadOfShip());
        head = wayOfIterating.apply(head);
        TypeOfCell type = head.getCell().getTypeOfCell();
        if (type == TypeOfCell.BARRIER) {
            getDamageByCollision(ship);
            return true;
        } else if (type == TypeOfCell.PART_OF_SHIP) {
            getDamageByCollision(ship);
            getDamageByCollision(head.getCell().getShip());
            return true;
        }
        return false;
    }
    private void getDamageByCollision(ProtoShip ship) {
        int maximumHealthOfShip = ship.getSizeOfShip();
        int damage = random.nextInt(maximumHealthOfShip) + 1;
        ship.getDamage(damage);
    }
    public static void main(String[] args) {
        GameField field1 = new GameField(20,10);
        Visualizer visualizer = new Visualizer(field1.field);
        visualizer.printField();
        ProtoShip ship = new Ship(5,new Position(10,1), OrientationOfShip.HORIZONTAL);
        field1.addShip(ship);
        visualizer.printField();
        field1.moveShipRight(ship);
        visualizer.printField();
        field1.moveShipRight(ship);
        visualizer.printField();
        field1.moveShipRight(ship);
        visualizer.printField();
    }
}
