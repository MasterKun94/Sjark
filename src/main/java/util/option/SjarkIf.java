package util.option;

import java.util.function.Consumer;
import java.util.function.Predicate;

class SjarkIf<E> {

    private Predicate<? super E> ifElse;
    private Consumer<E> thisConsumer;
    private Consumer<E> thatConsumer;

    SjarkIf(Predicate<? super E> predicate) {
        ifElse = predicate;
    }

    SjarkIf<E> _then(Consumer<E> thisConsumer) {
        this.thisConsumer = thisConsumer;
        return this;
    }

    SjarkIf<E> _else(Consumer<E> thatConsumer) {
        this.thatConsumer = thatConsumer;
        return this;
    }

    Consumer<E> getPip() {
        return e -> (ifElse.test(e) ? thisConsumer : thatConsumer).accept(e);//TODO
    }
}
