package org.example.field;

import org.example.consoleUI.Visualizer;
import org.example.field.fieldStructure.FieldBuilder;
import org.example.field.fieldStructure.Structure;

public class Field {
    private Structure field;
    private int sizeOfField;
    public Field(int sizeOfField) {
        this.sizeOfField = sizeOfField;
        FieldBuilder builder = new FieldBuilder(20,10);
        builder.createEmptyField();
        field = builder.getField();
    }

    public static void main(String[] args) {
        Field field1 = new Field(20);
        Visualizer visualizer = new Visualizer(field1.field);
        visualizer.printField();
    }
}
