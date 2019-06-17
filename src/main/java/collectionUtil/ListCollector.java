package collectionUtil;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;

public class ListCollector<T> implements Collector<T> {
    private final Collection<T> contents;

    ListCollector(Collection<T> contents) {
        this.contents = contents;
    }

    public Collection<T> get() {
        return contents;
    }

    @Override
    public <R> Collector<R> map(Function<? super T, ? extends R> mapper) {
        List<R> rList = new ArrayList<>(contents.size());
        contents.forEach(t -> rList.add(mapper.apply(t)));
        return Collector.of(rList);
    }

    @Override
    public <K, V> TupleListCollector<K, V> mapToTuple(
            Function<? super T, ? extends K> keyMapper,
            Function<? super T, ? extends V> valueMapper)
    {
        List<K> first = new ArrayList<>(contents.size());
        List<V> second = new ArrayList<>(contents.size());
        contents.forEach(t -> {
            first.add(keyMapper.apply(t));
            second.add(valueMapper.apply(t));
        });
        return new TupleListCollector<>(first, second);
    }

    @Override
    public <R> Collector<R> flatMap(Function<? super T, ? extends Collection<? extends R>> flatMapper) {
        List<R> rList = new ArrayList<>();
        contents.forEach(t -> flatMapper.apply(t).forEach(rList::add));
        return Collector.of(rList);
    }

    @Override
    public Collector<T> filter(Predicate<? super T> predicate) {
        List<T> filterList = new ArrayList<>();
        contents.forEach(t -> {
            if (predicate.test(t)) {
                filterList.add(t);
            }
        });
        return Collector.of(filterList);
    }

    @Override
    public Collector<T> filterNot(Predicate<? super T> predicate) {
        return filter(predicate.negate());
    }

    @Override
    public boolean forAll(Predicate<? super T> predicate) {
        return exists(predicate.negate());
    }

    @Override
    public boolean exists(Predicate<? super T> predicate) {
        for (T t : contents) {
            if (predicate.test(t)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int count(Predicate<? super T> predicate) {
        int i = 0;
        for (T t : contents) {
            if (predicate.test(t)) {
                i++;
            }
        }
        return i;
    }

    @Override
    public T fold(T t, BinaryOperator<T> operator) {
        T init = t;
        for (T element : contents) {
            init = operator.apply(init, element);
        }
        return init;
    }

    @Override
    public <K> Collector<T> foldBy(
            Function<? super T, ? extends K> keyGetter,
            BinaryOperator<T> accumulator)
    {
        Map<K, T> map = new HashMap<>();
        K key;
        BinaryOperator<T> operator = (oldV, newV) -> oldV == null ? newV : accumulator.apply(oldV, newV);
        for (T element : contents) {
            key = keyGetter.apply(element);
            map.merge(key, element, operator);
        }
        return Collector.of(map.values());
    }

    @Override
    public <R> R reduce(R r, BiFunction<? super R, ? super T, ? extends R> biFunction) {
        R init = r;
        for (T element : contents) {
            init = biFunction.apply(init, element);
        }
        return init;
    }

    @Override
    public <K, R> Collector<R> reduceBy(
            Function<? super T, ? extends K> keyGetter,
            Function<? super T, ? extends R> mapper,
            BiFunction<? super R, ? super T, ? extends R> accumulator)
    {
        Map<K, R> map = new HashMap<>();
        K key;
        for (T element : contents) {
            key = keyGetter.apply(element);
            map.compute(key, (k, r) -> r == null ? mapper.apply(element) : accumulator.apply(r, element));
        }
        return Collector.of(map.values());
    }

    @Override
    public <K> Collector<List<T>> groupBy(Function<? super T, ? extends K> keyGetter) {
        Function<T, List<T>> function = t -> {
            List<T> list = new ArrayList<>();
            list.add(t);
            return list;
        };

        BiFunction<List<T>, T, List<T>> biFunction = (list1, t) -> {
            list1.add(t);
            return list1;
        };

        return reduceBy(keyGetter, function, biFunction);
    }
}
