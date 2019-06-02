package Tools;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ThisOrElse {
    public static <E> Consumer<E> accept(Predicate<E> ifThis, Consumer<E> thenAccept, Consumer<E> orElseAccept) {
        return e -> (ifThis.test(e) ? thenAccept : orElseAccept).accept(e);
    }

    public static <E> Consumer<E> acceptThis(Predicate<? super E> ifThis, Consumer<? super E> thenGet) {
        return e -> {
            if (ifThis.test(e)) {
                thenGet.accept(e);
            }
        };
    }

    public static <E, R> Function<E, R> apply(Predicate<E> ifThis, Function<E, R> thenDo, Function<E, R> orElseDo) {
        return e -> (ifThis.test(e) ? thenDo : orElseDo).apply(e);
    }

    public static <T, E> Function<T, E> get(Predicate<T> ifThis, Supplier<E> thenGet, Supplier<E> orElseGet) {
        return e -> (ifThis.test(e) ? thenGet : orElseGet).get();
    }
}
