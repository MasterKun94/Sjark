package util.option;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class SjarkIf<E> {

    private Predicate<? super E> ifElse;
    private Sjark<E> thisSjark;
    private Sjark<E> thatSjark;

    public SjarkIf(Predicate<? super E> predicate) {
        ifElse = predicate;
        thisSjark = new Sjark<>();
        thatSjark = new Sjark<>();
    }

    public Sjark<E> _then() {
        return thatSjark;
    }

    public Sjark<E> _else() {
        return thatSjark;
    }

    public Consumer<E> getPip() {
        return e -> (ifElse.test(e) ? thisSjark.getPip() : thatSjark.getPip()).accept(e);//TODO
    }
}
