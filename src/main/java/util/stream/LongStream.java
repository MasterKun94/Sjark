package util.stream;

import java.util.Collection;
import java.util.function.*;

public interface LongStream extends SjarkBaseStream {
    void sink(Consumer<Long> collector);

    LongStream streamMap(LongFunction<? extends LongStream> mapper);

    LongStream map(LongBinaryOperator mapper);

    LongStream flatMap(LongFunction<? extends Collection<Long>> flatMapper);

    LongStream filter(LongPredicate filter);

    LongStream filterNot(LongPredicate filter);

    long count(LongPredicate filter);

    long fold(long identity, LongBinaryOperator op);

    long reduce(LongBinaryOperator op);
}
