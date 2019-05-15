package util.stream;

import java.util.Collection;
import java.util.function.*;

public interface IntStream extends SjarkBaseStream {
    void sink(Consumer<Integer> collector);

    IntStream streamMap(IntFunction<? extends IntStream> mapper);

    IntStream map(IntUnaryOperator mapper);

    IntStream flatMap(IntFunction<? extends Collection<IntStream>> flatMapper);

    IntStream filter(IntPredicate filter);

    IntStream filterNot(IntPredicate filter);

    int count(IntPredicate filter);

    <R> R fold(int identity, IntBinaryOperator op);

    <R> R reduce(IntBinaryOperator op);
}
