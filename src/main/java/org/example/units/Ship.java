package org.example.units;

import org.example.field.fieldStructure.Position;

public class Ship implements ProtoShip{
    //сделать битовую маску чтобы можно было задавать разные состояние как раненные и может двигаться и добавить енам
    //в котором будут указываться эти состояния
    private int health;
    private final int size;
    private Position posOfShipHead;
    private int  shootingDistance;
    private OrientationOfShip orientation;
    private boolean isStuck;

    public Ship(int size, Position position, OrientationOfShip orientation) {
        this.size = size;
        this.health = size;
        this.posOfShipHead = position;
        this.orientation = orientation;
        this.shootingDistance = size * 2;
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
    public void getDamage(int damage) {
        if (damage < 0) {
            return;
        }
        health -= damage;
    }

    @Override
    public boolean isAlive() {
        return health > 0;
    }

    @Override
    public OrientationOfShip getOrientation() {
        return orientation;
    }

    @Override
    public boolean isShipStuck() {
        return isStuck;
    }
    public void setStuck() {
        isStuck = true;
    }

    @Override
    public void setPositionOfHead(Position position) {
        this.posOfShipHead = position;
    }

    @Override
    public void shipRotation(OrientationOfShip orientation) {
        this.orientation = orientation;
    }
}
