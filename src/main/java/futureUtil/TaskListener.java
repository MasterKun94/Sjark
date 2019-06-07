package futureUtil;

public abstract class TaskListener<T> {
    public abstract void whileGet(T t) throws InterruptedException;

    public abstract void catchException(InterruptedException e);

    private void handle(T t) {
        try {
            whileGet(t);
        } catch (InterruptedException e) {
            catchException(e);
        }
    }
}
