package futureUtil;

import java.io.Closeable;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;

public interface SingleTask<T> extends Closeable {
    void add(CompletableFuture<T> future);

    void add(Supplier<T> supplier, Executor executor);

    void addAll(List<CompletableFuture<T>> futures);

    T take() throws InterruptedException;

    T poll();

    T remove();

    int count();

    void addListener(TaskListener<T> listener, ExecutorService executor);
}
