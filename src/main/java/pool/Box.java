package pool;

public class Box {
    private static final int EMPTY = -1;
    private static final int SEND_BACK = -2;

    private volatile int pointer;

    public void setPointer(int pointer) {
        this.pointer = pointer;
    }

    public int getPointer() {
        return pointer;
    }

    public void sendBack() {
        pointer = SEND_BACK;
    }

    public boolean isSendBack() {
        return pointer == SEND_BACK;
    }

    public void empty() {
        pointer = EMPTY;
    }

    public boolean isEmpty() {
        return pointer == EMPTY;
    }

    public static Box emptyBox() {
        Box box = new Box();
        box.pointer = EMPTY;
        return box;
    }
}
