package collectionUtil;

import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;

public interface TupleCollector<K, V> {


    TupleCollector<K, V> foldByKey(BinaryOperator<V> operator);

    <R> TupleCollector<K, R> reduceByKey(
            Function<? super V, ? extends R> mapper,
            BiFunction<? super R, ? super V, ? extends R> combiner);

    TupleCollector<K, List<V>> groupByKey();

    Collection<K> getFirst();

    Collection<V> getSecond();

    <P> TupleCollector<P, V> mapKeys(Function<? super K, ? extends P> mapper);

    <P> TupleCollector<K, P> mapValues(Function<? super V, ? extends P> mapper);

    <P> Collector<P> map(BiFunction<K, V, P> mapper);
}
