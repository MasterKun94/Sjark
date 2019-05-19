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

    private static <T, R, E> PipBuilder<T, R> with(PipBuilder<T, E> builder, Function<Sjark<E>, Sjark<R>> sjarkMapper) {
        PipBuilder<T, R> newBuilder = new PipBuilder<>();
        newBuilder.pipGetter = builder.pipGetter;
        newBuilder.source = builder.source;
        newBuilder.sjark = sjarkMapper.apply(builder.sjark);

        return newBuilder;
    }

    public <R> PipBuilder<T, R> _do(Function<E, R> mapper) {
        return with(this, sj -> sj._do(mapper));
    }

    public IfBuilder<E> _if(
            Predicate<E> predicate
    ) {
        IfBuilder<E> ifBuilder = new IfBuilder<>();
        ifBuilder.setSjarkIf(new SjarkIf<>(predicate));
        return ifBuilder;
    }

    public PipBuilder<T, E> _while(SjarkIf<E> sjarkIf) {
        return null;
    }

    public void  _return(Consumer<E> consumer) {
        with(this, sj -> sj._return(consumer));
    }

    private class IfBuilder<P> {
        private SjarkIf<P> sjarkIf;
        private  <R> IfBuilder<P> _then(Function<PipBuilder<P, P>, PipBuilder<P, R>> then) {
            sjarkIf._then(then.apply(withNullSource()).pipGetter.get());
            IfBuilder<P> ifBuilder = new IfBuilder<>();
            ifBuilder.setSjarkIf(sjarkIf);
            return ifBuilder;
        }

        private  <R> IfBuilder<P> _else(Function<PipBuilder<P, P>, PipBuilder<P, R>> then) {
            sjarkIf._else(then.apply(withNullSource()).pipGetter.get());
            IfBuilder<P> ifBuilder = new IfBuilder<>();
            ifBuilder.setSjarkIf(sjarkIf);
            return ifBuilder;
        }

        private void setSjarkIf(SjarkIf<P> sjarkIf) {
            this.sjarkIf = sjarkIf;
        }
    }
}
