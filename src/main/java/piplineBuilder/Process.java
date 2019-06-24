package piplineBuilder;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import java.util.function.Function;

import static piplineBuilder.PipBuilder.start;

public class Process {

    public static class If<T, P> {
        private TrFlange<P> trFlange;
        private Flange<T> headFlange;
        If(TrFlange<P> trFlange, Flange<T> headFlange) {
            this.trFlange = trFlange;
            this.headFlange = headFlange;
        }

        public Then<T, P> _then(Function<Piper<P, P>, End<P>> then) {
            TrFlange<P> newIf = trFlange._then(then.apply(start()).getPip());
            return new Then<>(newIf, headFlange);
        }

        public Piper<T, P> _do(Function<Piper<P, P>, Piper<P, P>> then) {
            Flange<P> newFlange = new Flange<>();
            trFlange._then(then.apply(start())._return(newFlange.getPip()).getPip());
            trFlange._else(newFlange.getPip());
            return PipBuilder.start(headFlange, newFlange);
        }
    }

    public static class Then<T, P> {
        private TrFlange<P> trFlange;
        private Flange<T> headFlange;
        Then(TrFlange<P> trFlange, Flange<T> headFlange) {
            this.trFlange = trFlange;
            this.headFlange = headFlange;
        }

        public End<T> _else(Function<Piper<P, P>, End<P>> orElse) {
            trFlange._else(orElse.apply(start()).getPip());
            return new End<>(this.headFlange);
        }
    }

    public static class While<T, P> {
        private TrFlange<P> trFlange;
        private Flange<T> headFlange;
        While(TrFlange<P> trFlange, Flange<T> headFlange) {
            this.trFlange = trFlange;
            this.headFlange = headFlange;
        }

        public Piper<T, P> _do(Function<Piper<P, P>, Piper<P, P>> then) {
            Function<Piper<P, P>, End<P>> afterWhile = pip -> then.apply(pip)._return(trFlange.getPip());
            trFlange._then(afterWhile.apply(start()).getPip());
            return PipBuilder.start(headFlange, trFlange.getThatFlange());
        }
    }

    public static class End<T> {
        private Flange<T> headFlange;
        End(Flange<T> headFlange) {
            this.headFlange = headFlange;
        }

        public Consumer<T> getPip() {
            return headFlange.getPip();
        }

        public void run(T source) {
            headFlange.getPip().accept(source);
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
