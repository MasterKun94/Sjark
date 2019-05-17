package util.option;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class SjarkIf<E> {

    public SjarkIf(Predicate<? super E> predicate, Supplier<E> sjarkSupplier) {
        this.predicate = predicate;
    }

    private Predicate<? super E> predicate;

    private SjarkPip<E> thisPip;

    private SjarkPip<E> thatPip;


    public Sjark<E> then() {
        return null;
    }

    public Sjark<E> orElse() {
        return null;
    }

    private Consumer<E> thisConsumer;

    private Consumer<E> thatConsumer;

    private Supplier<E> supplier;

    public Consumer<E> getConsumer() {
        return e -> (predicate.test(e) ? thisPip.getConsumer() : thatPip.getConsumer()).accept(e);//TODO
    }
}
