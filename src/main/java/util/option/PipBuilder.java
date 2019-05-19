package util.option;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class PipBuilder<T, E> {
    private Supplier<Consumer<T>> pipGetter;
    private Supplier<T> source;
    private Sjark<E> sjark;

    public static <R> PipBuilder<R, R> withNullSource() {
        PipBuilder<R, R> builder = new PipBuilder<>();
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

    public <R> PipBuilder<T, E> _if(Predicate<E> predicate, Function<PipBuilder<E, E>, PipBuilder<E, R>> then, Function<PipBuilder<E, E>, PipBuilder<E, R>> orElse) {
        Sjark<E> sjark = new Sjark<>();
        sjark._if(predicate)
                ._then(then.apply(withNullSource()).pipGetter.get())
                ._else(orElse.apply(withNullSource()).pipGetter.get());
        return _return(sjark.getPip());
    }

    public PipBuilder<T, E> _else(SjarkIf<E> sjarkIf) {
//        return of(this, sj -> sj._if(sjarkIf)._else());
        return null;
    }

    public PipBuilder<T, E> _while(SjarkIf<E> sjarkIf) {
        return null;
    }

    public PipBuilder<T, E> _return(SjarkIf<E> sjarkIf) {
//        this.sjark._goto(sjarkIf);
//        return _else(sjarkIf);
        return null;
    }

    public PipBuilder<T, E> _return(Consumer<E> consumer) {
        return of(this, sj -> sj._return(consumer));
    }
}
