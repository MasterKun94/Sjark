package futureUtil.tasker;

import futureUtil.MultiTask;

public class Box<T> {
    private final String id;
    private volatile T goods;

    public Box(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public T getGoods() {
        return goods;
    }

    public void setGoods(T goods) {
        this.goods = goods;
    }

    public static <T> T requestFrom(MultiTask<Box<T>> multiTask) {
        return null;
    }
}
