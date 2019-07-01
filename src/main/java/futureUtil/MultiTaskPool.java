package futureUtil;

import java.io.Closeable;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;

/**
 * @param <T>
 */
public interface MultiTaskPool<T> extends Closeable {
    /**
     *
     *
     * @param future
     * @param taskId
     */
    void add(CompletableFuture<T> future, String taskId);

    /**
     * @param supplier
     * @param tackId
     * @param executor
     */
    void add(Supplier<T> supplier, String tackId, Executor executor);

    /**
     * @param taskId
     * @return
     * @throws InterruptedException
     */
    T take(String taskId) throws InterruptedException;

    /**
     * @param taskId
     * @return
     */
    T poll(String taskId);

    /**
     * @param taskId
     * @return
     */
    T remove(String taskId);

    /**
     * @param taskId
     * @return
     */
    int count(String taskId);

    /**
     * @return
     */
    int countTask();

    /**
     * @return
     */
    Set<String> taskIdSet();

    /**
     * @param listener
     * @param taskId
     * @param executor
     */
    void addListener(TaskListener<T> listener, String taskId, ExecutorService executor);

    /**
     * @param listener
     * @param taskId
     * @param executor
     * @param threadNumber
     */
    void addListener(TaskListener<T> listener, String taskId, ExecutorService executor, int threadNumber);

}
