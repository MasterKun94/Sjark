package util.option;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class PipBuilder<T, E> {
    private List<Sjark> sjarkStack;
    private List<SjarkIf> sjarkIfStack;
    private Supplier<Consumer<T>> pipGetter;
    private Supplier<T> source;
    private Sjark<E> sjark;

    public static <R> PipBuilder<R, R> of(Supplier<R> supplier) {
        PipBuilder<R, R> builder = new PipBuilder<>();
        builder.source = supplier;
        builder.sjark = new Sjark<>();
        builder.pipGetter = builder.sjark::getPip;
        return builder;
    }

    private static <T, R, E> PipBuilder<T, R> of(PipBuilder<T, E> builder, Function<Sjark<E>, Sjark<R>> sjarkMapper) {
        PipBuilder<T, R> newBuilder = new PipBuilder<>();
        newBuilder.pipGetter = builder.pipGetter;
        newBuilder.source = builder.source;
        newBuilder.sjark = sjarkMapper.apply(builder.sjark);
        return newBuilder;
    }

    public <R> PipBuilder<T, R> _do(Function<E, R> mapper) {
        return of(this, sj -> sj._do(mapper));
    }

    public PipBuilder<T, E> _if(SjarkIf<E> sjarkIf) {
        return of(this, sj -> sj._if(sjarkIf)._then());
    }

    public PipBuilder<T, E> _else(SjarkIf<E> sjarkIf) {
        return of(this, sj -> sj._if(sjarkIf)._else());
    }

    public PipBuilder<T, E> _while(SjarkIf<E> sjarkIf) {
        return _if(sjarkIf);
    }

    public PipBuilder<T, E> _return(SjarkIf<E> sjarkIf) {
        this.sjark._goto(sjarkIf);
        return _else(sjarkIf);
    }

    public PipBuilder<T, E> _return(Consumer<E> consumer) {
        return null;
    }
}
