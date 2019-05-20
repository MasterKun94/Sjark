package util.option;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static util.option.PipBuilder.start;

class Process {

    public static class If<T, P> {
        private SjarkIf<P> sjarkIf;
        private Supplier<Consumer<T>> pipGetter;
        If(SjarkIf<P> sjarkIf, Supplier<Consumer<T>> pipGetter) {
            this.sjarkIf = sjarkIf;
            this.pipGetter = pipGetter;
        }

        public Then<T, P> _then___(Function<PipBuilder<P, P>, End<P>> then) {
            SjarkIf<P> newIf = sjarkIf._then(then.apply(start()).getPip());
            return new Then<>(newIf, pipGetter);
        }
    }

    public static class Then<T, P> {
        private SjarkIf<P> sjarkIf;
        private Supplier<Consumer<T>> pipGetter;
        Then(SjarkIf<P> sjarkIf, Supplier<Consumer<T>> pipGetter) {
            this.sjarkIf = sjarkIf;
            this.pipGetter = pipGetter;
        }

        public End<T> _else___(Function<PipBuilder<P, P>, End<P>> orElse) {
            sjarkIf._else(orElse.apply(start()).getPip());
            return new End<>(this.pipGetter);
        }
    }

    public static class End<O> {
        private Supplier<Consumer<O>> pipGetter;
        End(Supplier<Consumer<O>> pipGetter) {
            this.pipGetter = pipGetter;
        }

        public Consumer<O> getPip() {
            return pipGetter.get();
        }
    }
}
