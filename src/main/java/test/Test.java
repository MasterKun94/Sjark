package test;

import util.option.PipBuilder;

public class Test {
    public static void main(String[] args) {
        PipBuilder.<Integer>start()
                ._do_____(integer -> integer * 2)
                ._do_____(Object::toString)
                ._do_____(String::hashCode)
                ._do_____(integer -> integer * 31 + 19)
                ._if_____(i -> i < 0)
                ._then___(pip -> pip
                        ._do_____(i -> i % 10)
                        ._do_____(Object::toString)
                        ._do_____(s -> s + ": i < 0")
                        ._return_(System.out::println)
                )
                ._else___(pip -> pip
                        ._do_____(i -> i % 10)
                        ._do_____(Object::toString)
                        ._do_____(s -> s + ": i > 0")
                        ._return_(System.out::println)
                )
                .getPip().accept(123123);
    }
}
