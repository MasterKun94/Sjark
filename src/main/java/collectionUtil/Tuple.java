package collectionUtil;

import java.util.Objects;

public class Tuple<E, R> {
    public E _1;
    public R _2;

    public static <E, R> Tuple<E, R> of(E first, R second) {
        return new Tuple<>(first, second);
    }

    private Tuple(E first, R second) {
        _1 = Objects.requireNonNull(first);
        _2 = Objects.requireNonNull(second);
    }

    @Override
    public String toString() {
        return "(" + _1 + ", " + _2 + ")";
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof Tuple)) return false;
        final Tuple<?, ?> other = (Tuple<?, ?>) o;
        return this._1.equals(other._1) && this._2.equals(other._2);
    }

    @Override
    public int hashCode() {
        return _1.hashCode() * 37 + _2.hashCode() * 59 + 23;
    }

    public int firstHash() {
        return _1.hashCode() * 37 + 41;
    }
}
