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

        if (!isSpaceAvailableForShip(headOfShip, orientation, length, way)) {
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

    public boolean moveShip(ProtoShip ship, OrientationOfShip incorrectOrientation,
                            UnaryOperator<Node> wayOfIterating,
                            UnaryOperator<Node> iterateInOppositeDirection,
                            int deltaX, int deltaY) {
        OrientationOfShip orientation = ship.getOrientation();
        if (orientation == incorrectOrientation) {
            return false;
        }
        Position shipPos = ship.getHeadOfShip();
        Node head = field.getCellByPosition(shipPos);
        if (!isHeadOfShipInProperDirection(ship, wayOfIterating, head)) {
            return false;
        }
        if (checkCollision(ship, wayOfIterating)) {
            return true;
        }
        wayOfIterating.apply(head).getCell().shipSailTo(ship);
        for (int i = 0; i < ship.getSizeOfShip() - 1; i++) {
            head = iterateInOppositeDirection.apply(head);
        }

        head.getCell().shipSailedAway();
        shipPos.setX(shipPos.getX() + deltaX);
        shipPos.setY(shipPos.getY() + deltaY);
        return true;
    }

    public boolean moveShipRight(ProtoShip ship) {
        return moveShip(ship, OrientationOfShip.VERTICAL, Node::getRight, Node::getLeft, 1, 0);
    }

    public boolean moveShipLeft(ProtoShip ship) {
        return moveShip(ship, OrientationOfShip.VERTICAL, Node::getLeft, Node::getRight, -1, 0);
    }

    public boolean moveShipUp(ProtoShip ship) {
        return moveShip(ship, OrientationOfShip.HORIZONTAL, Node::getUp, Node::getDown, 0, -1);
    }

    public boolean moveShipDown(ProtoShip ship) {
        return moveShip(ship, OrientationOfShip.HORIZONTAL, Node::getDown, Node::getUp, 0, 1);
    }

    /**
     * @param iterateWay - метод итерирования по клеткам
     * @return false, если итерируясь заданным в аргументах способом мы попали на клетку, на которой тот же корабль
     * true, если на этой клетке нет заданного корабля.
     */
    private boolean isHeadOfShipInProperDirection(ProtoShip ship, UnaryOperator<Node> iterateWay, Node head) {
        Node cellForTest = iterateWay.apply(head);
        return !(cellForTest.getCell().getShip() == ship);
    }

    private boolean checkCollision(ProtoShip ship, UnaryOperator<Node> wayOfIterating) {
        Node head = field.getCellByPosition(ship.getHeadOfShip());
        head = wayOfIterating.apply(head);
        TypeOfCell type = head.getCell().getTypeOfCell();
        if (type == TypeOfCell.BARRIER) {
            damageByCollision(ship);
            return true;
        } else if (type == TypeOfCell.PART_OF_SHIP) {
            damageByCollision(ship);
            damageByCollision(head.getCell().getShip());
            return true;
        }
        return false;
    }

    private void damageByCollision(ProtoShip ship) {
        int maximumHealthOfShip = ship.getSizeOfShip();
        int damage = random.nextInt(maximumHealthOfShip) + 1;
        ship.getDamage(damage);
    }
    public boolean clockwiseRotation(ProtoShip ship) {
        return shipRotation(ship, true);
    }
    public boolean counterclockwiseRotation(ProtoShip ship) {
        return shipRotation(ship, false);
    }
    private boolean shipRotation(ProtoShip ship, boolean clockwiseRotation) {
        if (ship.getOrientation() == OrientationOfShip.SINGLE_DECK) {
            return false;
        }

        int centerOfShip = ((ship.getSizeOfShip() + 1) / 2);
        int numberOfIteratingBack = ship.getSizeOfShip() - centerOfShip;
        Node headOfShip = field.getCellByPosition(ship.getHeadOfShip());
        OrientationOfShip orientation = ship.getOrientation();
        UnaryOperator<Node> iteratingToShipCenter; // в какую сторону нужно итерировать, чтобы к центру корабля попасть
        UnaryOperator<Node> iteratingCellByRotation; // способ перебирать клетки которые проверяются для того чтобы голову корабля поместить
        UnaryOperator<Node> iteratingCellAgainstRotation; // проверяем клетки в которые хвост встанет
        UnaryOperator<Node> iteratingFromTheShipCenter; // нужно, чтобы вернуться к голове корабля.
        OrientationOfShip newOrientationOfShip;
        //это полнейшая шляпа, но я пока не знаю, как по-нормальному разобраться с этими ифами.
        if (orientation == OrientationOfShip.HORIZONTAL) {
            if (isHeadOfShipInProperDirection(ship, Node::getRight, headOfShip)) {
                iteratingToShipCenter = Node::getLeft;
                iteratingFromTheShipCenter = Node::getRight;
                if (clockwiseRotation) {
                    iteratingCellByRotation = Node::getDown;
                    iteratingCellAgainstRotation = Node::getUp;
                } else {
                    iteratingCellByRotation = Node::getUp;
                    iteratingCellAgainstRotation = Node::getDown;
                }
            } else {
                iteratingToShipCenter = Node::getRight;
                iteratingFromTheShipCenter = Node::getLeft;
                if (clockwiseRotation) {
                    iteratingCellByRotation = Node::getUp;
                    iteratingCellAgainstRotation = Node::getDown;
                } else {
                    iteratingCellByRotation = Node::getDown;
                    iteratingCellAgainstRotation = Node::getUp;
                }
            }
            newOrientationOfShip = OrientationOfShip.VERTICAL;
        } else {
            if (isHeadOfShipInProperDirection(ship, Node::getUp, headOfShip)) {
                iteratingToShipCenter = Node::getDown;
                iteratingFromTheShipCenter = Node::getUp;
                if (clockwiseRotation) {
                    iteratingCellByRotation = Node::getRight;
                    iteratingCellAgainstRotation = Node::getLeft;
                } else {
                    iteratingCellByRotation = Node::getLeft;
                    iteratingCellAgainstRotation = Node::getRight;
                }
            } else {
                iteratingToShipCenter = Node::getUp;
                iteratingFromTheShipCenter = Node::getDown;
                if (clockwiseRotation) {
                    iteratingCellByRotation = Node::getLeft;
                    iteratingCellAgainstRotation = Node::getRight;
                } else {
                    iteratingCellByRotation = Node::getRight;
                    iteratingCellAgainstRotation = Node::getLeft;
                }
            }
            newOrientationOfShip = OrientationOfShip.HORIZONTAL;
        }

        Node centerOfShipNode = headOfShip; // нода указывающая на центр корабля
        for (int i = 0; i < numberOfIteratingBack; i++) {
            centerOfShipNode = iteratingToShipCenter.apply(centerOfShipNode);
        }
        int countOfCellToCheckDown = numberOfIteratingBack; // если поворачиваем по часовой, то количество клеток занятых после поворота равно кол-во клеток до центра
        int countOfCellToCheckUp = (ship.getSizeOfShip() % 2 == 0) ? countOfCellToCheckDown - 1 : countOfCellToCheckDown; // кол-во клеток для провекри места, где будет хвост корабля
        // если по часовой то так как написано рассчитывается, если против то значения переменных меняются местами.
        int count = (ship.getSizeOfShip() < 3) ? 0 : ((ship.getSizeOfShip() + 2 - 1) / 2);

        Node movingToTail = centerOfShipNode;
        Node movingTOHead = centerOfShipNode;

        Node newHead = checkSpaceForShipRotate(movingTOHead, numberOfIteratingBack + 1,
                countOfCellToCheckDown, iteratingFromTheShipCenter, iteratingCellByRotation); // сделать методы, которые будут проверять, можно ли расположит так корабль. Будут возвращать ноду нового хвоста или головы
        Node newTail = checkSpaceForShipRotate(movingToTail, count, countOfCellToCheckUp, iteratingToShipCenter, iteratingCellAgainstRotation); // если вернули null, то так корабль поместить нельзя проверки сделаем и всё будет в ажуре.

        if (newHead == null || newTail == null) {
            return false;
        }
        ship.setPositionOfHead(newHead.getPosition());
        ship.shipRotation(newOrientationOfShip);

        setNewPositionOfShip(movingTOHead, newHead, ship, numberOfIteratingBack, iteratingToShipCenter, iteratingCellAgainstRotation);
        setNewPositionOfShip(movingToTail, newTail, ship, count - 1, iteratingFromTheShipCenter, iteratingCellByRotation);
        return true;
    }

    private Node checkSpaceForShipRotate(Node shipCenterNode, int countOfShipCells,
                                         int countOfCellsToCheck, UnaryOperator<Node> iterateOnShip,
                                         UnaryOperator<Node> iterateToCheckCells) {
        Node newExtremePointOfShip = null;
        for (int i = 0; i < countOfShipCells; i++) {
            Node tmp = shipCenterNode;
            int countToCheck = (i == 1) ? 0 : i;
            for (int j = countToCheck; j < countOfCellsToCheck; j++) {
                tmp = iterateToCheckCells.apply(tmp);
                TypeOfCell type = tmp.getCell().getTypeOfCell();
                if (type == TypeOfCell.BARRIER || type == TypeOfCell.PART_OF_SHIP) {
                    return null;
                }
            }
            if (i == 0) {
                newExtremePointOfShip = tmp;
            }
            shipCenterNode = iterateOnShip.apply(shipCenterNode);
        }
        return newExtremePointOfShip;
    }

    private void setNewPositionOfShip(Node extremePartOfShip, Node newEndingOfShip, ProtoShip ship,
                                      int numberOfCells, UnaryOperator<Node> iterateOnShip,
                                      UnaryOperator<Node> iterateOnCells) {

        extremePartOfShip = iterateOnShip.apply(extremePartOfShip);
        for (int i = 0; i < numberOfCells; i++) {
            newEndingOfShip.getCell().shipSailTo(ship);
            extremePartOfShip.getCell().shipSailedAway();
            newEndingOfShip = iterateOnCells.apply(newEndingOfShip);
            extremePartOfShip = iterateOnShip.apply(extremePartOfShip);
        }
    }

    public static void main(String[] args) {
        GameField field1 = new GameField(20, 10);
        Visualizer visualizer = new Visualizer(field1.field);
        visualizer.printField();
        ProtoShip ship = new Ship(3, new Position(4, 2), OrientationOfShip.HORIZONTAL);
        field1.addShip(ship);
        visualizer.printField();
        field1.clockwiseRotation(ship);
        visualizer.printField();
        field1.clockwiseRotation(ship);
        visualizer.printField();
        field1.moveShipLeft(ship);
        visualizer.printField();
    }
}
