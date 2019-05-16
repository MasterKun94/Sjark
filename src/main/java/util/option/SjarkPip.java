package util.option;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class SjarkPip<E, R> {
    public SjarkPip(Supplier<E> supplier) {
        this.supplier = supplier;
    }

    private Consumer<E> consumer;

    private SjarkPip<R, ?> nextPip;

    private Supplier<E> supplier;

    private Function<E, R> mapper;

    public Consumer<Supplier<E>> getConsumer() {
        return supplier -> nextPip.getConsumer().accept(() -> mapper.apply(supplier.get()));
    }

    public SjarkPip<R, ?> map(Function<? super E, ? extends R> mapper) {
        Supplier<R> newSupplier = () -> mapper.apply(supplier.get());
        nextPip = new SjarkPip<>(newSupplier);
        return nextPip;
    }
}
