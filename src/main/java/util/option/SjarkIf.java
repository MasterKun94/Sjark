package util.option;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class SjarkIf<E> {

    private Predicate<? super E> ifElse;
    private Consumer<E> thisConsumer;
    private Consumer<E> thatConsumer;

    public SjarkIf(Predicate<? super E> predicate) {
        ifElse = predicate;
    }

    public SjarkIf<E> _then(Consumer<E> thisConsumer) {
        this.thisConsumer = thisConsumer;
        return this;
    }

    public SjarkIf<E> _else(Consumer<E> thatConsumer) {
        this.thatConsumer = thatConsumer;
        return this;
    }

    public Consumer<E> getPip() {
        return e -> (ifElse.test(e) ? thisConsumer : thatConsumer).accept(e);//TODO
    }
}
