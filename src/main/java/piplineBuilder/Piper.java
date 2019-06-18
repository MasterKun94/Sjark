package piplineBuilder;

import piplineBuilder.sjark.Sjark;

import java.util.Collection;
import java.util.concurrent.Executor;
import java.util.function.*;

public interface Piper<T, E> {
    Sjark<T> getHeadSjark();

    Sjark<E> getTailSjark();

    <R> Piper<T, R> map(Function<? super E, ? extends R> mapper);

    <R> Piper<T, R> flatMap(Function<? super E, ? extends Collection<? extends R>> flatMapper);

    <R> Piper<T, R> ref(Piper<E, R> quotePip);

    <R, P> Piper<T, P> apply(
            Supplier<? extends R> supplier,
            BiFunction<? super E, ? super R, ? extends P> biFunction);

    <R, P> Piper<T, P> applyAsync(
            Supplier<? extends R> supplier,
            BiFunction<? super E, ? super R, ? extends P> biFunction,
            Executor executor);

    Piper<T, E> annotate(Consumer<? super E> annotate);

    Process.If<T, E> _if(Predicate<? super E> predicate);

    Process.While<T, E> _while(Predicate<? super E> predicate);

    Piper<T, E> _index();

    <R> Piper<T, R> _goto();

    Process.End<T> _return(Consumer<E> consumer);

}
