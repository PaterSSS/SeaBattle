package org.example.field.fieldStructure;

import org.example.field.Cell;

public class FieldBuilder {
    private final int width;
    private final int height;
    private Structure field;

    public FieldBuilder(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void createEmptyField() {
        field = new Structure();
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                Position position = new Position(col, row);
                Cell cell;
                if (col == 0 || row == 0 || col == width - 1  || row == height - 1) {
                    cell = new Cell(TypeOfCell.BARRIER);
                } else {
                    cell = new Cell(TypeOfCell.EMPTY);
                }
                field.addNode(cell, position);
            }
        }
    }
    public Structure getField() {
        return field;
    }
}
