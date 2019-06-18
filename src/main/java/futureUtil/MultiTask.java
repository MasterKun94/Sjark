package futureUtil;

import java.io.Closeable;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;

public interface MultiTask<T> extends Closeable {
    void add(CompletableFuture<T> future, String taskId);

    void add(Supplier<T> supplier, String tackId, Executor executor);

    T take(String taskId) throws InterruptedException;

    T poll(String taskId);

    T remove(String taskId);

    int count(String taskId);

    int countTask();

    Set<String> taskIdSet();

    void addListener(TaskListener<T> listener, String taskId, ExecutorService executor);

    void close() throws IOException;
}
