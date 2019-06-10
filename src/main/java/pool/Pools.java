package pool;

public class Pools {
    public static <T> Pool<T> immutablePool(int capacity, Class<T> clazz) {
        return new ImmutablePool<>(capacity, clazz);
    }
}
