package util;

import util.Tools.Copy;
import util.Tools.ThisOrElse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.*;

public class BaseStream<E> implements Stream<E> {

    private Consumer<Consumer<? super E>> sourceApplier;

    private BaseStream(Consumer<Consumer<? super E>> consumer) {
        this.sourceApplier = consumer;
    }

    private BaseStream(Collection<E> source) {
        this.sourceApplier = source::forEach;
    }

    private BaseStream(Supplier<E> supplier) {
        this.sourceApplier = eConsumer -> eConsumer.accept(supplier.get());
    }

    private BaseStream(E e) {
        this.sourceApplier = eConsumer -> eConsumer.accept(e);
    }

    @Override
    public void sink(Consumer<? super E> collector) {
        sourceApplier.accept(collector);
    }

    @Override
    public <R> Stream<R> map(Function<? super E, ? extends R> mapper) {
        Consumer<Consumer<? super R>> applier = rConsumer -> {
            sourceApplier.accept(e -> rConsumer.accept(mapper.apply(e)));
        };
        return new BaseStream<>(applier);
    }

    @Override
    public <R> BaseStream<R> streamMap(Function<? super E, ? extends Stream<? extends R>> mapper) {
        Consumer<Consumer<? super R>> applier = rConsumer -> {
            sourceApplier.accept(e -> mapper.apply(e).sink(rConsumer));
        };
        return new BaseStream<>(applier);
    }

    @Override
    public <R> Stream<R> flatMap(Function<? super E, ? extends Collection<? extends R>> flatMapper) {
        Function<? super E, ? extends BaseStream<? extends R>> mapper = flatMapper.andThen(BaseStream::new);
        return streamMap(mapper);
    }

    @Override
    public BaseStream<E> filter(Predicate<? super E> predicate) {
        Consumer<Consumer<? super E>> applier = eConsumer -> sourceApplier.accept(
                ThisOrElse.acceptThis(predicate, eConsumer));
        return new BaseStream<>(applier);
    }

    @Override
    public BaseStream<E> filterNot(Predicate<? super E> predicate) {
        return filter(predicate.negate());
    }

    @Override
    public Stream<E> takeWhile(Predicate<? super E> predicate) { //TODO
        final boolean[] flag = {true};
        Consumer<Consumer<? super E>> applier = eConsumer -> {
            sourceApplier.accept(e -> {
                if (flag[0]) {
                    eConsumer.accept(e);
                    flag[0] = flag[0] && !predicate.test(e);
                }
            });
        };
        return new BaseStream<>(applier);
    }

    @Override
    public Stream<E> dropWhile(Predicate<? super E> predicate) { //TODO
        final boolean[] flag = {true};
        Consumer<Consumer<? super E>> applier = eConsumer -> sourceApplier.accept(e -> {
            if (flag[0]) {
                flag[0] = !predicate.test(e);
            } else {
                eConsumer.accept(e);
            }
        });
        return new BaseStream<>(applier);
    }

    @Override
    public Stream<E> take(int n) {//TODO
        final int[] count = {0};
        Consumer<Consumer<? super E>> applier = eConsumer -> sourceApplier.accept(e -> {
            if (count[0] < n) {
                eConsumer.accept(e);
                count[0]++;
            }
        });
        return new BaseStream<>(applier);
    }

    @Override
    public Stream<E> drop(int n) {//TODO
        final int[] count = {0};
        Consumer<Consumer<? super E>> applier = eConsumer -> sourceApplier.accept(e -> {
            if (count[0] < n) {
                count[0]++;
            } else {
                eConsumer.accept(e);
            }
        });
        return new BaseStream<>(applier);
    }

    @Override
    public boolean forAll(Predicate<? super E> filter) {
        final boolean[] flag = {true};
        Consumer<? super E> consumer = e -> flag[0] = flag[0] && filter.test(e);
        sourceApplier.accept(consumer);
        return flag[0];
    }

    @Override
    public boolean exists(Predicate<? super E> filter) {//TODO
        final boolean[] flag = {false};
        Consumer<? super E> consumer = e -> flag[0] = flag[0] || filter.test(e);
        sourceApplier.accept(consumer);
        return flag[0];
    }

    @Override
    public int count(Predicate<? super E> filter) {
        final int[] count = {0};
        Consumer<? super E> counter = e -> count[0]++;
        sourceApplier.accept(counter);
        return count[0];
    }

    @Override
    public E fold(E e, BiFunction<E, E, E> function) {
        E e1 = Copy.doCopy(e);
        Consumer<? super E> combiner = e2 -> function.apply(e2, e1);
        sourceApplier.accept(combiner);
        return e1;
    }

    @Override
    public <R> R fold(E e, Function<E, R> er, BiFunction<E, R, R> err) {
        R e1 = er.apply(e);
        Consumer<? super E> combiner = e2 -> err.apply(e2, e1);
        sourceApplier.accept(combiner);
        return e1;
    }

    @Override
    public E reduce(BiFunction<E, E, E> function) {
        return null;
    }

    @Override
    public <R> R reduce(Function<E, R> er, BiFunction<E, R, R> err) {
        return null;
    }



    public static void main(String[] args) {
        List<Integer> integers = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            integers.add(i);
        }
        List<String> strings = new ArrayList<>();
        BaseStream<Integer> stringStream = new BaseStream<>(integers);
        stringStream.map(i -> i * i)
                .map(i -> "\n\ti 的平方是: " + i)
                .flatMap(s -> {
                    List<String> list = new ArrayList<>();
                    for (int i = 0; i < 3; i++) {
                        list.add(s + "\t" + i);
                    }
                    return list;
                })
                .sink(strings::add);
        System.out.println(strings.size());
        System.out.println(strings);

    }
}
