package collectionUtil;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;

public class TupleCollectorForArray<K, V> extends CollectorForArray<Tuple<K, V>> implements TupleCollector<K, V> {

    private Map<K, V> map;
    private boolean mapAvailable = false;

    TupleCollectorForArray(List<Tuple<K, V>> tupleList) {
        super(tupleList);
    }

    private TupleCollectorForArray() {
        super(null);
        map = new HashMap<>();
        mapAvailable = true;
    }

    @Override
    public TupleCollector<K, V> reduceByKey(BinaryOperator<V> combiner) {
        TupleCollectorForArray<K, V> tupleCollector = new TupleCollectorForArray<>();
        Map<K, V> krMap = tupleCollector.map;
        List<Tuple<K, V>> list = getList();
        for (Tuple<K, V> tuple : list) {
            krMap.compute(tuple._1, ((k, r) -> {
//                r = r == null ? tuple._2 : combiner.apply(tuple._2, r);
                if (r == null) {
                    r = tuple._2;
                } else {
                    r = combiner.apply(tuple._2, r);
                }
                return r;
            }));
        }
        return tupleCollector;
    }

    @Override
    public <R> TupleCollector<K, R> reduceByKey(BiFunction<V, R, R> combiner, Function<V, R> function) {
        TupleCollectorForArray<K, R> tupleCollector = new TupleCollectorForArray<>();
        Map<K, R> krMap = tupleCollector.map;
        List<Tuple<K, V>> list = getList();
        for (Tuple<K, V> tuple : list) {
            krMap.compute(tuple._1, ((k, r) -> {
                r = r == null ? function.apply(tuple._2) : combiner.apply(tuple._2, r);
                return r;
            }));
        }
        return tupleCollector;
    }

    @Override
    public TupleCollector<K, List<V>> groupByKey() {
        Function<V, List<V>> function = v -> {
            List<V> vList = new ArrayList<>();
            vList.add(v);
            return vList;
        };

        return this.reduceByKey(
                (v, vList) -> {
                    vList.add(v);
                    return vList;
                },
                function
        );
    }

    @Override
    public Collection<K> getFirst() {
        if (mapAvailable) {
            return map.keySet();
        }
        List<K> kList = new ArrayList<>(getList().size());
        getList().forEach(tuple -> kList.add(tuple._1));
        return kList;
    }

    @Override
    public Collection<V> getSecond() {
        if (mapAvailable) {
            return map.values();
        }
        List<V> kList = new ArrayList<>(getList().size());
        getList().forEach(tuple -> kList.add(tuple._2));
        return kList;
    }

    @Override
    public List<Tuple<K, V>> getList() {
        return mapAvailable ? map2List() : super.getList();
    }

    private List<Tuple<K, V>> map2List() {
        List<Tuple<K, V>> list = new ArrayList<>(map.size());
        for (K k : map.keySet()) {
            list.add(Tuple.of(k, map.get(k)));
        }
        return list;
    }
}
