package piplineBuilder;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class TrFlange<E> {

    private Predicate<? super E> ifElse;
//    private Consumer<E> thisConsumer;
//    private Consumer<E> thatConsumer;

    private Flange<E> thisFlange;
    private Flange<E> thatFlange;

    public TrFlange(Predicate<? super E> predicate) {
        ifElse = predicate;
        thisFlange = new Flange<>();
        thatFlange = new Flange<>();
    }

    public TrFlange<E> _then(Consumer<E> thisConsumer) {
        thisFlange.setPip(thisConsumer);
        return this;
    }

    public TrFlange<E> _else(Consumer<E> thatConsumer) {
        thatFlange.setPip(thatConsumer);
        return this;
    }

    public Consumer<E> getPip() {
        return e -> (ifElse.test(e) ? thisFlange.getPip() : thatFlange.getPip()).accept(e);//TODO
    }

    public Flange<E> getThatFlange() {
        return thatFlange;
    }
}
