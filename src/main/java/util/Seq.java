package util;

import util.Tools.Copy;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public interface Seq<E> extends Traversable<E>, List<E>{

    Stream<E> toStream();

    Seq<E> getContent();

    @Override
    default boolean forAll(Predicate<? super E> filter) {
        Predicate<? super E> finalFilter = filter.negate();
        return !exists(finalFilter);
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
    default E foldLeft(E e, BiFunction<E, E, E> function) {
        E o = Copy.doCopy(e);

        for (E element : getContent()) {
            o = function.apply(element, o);
        }
        return o;
    }

    @Override
    default E foldRight(E e, BiFunction<E, E, E> function) {
        Seq<E> content = getContent();
        E o = Copy.doCopy(e);
        for (int i = content.size() - 1; i >= 0; i--) o = function.apply(content.get(i), o);
        return o;
    }

    @Override
    default <R> R foldLeft(E e, Function<E, R> er, BiFunction<E, R, R> err) {
        R r = er.apply(e);
        for (E element : getContent()) {
            r = err.apply(element, r);
        }
        return r;
    }

    @Override
    default <R> R foldRight(E e, Function<E, R> er, BiFunction<E, R, R> err) {
        Seq<E> content = getContent();
        R r = er.apply(e);
        for (int i = content.size() - 1; i >= 0; i--) r = err.apply(content.get(i), r);
        return r;
    }

    @Override
    default E reduceLeft(BiFunction<E, E, E> function) {
        Seq<E> content = getContent();
        E o = Copy.doCopy(content.get(0));
        for (int i = 1; i < content.size(); i++) o = function.apply(content.get(i), o);
        return o;
    }

    @Override
    default E reduceRight(BiFunction<E, E, E> function) {
        Seq<E> content = getContent();
        E o = Copy.doCopy(content.get(content.size() - 1));
        for (int i = content.size() - 2; i >= 0; i--) o = function.apply(content.get(i), o);

        return o;
    }

    @Override
    default <R> R reduceLeft(Function<E, R> er, BiFunction<E, R, R> err) {
        Seq<E> content = getContent();
        R o = er.apply(content.get(0));
        for (int i = 1; i < content.size(); i++) o = err.apply(content.get(i), o);
        return o;
    }

    @Override
    default <R> R reduceRight(Function<E, R> er, BiFunction<E, R, R> err) {
        Seq<E> content = getContent();
        R o = er.apply(content.get(content.size() - 1));
        for (int i = content.size() - 2; i >= 0; i--) o = err.apply(content.get(i), o);
        return o;
    }
}
