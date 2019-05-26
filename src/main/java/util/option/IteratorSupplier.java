package util.option;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

public class IteratorSupplier<P> implements Supplier<P> {
    private CompletableFuture<P> future;
    private Supplier<P> supplier;

    public IteratorSupplier(Supplier<P> supplier, Executor executor) {
        this.supplier = supplier;
        future = CompletableFuture.supplyAsync(supplier, executor);
    }

    @Override
    public P get() {
        P p;
        try {
            p = future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        future = future.thenApply(p1 -> supplier.get());
        return p;
    }
}
