package pool;

import java.util.concurrent.atomic.AtomicInteger;

public class Node {
    private int start;
    private int end;
    private int layer;
    private AtomicInteger availableAmount;

    private Node left;
    private Node right;

    public Node getLeft() {
        return left;
    }

    public void setLeft(Node left) {
        this.left = left;
    }

    public Node getRight() {
        return right;
    }

    public void setRight(Node right) {
        this.right = right;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public AtomicInteger getAvailableAmount() {
        return availableAmount;
    }

    public void setAvailableAmount(AtomicInteger availableAmount) {
        this.availableAmount = availableAmount;
    }

    public void passDownLayer(int layer) {
        this.layer = layer;
        if (left != null) {
            left.passDownLayer(layer + 1);
        }
        if (right != null) {
            right.passDownLayer(layer + 1);
        }
    }

    public int getLayer() {
        return layer;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < layer; i++) {
            sb.append("\t");
        }
        String tab = sb.toString();
        return "{\n" + tab + "\t\"start\" : " + this.getStart() +
                ", \n" + tab + "\t\"end\" : " + this.getEnd() +
                ", \n" + tab + "\t\"layer\" : " + this.getLayer() +
                ", \n" + tab + "\t\"availableAmount\" : " + this.getAvailableAmount() +
                ", \n" + tab + "\t\"left\" : " + this.getLeft() + ", \n" +
                tab + "\t\"right\" : " + this.getRight() + "\n" +
                tab + "}";
    }
}
