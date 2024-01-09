package org.example.gameAI.BotStructure;

import org.example.field.fieldStructure.Node;
import org.example.field.fieldStructure.Position;
import org.example.field.fieldStructure.Structure;
import org.example.field.fieldStructure.TypeOfCell;
import org.example.players.PossibleMoves;
import org.example.units.OrientationOfShip;
import org.example.units.ProtoShip;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.UnaryOperator;

public abstract class AI {
    protected final List<Action> listOfMoves;
    protected final Structure field;
    protected final List<ProtoShip> yourAliveShips;
    protected final List<ProtoShip> enemyAliveShips;

    public AI(Structure field, List<ProtoShip> yourShips, List<ProtoShip> enemyShips) {
        this.field = field;
        this.listOfMoves = new ArrayList<>();
        this.yourAliveShips = yourShips;
        this.enemyAliveShips = enemyShips;
    }
    public void checkHealthOfShip() {
        yourAliveShips.removeIf(ship -> !ship.isAlive());
    }
    protected void calculateAllPossibleMoves(ProtoShip ship) {
        listOfMoves.clear();

        waysOfMovingShip(ship);
        checkRotations(ship);
        findEnemyShipsToShoot(ship);
    }

    private void waysOfMovingShip(ProtoShip ship) {
        if (ship.isShipStuck()) {
            return;
        }

        OrientationOfShip orientation = ship.getOrientation();
        if (orientation == OrientationOfShip.HORIZONTAL) {
            UnaryOperator<Node> wayOfIterating = Node::getRight;
            PossibleMoves move = PossibleMoves.MOVE_RIGHT;
            if (!isHeadOfShipInRightPosition(ship, wayOfIterating)) {
                wayOfIterating = Node::getLeft;
                move = PossibleMoves.MOVE_LEFT;
            }
            if (!checkNextNode(field.getCellByPosition(ship.getHeadOfShip()), wayOfIterating)) {
                return;
            }
            listOfMoves.add(new Action(move, ship));
        } else if (orientation == OrientationOfShip.VERTICAL) {
            UnaryOperator<Node> wayOfIterating = Node::getUp;
            PossibleMoves move = PossibleMoves.MOVE_UP;
            if (!isHeadOfShipInRightPosition(ship, wayOfIterating)) {
                wayOfIterating = Node::getDown;
                move = PossibleMoves.MOVE_DOWN;
            }
            if (!checkNextNode(field.getCellByPosition(ship.getHeadOfShip()), wayOfIterating)) {
                return;
            }
            listOfMoves.add(new Action(move, ship));
        } else {
            listOfMoves.add(new Action(PossibleMoves.MOVE_LEFT, ship));
            listOfMoves.add(new Action(PossibleMoves.MOVE_RIGHT, ship));
            listOfMoves.add(new Action(PossibleMoves.MOVE_UP, ship));
            listOfMoves.add(new Action(PossibleMoves.MOVE_DOWN, ship));
        }
    }

    private boolean isHeadOfShipInRightPosition(ProtoShip ship, UnaryOperator<Node> wayOfIterating) {
        Node head = field.getCellByPosition(ship.getHeadOfShip());
        Node cellForTest = wayOfIterating.apply(head);
        return !(cellForTest.getCell().getShip() == ship);
    }

    private boolean checkNextNode(Node startNode, UnaryOperator<Node> wayOfIterating) {
        startNode = wayOfIterating.apply(startNode);
        TypeOfCell type = startNode.getCell().getTypeOfCell();
        return (type != TypeOfCell.BARRIER && type != TypeOfCell.PART_OF_SHIP);
    }
    void checkRotations(ProtoShip ship) {
        waysOfRotations(ship,true);
        waysOfRotations(ship,false);

    }
    private void waysOfRotations(ProtoShip ship, boolean clockwiseRotation) {
        if (ship.isShipStuck()) {
            return;
        }

        int centerOfShip = ((ship.getSizeOfShip() + 1) / 2);
        int numberOfIteratingBack = ship.getSizeOfShip() - centerOfShip;
        Node headOfShip = field.getCellByPosition(ship.getHeadOfShip());
        OrientationOfShip orientation = ship.getOrientation();

        if (orientation == OrientationOfShip.SINGLE_DECK) {
            return;
        }
        UnaryOperator<Node> wayOfIterating = (orientation == OrientationOfShip.HORIZONTAL) ? Node::getRight : Node::getUp;
        PossibleMoves rotation = (clockwiseRotation) ? PossibleMoves.CLOCKWISE_ROTATE : PossibleMoves.COUNTERCLOCKWISE_ROTATE;
        UnaryOperator<Node> iterateToTail = (orientation == OrientationOfShip.HORIZONTAL) ? Node::getLeft : Node::getDown;
        UnaryOperator<Node> checkForHead = (orientation == OrientationOfShip.HORIZONTAL) ? Node::getDown : Node::getRight;
        UnaryOperator<Node> iterateToHead = wayOfIterating;
        UnaryOperator<Node> checkForTail = (orientation == OrientationOfShip.HORIZONTAL) ? Node::getUp : Node::getLeft;
        //крутим против часовой для проверки
        if (!clockwiseRotation) {
            UnaryOperator<Node> tmp = checkForHead;
            checkForHead = checkForTail;
            checkForTail = tmp;
        }
        //если корабль в обратную сторону развёрнут
        if (!isHeadOfShipInRightPosition(ship, wayOfIterating)) {
            if (clockwiseRotation) {
                UnaryOperator<Node> tmp = checkForHead;
                checkForHead = checkForTail;
                checkForTail = tmp;
            } else {
                checkForHead = (orientation == OrientationOfShip.HORIZONTAL) ? Node::getDown : Node::getRight;
                checkForTail = (orientation == OrientationOfShip.HORIZONTAL) ? Node::getUp : Node::getLeft;
            }
            UnaryOperator<Node> tmp = iterateToHead;
            iterateToHead = iterateToTail;
            iterateToTail = tmp;
        }

        Node centerOfShipNode = headOfShip; // нода указывающая на центр корабля
        for (int i = 0; i < numberOfIteratingBack; i++) {
            centerOfShipNode = iterateToTail.apply(centerOfShipNode);
        }
        int countOfCellToCheckDown = numberOfIteratingBack; // если поворачиваем по часовой, то количество клеток занятых после поворота равно кол-во клеток до центра
        int countOfCellToCheckUp = (ship.getSizeOfShip() % 2 == 0) ? countOfCellToCheckDown - 1 : countOfCellToCheckDown; // кол-во клеток для провекри места, где будет хвост корабля
        // если по часовой то так как написано рассчитывается, если против то значения переменных меняются местами.
        int count = (ship.getSizeOfShip() < 3) ? 0 : ((ship.getSizeOfShip() + 2 - 1) / 2);

        Node movingToTail = centerOfShipNode;
        Node movingTOHead = centerOfShipNode;

        boolean checkSpaceForHead = checkSpaceForShipRotate(movingTOHead, numberOfIteratingBack + 1,
                countOfCellToCheckDown, iterateToHead, checkForHead); // сделать методы, которые будут проверять, можно ли расположит так корабль. Будут возвращать ноду нового хвоста или головы
        boolean checkSpaceForTail = checkSpaceForShipRotate(movingToTail, count, countOfCellToCheckUp, iterateToTail, checkForTail); // если вернули null, то так корабль поместить нельзя проверки сделаем и всё будет в ажуре.

        if (!checkSpaceForHead  || !checkSpaceForTail) {
            return;
        }
        listOfMoves.add(new Action(rotation, ship));
    }
    private boolean checkSpaceForShipRotate(Node shipCenterNode, int countOfShipCells,
                                    int countOfCellsToCheck, UnaryOperator<Node> iterateOnShip,
                                    UnaryOperator<Node> iterateToCheckCells) {
        for (int i = 0; i < countOfShipCells; i++) {
            Node tmp = shipCenterNode;
            int countToCheck = (i == 1) ? 0 : i;
            for (int j = countToCheck; j < countOfCellsToCheck; j++) {
                tmp = iterateToCheckCells.apply(tmp);
                TypeOfCell type = tmp.getCell().getTypeOfCell();
                if (type == TypeOfCell.BARRIER || type == TypeOfCell.PART_OF_SHIP) {
                    return false;
                }
            }
            shipCenterNode = iterateOnShip.apply(shipCenterNode);
        }
        return true;
    }
    private void findEnemyShipsToShoot(ProtoShip ship) {
        double maximumRadiusOfShooting = (double) ship.getShootingDistance();
        int xShip = ship.getHeadOfShip().getX();
        int yShip = ship.getHeadOfShip().getY();

        for (ProtoShip enemyShip : enemyAliveShips) {
            Position posOfHeadEnemy = enemyShip.getHeadOfShip();
            Node headOfShip = field.getCellByPosition(posOfHeadEnemy);
            OrientationOfShip orientation = enemyShip.getOrientation();
            UnaryOperator<Node> iterateToTail = (orientation == OrientationOfShip.HORIZONTAL) ? Node::getRight
                    : Node::getUp;
            int row = enemyShip.getHeadOfShip().getY();
            int col = enemyShip.getHeadOfShip().getX();
            int deltaY = (orientation == OrientationOfShip.HORIZONTAL) ? 0 : 1;
            int deltaX = (orientation == OrientationOfShip.HORIZONTAL) ? -1 : 0;

            if (isHeadOfShipInRightPosition(enemyShip, iterateToTail)) {
                iterateToTail = (orientation == OrientationOfShip.HORIZONTAL) ? Node::getLeft
                        : Node::getDown;
                deltaX = (orientation == OrientationOfShip.HORIZONTAL) ? 1 : 0;
                deltaY = (orientation == OrientationOfShip.HORIZONTAL) ? 0 : -1;
            }

            for (int i = 0; i < enemyShip.getSizeOfShip(); i++) {
                int xShoot = headOfShip.getPosition().getX();
                int yShoot = headOfShip.getPosition().getY();
                double distanceToShoot = Math.sqrt((xShoot - xShip)*(xShoot - xShip) + (yShoot - yShip)*(yShoot - yShip));

                if (distanceToShoot <= maximumRadiusOfShooting) {
                    listOfMoves.add(new Action(PossibleMoves.SHOOT, new Position(col, row), ship));
                }

                row += deltaY;
                col += deltaX;
                headOfShip = iterateToTail.apply(headOfShip);
            }
        }
    }
    public abstract Action makeMove(ProtoShip ship, int width, int height);
    public List<ProtoShip> getYourAliveShips() {
        return yourAliveShips;
    }
}
