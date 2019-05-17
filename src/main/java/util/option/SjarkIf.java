package util.option;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class SjarkIf<E> {

    private Predicate<? super E> ifElse;
    private Sjark<E> thisSjark;
    private Sjark<E> thatSjark;

    public SjarkIf(Predicate<? super E> predicate) {
        ifElse = predicate;
        thisSjark = new SjarkPip<>();
        thatSjark = new SjarkPip<>();
    }

    public Sjark<E> then() {
        return thatSjark;
    }

    public Sjark<E> orElse() {
        return thatSjark;
    }

    public Consumer<E> getPip() {
        return e -> (ifElse.test(e) ? thisSjark.getPip() : thatSjark.getPip()).accept(e);//TODO
    }
}
