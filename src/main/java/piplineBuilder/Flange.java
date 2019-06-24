package piplineBuilder;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class Flange<E> {

    private Consumer<E> pip;

    public Consumer<E> getPip() {
        return pip;
    }

    public void setPip(Consumer<E> pip) {
        this.pip = pip;
    }

    public Flange<E> _return(Consumer<E> sink) {
        pip = sink;
        return this;
    }

    public Flange<E> _declare(Consumer<? super E> doer) {
        Flange<E> nextFlange = new Flange<>();
        pip = e -> {
            doer.accept(e);
            nextFlange.pip.accept(e);
        };
        return nextFlange;
    }

    public <R> Flange<R> _map(Function<? super E, ? extends R> mapper) {
        Flange<R> nextFlange = new Flange<>();
        pip = e -> nextFlange.pip.accept(mapper.apply(e));
        return nextFlange;
    }

    public <R> Flange<R> _flatMap(Function<? super E, ? extends Collection<? extends R>> mapper) {
        Flange<R> nextFlange = new Flange<>();
        pip = e -> mapper.apply(e).forEach(r -> nextFlange.pip.accept(r));
        return nextFlange;
    }

    public TrFlange<E> _if(Predicate<? super E> predicate) {
        TrFlange<E> trFlange = new TrFlange<>(predicate);
        pip = e -> trFlange.getPip().accept(e);
        return trFlange;
    }

    public Flange<E> _index(Flange<E> flange) {
        flange.pip = e -> pip.accept(e);
        return flange;
    }

    public Flange<E> _goto(Flange<E> flange) {
        pip = flange.pip;
        return this;
    }
}
