package collectionUtil;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;

public interface Collector<E> {
     List<E> getList();

     <R> List<R> map(Function<? super E, ? extends R> mapper);

     <K, V> TupleCollector<K, V> mapToTuple(Function<? super E, Tuple<K, V>> mapper);

     <R> List<R> flatMap(Function<? super E, ? extends List<? extends R>> flatMapper);

     <K, V> TupleCollectorForArray<K, V> flatMapToTuple(Function<? super E, ? extends List<Tuple<K, V>>> flatMapper);

     List<E> filter(Predicate<? super E> predicate);

     List<E> filterNot(Predicate<? super E> predicate);

     List<E> takeWhile(Predicate<? super E> predicate);

     List<E> dropWhile(Predicate<? super E> predicate);

     List<E> take(int n);

     List<E> drop(int n);

     boolean forAll(Predicate<? super E> predicate);

     boolean exists(Predicate<? super E> predicate);

     int count(Predicate<? super E> predicate);

     E fold(E e, BinaryOperator<E> operator);

     <R> R reduce(R r, BiFunction<R, ? super E, R> biFunction);

     static <E> Collector<E> of(List<E> list) {
          return new CollectorForArray<>(list);
     }
}
