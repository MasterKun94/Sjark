package futureUtil;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

public class Tasks {
    public static <T> MultiTask<T> multiTask(
            ConcurrentMap<CompletableFuture<T>, String> futureMap,
            ConcurrentMap<String, BlockingQueue<T>> queueMap,
            Supplier<BlockingQueue<T>> queueGenerator)
    {
        return new MultiTaskWorker<>(futureMap, queueMap, queueGenerator);
    }

    public static <T> MultiTask<T> simpleMultiTask() {
        return new MultiTaskWorker<>();
    }

    public static <T> SingleTask<T> singleTask(
            ConcurrentMap<CompletableFuture<T>, Void> map,
            BlockingQueue<T> queue)
    {
        return new SingleTaskWorker<>(map, queue);
    }

    public static <T> SingleTask<T> simpleSingleTask() {
        return new SingleTaskWorker<>();
    }
}
