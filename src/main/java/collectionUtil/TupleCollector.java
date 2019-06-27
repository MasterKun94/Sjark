package collectionUtil;

import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;

/**
 * @param <K>
 * @param <V>
 */
public interface TupleCollector<K, V> {


    /**
     * Performs a reduction on the elements of this tupleCollector, the element is reduced
     * by the elements from the first column, elements group by the same key are apply to
     * the given {@code accumulator}, and the results are the elements of the second column
     * of the returned collector
     *
     * @param accumulator an associative function for combining two values
     * @return the new tupleCollector
     */
    TupleCollector<K, V> reduceByKey(BinaryOperator<V> accumulator);

    /**
     * Performs a reduction on the elements of this tupleCollector, the element is reduced
     * by the elements from the first column, elements group by the same key are apply to
     * the given {@code accumulator}, and the results are the elements of the second column
     * of the returned collector
     *
     * @param mapper a function for incorporating an additional element into a result
     * @param accumulator an associative function for combining two values
     * @param <R> the second element type of the collector
     * @return the new tupleCollector
     */
    <R> TupleCollector<K, R> reduceByKey(
            Function<? super V, ? extends R> mapper,
            BiFunction<? super R, ? super V, ? extends R> accumulator);

    /**
     * Returns a tupleCollector and its second column consists of the lists of elements
     * that group by the given {@code keyFunction} function, this is equivalent to :
     * {@code
     *        Function<T, List<T>> mapper = t -> {
     *            List<T> list = new ArrayList();
     *            list.add(t);
     *            return list;
     *        }
     *        BiFunction<List<T>, T, List<T>> accumulator = (list, t) -> {
     *            list.add(t);
     *            return list;
     *        }
     *        reduceByKey(mapper, accumulator);
     * }
     *
     * @return the new tupleCollector
     */
    TupleCollector<K, List<V>> groupByKey();

    /**
     * @return the first column of the tupleCollector
     */
    Collection<K> getFirst();

    /**
     * @return the second column of the tupleCollector
     */
    Collection<V> getSecond();

    /**
     * Returns a tupleCollector and the first column consists of the results of applying
     * the given function to the elements of this collector, and the second column remains
     * the same
     *
     * @param mapper a function to apply to each element of the first column
     * @param <P> the element type of the second element
     * @return the new tupleCollector
     */
    <P> TupleCollector<P, V> mapKeys(Function<? super K, ? extends P> mapper);

    /**
     * Returns a tupleCollector and the second column consists of the results of applying
     * the given function to the elements of this collector, and the first column remains
     * the same
     *
     * @param mapper a function to apply to each element of the second column
     * @param <P> the element type of the second element
     * @return the new tupleCollector
     */
    <P> TupleCollector<K, P> mapValues(Function<? super V, ? extends P> mapper);

    /**
     * Returns a collector consisting of the results of applying the given function to the
     * elements of this collector.
     *
     * @param mapper a function to apply to each element of the first and second column
     * @param <P> the element type of the second element
     * @return the new tupleCollector
     */
    <P> Collector<P> map(BiFunction<K, V, P> mapper);
}
