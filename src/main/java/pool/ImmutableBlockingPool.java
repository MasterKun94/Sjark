package pool;

import java.util.concurrent.BlockingQueue;

public class ImmutableBlockingPool<T> extends ImmutablePool<T> {
    private BlockingQueue<Runnable> blockingQueue;

    public ImmutableBlockingPool(int capacity, Class<T> clazz, BlockingQueue<Runnable> blockingQueue) {
        super(capacity, clazz);
        this.blockingQueue = blockingQueue;
    }

    @Override
    public T take() {
        return null;
    }

    @Override
    public int release(T t) {
        return 0;
    }
}
