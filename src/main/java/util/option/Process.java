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

        public PipBuilder<T, P> _do(Function<PipBuilder<P, P>, PipBuilder<P, P>> then) {
            Sjark<P> newSjark = new Sjark<>();
            sjarkIf._then(then.apply(start())._return(newSjark.getPip()).getPip());
            sjarkIf._else(newSjark.getPip());
            return PipBuilder.start(pipGetter, newSjark);
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
    }

    public static class While<T, P> {
        private SjarkIf<P> sjarkIf;
        private Supplier<Consumer<T>> pipGetter;
        While(SjarkIf<P> sjarkIf, Supplier<Consumer<T>> pipGetter) {
            this.sjarkIf = sjarkIf;
            this.pipGetter = pipGetter;
        }

        public PipBuilder<T, P> _do(Function<PipBuilder<P, P>, PipBuilder<P, P>> then) {
            Function<PipBuilder<P, P>, End<P>> afterWhile = pip -> then.apply(pip)._return(sjarkIf.getPip());
            sjarkIf._then(afterWhile.apply(start()).getPip());
            return PipBuilder.start(pipGetter, sjarkIf.getThatSjark());
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
