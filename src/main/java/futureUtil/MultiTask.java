package futureUtil;

import java.util.NoSuchElementException;
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
        futureMap.put(future, null);
        future.thenAccept(e -> {
            BlockingQueue<T> queue = queueMap.get(taskId);
            if (queue == null) {
                queue = new LinkedBlockingQueue<>();
                queue.add(e);
                queueMap.put(taskId, queue);
            } else {
                queue.add(e);
            }
            futureMap.remove(future);
        });
    }

    public void add(Supplier<T> supplier, Executor executor, String tackId) {
        add(CompletableFuture.supplyAsync(supplier, executor), tackId);
    }

    public T take(String taskId) throws InterruptedException {
        createTask(taskId);
        return queueMap.get(taskId).take();
    }

    public T poll(String taskId) {
        BlockingQueue<T> queue = queueMap.get(taskId);
        return queue == null ? null : queue.poll();
    }

    public T remove(String taskId) {
        BlockingQueue<T> queue = queueMap.get(taskId);
        if (queue == null) {
            throw new NoSuchElementException();
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

    private void createTask(String taskId) {
        queueMap.putIfAbsent(taskId, new LinkedBlockingQueue<>());
    }
}
