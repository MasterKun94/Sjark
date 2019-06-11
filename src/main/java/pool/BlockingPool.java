package pool;

public interface BlockingPool<T> extends Pool<T> {

    int getQueueRemaining();

}
