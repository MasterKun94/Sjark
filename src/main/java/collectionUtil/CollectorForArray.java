package collectionUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;

public class CollectorForArray<T> implements Collector<T> {
    private final List<T> list;

    CollectorForArray(List<T> list) {
        this.list = list;
    }

    public List<T> getList() {
        return list;
    }

    public static <E> CollectorForArray<E> of(List<E> list) {
        return new CollectorForArray<>(list);
    }

    @Override
    public <R> List<R> map(Function<? super T, ? extends R> mapper) {
        List<R> rList = new ArrayList<>(list.size());
        list.forEach(t -> rList.add(mapper.apply(t)));
        return rList;
    }

    @Override
    public <K, V> TupleCollectorForArray<K, V> mapToTuple(Function<? super T, Tuple<K, V>> mapper) {
        List<Tuple<K, V>> tupleList = new ArrayList<>(list.size());
        list.forEach(t -> tupleList.add(mapper.apply(t)));
        return new TupleCollectorForArray<>(tupleList);
    }

    @Override
    public <R> List<R> flatMap(Function<? super T, ? extends List<? extends R>> flatMapper) {
        List<R> rList = new ArrayList<>();
        list.forEach(t -> flatMapper.apply(t).forEach(rList::add));
        return rList;
    }

    @Override
    public <K, V> TupleCollectorForArray<K, V> flatMapToTuple(Function<? super T, ? extends List<Tuple<K, V>>> flatMapper) {
        return null;
    }

    @Override
    public List<T> filter(Predicate<? super T> predicate) {
        List<T> filterList = new ArrayList<>();
        list.forEach(t -> {
            if (predicate.test(t)) {
                filterList.add(t);
            }
        });
        return filterList;
    }

    @Override
    public List<T> filterNot(Predicate<? super T> predicate) {
        return filter(predicate.negate());
    }

    @Override
    public List<T> takeWhile(Predicate<? super T> predicate) {
        int i = 0;
        for (T t : list) {
            i++;
            if (predicate.test(t)) {
                return take(i);
            }
        }
        return take(i);
    }


    @Override
    public List<T> dropWhile(Predicate<? super T> predicate) {
        int i = 0;
        for (T t : list) {
            i++;
            if (predicate.test(t)) {
                return drop(i);
            }
        }
        return drop(i);
    }

    @Override
    public List<T> take(int n) {
        return list.subList(0, n);
    }

    @Override
    public List<T> drop(int n) {
        return list.subList(n, list.size());
    }

    @Override
    public boolean forAll(Predicate<? super T> predicate) {
        return exists(predicate.negate());
    }

    @Override
    public boolean exists(Predicate<? super T> predicate) {
        for (T t : list) {
            if (predicate.test(t)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int count(Predicate<? super T> predicate) {
        int i = 0;
        for (T t : list) {
            if (predicate.test(t)) {
                i++;
            }
        }
        return i;
    }

    @Override
    public T fold(T t, BinaryOperator<T> operator) {
        T init = t;
        for (T element : list) {
            init = operator.apply(init, element);
        }
        return init;
    }

    @Override
    public <R> R reduce(R r, BiFunction<R, ? super T, R> biFunction) {
        R init = r;
        for (T element : list) {
            init = biFunction.apply(init, element);
        }
        return init;
    }
}
