package org.example.field;

import org.example.field.fieldStructure.TypeOfCell;
import org.example.units.ProtoShip;

public class Cell {
    private TypeOfCell typeOfCell;
    private ProtoShip ship;
    public Cell(TypeOfCell type) {
        typeOfCell = type;
    }
    public Cell(TypeOfCell type, ProtoShip ship) {
        this.typeOfCell = type;
        this.ship = ship;
    }
    public TypeOfCell getTypeOfCell() {
        return typeOfCell;
    }

    public void setTypeOfCell(TypeOfCell typeOfCell) {
        this.typeOfCell = typeOfCell;
    }
    public ProtoShip getShip() {
        return ship;
    }
    void shipSailedAway() {
        if (ship == null) {
            return;
        }
        ship = null;
        typeOfCell = TypeOfCell.EMPTY;
    }
    void shipSailTo(ProtoShip ship) {
        if (ship == null || this.ship != null) {
            return;
        }
        this.ship = ship;
        typeOfCell = TypeOfCell.PART_OF_SHIP;
    }
}
