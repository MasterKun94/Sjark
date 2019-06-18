package futureUtil;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Supplier;

public class SingleTaskWorker<T> implements SingleTask<T> {
    private final ConcurrentMap<CompletableFuture<T>, Void> futureMap;
    private final BlockingQueue<T> queue;
    private volatile boolean signal;

    public SingleTaskWorker(ConcurrentMap<CompletableFuture<T>, Void> map, BlockingQueue<T> queue) {
        this.futureMap = map;
        this.queue = queue;
    }

    public SingleTaskWorker() {
        this.futureMap = new ConcurrentHashMap<>();
        this.queue = new LinkedBlockingQueue<>();
    }

    @Override
    public void add(CompletableFuture<T> future) {
        futureMap.put(future, null);
        future.thenAccept(e -> {
            queue.add(e);
            futureMap.remove(future);
        });
    }

    @Override
    public void add(Supplier<T> supplier, Executor executor) {
        add(CompletableFuture.supplyAsync(supplier, executor));
    }

    @Override
    public void addAll(List<CompletableFuture<T>> futures) {
        futures.forEach(this::add);
    }

    @Override
    public T take() throws InterruptedException {
        return queue.take();
    }

    @Override
    public T poll() {
        return queue.poll();
    }

    @Override
    public T remove() {
        return queue.remove();
    }

    @Override
    public int count() {
        return futureMap.size();
    }

    @Override
    public void addListener(TaskListener<T> listener, ExecutorService executor) {
        executor.submit(() -> {
            try {
                while (signal) {
                    listener.handle(queue.take());
                }
            } catch (InterruptedException e) {
                listener.catchException(e);
            }
        });
    }

    @Override
    public void close() throws IOException {
        signal = false;
    }
}
