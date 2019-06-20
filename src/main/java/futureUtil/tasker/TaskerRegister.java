package futureUtil.tasker;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public interface TaskerRegister<IN, OUT> {

    CompletableFuture<OUT> request(String taskId, IN in);

    void addWorker(String taskId, Function<IN, OUT> worker);

    int count(String taskId);

    int countTask();

    Set<String> taskIdSet();


}
