package futureUtil;

import java.util.List;
import java.util.concurrent.*;
import java.util.function.Supplier;

public class SingleTask<T> {
    private ConcurrentMap<CompletableFuture<T>, Void> futureMap;
    private BlockingQueue<T> queue;

    public SingleTask(ConcurrentMap<CompletableFuture<T>, Void> map, BlockingQueue<T> queue) {
        this.futureMap = map;
        this.queue = queue;
    }

    public SingleTask() {
        this.futureMap = new ConcurrentHashMap<>();
        this.queue = new LinkedBlockingQueue<>();
    }

    public void add(CompletableFuture<T> future) {
        futureMap.put(future, null);
        future.thenAccept(e -> {
            queue.add(e);
            futureMap.remove(future);
        });
    }

    public void add(Supplier<T> supplier, Executor executor) {
        add(CompletableFuture.supplyAsync(supplier, executor));
    }

    public void addAll(List<CompletableFuture<T>> futures) {
        futures.forEach(this::add);
    }

    public T take() throws InterruptedException {
        return queue.take();
    }

    public T poll() {
        return queue.poll();
    }

    public T remove() {
        return queue.remove();
    }

    public int count() {
        return futureMap.size();
    }
}
