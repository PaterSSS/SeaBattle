package org.example.field.fieldStructure;

import org.example.consoleUI.Visualizer;
import org.example.field.Cell;
import org.example.units.OrientationOfShip;
import org.example.units.ProtoShip;
import org.example.units.Ship;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FieldBuilder {
    private int width;
    private int height;
    private String pathToFile;
    private Structure field;
    private final static char SHIP_TOKEN = 's';
    private final static char BARRIER_TOKEN = 'x';
    private final static char WATER_TOKEN = '~';
    private final static char SHOAL_TOKEN = 'a';
    private final static char HEAD_SHIP_TOKEN = 'h';

//сделать чтение из файла поля
    public FieldBuilder(int width, int height) {
        this.width = width;
        this.height = height;
    }
    public FieldBuilder(String path) {
        this.pathToFile = path;
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
    //x - barrier, s - part of a ship, h - head of a ship, ~ - clear water, a - is a shoal.
    public void readFieldFromFile() {
        readFieldFromFile(pathToFile);
    }
    public void readFieldFromFile(String path) {
        try {
            List<String> stringList = Files.readAllLines(Path.of(path));
            this.height = stringList.size();
            this.width = stringList.get(0).length();
            createFieldFromFile(stringList);
        } catch (IOException e) {
            //TODO write my own exceptions for game.
            throw new RuntimeException(e);
        }

    }
    private void createFieldFromFile(List<String> list) {
        field = new Structure();
        Map<String, List<ProtoShip>> playersShips = new HashMap<>();
        Map<Position, ProtoShip> positionProtoShipMap = new HashMap<>();
        playersShips.put("first", new ArrayList<>());
        playersShips.put("second", new ArrayList<>());
        for (int row = 0; row < list.size(); row++) {
            String stringToCheck = list.get(row);
            int sizeOfRow = stringToCheck.length();
            for (int col = 0; col < sizeOfRow; col++) {
                Position pos = new Position(col,row);
                char token = stringToCheck.charAt(col);
                switch (token) {
                    case BARRIER_TOKEN -> field.addNode(new Cell(TypeOfCell.BARRIER), pos);
                    case SHOAL_TOKEN -> field.addNode(new Cell(TypeOfCell.SHOAL), pos);
                    case WATER_TOKEN -> field.addNode(new Cell(TypeOfCell.EMPTY), pos);
                    case SHIP_TOKEN, HEAD_SHIP_TOKEN -> handleShip(row, col, positionProtoShipMap, playersShips, list);
                }
                }
        }
    }
    private void handleShip(int row, int col, Map<Position, ProtoShip> positionProtoShipMap,
                            Map<String, List<ProtoShip>> playersShips, List<String> list) {

        Position tmp = new Position(col, row);
        if (positionProtoShipMap.containsKey(tmp)) {
            field.addNode(new Cell(TypeOfCell.PART_OF_SHIP, positionProtoShipMap.get(tmp)),tmp);
            return;
        }
        int horizontalLengthOfShip = iterateOverShip(col, row, list, 1000000);
        int verticalLengthOfShip = iterateOverShip(col, row, list, 1);
        ProtoShip ship;
        if (horizontalLengthOfShip > verticalLengthOfShip) {
            ship = makeShip(col, row, horizontalLengthOfShip, list, positionProtoShipMap, horizontalLengthOfShip, 1);
        } else if (horizontalLengthOfShip < verticalLengthOfShip) {
            ship = makeShip(col, row, verticalLengthOfShip, list, positionProtoShipMap, 1, verticalLengthOfShip);
        } else {
            ship = new Ship(1, new Position(col, row), OrientationOfShip.SINGLE_DECK);
        }

        if (col < list.get(row).length()/2) {
            playersShips.get("first").add(ship);
        } else {
            playersShips.get("second").add(ship);
        }
        field.addNode(new Cell(TypeOfCell.PART_OF_SHIP, ship), new Position(col, row));
    }
    private int iterateOverShip(int col, int row, List<String> listOfField, int deltaX) {
        int lengthOfShip = 0;

        for (int currentRow = row;; currentRow++) {
            String stringToCheck = listOfField.get(currentRow);
            for (int currentCol = col; currentCol < col + deltaX; currentCol++) {
                char token = stringToCheck.charAt(currentCol);
                if (token != SHIP_TOKEN && token != HEAD_SHIP_TOKEN) {
                    return lengthOfShip;
                }
                lengthOfShip++;
            }
        }
    }
    private ProtoShip makeShip(int col, int row, int sizeOfShip, List<String> list, Map<Position,
                               ProtoShip> positionProtoShipMap, int deltaX, int deltaY) {
        Position posOfHead = null;
        OrientationOfShip orientation = (deltaX == 1)? OrientationOfShip.VERTICAL : OrientationOfShip.HORIZONTAL;
        int xHead = (deltaX == 1) ? col : col + sizeOfShip - 1;
        int yHead = (deltaY == 1) ? row : row + sizeOfShip - 1;

        if (list.get(row).charAt(col) == HEAD_SHIP_TOKEN) {
            posOfHead = new Position(col, row);
        } else if (list.get(yHead).charAt(xHead) == HEAD_SHIP_TOKEN) {
            posOfHead = new Position(xHead, yHead);
        } else {
            throw new RuntimeException("incorrect head of a ship");
        }

        ProtoShip ship = new Ship(sizeOfShip, posOfHead, orientation);
        for (int currentRow = row; currentRow < row + deltaY; currentRow++) {
            String stringToCheck = list.get(currentRow);
            for (int currentCol = col; currentCol < col + deltaX; currentCol++) {
                positionProtoShipMap.put(new Position(currentCol, currentRow), ship);
            }
        }
        return ship;
    }
    public Structure getField() {
        return field;
    }

    public static void main(String[] args) {
        FieldBuilder fieldBuilder = new FieldBuilder("C:\\Материалы ВУЗ\\3й семестр\\ООП\\projects\\SeaBattle\\src\\main\\java\\fieldExample.txt");
        fieldBuilder.readFieldFromFile();
        System.out.println();
        Visualizer visualizer = new Visualizer(fieldBuilder.field);
        visualizer.printField();
    }
}
