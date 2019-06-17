package futureUtil;

import pool.BlockingPool;

import java.util.ArrayDeque;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.*;
import java.util.function.Supplier;

public class MultiTask<T> {
    private ConcurrentMap<CompletableFuture<T>, String> futureMap;
    private ConcurrentMap<String, BlockingQueue<T>> queueMap;

    public MultiTask(ConcurrentMap<CompletableFuture<T>, String> futureMap, ConcurrentMap<String, BlockingQueue<T>> queueMap) {
        this.futureMap = futureMap;
        this.queueMap = queueMap;
    }

    public MultiTask() {
        this.futureMap = new ConcurrentHashMap<>();
        this.queueMap = new ConcurrentHashMap<>();
    }

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

    public void add(Supplier<T> supplier, String tackId, Executor executor) {
        add(CompletableFuture.supplyAsync(supplier, executor), tackId);
    }

    public T take(String taskId) throws InterruptedException {

        BlockingQueue<T> queue = queueMap.get(taskId);
        if (queue == null) {
            queue = new LinkedBlockingQueue<>();
            queueMap.put(taskId, queue);
        }
        return queue.take();
    }

    public T poll(String taskId) {
        BlockingQueue<T> queue = queueMap.get(taskId);
        return queue == null ? null : queue.poll();
    }

    public T remove(String taskId) {
        BlockingQueue<T> queue = queueMap.get(taskId);
        if (queue == null) {
            throw new IllegalArgumentException("Task not exist!");
        } else {
            return queue.remove();
        }
    }

    public int count(String taskId) {
        BlockingQueue<T> queue = queueMap.get(taskId);
        return queue == null ? 0 : queue.size();
    }

    public int countTask() {
        return futureMap.size();
    }

    public Set<String> taskIdSet() {
        return queueMap.keySet();
    }
}
