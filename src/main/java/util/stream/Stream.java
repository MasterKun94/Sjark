package util.stream;

import java.util.Collection;
import java.util.function.*;

public interface Stream<E> {
    void sink(Consumer<? super E> collector);

    <R> Stream<R> streamMap(Function<? super E, ? extends Stream<? extends R>> mapper);

    <R> Stream<R> map(Function<? super E, ? extends R> mapper);

    <R> Stream<R> flatMap(Function<? super E, ? extends Collection<? extends R>> flatMapper);

    Stream<E> filter(Predicate<? super E> filter);

    Stream<E> filterNot(Predicate<? super E> filter);

    @Deprecated
    Stream<E> takeWhile(Predicate<? super E> filter);

    Stream<E> dropWhile(Predicate<? super E> filter);

    @Deprecated
    Stream<E> take(int n);

    Stream<E> drop(int n);

    boolean forAll(Predicate<? super E> filter);

    @Deprecated
    boolean exists(Predicate<? super E> filter);

    int count(Predicate<? super E> filter);

    <R> R fold(R r, BiFunction<R, ? super E, R> accumulator);

    <R> R reduce(BiFunction<R, ? super E, R> err);
}
