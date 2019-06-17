package collectionUtil;

import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;

public interface Collector<E> {
     Collection<E> get();

     <R> Collector<R> map(Function<? super E, ? extends R> mapper);

     <K, V> TupleCollector<K, V> mapToTuple(
             Function<? super E, ? extends K> keyMapper,
             Function<? super E, ? extends V> valueMapper);

     <R> Collector<R> flatMap(Function<? super E, ? extends Collection<? extends R>> flatMapper);

     Collector<E> filter(Predicate<? super E> predicate);

     Collector<E> filterNot(Predicate<? super E> predicate);

     boolean forAll(Predicate<? super E> predicate);

     boolean exists(Predicate<? super E> predicate);

     int count(Predicate<? super E> predicate);

     E fold(E init, BinaryOperator<E> operator);

     <R> R reduce(R init, BiFunction<? super R, ? super E, ? extends R> accumulator);

     <K> Collector<E> foldBy(Function<? super E, ? extends K> key, BinaryOperator<E> accumulator);

     <K, R> Collector<R> reduceBy(
             Function<? super E, ? extends K> keyGetter,
             Function<? super E, ? extends R> mapper,
             BiFunction<? super R, ? super E, ? extends R> accumulator);

     <K> Collector<List<E>> groupBy(Function<? super E, ? extends K> key);

     static <E> Collector<E> of(Collection<E> list) {
          return new ListCollector<>(list);
     }
}
