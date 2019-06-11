package pool;

import java.util.concurrent.atomic.AtomicInteger;

public class Node {
    private final int start;
    private final int end;
    private final Node left;
    private final Node right;

    private int layer;
    private final AtomicInteger availableAmount;

    public Node(int start, int end, Node left, Node right) {
        this.start = start;
        this.end = end;
        this.left = left;
        this.right = right;
        this.availableAmount = new AtomicInteger();
    }

    public Node getLeft() {
        return left;
    }

    public Node getRight() {
        return right;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public int getAvailableAmount() {
        return availableAmount.get();
    }

    public void setAvailableAmount(int availableAmount) {
        this.availableAmount.set(availableAmount);
    }

    public int incrementAndGetAmount() {
        return availableAmount.incrementAndGet();
    }

    public int decrementAndGetAmount() {
        return availableAmount.decrementAndGet();
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
                ", \n" + tab + "\t\"left\" : " + this.getLeft() +
                ", \n" + tab + "\t\"right\" : " + this.getRight() +
                "\n" + tab + "}";
    }
}
