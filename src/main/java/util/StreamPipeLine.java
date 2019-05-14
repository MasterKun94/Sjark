package util;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class StreamPipeLine<IN, OUT> {
    private Supplier<IN> supplier;
    private Function<IN, OUT> pip;
    private Consumer<OUT> consumer;

    public Supplier<IN> getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier<IN> supplier) {
        this.supplier = supplier;
    }

    public Function<IN, OUT> getPip() {
        return pip;
    }

    public void setPip(Function<IN, OUT> pip) {
        this.pip = pip;
    }

    public Consumer<OUT> getConsumer() {
        return consumer;
    }

    public void setConsumer(Consumer<OUT> consumer) {
        this.consumer = consumer;
    }
}
