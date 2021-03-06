package collectionUtil;

import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @param <T>
 */
public interface Collector<T> {

     /**
      * Returns a collector consisting of the results of applying the given function to
      * the elements of this collector.
      *
      * @param mapper a function to apply to each element
      * @param <R> The element type of the new stream
      * @return the new collector
      */
     <R> Collector<R> map(Function<? super T, ? extends R> mapper);

     /**
      * Returns an {@code TupleCollector} consisting of the results of applying the
      * given function to the elements of this collector.
      *
      * @param keyMapper a function to apply to each element that map to the key element
      *                  of TupleCollector
      * @param valueMapper a function to apply to each element that map to the value
      *                    element of TupleCollector
      * @param <K> The key element type of the TupleCollector
      * @param <V> The value element type of the TupleCollector
      * @return a TupleCollector
      */
     <K, V> TupleCollector<K, V> mapToTuple(
             Function<? super T, ? extends K> keyMapper,
             Function<? super T, ? extends V> valueMapper);

     /**
      * Returns a collector consisting of the contents from the results of replacing
      * each element of this collector with the contents of a mapped collection
      * produced by applying the provided mapping function to each element.
      *
      * @param flatMapper a function to apply to each element which produces a stream
      *                   of new values
      * @param <R> The element type of the new collector
      * @return the new collector
      */
     <R> Collector<R> flatMap(
             Function<? super T, ? extends Collection<? extends R>> flatMapper);

     /**
      * Returns a collector consisting of the elements of this collector that match
      * the given predicate.
      *
      * @param predicate a predicate to apply to each element to determine if it
      *                  should be included
      * @return the new collector
      */
     Collector<T> filter(Predicate<? super T> predicate);

     /**
      * Returns a collector consisting of the elements of this collector that not match
      * the given predicate.
      *
      * @param predicate a predicate to apply to each element to determine if it
      *                  should be included
      * @return the new collector
      */
     Collector<T> filterNot(Predicate<? super T> predicate);

     /**
      * Returns a collector consisting of the distinct elements of this collector.
      * @return the new stream
      */
     Collector<T> distinct();

     /**
      * Returns whether all elements of this collector match the provided predicate.
      * May not evaluate the predicate on all elements if not necessary for
      * determining the result.  If the collector is empty then {@code true} is
      * returned and the predicate is not evaluated.
      *
      * @param predicate a predicate to apply to elements of this collector
      * @return {@code true} if either all elements of the collector match the
      * provided predicate or the stream is empty, otherwise {@code false}
      */
     boolean forAll(Predicate<? super T> predicate);

     /**
      * Returns whether any elements of this collector match the provided predicate.
      * May not evaluate the predicate on all elements if not necessary for
      * determining the result.  If the collector is empty then {@code true} is
      * returned and the predicate is not evaluated.
      *
      * @param predicate a predicate to apply to elements of this collector
      * @return {@code true} if any elements of the collector match the provided
      * predicate, otherwise {@code false}
      */
     boolean exists(Predicate<? super T> predicate);

     /**
      * Returns the count of elements in this collector match the provided predicate.
      *
      * @param predicate a predicate to apply to elements of this collector
      * @return the count of elements in this collector
      */
     int count(Predicate<? super T> predicate);

     /**
      * Performs a fold on the elements of this collector, using the provided identity
      * value and an associative accumulation function, and returns the reduced value.
      * This is equivalent to:
      * {@code
      *     T result = identity;
      *     for (T element : this collector)
      *         result = accumulator.apply(result, element)
      *     return result;
      * }
      *
      * <p>The {@code identity} value must be an identity for the accumulator
      * function. This means that for all {@code t},
      * {@code accumulator.apply(identity, t)} is equal to {@code t}.
      * The {@code accumulator} function must be an associative function.
      *
      * @param identity the identity value for the accumulating function
      * @param accumulator an associative function for combining two values
      * @return the result of the reduction
      */
     T fold(T identity,
            BinaryOperator<T> accumulator);

     /**
      * Performs a reduction on the elements of this collector, using the provided
      * identity value and an associative accumulation function, and returns the
      * reduced value. This is equivalent to:
      * {@code
      *   R result = null;
      *   boolean isFirst = true;
      *   for (T element : contents) {
      *        if (isFirst) {
      *             result = idMapper.apply(element);
      *             isFirst = false;
      *        } else {
      *             result = accumulator.apply(result, element);
      *        }
      *   }
      *   return result;
      * }
      *
      * <p>The {@code identity} value must be an identity for the combiner
      * function.  This means that for all {@code u}, {@code combiner(identity, u)}
      * is equal to {@code u}.
      *
      * @param identityMapper the identity function for the accumulating function
      * @param accumulator an associative function for combining two values
      * @param <R> The type of the result
      * @return the result of the reduction
      */
     <R> R reduce(
             Function<T, R> identityMapper,
             BiFunction<? super R, ? super T, ? extends R> accumulator);
     /**
      * Performs a reduction on the elements of this collector, using the provided
      * identity value and an associative accumulation function, and returns the
      * reduced value. This is equivalent to:
      * {@code
      *   R result = null;
      *   boolean isFirst = true;
      *   for (T element : contents) {
      *        if (isFirst) {
      *             result = element;
      *             isFirst = false;
      *        } else {
      *             result = accumulator.apply(result, element);
      *        }
      *   }
      *   return result;
      * }
      *
      * <p>The {@code identity} value must be an identity for the combiner
      * function.  This means that for all {@code u}, {@code combiner(identity, u)}
      * is equal to {@code u}.
      *
      * @param accumulator an associative function for combining two values
      * @return the result of the reduction
      */
     T reduce(BinaryOperator<T> accumulator);

     /**
      * Performs a reduction on the elements of this collector, the element is fold by the
      * given {@code keyFunction} function, elements group by the same {@code keyFunction}
      * results are apply to the given {@code accumulator}, and the results are the
      * elements of the returned collector
      *
      * @param keyFunction a function to apply to each element to generate key that to
      *                    be fold by
      * @param accumulator an associative function for combining two values
      * @param <K> The type of the key
      * @return the new collector
      */
     <K> Collector<T> reduceBy(
             Function<? super T, ? extends K> keyFunction,
             BinaryOperator<T> accumulator);

     /**
      * Performs a reduction on the elements of this collector, the element is reduced by the
      * given {@code keyFunction} function, elements group by the same {@code keyFunction}
      * results are apply to the given {@code accumulator}, and the results are the
      * elements of the returned collector
      *
      * @param keyFunction a function to apply to each element to generate key that to
      *                    be reduced by
      * @param mapper a function for incorporating an additional element into a result
      * @param accumulator an associative function for combining two values
      * @param <K> The type of the key
      * @param <R> The element type of the result
      * @return the new collector
      */
     <K, R> Collector<R> reduceBy(
             Function<? super T, ? extends K> keyFunction,
             Function<? super T, ? extends R> mapper,
             BiFunction<? super R, ? super T, ? extends R> accumulator);

     /**
      * Returns a collector consisting of the lists of elements that group by the given
      * {@code keyFunction} function, this is equivalent to :
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
      *        reduceBy(keyFunction, mapper, accumulator);
      * }
      *
      * @param keyFunction a function to apply to each element to generate key that to
      *                    be reduced by
      * @param <K> The element type of the result
      * @return the new collector
      */
     <K> Collector<List<T>> groupBy(Function<? super T, ? extends K> keyFunction);

     /**
      * @return the {@code java.util.collection} source the collector contains
      */
     Collection<T> get();

     /**
      * returns a collector that contains the given {@code list} source
      *
      * @param list the collector source
      * @param <E> the element type of the new collector
      * @return
      */
     static <E> Collector<E> of(Collection<E> list) {
          return new ListCollector<>(list);
     }
}
