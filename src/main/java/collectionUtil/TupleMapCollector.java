package collectionUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;

public class TupleMapCollector<K, V> implements TupleCollector<K, V> {
    private Map<K, V> map;

    TupleMapCollector(Map<K, V> map) {
        this.map = map;
    }

    @Override
    public TupleCollector<K, V> foldByKey(BinaryOperator<V> operator) {
        return this;
    }

    @Override
    public <R> TupleCollector<K, R> reduceByKey(
            Function<? super V, ? extends R> function,
            BiFunction<? super R, ? super V, ? extends R> combiner)
    {
        return mapValues(function);
    }

    @Override
    public TupleCollector<K, List<V>> groupByKey() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<K> getFirst() {
        return map.keySet();
    }

    @Override
    public Collection<V> getSecond() {
        return map.values();
    }

    @Override
    public <P> TupleCollector<P, V> mapKeys(Function<? super K, ? extends P> mapper) {
        List<P> kList = new ArrayList<>(map.size());
        List<V> pList = new ArrayList<>(map.size());
        map.forEach((k, v) -> {
            kList.add(mapper.apply(k));
            pList.add(v);
        });
        return new TupleListCollector<>(kList, pList);
    }

    @Override
    public <P> TupleCollector<K, P> mapValues(Function<? super V, ? extends P> mapper) {
        List<K> kList = new ArrayList<>(map.size());
        List<P> pList = new ArrayList<>(map.size());
        map.forEach((k, v) -> {
            kList.add(k);
            pList.add(mapper.apply(v));
        });
        return new TupleListCollector<>(kList, pList);
    }

    @Override
    public <P> Collector<P> map(BiFunction<K, V, P> mapper) {
        List<P> list = new ArrayList<>(map.size());
        map.forEach(((k, v) -> list.add(mapper.apply(k, v))));
        return new ListCollector<>(list);
    }


}
