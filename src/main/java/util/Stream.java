package util;

import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

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

    E fold(E e, BiFunction<E, E, E> function);

    <R> R fold(E e, Function<E, R> er, BiFunction<E, R, R> err);

    E reduce(BiFunction<E, E, E> function);

    <R> R reduce(Function<E, R> er, BiFunction<E, R, R> err);

}
