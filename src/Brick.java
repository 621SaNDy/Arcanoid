import java.awt.Color;

public class Brick {
    int x, y, width, height;
    boolean isDestroyed;
    boolean indestructible;
    Color color;
    int powerUpChance;
    int powerUpType;

    private long hitTime = 0;
    private boolean isHit = false;

    public Brick(int x, int y, int width, int height, boolean indestructible) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.isDestroyed = false;
        this.indestructible = indestructible;
        this.color = indestructible ? new Color(120, 120, 120) : Color.GRAY;
        this.powerUpChance = 0;
        this.powerUpType = -1;
    }

    public Brick(int x, int y, int width, int height, boolean indestructible, Color color, int powerUpChance) {
        this(x, y, width, height, indestructible);
        this.color = color;
        this.powerUpChance = powerUpChance;
        if (Math.random() * 100 < powerUpChance) {
            this.powerUpType = Math.random() < 0.5 ? PowerUp.TYPE_WIDE_PADDLE : PowerUp.TYPE_MULTI_BALL;
        }
    }

    public void hit() {
        if (!indestructible) {
            isHit = true;
            hitTime = System.currentTimeMillis();
        }
    }

    public boolean shouldShowHitEffect() {
        return isHit && (System.currentTimeMillis() - hitTime < 200);
    }
}