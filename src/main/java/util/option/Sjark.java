package util.option;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

class Sjark<E> {

    private Consumer<E> pip;

    Consumer<E> getPip() {
        return pip;
    }

    Sjark<E> _return(Consumer<E> sink) {
        pip = sink;
        return this;
    }

    Sjark<E> _declare(Consumer<? super E> doer) {
        Sjark<E> nextSjark = new Sjark<>();
        pip = e -> {
            doer.accept(e);
            nextSjark.pip.accept(e);
        };
        return nextSjark;
    }

    <R> Sjark<R> _do(Function<? super E, ? extends R> mapper) {
        Sjark<R> nextSjark = new Sjark<>();
        pip = e -> nextSjark.pip.accept(mapper.apply(e));
        return nextSjark;
    }

    SjarkIf<E> _if(Predicate<E> predicate) {
        SjarkIf<E> sjarkIf = new SjarkIf<>(predicate);
        pip = e -> sjarkIf.getPip().accept(e);
        return sjarkIf;
    }

    Sjark<E> _index(Sjark<E> sjark) {
        sjark.pip = e -> pip.accept(e);
        return sjark;
    }

    Sjark<E> _goto(Sjark<E> sjark) {
        pip = sjark.pip;
        return this;
    }
}
