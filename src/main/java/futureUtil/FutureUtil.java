package futureUtil;

import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class FutureUtil<T> {
    ConcurrentHashMap<CompletableFuture<T>, Integer> futureList;
    BlockingQueue<T> queue = new LinkedBlockingQueue<>();

    public void add(CompletableFuture<T> future) {
        futureList.put(future, 1);
        future.thenAccept(e -> {
            queue.add(e);
            futureList.remove(future);
        });
    }

    public void addAll(List<CompletableFuture<T>> futures) {
        futures.forEach(this::add);
    }

    public void get() {

    }

    public boolean isAllDone() {
        return futureList.isEmpty();
    }

    public static void main(String[] args) {
    }
}
