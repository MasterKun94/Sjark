package util.option;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class Sjark<E> {

    public Sjark(Supplier<E> supplier) {
        this.supplier = supplier;
    }

    private Consumer<E> consumer;

    private Supplier<E> supplier;

    public <R> Sjark<R> thenDo(Function<? super E, ? extends R> mapper) {

        Supplier<R> newSupplier = () -> mapper.apply(supplier.get());
        Sjark<R> newJsark = new Sjark<>(newSupplier);
        consumer = e -> newJsark.consumer.accept(mapper.apply(e));
        return newJsark;
    }

    public SjarkIf<E> ifThis(Predicate<? super E> predicate) {
        return new SjarkIf<>(predicate, supplier);
    }
}
