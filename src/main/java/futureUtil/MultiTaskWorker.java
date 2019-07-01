package futureUtil;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.*;
import java.util.function.Supplier;

public class MultiTaskWorker<T> implements MultiTaskPool<T> {
    private final ConcurrentMap<CompletableFuture<T>, String> futureMap;
    private final ConcurrentMap<String, BlockingQueue<T>> queueMap;
    private final Supplier<BlockingQueue<T>> queueGenerator;
    private volatile boolean signal = true;

    public MultiTaskWorker(
            ConcurrentMap<CompletableFuture<T>, String> futureMap,
            ConcurrentMap<String, BlockingQueue<T>> queueMap,
            Supplier<BlockingQueue<T>> queueGenerator) {
        this.futureMap = futureMap;
        this.queueMap = queueMap;
        this.queueGenerator = queueGenerator;
    }

    public MultiTaskWorker() {
        this.queueGenerator = LinkedBlockingQueue::new;
        this.futureMap = new ConcurrentHashMap<>();
        this.queueMap = new ConcurrentHashMap<>();
    }

    @Override
    public void add(CompletableFuture<T> future, String taskId) {
        futureMap.put(future, taskId);
        if (queueMap.containsKey(taskId)) {
            future.thenAccept(e -> {
                BlockingQueue<T> queue = queueMap.get(taskId);
                queue.add(e);
                futureMap.remove(future);
            });
        } else {
            throw new IllegalArgumentException("Task not exist!");
        }
    }

    @Override
    public void add(Supplier<T> supplier, String tackId, Executor executor) {
        add(CompletableFuture.supplyAsync(supplier, executor), tackId);
    }

    @Override
    public T take(String taskId) throws InterruptedException {
        return getOrInit(taskId).take();
    }

    @Override
    public T poll(String taskId) {
        return getOrInit(taskId).poll();
    }

    @Override
    public T remove(String taskId) {
        BlockingQueue<T> queue = queueMap.get(taskId);
        if (queue == null) {
            throw new IllegalArgumentException("Task not exist!");
        } else {
            return queue.remove();
        }
    }

    @Override
    public int count(String taskId) {
        BlockingQueue<T> queue = queueMap.get(taskId);
        return queue == null ? 0 : queue.size();
    }

    @Override
    public int countTask() {
        return futureMap.size();
    }

    @Override
    public Set<String> taskIdSet() {
        return queueMap.keySet();
    }

    @Override
    public void addListener(TaskListener<T> listener, String taskId, ExecutorService executor) {
        BlockingQueue<T> queue = getOrInit(taskId);
        executor.submit(() -> {
                try {
                    while (signal) listener.handle(queue.take());
                } catch (Exception e) {
                    listener.catchException(e);
                }
            });
    }

    @Override
    public void addListener(TaskListener<T> listener, String taskId, ExecutorService executor, int threadNumber) {
        for (int i = 0; i < threadNumber; i++) {
            addListener(listener, taskId, executor, threadNumber);
        }
    }


    @Override
    public void close() throws IOException {
        signal = false;
    }

    private BlockingQueue<T> getOrInit(String taskId) {
        BlockingQueue<T> queue = queueMap.get(taskId);
        if (queue == null) {
            queue = queueGenerator.get();
            queueMap.put(taskId, queue);
        }
        return queue;
    }
}
