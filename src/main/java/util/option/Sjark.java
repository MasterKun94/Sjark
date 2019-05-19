package util.option;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class Sjark<E> {

    private Consumer<E> pip;

    public Consumer<E> getPip() {
        return pip;
    }

    public Sjark<E> _return(Consumer<E> sink) {
        pip = sink;
        return this;
    }

    public Sjark<E> _declare(Consumer<? super E> doer) {
        Sjark<E> nextSjark = new Sjark<>();
        pip = e -> {
            doer.accept(e);
            nextSjark.pip.accept(e);
        };
        return nextSjark;
    }

    public <R> Sjark<R> _do(Function<? super E, ? extends R> mapper) {
        Sjark<R> nextSjark = new Sjark<>();
        pip = e -> nextSjark.pip.accept(mapper.apply(e));
        return nextSjark;
    }

    public SjarkIf<E> _if(Predicate<E> predicate) {
        SjarkIf<E> sjarkIf = new SjarkIf<>(predicate);
        pip = e -> sjarkIf.getPip().accept(e);
        return sjarkIf;
    }

    public Sjark<E> _index(Sjark<E> sjark) {
        sjark.pip = e -> pip.accept(e);
        return sjark;
    }

    public Sjark<E> _goto(Sjark<E> sjark) {
        pip = sjark.pip;
        return this;
    }




    public static void main(String[] args) {
        Sjark<Integer> integerSjark = new Sjark<>();
        integerSjark
                ._do(integer -> integer * 2)
//                .map(integer -> integer.compareTo(123123))
                ._do(Object::toString)
                ._do(String::hashCode)
                ._do(integer -> integer * 31 + 19)
                ._if(i -> i > 0)
                ._then(
                        System.out::println
                )._else(
                        i -> System.out.println(123123)
                );

        integerSjark.getPip().accept(123123);
    }
}
