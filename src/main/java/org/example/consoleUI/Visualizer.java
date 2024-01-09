package org.example.consoleUI;

import org.example.field.GameField;
import org.example.field.fieldStructure.Node;
import org.example.field.fieldStructure.Structure;
import org.example.field.fieldStructure.TypeOfCell;

import java.util.List;

public class Visualizer {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_BLUE_BACKGROUND = "\u001B[44m";
    private static final String signForWater ="~";
    private static final String signForBarrier = "x";
    private static final String signForShip = "s";
    private static final String signForShoal = "a";
    private static final String emojiForShip = "⛵";
    private static final String emojiForWater = "🌊";
    private static final String emojiForBarrier = "🌋";
    private static final String emojiForShoal = "⛱️";
    private static final String emojiForDeadShip = "⚓";
    private Structure field;
    public Visualizer(Structure field) {
        this.field = field;
    }
    public void printField(List<String> actions) {
        for (Node row : field.getListOfHeads()) {
            for (Node col = row; col != null; col = col.getRight()) {
                TypeOfCell type = col.getCell().getTypeOfCell();
                switch (type) {
                    case EMPTY -> System.out.print(emojiForWater);
                    case PART_OF_SHIP -> System.out.print((col.getCell().getShip().isAlive())? emojiForShip: emojiForDeadShip);
                    case BARRIER -> System.out.print(emojiForBarrier);
                    case SHOAL -> System.out.print(emojiForShoal);
                }
            }
            System.out.println();
        }
        System.out.println();
        for (String action : actions) {
            System.out.println(action);
        }
    }

    public static void main(String[] args) {
        System.out.println(emojiForShip);
    }
}
