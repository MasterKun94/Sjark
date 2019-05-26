package util.option;

import util.option.sjark.Sjark;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public interface AsyncPiper<T, E> {
    Sjark<CompletableFuture<T>> getHeadSjark();

    Sjark<CompletableFuture<E>> getTailSjark();

    <R> AsyncPiper<T, R> map(Function<? super E, ? extends R> mapper);

    <R> AsyncPiper<T, R> flatMap(Function<? super E, ? extends Collection<? extends R>> flatMapper);

    <R> AsyncPiper<T, R> ref(Piper<E, R> quotePip);

    AsyncPiper<T, E> annotate(Consumer<? super E> annotate);

    Process.If<T, E> _if(Predicate<? super E> predicate);

    Process.While<T, E> _while(Predicate<? super E> predicate);

    AsyncPiper<T, E> _index();

    <R> AsyncPiper<T, R> _goto();

    Process.End<T> _return(Consumer<E> consumer);
}
