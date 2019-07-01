package futureUtil;

import java.io.Closeable;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;

/**
 * @param <T>
 */
public interface SingleTaskPool<T> extends Closeable {
    /**
     * add an async task to this taskPool after the task is done the return value
     * will be pushed into a blocking queue and can be got by method {@code take()} or
     * {@code poll()}
     *
     * @param future the task that be added into the pool
     */
    void add(CompletableFuture<T> future);

    /**
     * add an async task to this taskPool after the task is done the return value
     * will be pushed into a blocking queue and can be got by method {@code take()} or
     * {@code poll()}
     *
     * @param supplier a function returning the value to be used to complete the
     *                 returned CompletableFuture
     * @param executor the executor to use for asynchronous execution
     */
    void add(Supplier<T> supplier, Executor executor);

    /**
     * @param futures
     */
    void addAll(List<CompletableFuture<T>> futures);

    /**
     * @return
     * @throws InterruptedException
     */
    T take() throws InterruptedException;

    /**
     * @return
     */
    T poll();

    /**
     * @return
     */
    T remove();

    /**
     * @return
     */
    int count();

    /**
     * @param listener
     * @param executor
     */
    void addListener(TaskListener<T> listener, ExecutorService executor);

    /**
     * @param listener
     * @param executor
     * @param threadNumber
     */
    void addListener(TaskListener<T> listener, ExecutorService executor, int threadNumber);
}
