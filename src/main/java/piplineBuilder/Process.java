package piplineBuilder;

import piplineBuilder.sjark.Sjark;
import piplineBuilder.sjark.SjarkIf;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import java.util.function.Function;

import static piplineBuilder.PipBuilder.start;

public class Process {

    public static class If<T, P> {
        private SjarkIf<P> sjarkIf;
        private Sjark<T> headSjark;
        If(SjarkIf<P> sjarkIf, Sjark<T> headSjark) {
            this.sjarkIf = sjarkIf;
            this.headSjark = headSjark;
        }

        public Then<T, P> _then(Function<Piper<P, P>, End<P>> then) {
            SjarkIf<P> newIf = sjarkIf._then(then.apply(start()).getPip());
            return new Then<>(newIf, headSjark);
        }

        public Piper<T, P> _do(Function<Piper<P, P>, Piper<P, P>> then) {
            Sjark<P> newSjark = new Sjark<>();
            sjarkIf._then(then.apply(start())._return(newSjark.getPip()).getPip());
            sjarkIf._else(newSjark.getPip());
            return PipBuilder.start(headSjark, newSjark);
        }
    }

    public static class Then<T, P> {
        private SjarkIf<P> sjarkIf;
        private Sjark<T> headSjark;
        Then(SjarkIf<P> sjarkIf, Sjark<T> headSjark) {
            this.sjarkIf = sjarkIf;
            this.headSjark = headSjark;
        }

        public End<T> _else(Function<Piper<P, P>, End<P>> orElse) {
            sjarkIf._else(orElse.apply(start()).getPip());
            return new End<>(this.headSjark);
        }
    }

    public static class While<T, P> {
        private SjarkIf<P> sjarkIf;
        private Sjark<T> headSjark;
        While(SjarkIf<P> sjarkIf, Sjark<T> headSjark) {
            this.sjarkIf = sjarkIf;
            this.headSjark = headSjark;
        }

        public Piper<T, P> _do(Function<Piper<P, P>, Piper<P, P>> then) {
            Function<Piper<P, P>, End<P>> afterWhile = pip -> then.apply(pip)._return(sjarkIf.getPip());
            sjarkIf._then(afterWhile.apply(start()).getPip());
            return PipBuilder.start(headSjark, sjarkIf.getThatSjark());
        }
    }

    public static class End<T> {
        private Sjark<T> headSjark;
        End(Sjark<T> headSjark) {
            this.headSjark = headSjark;
        }

        public Consumer<T> getPip() {
            return headSjark.getPip();
        }

        public void run(T source) {
            headSjark.getPip().accept(source);
        }

        public void run(Collection<T> sources) {
            sources.forEach(getPip());
        }

        public void runAsync(T source, ExecutorService service) {
            service.submit(() -> run(source));
        }

        public void runAsync(Collection<T> sources, ExecutorService service) {
            sources.forEach(t -> runAsync(t, service));
        }
    }
}