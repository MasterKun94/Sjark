package pool;

public interface Pool<T> {
    T borrow();

    int addReference(T t);

    int release(T t);

    int getCounter(T t);

    int getIndex(T t);

    int availableAmount();

    int size();
}
