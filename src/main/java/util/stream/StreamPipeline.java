package util.stream;

import util.Tools.ThisOrElse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.*;

public class StreamPipeline<E> implements Stream<E> {

    private Consumer<Consumer<? super E>> sourceApplier;

    public StreamPipeline(Consumer<Consumer<? super E>> consumer) {
        this.sourceApplier = consumer;
    }

    public StreamPipeline(Collection<E> source) {
        this.sourceApplier = source::forEach;
    }

    public StreamPipeline(Supplier<E> supplier) {
        this.sourceApplier = eConsumer -> eConsumer.accept(supplier.get());
    }

    public StreamPipeline(E e) {
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
        return new StreamPipeline<>(applier);
    }

    @Override
    public <R> StreamPipeline<R> streamMap(Function<? super E, ? extends Stream<? extends R>> mapper) {
        Consumer<Consumer<? super R>> applier = rConsumer -> {
            sourceApplier.accept(e -> mapper.apply(e).sink(rConsumer));
        };
        return new StreamPipeline<>(applier);
    }

    @Override
    public <R> Stream<R> flatMap(Function<? super E, ? extends Collection<? extends R>> flatMapper) {
        Function<? super E, ? extends StreamPipeline<? extends R>> mapper = flatMapper.andThen(StreamPipeline::new);
        return streamMap(mapper);
    }

    @Override
    public StreamPipeline<E> filter(Predicate<? super E> predicate) {
        Consumer<Consumer<? super E>> applier = eConsumer -> sourceApplier.accept(
                ThisOrElse.acceptThis(predicate, eConsumer));
        return new StreamPipeline<>(applier);
    }

    @Override
    public StreamPipeline<E> filterNot(Predicate<? super E> predicate) {
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
        return new StreamPipeline<>(applier);
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
        return new StreamPipeline<>(applier);
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
        return new StreamPipeline<>(applier);
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
        return new StreamPipeline<>(applier);
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

    @SuppressWarnings("unchecked")
    @Override
    public <R> R fold(R r, BiFunction<R, ? super E, R> accumulator) {
        final R[] ele = (R[]) new Object[]{r};
        Consumer<? super E> combiner = e -> ele[0] = accumulator.apply(ele[0], e);
        sourceApplier.accept(combiner);
        return r;
    }

    @Override
    public <R> R reduce(BiFunction<R, ? super E, R> err) {
        return null;
    }

    public static void main(String[] args) {
        List<Integer> integers = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            integers.add(i);
        }
        List<String> strings = new ArrayList<>();
        StreamPipeline<Integer> stringStream = new StreamPipeline<>(integers);
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
