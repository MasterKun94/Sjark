package util.option;

import java.util.function.Consumer;
import java.util.function.Predicate;

class SjarkIf<E> {

    private Predicate<? super E> ifElse;
//    private Consumer<E> thisConsumer;
//    private Consumer<E> thatConsumer;

    private Sjark<E> thisSjark;
    private Sjark<E> thatSjark;

    SjarkIf(Predicate<? super E> predicate) {
        ifElse = predicate;
        thisSjark = new Sjark<>();
        thatSjark = new Sjark<>();
    }

    SjarkIf<E> _then(Consumer<E> thisConsumer) {
        thisSjark.setPip(thisConsumer);
        return this;
    }

    SjarkIf<E> _else(Consumer<E> thatConsumer) {
        thatSjark.setPip(thatConsumer);
        return this;
    }

    Consumer<E> getPip() {
        return e -> (ifElse.test(e) ? thisSjark.getPip() : thatSjark.getPip()).accept(e);//TODO
    }

    Sjark<E> getThatSjark() {
        return thatSjark;
    }
}
