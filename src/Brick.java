public class Brick {
    int x, y, width, height;
    boolean isDestroyed;
    boolean indestructible;

    public Brick(int x, int y, int width, int height, boolean indestructible) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.isDestroyed = false;
        this.indestructible = indestructible;
    }
}