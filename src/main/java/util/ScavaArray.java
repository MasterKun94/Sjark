package util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;
import java.util.function.Predicate;

public class ScavaArray<E> extends ArrayList<E> implements Seq<E> {

    public ScavaArray() {
        super();
    }

    public ScavaArray(int capacity) {
        super(capacity);
    }

    public ScavaArray(Collection<? extends E > collection) {
        super(collection);
    }

    @Override
    public Seq<E> getContent() {
        return this;
    }

    @Override
    public <R> Seq<R> map(Function<? super E, ? extends R> mapper) {
        ScavaArray<R> list = new ScavaArray<>(this.size());
        this.forEach(element -> list.add(mapper.apply(element)));
        return list;
    }

    @Override
    public <R> Seq<R> flatMap(Function<? super E, ? extends Collection<? extends R>> flatMapper) {
        ScavaArray<R> array = new ScavaArray<>();
        this.forEach(element -> array.addAll(flatMapper.apply(element)));
        return array;
    }

    @Override
    public Seq<E> filter(Predicate<? super E> filter) {
        ScavaArray<E> array = new ScavaArray<>();
        this.forEach(element -> {
            if (filter.test(element)) array.add(element);
        });
        return array;
    }

    @Override
    public Seq<E> filterNot(Predicate<? super E> filter) {
        return filter(filter.negate());
    }

    @Override
    public Seq<E> takeWhile(Predicate<? super E> filter) {
        for (int i = 0; i < size(); i++) {
            if (filter.test(this.get(i))) {
                return take(i);
            }
        }
        return new ScavaArray<>(this);
    }

    @Override
    public Seq<E> dropWhile(Predicate<? super E> filter) {
        for (int i = 0; i < size(); i++) {
            if (filter.test(get(i))) {
                return drop(i);
            }
        }
        return new ScavaArray<>();
    }

    @Override
    public Seq<E> take(int n) {
        return new ScavaArray<>(this.subList(0, n));
    }

    @Override
    public Seq<E> drop(int n) {
        return new ScavaArray<>(this.subList(n, size()));
    }

    @Override
    public Stream<E> toStream() {
        return null;//TODO
    }

    public static void main(String[] args) {
        class Test {
            public int value;

            public Test() { }

            public Test(int value) {
                this.value = value;
            }

            @Override
            public String toString() {
                return "test: " + value;
            }
        }

        Seq<Test> tests = new ScavaArray<>();
        for (int i = 0; i < 100; i++) {
            tests.add(new Test(i));
        }

        System.out.println(tests.filter(test -> test.value > 80));
        System.out.println(tests.drop(80));
        System.out.println(tests.take(80));
        System.out.println(tests.dropWhile(test -> test.value > 80));
        System.out.println(tests.<Test>foldLeft(new Test(0), (test1, test2) -> new Test(test1.value + test2.value)));
        System.out.println(tests.<Test>reduceLeft((test1, test2) -> new Test(test1.value + test2.value)));
    }

}
