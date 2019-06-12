package pool;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.LockSupport;

public class ImmutableBlockingPool<T> extends ImmutablePool<T> {
    private BlockingQueue<Box> blockingQueue;

    public ImmutableBlockingPool(int capacity, Class<T> clazz, BlockingQueue<Box> blockingQueue) {
        super(capacity, clazz);
        this.blockingQueue = blockingQueue;
    }

    @Override
    public T take() {
        T t = borrow();
        Box box = new Box();
        box.setThread(Thread.currentThread());
        blockingQueue.add(box);
        LockSupport.park();
        return getElement(box.getPointer());
    }

    @Override
    public int release(T t) {
        int index = getPointer(t);
        if (getCounter(index) == 1) {
            Box box = blockingQueue.poll();
            if (box != null) {
                box.setPointer(index);
                LockSupport.unpark(box.getThread());
                return 0;
            }
        }
        return super.release(index);
    }
}
