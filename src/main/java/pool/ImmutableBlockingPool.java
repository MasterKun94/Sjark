package pool;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.LockSupport;
import java.util.function.Supplier;

public class ImmutableBlockingPool<T> extends ImmutablePool<T> {
    private BlockingQueue<Runnable> blockingQueue;

    public ImmutableBlockingPool(int capacity, Class<T> clazz, BlockingQueue<Runnable> blockingQueue) {
        super(capacity, clazz);
        this.blockingQueue = blockingQueue;
    }

    @Override
    public T take() {
        T t = borrow();
        Thread[] th = new Thread[1];
        CompletableFuture<T> future = CompletableFuture.supplyAsync(() -> {
            th[0] = Thread.currentThread();
            LockSupport.park();
            return null;

        });
        return null;
    }

    @Override
    public int release(T t) {
        return 0;
    }


}
