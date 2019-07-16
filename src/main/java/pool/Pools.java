package pool;

import java.util.concurrent.BlockingQueue;

public class Pools {
    public static <T> Pool<T> immutablePool(
            int capacity,
            Class<T> clazz) {
        return new ImmutablePool<>(capacity, clazz);
    }

    public static <T> BlockingPool<T> immutableBlockingPool(
            int capacity,
            Class<T> clazz,
            BlockingQueue<Box> blockingQueue) {
        return new ImmutableBlockingPool<>(
                capacity,
                clazz,
                blockingQueue);
    }
}
