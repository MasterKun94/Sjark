package pool;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class ImmutablePool<T> implements Pool<T> {
    private static final int ELE_SIZE = 8;
    private static final int LOOP = 5;

    private final T[] elements;
    private final AtomicIntegerArray referenceCounter;
    private final PointerIndexer indexer;

    private final Node root;

    @SuppressWarnings("unchecked")
    public ImmutablePool(int capacity, Class<T> clazz) {
        elements = (T[]) new Object[capacity];
        referenceCounter = new AtomicIntegerArray(capacity);
        HashMap<T, Integer> hashIndexMap = new HashMap<>(capacity);
        try {
            for (int i = 0; i < capacity; i++) {
                T t = clazz.getConstructor().newInstance();
                elements[i] = t;
                hashIndexMap.put(t, i);
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException  e) {
            e.printStackTrace();
        }
        indexer = new PointerIndexer(hashIndexMap);
        Stack<Integer> intStack = new Stack<>();
        Stack<Node> nodeStack = new Stack<>();

        Node node;
        int start;
        int end;
        for (int i = 0; i < capacity; i = i + ELE_SIZE) {
            start = i;
            end = capacity < i + ELE_SIZE ? capacity - 1 : i + ELE_SIZE - 1;
            node = new Node(start, end, null, null);
            node.setAvailableAmount(end - start + 1);
            nodeStack.push(node);
            intStack.push(1);
            refresh(nodeStack, intStack);
        }
        reduce(nodeStack, intStack);
        root = nodeStack.pop();
        root.passDownLayer(0);
    }

    @Override
    public T borrow() {
        return root.getAvailableAmount() == 0 ? null : getAvailable(root);
    }

    @Override
    public T take() {
        if (root.getAvailableAmount() == 0) {
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

    @Override
    public int addReference(T t) {
        return addReference(getPointer(t));
    }

    @Override
    public int addReference(int pointer) {
        return referenceCounter.incrementAndGet(pointer);
    }

    @Override
    public int release(T t) {
        return release(getPointer(t));
    }

    @Override
    public int release(int pointer) {
        int count = referenceCounter.decrementAndGet(pointer);
        if (count < 0) {
            referenceCounter.incrementAndGet(pointer);
            throw new IllegalArgumentException();
        } else if (count >= 1) {
            return count;
        } else {
            releaseReference(root, pointer);
            return 0;
        }
    }

    @Override
    public int getCounter(T t) {
        return getCounter(getPointer(t));
    }

    @Override
    public int getCounter(int pointer) {
        return referenceCounter.get(pointer);
    }

    @Override
    public int getPointer(T t) {
        return indexer.findPointer(t);
    }

    @Override
    public T getElement(int i) {
        return elements[i];
    }

    @Override
    public int availableAmount() {
        return root.getAvailableAmount();
    }

    @Override
    public int size() {
        return root.getEnd() - root.getStart() + 1;
    }

    private T getAvailable(Node node) {
        int key = node.decrementAndGetAmount();
        Node child;
        if (key >= 0) {
            for (int i = 0; i < LOOP; i++) {
                Node left = node.getLeft();
                Node right = node.getRight();
                child = (key + i) % 2 == 0 ?
                        left.getAvailableAmount() > 0 ?
                                left :
                                right :
                        right.getAvailableAmount() > 0 ?
                                right :
                                left;

                if (child != null) {
                    if (child.getLeft() != null) {
                        return getAvailable(child);
                    }
                    if (child.decrementAndGetAmount() >= 0) {
                        T t = getAvailableElement(child);
                        if (t != null) {
                            return t;
                        }
                    } else {
                        child.incrementAndGetAmount();
                    }
                }
            }
        }
        node.incrementAndGetAmount();
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

                Node parentNode = new Node(leftNode.getStart(), rightNode.getEnd(), leftNode, rightNode);
                parentNode.setAvailableAmount(leftNode.getAvailableAmount() + rightNode.getAvailableAmount());
                intStack.push(integer << 1);
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

    private void releaseReference(Node node, int pointer) {
        Node left = node.getLeft();
        Node right = node.getRight();
        if (left != null) {
            releaseReference(left.getEnd() >= pointer ? left : right, pointer);
        }
        node.incrementAndGetAmount();
    }

    private class PointerIndexer {
        private int[] hashcodeArray;
        private int[] pointerArray;

        private PointerIndexer(Map<T, Integer> map) {
            List<T> list = new ArrayList<>(map.keySet());
            list.sort(Comparator.comparingInt(Object::hashCode));
            int listSize = list.size();
            hashcodeArray = new int[listSize];
            pointerArray = new int[listSize];
            T o;
            for (int i = 0; i < listSize; i++) {
                o = list.get(i);
                hashcodeArray[i] = o.hashCode();
                pointerArray[i] = map.get(o);
            }
        }

        private int findPointer(T object) {
            System.out.println("test");
            int reqHash = object.hashCode();
            int capacity = pointerArray.length;
            int minIdx = 0;
            int maxIdx = capacity - 1;
            int minHash, maxHash, get, idx;
            minHash = hashcodeArray[minIdx];
            maxHash = hashcodeArray[maxIdx];
            do {
                idx = (maxIdx - minIdx) * (reqHash - minHash) / (maxHash - minHash) + minIdx;
//                System.out.println(idx);
                get = hashcodeArray[idx];
//                System.out.println(get + " " + reqHash + " : " + minIdx + " " + maxIdx);
                if (reqHash > get) {
                    minIdx = idx + 1;
                    minHash = hashcodeArray[minIdx];
                } else if (reqHash < get) {
                    maxIdx = idx - 1;
                    maxHash = hashcodeArray[maxIdx];
                }
            } while (reqHash != get || maxIdx != minIdx);
                System.out.println(idx);

            if (object.equals(getElement(get))) {
                return idx;
            }
            int idx2 = idx + 1;
            while (reqHash == hashcodeArray[idx2]) {
                if (object.equals(getElement(get))) {
                    return idx2;
                }
                idx2++;
            }
            idx2 = idx - 1;
            while (reqHash == hashcodeArray[idx2]) {
                if (object.equals(getElement(get))) {
                    return idx2;
                }
                idx2--;
            }
            throw new IllegalArgumentException();
        }
    }
}
