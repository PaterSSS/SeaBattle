package org.example.units;

import org.example.field.fieldStructure.Position;

public interface ProtoShip {
    int getHealth();
    int getSizeOfShip();
    int getShootingDistance();
    Position getHeadOfShip();
    void getDamage();
    boolean isAlive();
    OrientationOfShip getOrientation();
}
