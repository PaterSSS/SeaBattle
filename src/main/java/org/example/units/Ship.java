package org.example.units;

import org.example.field.fieldStructure.Position;

public class Ship implements ProtoShip{
    private int health;
    private final int size;
    private Position posOfShipHead;
    private int shootingDistance;
    OrientationOfShip orientation;

    public Ship(int size, Position position, OrientationOfShip orientation) {
        this.size = size;
        this.health = size;
        this.posOfShipHead = position;
        calculateShootingDis();
        this.orientation = orientation;
    }
    private void calculateShootingDis() {
        switch (size) {
            case 1 -> shootingDistance = 4;
            case 2 -> shootingDistance = 6;
            case 3 -> shootingDistance = 8;
            case 4 -> shootingDistance = 10;
        }
    }
    @Override
    public int getHealth() {
        return health;
    }

    @Override
    public int getSizeOfShip() {
        return size;
    }

    @Override
    public int getShootingDistance() {
        return shootingDistance;
    }

    @Override
    public Position getHeadOfShip() {
        return posOfShipHead;
    }

    @Override
    public void getDamage() {
        health--;
    }

    @Override
    public boolean isAlive() {
        return health > 0;
    }

    @Override
    public OrientationOfShip getOrientation() {
        return orientation;
    }
}
