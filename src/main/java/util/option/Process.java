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

        public Then<T, P> _then(Function<PipBuilder<P, P>, End<P>> then) {
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

        public End<T> _else(Function<PipBuilder<P, P>, End<P>> orElse) {
            sjarkIf._else(orElse.apply(start()).getPip());
            return new End<>(this.pipGetter);
        }

//        public PipBuilder<T, P> _else() {
//            Sjark<P> sjark = new Sjark<>();
//            Consumer<P>
//            sjark.setPip(sjarkIf.getPip());
//
//            PipBuilder<T, P> orElse = new PipBuilder<>();
//            sjarkIf._else(orElse.);
//            return null;
//        }
    }

    public static class While<T, P> {
        private SjarkIf<P> sjarkIf;
        private Supplier<Consumer<T>> pipGetter;
        While(SjarkIf<P> sjarkIf, Supplier<Consumer<T>> pipGetter) {
            this.sjarkIf = sjarkIf;
            this.pipGetter = pipGetter;
        }

        public Then<T, P> _do(Function<PipBuilder<P, P>, PipBuilder<P, P>> then) {
//            return new Then<>(newIf, pipGetter);
            return null;
        }
    }

    public static class End<T> {
        private Supplier<Consumer<T>> pipGetter;
        End(Supplier<Consumer<T>> pipGetter) {
            this.pipGetter = pipGetter;
        }

        public Consumer<T> getPip() {
            return pipGetter.get();
        }
    }
}
