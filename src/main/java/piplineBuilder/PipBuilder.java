package piplineBuilder;

import java.util.Collection;
import java.util.concurrent.*;
import java.util.function.*;

public class PipBuilder<T, E> implements Piper<T, E> {
    private Flange<T> headFlange;
    private Flange<E> tailFlange;

    public static <R> Piper<R, R> start() {
        PipBuilder<R, R> piper = new PipBuilder<>();
        piper.tailFlange = new Flange<>();
        piper.headFlange = piper.tailFlange;
        return piper;
    }

    static <R, P> Piper<R, P> start(Flange<R> headFlange, Flange<P> tailFlange) {
        PipBuilder<R, P> piper = new PipBuilder<>();
        piper.tailFlange = tailFlange;
        piper.headFlange = headFlange;
        return piper;
    }

    private static <T, R, E> Piper<T, R> with(
            PipBuilder<T, E> builder,
            Function<Flange<E>, Flange<R>> sjarkMapper) {
        PipBuilder<T, R> newPiper = new PipBuilder<>();
        newPiper.headFlange = builder.headFlange;
        newPiper.tailFlange = sjarkMapper.apply(builder.tailFlange);
        return newPiper;
    }

    @Override
    public Flange<T> getHeadFlange() {
        return headFlange;
    }

    @Override
    public Flange<E> getTailFlange() {
        return tailFlange;
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
        this.tailFlange._goto(quotePip.getHeadFlange());
        return start(this.headFlange, quotePip.getTailFlange());
    }

    @Override
    public <R, P> Piper<T, P> apply(
            Supplier<? extends R> supplier,
            BiFunction<? super E, ? super R, ? extends P> biFunction)
    {
        BiFunction<E, Supplier<? extends R>, P> newFunction = (e, s) -> biFunction.apply(e, s.get());
        return with(this, sjark -> sjark._map(e -> newFunction.apply(e, supplier)));
    }

    @Override
    public <R, P> Piper<T, P> applyAsync(
            Supplier<? extends R> supplier,
            BiFunction<? super E, ? super R, ? extends P> biFunction,
            Executor executor)
    {
        IteratorSupplier<? extends R> iteratorSupplier = new IteratorSupplier<>(supplier, executor);
        BiFunction<E, Supplier<? extends R>, P> asyncFunction = (e, supplier1) -> biFunction.apply(e, supplier.get());
        return with(this, sjark -> sjark._map(e -> asyncFunction.apply(e, iteratorSupplier)));
    }

    @Override
    public Piper<T, E> annotate(Consumer<? super E> annotate) {
        return with(this, sjark -> sjark._declare(annotate));
    }

    @Override
    public Process.If<T, E> _if(Predicate<? super E> predicate) {
        TrFlange<E> newIf = this.tailFlange._if(predicate);
        return new Process.If<>(newIf, headFlange);
    }

    @Override
    public Process.While<T, E> _while(Predicate<? super E> predicate) {
        TrFlange<E> newIf = this.tailFlange._if(predicate);
        return new Process.While<>(newIf, headFlange);
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
    public Process.End<T> _return(Consumer<E> consumer) {
        Piper<T, E> piper = with(this, sjark -> sjark._return(consumer));
        return new Process.End<>(piper.getHeadFlange());
    }
}
