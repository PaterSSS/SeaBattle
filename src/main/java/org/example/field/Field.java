package org.example.field;

import org.example.consoleUI.Visualizer;
import org.example.field.fieldStructure.*;
import org.example.units.OrientationOfShip;
import org.example.units.ProtoShip;
import org.example.units.Ship;

import java.util.function.UnaryOperator;

public class Field {
    private Structure field;
    private final int fieldWidth;
    private final int fieldHeight;

    public Field(int width, int height) {
        this.fieldHeight = height;
        this.fieldWidth = width;
        FieldBuilder builder = new FieldBuilder(20, 10);
        builder.createEmptyField();
        field = builder.getField();
    }

    public boolean addShip(ProtoShip ship) {

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
            if (shipLine.getCell().getTypeOfCell() == TypeOfCell.BARRIER ||
            shipLine.getCell().getTypeOfCell() == TypeOfCell.PART_OF_SHIP) {
                return false;
            }
            nearLine = wayOfChecking.apply(nearLine);
            shipLine = wayOfChecking.apply(shipLine);
            farLine = wayOfChecking.apply(farLine);
        }
        return true;
    }

    public static void main(String[] args) {
    }
}
