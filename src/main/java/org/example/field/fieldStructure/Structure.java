package org.example.field.fieldStructure;

import org.example.field.Cell;

import java.util.ArrayList;
import java.util.List;
//проверил на 2x2, всё строится правильно, но возникает вопрос как мне использовать связи
//для проверки следующих клеток, а то получится что я просто итерируеюсь по листу.
//ведь из внешних классов не будет доступа до позиции и ссылок, возможно во внешних классах придётся
//работать с нодами. это странно, можно из переменовать в клетки, а cell рассматривать именно как хранили-
//ще информации именно о содержимом клетки
public class Structure {
    //здесь хранятся все первые клетки первого столбца поля
    private final List<Node> listOfHeads;
    //нужен если мы ищем просто последнюю клетку на строке
    private static final Integer infiniteIndex = Integer.MAX_VALUE;
    public Structure() {
        listOfHeads = new ArrayList<>();
    }
    public void addNode(Cell cell, Position position) {
        Node curNode = new Node(cell, position);
        if (position.getX() == 0) {
            listOfHeads.add(curNode);
            if (listOfHeads.size() == 1) {
                return;
            }
            Node upper = listOfHeads.get(position.getY() - 1);
            curNode.setUp(upper);
            upper.setDown(curNode);
        } else  {
            Node headOfProperLine = listOfHeads.get(position.getY());
            Node leftNode = getNodeOfLine(headOfProperLine, infiniteIndex);
            leftNode.setRight(curNode);
            curNode.setLeft(leftNode);

            if (position.getY() == 0) {
                return;
            }
            Node upperNode = getNodeOfLine(listOfHeads.get(position.getY() - 1), position.getX());
            upperNode.setDown(curNode);
            curNode.setUp(upperNode);
        }
    }
    public Cell getCellByPosition(Position position) {
        Node currNode = listOfHeads.get(position.getY());
        int i = 0;
        while (i < position.getX()) {
            currNode = currNode.getRight();
            i++;
        }
        return currNode.getCell();
    }
    private Node getNodeOfLine(Node headNode, Integer index) {
        Node curr = headNode;
        int i = 0;
        while (curr.getRight() != null && i < index) {
            curr = curr.getRight();
            i++;
        }
        return curr;
    }
    public List<Node> getListOfHeads() {
        return listOfHeads;
    }
}
