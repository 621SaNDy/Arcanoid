public class PowerUp {
    public static final int TYPE_WIDE_PADDLE = 0;
    public static final int TYPE_MULTI_BALL = 1;

    int x, y;
    int width = 25;
    int height = 15;
    int type;
    int speedY = 2;
    boolean active = true;

    public PowerUp(int x, int y, int type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }

    public void move() {
        y += speedY;
    }
}

