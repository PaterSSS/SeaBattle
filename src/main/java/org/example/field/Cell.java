package org.example.field;

import org.example.field.fieldStructure.TypeOfCell;

public class Cell {
    private TypeOfCell typeOfCell;
    public Cell(TypeOfCell type) {
        typeOfCell = type;
    }
    public TypeOfCell getTypeOfCell() {
        return typeOfCell;
    }

    public void setTypeOfCell(TypeOfCell typeOfCell) {
        this.typeOfCell = typeOfCell;
    }
}
