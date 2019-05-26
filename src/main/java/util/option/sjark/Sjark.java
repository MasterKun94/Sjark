package util.option.sjark;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class Sjark<E> {

    private Consumer<E> pip;

    public Consumer<E> getPip() {
        return pip;
    }

    public void setPip(Consumer<E> pip) {
        this.pip = pip;
    }

    public Sjark<E> _return(Consumer<E> sink) {
        pip = sink;
        return this;
    }

    public Sjark<E> _declare(Consumer<? super E> doer) {
        Sjark<E> nextSjark = new Sjark<>();
        pip = e -> {
            doer.accept(e);
            nextSjark.pip.accept(e);
        };
        return nextSjark;
    }

    public <R> Sjark<R> _map(Function<? super E, ? extends R> mapper) {
        Sjark<R> nextSjark = new Sjark<>();
        pip = e -> nextSjark.pip.accept(mapper.apply(e));
        return nextSjark;
    }

    public <R> Sjark<R> _flatMap(Function<? super E, ? extends Collection<? extends R>> mapper) {
        Sjark<R> nextSjark = new Sjark<>();
        pip = e -> mapper.apply(e).forEach(r -> nextSjark.pip.accept(r));
        return nextSjark;
    }

    public SjarkIf<E> _if(Predicate<? super E> predicate) {
        SjarkIf<E> sjarkIf = new SjarkIf<>(predicate);
        pip = e -> sjarkIf.getPip().accept(e);
        return sjarkIf;
    }

    public Sjark<E> _index(Sjark<E> sjark) {
        sjark.pip = e -> pip.accept(e);
        return sjark;
    }

    public Sjark<E> _goto(Sjark<E> sjark) {
        pip = sjark.pip;
        return this;
    }
}
