package util;

import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

interface Traversable<E> {
     <R> Traversable<R> map(Function<? super E, ? extends R> mapper);

     <R> Traversable<R> flatMap(Function<? super E, ? extends Collection<? extends R>> flatMapper);

     Traversable<E> filter(Predicate<? super E> filter);

     Traversable<E> filterNot(Predicate<? super E> filter);

     Traversable<E> takeWhile(Predicate<? super E> filter);

     Traversable<E> dropWhile(Predicate<? super E> filter);

     Traversable<E> take(int n);

     Traversable<E> drop(int n);

     boolean forAll(Predicate<? super E> filter);

     boolean exists(Predicate<? super E> filter);

     int count(Predicate<? super E> filter);

     E foldLeft(E e, BiFunction<E, E, E> function);

     E foldRight(E e, BiFunction<E, E, E> function);

     <R> R foldLeft(E e, Function<E, R> er, BiFunction<E, R, R> err);

     <R> R foldRight(E e, Function<E, R> er, BiFunction<E, R, R> err);

     E reduceLeft(BiFunction<E, E, E> function);

     E reduceRight(BiFunction<E, E, E> function);

     <R> R reduceLeft(Function<E, R> er, BiFunction<E, R, R> err);

     <R> R reduceRight(Function<E, R> er, BiFunction<E, R, R> err);

}
