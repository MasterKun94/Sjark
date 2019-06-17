package pool;

import java.util.concurrent.BlockingQueue;

public class ImmutableBlockingPool<T> extends ImmutablePool<T> implements BlockingPool<T> {
    private final BlockingQueue<Box> blockingQueue;

    public ImmutableBlockingPool(int capacity, Class<T> clazz, BlockingQueue<Box> blockingQueue) {
        super(capacity, clazz);
        this.blockingQueue = blockingQueue;
    }

    @Override
    public T get() throws InterruptedException {
        Box box = null;
        T t;
        do {
            while (!isFull()) {
                t = borrow();
                if (t != null) {
                    return t;
                }
            }
            if (box == null) {
                box = Box.emptyBox();
            } else {
                box.empty();
            }
            if (isFull()) {
                blockingQueue.add(box);
            } else {
                box.sendBack();
            }
            while (box.isEmpty()) {
                box.wait();
            }
        } while (box.isSendBack());

        return getElement(box.getPointer());
    }

    @Override
    public int waitingCapacity() {
        return blockingQueue.size();
    }

    @Override
    public int release(T t) {
        int index = getPointer(t);
        if (getCounter(index) == 1) {
            Box box = blockingQueue.poll();
            if (box != null) {
                box.setPointer(index);
                box.notify();
                return 0;
            }
        }
        int count = release(index);
        if (count == 0) {
            Box box = blockingQueue.poll();
            if (box != null) {
                box.sendBack();
                box.notify();
            }
        }
        return count;
    }
}
