package util.option;

import com.sun.xml.internal.bind.v2.TODO;
import util.option.Process.*;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class Piper<T, E> {
    private Sjark<T> headSjark;
    private Sjark<E> tailSjark;

    public static <R> Piper<R, R> start() {
        Piper<R, R> piper = new Piper<>();
        piper.tailSjark = new Sjark<>();
        piper.headSjark = piper.tailSjark;
        return piper;
    }

    public static <R, P> Piper<R, P> start(Sjark<R> headSjark, Sjark<P> tailSjark) {
        Piper<R, P> piper = new Piper<>();
        piper.tailSjark = tailSjark;
        piper.headSjark = headSjark;
        return piper;
    }

    private static <T, R, E> Piper<T, R> with(Piper<T, E> builder, Function<Sjark<E>, Sjark<R>> sjarkMapper) {
        Piper<T, R> newPiper = new Piper<>();
        newPiper.headSjark = builder.headSjark;
        newPiper.tailSjark = sjarkMapper.apply(builder.tailSjark);
        return newPiper;
    }

    public <R> Piper<T, R> map(Function<E, R> mapper) {
        return with(this, sj -> sj._do(mapper));
    }

    public <R> Piper<T, R> ref(Piper<E, R> quotePip) {//TODO
        this.tailSjark._goto(quotePip.headSjark);
        return start(this.headSjark, quotePip.tailSjark);
    }

    public Piper<T, E> annotate(Consumer<E> annotate) {
        return with(this, sjark -> sjark._declare(annotate));
    }

    public If<T, E> _if(Predicate<E> predicate) {
        SjarkIf<E> newIf = this.tailSjark._if(predicate);
        return new If<>(newIf, headSjark);
    }

    public While<T, E> _while(Predicate<E> predicate) {
        SjarkIf<E> newIf = this.tailSjark._if(predicate);
        return new While<>(newIf, headSjark);
    }

    public Piper<T, E> _index() {
        return null;
    }

    public <R> Piper<T, R> _goto() {
        return null;
    }

    public End<T> _return(Consumer<E> consumer) {
        Piper<T, E> piper = with(this, sj -> sj._return(consumer));
        return new End<>(piper.headSjark);
    }
}
