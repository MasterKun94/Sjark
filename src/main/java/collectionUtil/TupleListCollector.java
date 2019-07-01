package collectionUtil;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;

public class TupleListCollector<K, V> implements TupleCollector<K, V> {

    private List<K> first;
    private List<V> second;

    TupleListCollector(List<K> first, List<V> second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public TupleCollector<K, V> reduceByKey(BinaryOperator<V> accumulator) {
        Map<K, V> krMap = new HashMap<>();
        BinaryOperator<V> operator = (oldV, newV) -> oldV == null ? newV : accumulator.apply(oldV, newV);
        for (int i = 0; i < first.size(); i++) {
            krMap.merge(first.get(i), second.get(i), operator);
        }
        return new TupleMapCollector<>(krMap);
    }

    @Override
    public <R> TupleCollector<K, R> reduceByKey(
            Function<? super V, ? extends R> function,
            BiFunction<? super R, ? super V, ? extends R> accumulator)
    {
        Map<K, R> krMap = new HashMap<>();
        for (int i = 0; i < first.size(); i++) {
            V v = second.get(i);
            krMap.compute(first.get(i), ((k, r) -> {
                r = r == null ? function.apply(v) : accumulator.apply(r, v);
                return r;
            }));
        }
        return new TupleMapCollector<>(krMap);
    }

    @Override
    public TupleCollector<K, List<V>> groupByKey() {
        Function<V, List<V>> function = v -> {
            List<V> vList = new ArrayList<>();
            vList.add(v);
            return vList;
        };

        return this.reduceByKey(
                function,
                (vList, v) -> {
                    vList.add(v);
                    return vList;
                }
        );
    }

    @Override
    public Collection<K> getFirst() {
        return first;
    }

    @Override
    public Collection<V> getSecond() {
        return second;
    }

    @Override
    public <P> TupleCollector<P, V> mapKeys(Function<? super K, ? extends P> mapper) {
        List<P> list = new ArrayList<>(first.size());
        first.forEach(k -> list.add(mapper.apply(k)));
        return new TupleListCollector<>(list, second);
    }

    @Override
    public <P> TupleCollector<K, P> mapValues(Function<? super V, ? extends P> mapper) {
        List<P> list = new ArrayList<>(first.size());
        second.forEach(k -> list.add(mapper.apply(k)));
        return new TupleListCollector<>(first, list);
    }

    @Override
    public <P> Collector<P> map(BiFunction<K, V, P> mapper) {
        List<P> list = new ArrayList<>(first.size());
        for (int i = 0; i < first.size(); i++) {
            list.add(mapper.apply(first.get(i), second.get(i)));
        }
        return new ListCollector<>(list);
    }
}
