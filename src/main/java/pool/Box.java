package pool;

public class Box {
    private static final int EMPTY = -1;
    private static final int SEND_BACK = -2;

    private volatile int payload;

    public void setPayload(int payload) {
        this.payload = payload;
    }

    public int getPayload() {
        return payload;
    }

    public void sendBack() {
        payload = SEND_BACK;
    }

    public boolean isSendBack() {
        return payload == SEND_BACK;
    }

    public void empty() {
        payload = EMPTY;
    }

    public boolean isEmpty() {
        return payload == EMPTY;
    }

    public static Box emptyBox() {
        Box box = new Box();
        box.payload = EMPTY;
        return box;
    }
}
