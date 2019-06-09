package pool;

import java.util.ArrayList;
import java.util.List;

public class Stack<T> {
    private List<T> list;
    private int index;

    public Stack() {
        this.list = new ArrayList<>();
        this.index = -1;
    }

    public void push(T t) {
        list.add(t);
        index++;
    }

    public T pop() {
        if (index < 0) {
            throw new IllegalArgumentException();
        } else {
            T t = list.remove(index);
            index--;
            return t;
        }
    }

    public T peek() {
        if (index < 0) {
            throw new IllegalArgumentException();
        } else {
            return get(-1);
        }
    }

    public T get(int i) {
        if (i < 0) {
            return get(i + index + 1);
        }
        return list.get(i);
    }

    public int getIndex() {
        return index;
    }

    @Override
    public String toString() {
        return list.toString();
    }
}
