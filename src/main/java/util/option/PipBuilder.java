package util.option;

import util.option.Process.*;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class PipBuilder<T, E> {
    private Supplier<Consumer<T>> pipGetter;
    private Sjark<E> sjark;

    public static <R> PipBuilder<R, R> start() {
        PipBuilder<R, R> builder = new PipBuilder<>();
        builder.sjark = new Sjark<>();
        builder.pipGetter = builder.sjark::getPip;
        return builder;
    }

    public static <R, P> PipBuilder<R, P> start(Supplier<Consumer<R>> pipGetter, Sjark<P> sjark) {
        PipBuilder<R, P> builder = new PipBuilder<>();
        builder.sjark = sjark;
        builder.pipGetter = pipGetter;
        return builder;
    }

    private static <T, R, E> PipBuilder<T, R> with(PipBuilder<T, E> builder, Function<Sjark<E>, Sjark<R>> sjarkMapper) {
        PipBuilder<T, R> newBuilder = new PipBuilder<>();
        newBuilder.pipGetter = builder.pipGetter;
        newBuilder.sjark = sjarkMapper.apply(builder.sjark);
        return newBuilder;
    }

    public <R> PipBuilder<T, R> map(Function<E, R> mapper) {
        return with(this, sj -> sj._do(mapper));
    }

    public If<T, E> _if(Predicate<E> predicate) {
        SjarkIf<E> newIf = this.sjark._if(predicate);
        return new If<>(newIf, pipGetter);
    }

    public While<T, E> _while(SjarkIf<E> sjarkIf) {
        return null;
    }

    public <R> PipBuilder<T, R> _do(Function<PipBuilder<T, E>, While<T, R>> doWhile) {
        return null;
    }

    public End<T> _return(Consumer<E> consumer) {
        PipBuilder<T, E> pipBuilder = with(this, sj -> sj._return(consumer));
        return new End<>(pipBuilder.pipGetter);
    }
}
