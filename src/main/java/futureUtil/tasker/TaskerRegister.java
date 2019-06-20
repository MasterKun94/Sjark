package futureUtil.tasker;

import java.util.function.Supplier;

public interface TaskerRegister<T> {

    void request(String taskId, Supplier taskSupplier);

    int getInt(String taskId);

    long getLong(String taskId);

    String getString(String taskId);

    <T> T getObject(String taskId);

}
