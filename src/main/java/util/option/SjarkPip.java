package util.option;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class SjarkPip<E> implements Sjark<E> {

    private Supplier<Consumer<E>> pip;

    public Consumer<E> getPip() {
        return pip.get();
    }

    public Sjark<E> sink(Consumer<E> sink) {
        pip = () -> sink;
        return this;
    }

    public <R> Sjark<R> map(Function<? super E, ? extends R> mapper) {
        SjarkPip<R> nextPip = new SjarkPip<>();
        pip = () -> e -> nextPip.getPip().accept(mapper.apply(e));
        return nextPip;
    }

    public SjarkIf<E> sjarkIf(Predicate<E> it) {
        SjarkIf<E> sjarkIf = new SjarkIf<>(it);
        pip = () -> e -> sjarkIf.getPip().accept(e);
        return sjarkIf;
    }



    public static void main(String[] args) {
        Sjark<Integer> integerSjark = new SjarkPip<>();
        integerSjark
                .map(integer -> integer * 2)
//                .map(integer -> integer.compareTo(123123))
                .map(Object::toString)
                .map(String::hashCode)
                .map(Object::toString)
                .map(String::toUpperCase)
                .sink(System.out::println);
        integerSjark.getPip().accept(123123);
    }
}
