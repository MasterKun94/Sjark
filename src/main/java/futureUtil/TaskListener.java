package futureUtil;

public interface TaskListener<T> {
    void handle(T t) throws InterruptedException;

    default void catchException(Exception e) {
        e.printStackTrace();
    }

}
