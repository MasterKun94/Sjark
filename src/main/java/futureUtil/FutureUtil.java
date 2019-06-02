package futureUtil;

import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;

public class FutureUtil<T> {
    List<CompletableFuture<T>> futureList;
    BlockingQueue<T> queue = new LinkedBlockingQueue<>();

    public void add(CompletableFuture<T> future) {
        future.thenAccept(queue::add);
    }

    public void addAll(List<CompletableFuture<T>> futures) {
        futures.forEach(this::add);
    }

    public void get() {

    }

    public boolean isAllDone() {
        for (CompletableFuture<T> future : futureList) {
            if (!future.isDone()) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
    }
}
