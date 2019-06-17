package futureUtil;

public interface TaskListener<T> {
    void handle(T t) throws InterruptedException;

    default void catchException(InterruptedException e) {
        e.printStackTrace();
    }

}
