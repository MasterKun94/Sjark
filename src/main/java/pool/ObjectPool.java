package pool;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class ObjectPool<T> implements Pool<T> {
    private static final int ELE_SIZE = 8;
    private static final int LOOP = 5;

    private T[] elements;
    private AtomicIntegerArray referenceCounter;
    private HashMap<T, Integer> hashIndexMap;

    private Node root;

    @SuppressWarnings("unchecked")
    public ObjectPool(int capacity, Class<T> clazz) {
        elements = (T[]) new Object[capacity];
        referenceCounter = new AtomicIntegerArray(capacity);
        hashIndexMap = new HashMap<>(capacity);
        try {
            for (int i = 0; i < capacity; i++) {
                T t = clazz.getConstructor().newInstance();
                elements[i] = t;
                System.out.println(t.hashCode());
                hashIndexMap.put(t, i);
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException  e) {
            e.printStackTrace();
        }

        Stack<Integer> intStack = new Stack<>();
        Stack<Node> nodeStack = new Stack<>();

        Node node;
        int start;
        int end;
        for (int i = 0; i < capacity; i = i + ELE_SIZE) {
            start = i;
            end = capacity < i + ELE_SIZE ? capacity - 1 : i + ELE_SIZE - 1;
            node = new Node();
            node.setStart(start);
            node.setEnd(end);
            node.setAvailableAmount(new AtomicInteger(end - start + 1));
            nodeStack.push(node);
            intStack.push(1);
            refresh(nodeStack, intStack);
        }
        reduce(nodeStack, intStack);
        root = nodeStack.pop();
        root.passDownLayer(0);
    }

    public T borrow() {
        if (root.getAvailableAmount().get() == 0) {
            throw new IllegalStateException("Pool full");
        } else {
            T t = getAvailable(root);
            if (t != null) {
                return t;
            } else {
                throw new RuntimeException();
            }
        }
    }

    public int addReference(T t) {
        return referenceCounter.incrementAndGet(getIndex(t));
    }

    public int release(T t) {
        return referenceCounter.decrementAndGet(getIndex(t));
    }

    public int getCounter(T t) {
        return referenceCounter.get(getIndex(t));
    }

    public int getIndex(T t) {
        Integer integer = hashIndexMap.get(t);
        if (integer == null || referenceCounter.get(integer) == 0) {
            throw new IllegalArgumentException();
        }
        return integer;
    }

    @Override
    public int availableAmount() {
        return root.getAvailableAmount().get();
    }

    @Override
    public int size() {
        return root.getEnd() - root.getStart() + 1;
    }

    private T getAvailable(Node node) {
        AtomicInteger atomic = node.getAvailableAmount();
        int key = atomic.decrementAndGet();
        Node child;
        if (key >= 0) {
            for (int i = 0; i < LOOP; i++) {
                Node left = node.getLeft();
                Node right = node.getRight();
                child = (key + i) % 2 == 0 ?
                        left.getAvailableAmount().get() > 0 ?
                                left :
                                right :
                        right.getAvailableAmount().get() > 0 ?
                                right :
                                left;

                if (child != null) {
                    if (child.getLeft() != null) {
                        return getAvailable(child);
                    }
                    if (child.getAvailableAmount().decrementAndGet() >= 0) {
                        T t = getAvailableElement(child);
                        if (t != null) {
                            return t;
                        }
                    } else {
                        child.getAvailableAmount().incrementAndGet();
                    }

                }

            }
        }
        atomic.incrementAndGet();
        return null;
    }

    private T getAvailableElement(Node node) {
        int start = node.getStart();
        int end = node.getEnd();
        for (int i = start; i <= end; i++) {
            if (referenceCounter.compareAndSet(i, 0, 1)) {
                return elements[i];
            }
        }
        return null;
    }

    private void refresh(Stack<Node> nodeStack, Stack<Integer> intStack) {
        if (intStack.getIndex() > 0) {
            if (intStack.get(-1).equals(intStack.get(-2))) {
                intStack.pop();
                Integer integer = intStack.pop();
                Node rightNode = nodeStack.pop();
                Node leftNode = nodeStack.pop();

                Node parentNode = new Node();
                parentNode.setStart(leftNode.getStart());
                parentNode.setEnd(rightNode.getEnd());
                parentNode.setLeft(leftNode);
                parentNode.setRight(rightNode);
                parentNode.setAvailableAmount(new AtomicInteger(leftNode.getAvailableAmount().get() + rightNode.getAvailableAmount().get()));
                intStack.push(integer * 2);
                nodeStack.push(parentNode);
                refresh(nodeStack, intStack);
            }
        }
    }

    private void reduce(Stack<Node> nodeStack, Stack<Integer> intStack) {
        if (intStack.getIndex() > 0) {
            Integer integer = intStack.pop() << 1;
            intStack.push(integer);
            refresh(nodeStack, intStack);
            reduce(nodeStack, intStack);
        }
    }

    public static void main(String[] args) {

    }
}
