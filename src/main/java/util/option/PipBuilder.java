package util.option;

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

    private static <T, R, E> PipBuilder<T, R> with(PipBuilder<T, E> builder, Function<Sjark<E>, Sjark<R>> sjarkMapper) {
        PipBuilder<T, R> newBuilder = new PipBuilder<>();
        newBuilder.pipGetter = builder.pipGetter;
        newBuilder.sjark = sjarkMapper.apply(builder.sjark);

        return newBuilder;
    }

    public <R> PipBuilder<T, R> _do_____(Function<E, R> mapper) {
        return with(this, sj -> sj._do(mapper));
    }

    public Process.If<T, E> _if_____(Predicate<E> predicate) {
        SjarkIf<E> newIf = this.sjark._if(predicate);
        return new Process.If<>(newIf, pipGetter);
    }

    public PipBuilder<T, E> _while_(SjarkIf<E> sjarkIf) {
        return null;
    }

    public Process.End<T> _return_(Consumer<E> consumer) {
        PipBuilder<T, E> pipBuilder = with(this, sj -> sj._return(consumer));
        return new Process.End<>(pipBuilder.pipGetter);
    }
}
