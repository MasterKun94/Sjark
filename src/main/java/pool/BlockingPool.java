package pool;

public interface BlockingPool<T> extends Pool<T> {
    T get() throws InterruptedException;

    int waitingCapacity();
}
