package org.example.consoleUI;

import org.example.field.fieldStructure.Node;
import org.example.field.fieldStructure.Structure;
import org.example.field.fieldStructure.TypeOfCell;

public class Visualizer {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_BLUE_BACKGROUND = "\u001B[44m";
    private static final String signForWater ="~";
    private static final String signForBarrier = "x";
    private static final String signForShip = "s";
    private Structure field;
    public Visualizer(Structure field) {
        this.field = field;
    }
    public void printField() {
        for (Node row : field.getListOfHeads()) {
            for (Node col = row; col != null; col = col.getRight()) {
                TypeOfCell type = col.getCell().getTypeOfCell();
                switch (type) {
                    case EMPTY -> System.out.print(signForWater);
                    case PART_OF_SHIP -> System.out.print(signForShip);
                    case BARRIER -> System.out.print(signForBarrier);
                }
            }
            System.out.println();
        }
    }
}
