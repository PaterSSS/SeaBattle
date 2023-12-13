package org.example.field.fieldStructure;

import org.example.field.Cell;

public class Node {
    private Cell cell;
    private Position position;
    private Node up;
    private Node left;
    private Node right;
    private Node down;
    public Node(Cell cell, Position position) {
        this.cell = cell;
        this.position = position;
    }

     void setCell(Cell cell) {
        this.cell = cell;
    }

    void setUp(Node up) {
        this.up = up;
    }

    void setLeft(Node left) {
        this.left = left;
    }

    void setRight(Node right) {
        this.right = right;
    }

    void setDown(Node down) {
        this.down = down;
    }

    public Cell getCell() {
        return cell;
    }

    public Position getPosition() {
        return position;
    }

    public Node getUp() {
        return up;
    }

    public Node getLeft() {
        return left;
    }

    public Node getRight() {
        return right;
    }

    public Node getDown() {
        return down;
    }
}
