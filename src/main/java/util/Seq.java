package util;

import util.Tools.Copy;
import util.stream.Stream;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;

public interface Seq<E> extends Traversable<E>, List<E>{

    Stream<E> toStream();

    Seq<E> getContent();

    @Override
    default boolean forAll(Predicate<? super E> filter) {
        return !exists(filter.negate());
    }

    @Override
    default boolean exists(Predicate<? super E> filter) {
        for (E element : getContent()) {
            if (filter.test(element)) {
                return true;
            }
        }
        return false;
    }

    @Override
    default int count(Predicate<? super E> filter) {
        int count = 0;
        for (E element : getContent()) {
            count = filter.test(element) ? count + 1 : count;
        }
        return count;
    }

    @Override
    default E foldLeft(E e, BinaryOperator<E> accumulator) {
        E o = Copy.doCopy(e);

        for (E element : getContent()) {
            o = accumulator.apply(o, element);
        }
        return o;
    }

    @Override
    default E foldRight(E e, BinaryOperator<E> accumulator) {
        Seq<E> content = getContent();
        E o = Copy.doCopy(e);
        for (int i = content.size() - 1; i >= 0; i--) o = accumulator.apply(o, content.get(i));
        return o;
    }

    @Override
    default <R> R foldLeft(R r, BiFunction<R, ? super E, R> accumulator) {
        for (E element : getContent()) {
            r = accumulator.apply(r, element);
        }
        return r;
    }

    @Override
    default <R> R foldRight(R r, BiFunction<R, ? super E, R> accumulator) {
        Seq<E> content = getContent();
        for (int i = content.size() - 1; i >= 0; i--) r = accumulator.apply(r, content.get(i));
        return r;
    }

    @Override
    default E reduceLeft(BinaryOperator<E> accumulator) {
        Seq<E> content = getContent();
        E o = Copy.doCopy(content.get(0));
        for (int i = 1; i < content.size(); i++) o = accumulator.apply(o, content.get(i));
        return o;
    }

    @Override
    default E reduceRight(BinaryOperator<E> accumulator) {
        Seq<E> content = getContent();
        E o = Copy.doCopy(content.get(content.size() - 1));
        for (int i = content.size() - 2; i >= 0; i--) o = accumulator.apply(o, content.get(i));

        return o;
    }

    @Override
    default <R> R reduceLeft(Function<E, R> er, BiFunction<R, ? super E, R> accumulator) {
        Seq<E> content = getContent();
        R o = er.apply(content.get(0));
        for (int i = 1; i < content.size(); i++) o = accumulator.apply(o, content.get(i));
        return o;
    }

    @Override
    default <R> R reduceRight(Function<E, R> er, BiFunction<R, ? super E, R> accumulator) {
        Seq<E> content = getContent();
        R o = er.apply(content.get(content.size() - 1));
        for (int i = content.size() - 2; i >= 0; i--) o = accumulator.apply(o, content.get(i));
        return o;
    }
}
