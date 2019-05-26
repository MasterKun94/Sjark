package util.option;

import util.option.Process.*;
import util.option.sjark.Sjark;
import util.option.sjark.SjarkIf;

import java.util.Collection;
import java.util.concurrent.*;
import java.util.function.*;

public class PipBuilder<T, E> implements Piper<T, E> {
    private Sjark<T> headSjark;
    private Sjark<E> tailSjark;

    public static <R> Piper<R, R> start() {
        PipBuilder<R, R> piper = new PipBuilder<>();
        piper.tailSjark = new Sjark<>();
        piper.headSjark = piper.tailSjark;
        return piper;
    }

    public static <R, P> Piper<R, P> start(Sjark<R> headSjark, Sjark<P> tailSjark) {
        PipBuilder<R, P> piper = new PipBuilder<>();
        piper.tailSjark = tailSjark;
        piper.headSjark = headSjark;
        return piper;
    }

    private static <T, R, E> Piper<T, R> with(PipBuilder<T, E> builder, Function<Sjark<E>, Sjark<R>> sjarkMapper) {
        PipBuilder<T, R> newPiper = new PipBuilder<>();
        newPiper.headSjark = builder.headSjark;
        newPiper.tailSjark = sjarkMapper.apply(builder.tailSjark);
        return newPiper;
    }

    @Override
    public Sjark<T> getHeadSjark() {
        return headSjark;
    }

    @Override
    public Sjark<E> getTailSjark() {
        return tailSjark;
    }

    @Override
    public <R> Piper<T, R> map(Function<? super E, ? extends R> mapper) {
        return with(this, sjark -> sjark._map(mapper));
    }

    @Override
    public <R> Piper<T, R> flatMap(Function<? super E, ? extends Collection<? extends R>> flatMapper) {
        return with(this, sjark -> sjark._flatMap(flatMapper));
    }

    @Override
    public <R> Piper<T, R> ref(Piper<E, R> quotePip) {
        this.tailSjark._goto(quotePip.getHeadSjark());
        return start(this.headSjark, quotePip.getTailSjark());
    }

    @Override
    public <R, P> Piper<T, P> apply(Supplier<? extends R> supplier, BiFunction<? super E, ? super R, ? extends P> biFunction) {
        BiFunction<E, Supplier<? extends R>, P> newFunction = (e, s) -> biFunction.apply(e, s.get());
        return with(this, sjark -> sjark._map(e -> newFunction.apply(e, supplier)));
    }

    @Override
    public <R, P> Piper<T, P> applyAsync(Supplier<? extends R> supplier, BiFunction<? super E, ? super R, ? extends P> biFunction, Executor executor) {
        IteratorSupplier<? extends R> iteratorSupplier = new IteratorSupplier<>(supplier, executor);
        BiFunction<E, Supplier<? extends R>, P> asyncFunction = (e, supplier1) -> biFunction.apply(e, supplier.get());
        return with(this, sjark -> sjark._map(e -> asyncFunction.apply(e, iteratorSupplier)));
    }

    @Override
    public Piper<T, E> annotate(Consumer<? super E> annotate) {
        return with(this, sjark -> sjark._declare(annotate));
    }

    @Override
    public If<T, E> _if(Predicate<? super E> predicate) {
        SjarkIf<E> newIf = this.tailSjark._if(predicate);
        return new If<>(newIf, headSjark);
    }

    @Override
    public While<T, E> _while(Predicate<? super E> predicate) {
        SjarkIf<E> newIf = this.tailSjark._if(predicate);
        return new While<>(newIf, headSjark);
    }

    @Override
    public Piper<T, E> _index() {
        return null;
    }

    @Override
    public <R> Piper<T, R> _goto() {
        return null;
    }

    @Override
    public End<T> _return(Consumer<E> consumer) {
        Piper<T, E> piper = with(this, sjark -> sjark._return(consumer));
        return new End<>(piper.getHeadSjark());
    }
}
