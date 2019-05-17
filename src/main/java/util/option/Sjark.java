package util.option;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public interface Sjark<E> {

    Consumer<E> getPip();

    <R> Sjark<R> map(Function<? super E, ? extends R> mapper);

    Sjark<E> sink(Consumer<E> sink);

//    public Sjark(Supplier<E> supplier) {
//        this.supplier = supplier;
//    }
//
//    private Supplier<E> supplier;
//
//    public <R> Sjark<R> thenDo(Function<? super E, ? extends R> mapper) {
//        Supplier<R> newSupplier = () -> mapper.apply(supplier.get());
//        return new Sjark<>(newSupplier);
//    }
//
//    public SjarkIf<E> ifThis(Predicate<? super E> predicate) {
//        return new SjarkIf<>(predicate, supplier);
//    }
}
