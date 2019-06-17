package pool;

public interface Pool<T> {
    T poll();

    T element();

    int addReference(T t);

    int addReference(int pointer);

    int release(T t);

    int release(int pointer);

    int getCounter(T t);

    int getCounter(int pointer);

    int getPointer(T t);

    T getElement(int pointer);

    int availableAmount();

    boolean isFull();

    int size();
}
