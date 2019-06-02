package collectionUtil;

import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;

public interface TupleCollector<K, V> extends Collector<Tuple<K, V>> {
    TupleCollector<K, V> reduceByKey(BinaryOperator<V> operator);

    <R> TupleCollector<K, R> reduceByKey(BiFunction<V, R, R> combiner, Function<V, R> function);

    TupleCollector<K, List<V>> groupByKey();

    Collection<K> getFirst();

    Collection<V> getSecond();
}
