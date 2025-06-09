import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static javax.swing.BorderFactory.*;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Arkanoid");
        MenuPanel menuPanel = new MenuPanel(frame);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 600);
        frame.add(menuPanel);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}

class MenuPanel extends JPanel {
    private final JFrame frame;    public MenuPanel(JFrame frame) {
        this.frame = frame;
        this.setLayout(new BorderLayout());

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.setBorder(createEmptyBorder(60, 0, 60, 0));

        JLabel titleLabel = new JLabel("ARKANOID", JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 56));
        titleLabel.setForeground(new Color(255, 215, 0));
        titleLabel.setBorder(createEmptyBorder(0, 0, 20, 0));

        titleLabel.setText("<html><div style='text-align: center; text-shadow: 2px 2px 4px rgba(0,0,0,0.5);'>ARKANOID</div></html>");
        JLabel subtitleLabel = new JLabel("Choose difficulty level", JLabel.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        subtitleLabel.setForeground(new Color(220, 220, 220));titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(subtitleLabel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 0, 25));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(createEmptyBorder(0, 200, 120, 200));        JButton easyButton = createStyledButton("EASY", new Color(76, 175, 80), new Color(56, 142, 60));
        JButton mediumButton = createStyledButton("MEDIUM", new Color(255, 152, 0), new Color(245, 124, 0));
        JButton hardButton = createStyledButton("HARD", new Color(244, 67, 54), new Color(211, 47, 47));

        easyButton.addActionListener(_ -> startGame("EASY"));
        mediumButton.addActionListener(_ -> startGame("MEDIUM"));
        hardButton.addActionListener(_ -> startGame("HARD"));

        buttonPanel.add(easyButton);
        buttonPanel.add(mediumButton);
        buttonPanel.add(hardButton);

        this.add(titlePanel, BorderLayout.NORTH);
        this.add(buttonPanel, BorderLayout.CENTER);
    }    private JButton createStyledButton(String text, Color primaryColor, Color darkColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 22));
        button.setForeground(Color.WHITE);
        button.setBackground(primaryColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(300, 50));

        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createRaisedBevelBorder(),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(darkColor);
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLoweredBevelBorder(),
                        BorderFactory.createEmptyBorder(10, 20, 10, 20)
                ));
            }
            public void mouseExited(MouseEvent evt) {
                button.setBackground(primaryColor);
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createRaisedBevelBorder(),
                        BorderFactory.createEmptyBorder(10, 20, 10, 20)
                ));
            }
        });

        return button;
    }

    @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        GradientPaint gradient = new GradientPaint(
                0, 0, new Color(25, 25, 60),
                0, getHeight(), new Color(10, 10, 30)
        );
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        g2d.setColor(new Color(255, 255, 255, 8));
        for (int i = 0; i < getWidth(); i += 40) {
            for (int j = 0; j < getHeight(); j += 40) {
                g2d.fillOval(i, j, 2, 2);
            }
        }
    }

    private void startGame(String difficulty) {
        GamePanel gamePanel = new GamePanel(difficulty);
        frame.getContentPane().removeAll();
        frame.add(gamePanel);
        frame.revalidate();
        frame.repaint();
        gamePanel.requestFocusInWindow();
    }
}

class GamePanel extends JPanel implements KeyListener, ActionListener {
    Timer timer;
    Timer moveTimer;
    int paddleX = 400;
    int paddleWidth = 150;
    int paddleHeight = 10;
    int paddleSpeed = 15;    float initialPaddleSpeed = 10;
    float maxPaddleSpeed = 20;
    float paddleAcceleration = 0.01f;

    int ballXDir = -3;
    int ballYDir = -3;
    boolean ballLaunched = false;

    java.util.List<Ball> balls = new java.util.ArrayList<>();
    int ballSize = 12;


    Timer powerUpEffectTimer;
    java.util.List<PowerUp> activePowerUps = new java.util.ArrayList<>();

    int difficultyLevel = 1;

    boolean isMovingLeft = false;
    boolean isMovingRight = false;

    Brick[] bricks;
    int numBricks = 27;

    int countdown = 3;
    boolean gameStarted = false;
    private String difficulty;

    private float ballSpeed = 1;
    private long startTime;
    private int speedIncreaseInterval = 4000;
    private int navbarHeight = 40;
    private long gameStartTimeMillis;
    private long gameTimeSeconds;
    private Timer gameTimeTimer;
    private String lastMessage = "";
    private long messageTime = 0;
    private static final long MESSAGE_DURATION = 3000;
    private static final long ANIMATION_DURATION = 500;
    private float messageAlpha = 0.0f;
    private float messageScale = 1.0f;
    private int messageY = 0;
    private boolean messageAnimating = false;

    private BufferedImage backgroundImage;
    private boolean widePaddleActive = false;
    private int originalPaddleWidth;
    private long powerUpStartTime;
    private static final int POWER_UP_DURATION = 10000;
    private float currentPaddleWidth;
    private float targetPaddleWidth;
    private static final float PADDLE_ANIMATION_SPEED = 3.0f;


    private void startGame(String difficulty) {
        this.difficulty = difficulty;
        gameStarted = true;
        startTime = System.currentTimeMillis();

        gameStartTimeMillis = System.currentTimeMillis();
        gameTimeSeconds = 0;
        initGameTimeTimer();

        String difficultyMessage = difficulty + " Level Started!";
        showMessage(difficultyMessage);

        adjustDifficulty();
        initializeBricks();
        initializeBalls();
        activePowerUps.clear();
        widePaddleActive = false;
        originalPaddleWidth = paddleWidth;

        currentPaddleWidth = paddleWidth;
        targetPaddleWidth = paddleWidth;

        this.requestFocus();
    }

    private void initGameTimeTimer() {
        if (gameTimeTimer != null && gameTimeTimer.isRunning()) {
            gameTimeTimer.stop();
        }

        gameTimeTimer = new Timer(1000, e -> {
            gameTimeSeconds = (System.currentTimeMillis() - gameStartTimeMillis) / 1000;
            repaint();
        });
        gameTimeTimer.start();
    }

    private void showMessage(String message) {
        if (System.currentTimeMillis() - messageTime < 800) {
            return;
        }

        lastMessage = message;
        messageTime = System.currentTimeMillis();
        messageAnimating = true;
        messageAlpha = 0.0f;
        messageScale = 0.5f;
        messageY = navbarHeight / 2;
    }

    private void initializeBalls() {
        balls.clear();
        Ball mainBall = new Ball(
                paddleX + paddleWidth / 2 - ballSize / 2,
                getHeight() - paddleHeight - 30 - ballSize,
                ballSize,
                0,
                0
        );
        balls.add(mainBall);
    }

    public GamePanel(String difficulty) {
        this.difficulty = difficulty;
        this.setFocusable(true);
        this.addKeyListener(this);

        File file = new File("img/background.jpg");
        if (!file.exists()) {
            System.out.println("File does not exist: " + file.getAbsolutePath());
        } else {
            try {
                backgroundImage = ImageIO.read(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }        adjustDifficulty();

        currentPaddleWidth = paddleWidth;
        targetPaddleWidth = paddleWidth;

        timer = new Timer(10, this);
        timer.start();

        moveTimer = new Timer(10, _ -> movePaddle());
        moveTimer.start();

        initializeBricks();
        startCountdown();
    }

    private void movePaddle() {
        if (paddleSpeed < maxPaddleSpeed) {
            paddleSpeed += paddleAcceleration;
        }

        if (Math.abs(currentPaddleWidth - targetPaddleWidth) > 0.5f) {
            if (currentPaddleWidth < targetPaddleWidth) {
                currentPaddleWidth += PADDLE_ANIMATION_SPEED;
                if (currentPaddleWidth > targetPaddleWidth) {
                    currentPaddleWidth = targetPaddleWidth;
                }
            } else {
                currentPaddleWidth -= PADDLE_ANIMATION_SPEED;
                if (currentPaddleWidth < targetPaddleWidth) {
                    currentPaddleWidth = targetPaddleWidth;
                }
            }
            paddleWidth = (int) currentPaddleWidth;
        }

        if (isMovingLeft && paddleX > 0) {
            paddleX -= paddleSpeed;
        }
        if (isMovingRight && paddleX < getWidth() - paddleWidth) {
            paddleX += paddleSpeed;
        }
        repaint();
    }

    private void adjustDifficulty() {
        switch (difficulty) {
            case "EASY":
                ballXDir = -3;
                ballYDir = -3;
                paddleWidth = 150;
                speedIncreaseInterval = 10000;
                initialPaddleSpeed = 8;
                maxPaddleSpeed = 15;
                paddleAcceleration = 0.005f;
                break;
            case "MEDIUM":
                ballXDir = -4;
                ballYDir = -4;
                paddleWidth = 120;
                speedIncreaseInterval = 6000;
                initialPaddleSpeed = 10;
                maxPaddleSpeed = 18;
                paddleAcceleration = 0.01f;
                break;
            case "HARD":
                ballXDir = -5;
                ballYDir = -5;
                paddleWidth = 100;
                speedIncreaseInterval = 3000;
                initialPaddleSpeed = 12;
                maxPaddleSpeed = 20;
                paddleAcceleration = 0.015f;
                break;        }
        ballSpeed = 1;
        paddleSpeed = (int)initialPaddleSpeed;
    }

    private void initializeBricks() {
        int brickWidth = 70;
        int brickHeight = 25;
        int cols = 9;
        int spacing = ballSize + 5;
        int totalWidth = cols * (brickWidth + spacing) - spacing;
        int startX = (900 - totalWidth) / 2;

        bricks = new Brick[numBricks];

        Color[] rowColors = {
                new Color(255, 0, 0),
                new Color(255, 165, 0),
                new Color(255, 255, 0),
                new Color(0, 255, 0),
                new Color(0, 0, 255),
                new Color(75, 0, 130),
                new Color(238, 130, 238)
        };

        for (int i = 0; i < numBricks; i++) {
            int row = i / cols;
            int col = i % cols;
            boolean indestructible = false;
            int powerUpChance = 0;

            switch(difficulty) {
                case "EASY":
                    powerUpChance = 30;
                    break;
                case "MEDIUM":
                    if (Math.random() < 0.15) {
                        indestructible = true;
                    }
                    powerUpChance = 20;
                    break;
                case "HARD":
                    if (Math.random() < 0.25 || row == 0) {
                        indestructible = true;
                    }
                    powerUpChance = 15;
                    break;
            }
            Color brickColor = indestructible ? Color.GRAY : rowColors[row % rowColors.length];

            bricks[i] = new Brick(
                    startX + col * (brickWidth + spacing),
                    50 + row * (brickHeight + spacing),
                    brickWidth,
                    brickHeight,
                    indestructible,
                    brickColor,
                    powerUpChance
            );
        }
    }

    private void startCountdown() {
        Timer countdownTimer = new Timer(1000, e -> {
            if (countdown > 1) {
                countdown--;
            } else {
                countdown = 0;
                gameStarted = true;
                startGame(difficulty);
                this.requestFocus();
                ((Timer)e.getSource()).stop();
            }
            repaint();
        });
        countdownTimer.start();
    }

    private void resetGame() {
        isMovingLeft = false;
        isMovingRight = false;

        paddleX = 375;
        paddleWidth = originalPaddleWidth;
        widePaddleActive = false;
        ballSpeed = 1;

        balls.clear();
        activePowerUps.clear();

        adjustDifficulty();
        initializeBricks();

        countdown = 3;
        gameStarted = false;
        startCountdown();

        if (!timer.isRunning()) timer.start();
        if (!moveTimer.isRunning()) moveTimer.start();

        if (powerUpEffectTimer != null && powerUpEffectTimer.isRunning()) {
            powerUpEffectTimer.stop();
        }

        this.requestFocusInWindow();
    }


    private boolean checkWinCondition() {
        for (Brick brick : bricks) {
            if (!brick.isDestroyed && !brick.indestructible) {
                return false;
            }
        }
        return true;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Point hotSpot = new Point(0,0);
        BufferedImage cursorImage = new BufferedImage(1, 1, BufferedImage.TRANSLUCENT);
        Cursor invisibleCursor = toolkit.createCustomCursor(cursorImage, hotSpot, "InvisibleCursor");
        setCursor(invisibleCursor);

        if (backgroundImage != null) {
            g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(15, 15, 35),
                    0, getHeight(), new Color(5, 5, 20)
            );
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, getWidth(), getHeight());

            g2d.setColor(new Color(255, 255, 255, 30));
            for (int i = 0; i < 100; i++) {
                int x = (int)(Math.random() * getWidth());
                int y = (int)(Math.random() * getHeight());
                int size = (int)(Math.random() * 3) + 1;
                g2d.fillOval(x, y, size, size);
            }
        }
        if (!gameStarted) {
            g2d.setColor(new Color(0, 0, 0, 200));
            g2d.fillRect(0, 0, getWidth(), getHeight());

            long time = System.currentTimeMillis();
            int pulseAlpha = (int)(Math.sin(time * 0.01) * 30 + 50);
            g2d.setColor(new Color(255, 215, 0, pulseAlpha));
            g2d.fillRect(0, 0, getWidth(), getHeight());

            int rectWidth = 200;
            int rectHeight = 100;
            int rectX = (getWidth() - rectWidth) / 2;
            int rectY = (getHeight() - rectHeight) / 2;

            GradientPaint countdownBg = new GradientPaint(
                    rectX, rectY, new Color(40, 40, 70, 220),
                    rectX, rectY + rectHeight, new Color(20, 20, 40, 240)
            );
            g2d.setPaint(countdownBg);
            g2d.fillRoundRect(rectX, rectY, rectWidth, rectHeight, 20, 20);

            g2d.setColor(new Color(255, 215, 0, 180));
            g2d.setStroke(new BasicStroke(3));
            g2d.drawRoundRect(rectX, rectY, rectWidth, rectHeight, 20, 20);

            g2d.setColor(new Color(0, 0, 0, 100));
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 72));
            String countdownText = String.valueOf(countdown);
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(countdownText);
            int textHeight = fm.getHeight();

            g2d.drawString(countdownText,
                    (getWidth() - textWidth) / 2 + 3,
                    (getHeight() + textHeight/3) / 2 + 3);

            g2d.setColor(Color.WHITE);
            g2d.drawString(countdownText,
                    (getWidth() - textWidth) / 2,
                    (getHeight() + textHeight/3) / 2);
            g2d.setColor(new Color(200, 200, 200));
            g2d.setFont(new Font("Segoe UI", Font.PLAIN, 18));
            String subtitle = "Get ready...";
            FontMetrics fmSmall = g2d.getFontMetrics();
            int subtitleWidth = fmSmall.stringWidth(subtitle);
            g2d.drawString(subtitle,
                    (getWidth() - subtitleWidth) / 2,
                    rectY + rectHeight + 30);

            return;
        }

        int paddleY = getHeight() - paddleHeight - 30;
        GradientPaint paddleGradient;
        if (widePaddleActive) {
            paddleGradient = new GradientPaint(
                    paddleX, paddleY, new Color(0, 255, 100),
                    paddleX, paddleY + paddleHeight, new Color(0, 200, 80)
            );
        } else {
            paddleGradient = new GradientPaint(
                    paddleX, paddleY, new Color(70, 130, 255),
                    paddleX, paddleY + paddleHeight, new Color(30, 90, 200)
            );
        }

        g2d.setPaint(paddleGradient);
        g2d.fillRoundRect(paddleX, paddleY, paddleWidth, paddleHeight, 8, 8);

        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(paddleX, paddleY, paddleWidth, paddleHeight, 8, 8);

        for (Ball ball : balls) {
            g2d.setColor(new Color(255, 100, 100, 50));
            g2d.fillOval(ball.x - 3, ball.y - 3, ball.size + 6, ball.size + 6);

            GradientPaint ballGradient = new GradientPaint(
                    ball.x, ball.y, new Color(255, 80, 80),
                    ball.x + ball.size, ball.y + ball.size, new Color(200, 40, 40)
            );
            g2d.setPaint(ballGradient);
            g2d.fillOval(ball.x, ball.y, ball.size, ball.size);

            g2d.setColor(new Color(255, 255, 255, 150));
            g2d.fillOval(ball.x + 2, ball.y + 2, ball.size / 3, ball.size / 3);
        }

        for (Brick brick : bricks) {
            if (!brick.isDestroyed) {
                g2d.setColor(new Color(0, 0, 0, 80));
                g2d.fillRoundRect(brick.x + 2, brick.y + 2, brick.width, brick.height, 6, 6);

                Color lightColor = brick.color.brighter();
                Color darkColor = brick.color.darker();

                GradientPaint brickGradient = new GradientPaint(
                        brick.x, brick.y, lightColor,
                        brick.x, brick.y + brick.height, darkColor
                );
                g2d.setPaint(brickGradient);
                g2d.fillRoundRect(brick.x, brick.y, brick.width, brick.height, 6, 6);

                g2d.setColor(brick.indestructible ? Color.LIGHT_GRAY : Color.WHITE);
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(brick.x, brick.y, brick.width, brick.height, 6, 6);

                if (brick.indestructible) {
                    g2d.setColor(new Color(255, 255, 255, 100));
                    g2d.fillRoundRect(brick.x + 2, brick.y + 2, brick.width - 4, brick.height / 2, 4, 4);
                }
            }
        }
        for (PowerUp powerUp : activePowerUps) {
            if (powerUp.active) {
                Color glowColor;
                Color mainColor;
                String symbol;

                if (powerUp.type == 0) {
                    glowColor = new Color(0, 255, 100, 80);
                    mainColor = new Color(0, 200, 80);
                    symbol = "━━";
                } else {
                    glowColor = new Color(255, 215, 0, 80);
                    mainColor = new Color(255, 180, 0);
                    symbol = "×2";
                }

                g2d.setColor(glowColor);
                g2d.fillRoundRect(powerUp.x - 2, powerUp.y - 2,
                        powerUp.width + 4, powerUp.height + 4, 8, 8);

                GradientPaint powerUpGradient = new GradientPaint(
                        powerUp.x, powerUp.y, mainColor.brighter(),
                        powerUp.x, powerUp.y + powerUp.height, mainColor.darker()
                );
                g2d.setPaint(powerUpGradient);
                g2d.fillRoundRect(powerUp.x, powerUp.y, powerUp.width, powerUp.height, 6, 6);

                g2d.setColor(Color.WHITE);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(powerUp.x, powerUp.y, powerUp.width, powerUp.height, 6, 6);

                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 12));
                FontMetrics fm = g2d.getFontMetrics();
                int symbolWidth = fm.stringWidth(symbol);
                g2d.drawString(symbol,
                        powerUp.x + (powerUp.width - symbolWidth) / 2,
                        powerUp.y + powerUp.height / 2 + 4);
            }
        }

        GradientPaint navGradient = new GradientPaint(
                0, 0, new Color(30, 30, 50, 200),
                0, navbarHeight, new Color(10, 10, 25, 220)
        );
        g2d.setPaint(navGradient);
        g2d.fillRect(0, 0, getWidth(), navbarHeight);

        g2d.setColor(new Color(100, 150, 255, 120));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(0, navbarHeight - 1, getWidth(), navbarHeight - 1);
        g2d.setColor(new Color(100, 200, 255));
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 16));
        String timeText = "Time: " + gameTimeSeconds + "s";
        g2d.drawString(timeText, 15, navbarHeight - 12);

        int rightMargin = getWidth() - 15;
        int powerUpY = navbarHeight - 12;
        if (widePaddleActive) {
            long remainingTime = (POWER_UP_DURATION - (System.currentTimeMillis() - powerUpStartTime)) / 1000;
            String powerUpText = "Wide paddle: " + remainingTime + "s";
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(powerUpText);

            g2d.setColor(new Color(100, 255, 150));
            g2d.drawString(powerUpText, rightMargin - textWidth, powerUpY);
        }
        if (balls.size() > 1) {
            String ballsText = "Balls: " + balls.size();
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(ballsText);

            int xPos = widePaddleActive ? 200 : rightMargin - textWidth;

            g2d.setColor(new Color(255, 220, 100));
            g2d.drawString(ballsText, xPos, powerUpY);
        }
        if (!lastMessage.isEmpty() && System.currentTimeMillis() - messageTime < MESSAGE_DURATION) {
            long elapsed = System.currentTimeMillis() - messageTime;
            float progress = Math.min(1.0f, elapsed / (float) ANIMATION_DURATION);
            if (elapsed < ANIMATION_DURATION) {
                messageAlpha = easeOutQuart(progress);
                messageScale = 0.5f + (0.5f * easeOutBounce(progress));
                messageY = navbarHeight / 2 + (int)(10 * (1.0f - easeOutQuart(progress)));
            } else if (elapsed > MESSAGE_DURATION - ANIMATION_DURATION) {
                float fadeProgress = (elapsed - (MESSAGE_DURATION - ANIMATION_DURATION)) / (float) ANIMATION_DURATION;
                messageAlpha = 1.0f - easeInQuart(fadeProgress);
                messageScale = 1.0f - (0.2f * easeInQuart(fadeProgress));
                messageY = navbarHeight / 2;
            } else {
                messageAlpha = 1.0f;
                messageScale = 1.0f;
                messageY = navbarHeight / 2;
            }
            drawAnimatedMessage(g2d, lastMessage, messageAlpha, messageScale, messageY);
        }
    }

    private float easeOutQuart(float t) {
        return 1 - (float) Math.pow(1 - t, 4);
    }

    private float easeInQuart(float t) {
        return (float) Math.pow(t, 4);
    }

    private float easeOutBounce(float t) {
        if (t < 1 / 2.75f) {
            return 7.5625f * t * t;
        } else if (t < 2 / 2.75f) {
            return 7.5625f * (t -= 1.5f / 2.75f) * t + 0.75f;
        } else if (t < 2.5 / 2.75f) {
            return 7.5625f * (t -= 2.25f / 2.75f) * t + 0.9375f;
        } else {
            return 7.5625f * (t -= 2.625f / 2.75f) * t + 0.984375f;
        }
    }

    private void drawAnimatedMessage(Graphics2D g2d, String message, float alpha, float scale, int yPos) {
        var originalTransform = g2d.getTransform();
        var originalComposite = g2d.getComposite();

        Font messageFont = new Font("Segoe UI", Font.BOLD, (int)(14 * scale));
        g2d.setFont(messageFont);
        FontMetrics fm = g2d.getFontMetrics();
        int messageWidth = fm.stringWidth(message);
        int messageHeight = fm.getHeight();
        int centerX = getWidth() / 2;
        int actualY = Math.max(yPos, navbarHeight / 2);
        int bgWidth = (int)(messageWidth * 1.3f);
        int bgHeight = (int)(messageHeight * 1.1f);
        int bgX = centerX - bgWidth / 2;
        int bgY = actualY - (int)(messageHeight * 0.9f);

        for (int i = 6; i >= 1; i--) {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha * 0.15f));
            g2d.setColor(new Color(255, 215, 0, 80));
            g2d.fillRoundRect(bgX - i, bgY - i, bgWidth + 2*i, bgHeight + 2*i, 20 + i, 20 + i);
        }

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        GradientPaint bgGradient = new GradientPaint(
                bgX, bgY, new Color(25, 25, 45, 240),
                bgX, bgY + bgHeight, new Color(45, 45, 85, 250)
        );
        g2d.setPaint(bgGradient);
        g2d.fillRoundRect(bgX, bgY, bgWidth, bgHeight, 18, 18);

        g2d.setStroke(new BasicStroke(2.5f));
        GradientPaint borderGradient = new GradientPaint(
                bgX, bgY, new Color(255, 215, 0, (int)(alpha * 220)),
                bgX + bgWidth, bgY + bgHeight, new Color(255, 165, 0, (int)(alpha * 180))
        );
        g2d.setPaint(borderGradient);
        g2d.drawRoundRect(bgX, bgY, bgWidth, bgHeight, 18, 18);

        g2d.setColor(new Color(255, 255, 255, (int)(alpha * 120)));
        g2d.setStroke(new BasicStroke(1));
        g2d.drawRoundRect(bgX + 2, bgY + 2, bgWidth - 4, bgHeight - 4, 15, 15);

        for (int i = 3; i >= 1; i--) {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha * 0.3f));
            g2d.setColor(new Color(0, 0, 0, 120));
            g2d.drawString(message, centerX - messageWidth / 2 + i, actualY + i);
        }

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        GradientPaint textGradient = new GradientPaint(
                centerX - messageWidth / 2, actualY - messageHeight / 2,
                new Color(255, 255, 255),
                centerX - messageWidth / 2, actualY + messageHeight / 2,
                new Color(240, 240, 255)
        );
        g2d.setPaint(textGradient);
        g2d.drawString(message, centerX - messageWidth / 2, actualY);
        long currentTime = System.currentTimeMillis();
        for (int i = 0; i < 4; i++) {
            double angle = (currentTime * 0.003 + i * Math.PI / 2) % (2 * Math.PI);
            int sparkleX = (int)(centerX + Math.cos(angle) * (bgWidth * 0.5));
            int sparkleY = (int)(actualY + Math.sin(angle) * (bgHeight * 0.4));

            float sparkleAlpha = (float)(alpha * (0.3 + 0.3 * Math.sin(currentTime * 0.01 + i)));
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, sparkleAlpha));
            g2d.setColor(new Color(255, 255, 255, 150));
            g2d.fillOval(sparkleX - 1, sparkleY - 1, 2, 2);

            g2d.setColor(new Color(255, 215, 0, 200));
            g2d.fillOval(sparkleX, sparkleY, 1, 1);
        }
        g2d.setComposite(originalComposite);
        g2d.setTransform(originalTransform);    }

    public void actionPerformed(ActionEvent e) {
        if (!gameStarted) return;
        long elapsedTime = System.currentTimeMillis() - startTime;

        if (elapsedTime >= speedIncreaseInterval) {
            ballSpeed *= 1.03f;
            if (ballSpeed > 2.0f) {
                ballSpeed = 2.0f;
            }
            startTime = System.currentTimeMillis();

            if (ballSpeed < 1.8f) {
                showMessage("Speed Increased!");
            }
        }
        if (widePaddleActive && System.currentTimeMillis() - powerUpStartTime > POWER_UP_DURATION) {
            widePaddleActive = false;
            targetPaddleWidth = originalPaddleWidth;
            showMessage(" Wide Paddle Expired");
        }

        for (int i = activePowerUps.size() - 1; i >= 0; i--) {
            PowerUp powerUp = activePowerUps.get(i);
            if (powerUp.active) {
                powerUp.y += 2;

                if (powerUp.y + powerUp.height >= getHeight() - paddleHeight - 30 &&
                        powerUp.y <= getHeight() - 30 &&
                        powerUp.x + powerUp.width >= paddleX &&
                        powerUp.x <= paddleX + paddleWidth) {

                    activatePowerUp(powerUp);
                    powerUp.active = false;
                    activePowerUps.remove(i);
                }
                else if (powerUp.y > getHeight()) {
                    activePowerUps.remove(i);
                }
            }
        }

        for (int i = balls.size() - 1; i >= 0; i--) {
            Ball ball = balls.get(i);

            if (!ball.launched) {
                ball.x = paddleX + paddleWidth / 2 - ball.size / 2;
                ball.y = getHeight() - paddleHeight - 30 - ball.size;
                continue;
            }
            double currentSpeed = Math.sqrt(ball.xDir * ball.xDir + ball.yDir * ball.yDir);
            double targetSpeed = currentSpeed * ballSpeed;

            if (currentSpeed > 0) {
                double normalizedX = ball.xDir / currentSpeed;
                double normalizedY = ball.yDir / currentSpeed;

                ball.x += (int)(normalizedX * targetSpeed);
                ball.y += (int)(normalizedY * targetSpeed);
            } else {
                ball.x += ball.xDir;
                ball.y += ball.yDir;
            }

            if (ball.x <= 0 || ball.x >= getWidth() - ball.size) {
                ball.xDir = -ball.xDir;
            }

            if (ball.y <= 0) {
                ball.yDir = -ball.yDir;
            }

            if (ball.y + ball.size >= getHeight() - paddleHeight - 40 &&
                    ball.y + ball.size <= getHeight() - 30 &&
                    ball.x + ball.size >= paddleX &&
                    ball.x <= paddleX + paddleWidth) {

                int paddleCenter = paddleX + paddleWidth / 2;
                int ballCenter = ball.x + ball.size / 2;
                int distanceFromCenter = ballCenter - paddleCenter;

                double normalizedHit = distanceFromCenter / (double) (paddleWidth / 2);
                boolean hitOnEdge = Math.abs(normalizedHit) > 0.8;

                if (hitOnEdge) {
                    ball.xDir = (int) (normalizedHit * 4);
                } else {
                    ball.xDir = (int) (normalizedHit * 2);
                }
                ball.yDir = -Math.abs(ball.yDir);

                if (ball.xDir == 0) {
                    ball.xDir = (Math.random() > 0.5) ? 1 : -1;
                }
            }

            boolean hitBrick = false;
            for (Brick brick : bricks) {
                if (!brick.isDestroyed &&
                        ball.x + ball.size >= brick.x &&
                        ball.x <= brick.x + brick.width &&
                        ball.y + ball.size >= brick.y &&
                        ball.y <= brick.y + brick.height) {

                    if (brick.indestructible) {
                        int ballCenterX = ball.x + ball.size / 2;
                        int ballCenterY = ball.y + ball.size / 2;
                        int brickCenterX = brick.x + brick.width / 2;
                        int brickCenterY = brick.y + brick.height / 2;

                        int deltaX = ballCenterX - brickCenterX;
                        int deltaY = ballCenterY - brickCenterY;

                        int overlapX = (brick.width + ball.size) / 2 - Math.abs(deltaX);
                        int overlapY = (brick.height + ball.size) / 2 - Math.abs(deltaY);

                        if (overlapX < overlapY) {
                            ball.xDir = -ball.xDir;
                        } else {
                            ball.yDir = -ball.yDir;
                        }
                    } else {
                        int ballCenterX = ball.x + ball.size / 2;
                        int ballCenterY = ball.y + ball.size / 2;
                        int brickCenterX = brick.x + brick.width / 2;
                        int brickCenterY = brick.y + brick.height / 2;

                        int deltaX = ballCenterX - brickCenterX;
                        int deltaY = ballCenterY - brickCenterY;

                        int overlapX = (brick.width + ball.size) / 2 - Math.abs(deltaX);
                        int overlapY = (brick.height + ball.size) / 2 - Math.abs(deltaY);

                        if (overlapX < overlapY) {
                            ball.xDir = -ball.xDir;
                        } else {
                            ball.yDir = -ball.yDir;
                        }
                        brick.isDestroyed = true;
                        if (Math.random() < 0.03) {
                            String[] destructionMessages = {
                                    "SMASH!", "BOOM!", "NICE!", "PERFECT!", "BULLSEYE!"
                            };
                            showMessage(destructionMessages[(int)(Math.random() * destructionMessages.length)]);
                        }
                        if (brick.powerUpType >= 0) {
                            PowerUp powerUp = new PowerUp(
                                    brick.x + brick.width / 2 - 10,
                                    brick.y + brick.height,
                                    brick.powerUpType
                            );
                            activePowerUps.add(powerUp);

                            if (Math.random() < 0.5) {
                                String powerUpName = (brick.powerUpType == 0) ? "Wide Paddle" : "Multi-Ball";
                                showMessage(powerUpName + " Power-Up!");
                            }
                        }
                    }
                    hitBrick = true;
                    break;
                }
            }
            if (ball.y > getHeight()) {
                if (balls.size() > 1) {
                    balls.remove(i);
                    showMessage("Ball Lost! " + (balls.size()) + " remaining");
                } else {
                    timer.stop();
                    moveTimer.stop();
                    isMovingLeft = false;
                    isMovingRight = false;

                    showMessage("GAME OVER!");


                    Timer gameOverDelay = new Timer(1500, new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            ((Timer)e.getSource()).stop();
                            int response = JOptionPane.showConfirmDialog(
                                    GamePanel.this,
                                    "Game Over! \nWould you like to play again?",
                                    "Game Over",
                                    JOptionPane.YES_NO_OPTION
                            );
                            if (response == JOptionPane.YES_OPTION) {
                                showMessage("Restarting game...");
                                resetGame();
                            } else {
                                System.exit(0);
                            }
                        }
                    });
                    gameOverDelay.setRepeats(false);
                    gameOverDelay.start();
                    return;
                }
            }
        }
        if (checkWinCondition()) {
            timer.stop();
            moveTimer.stop();
            isMovingLeft = false;
            isMovingRight = false;

            showMessage("VICTORY! Level Completed!");

            Timer victoryDelay = new Timer(1500, new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                    ((Timer)e.getSource()).stop();
                    showVictoryDialog();
                }
            });
            victoryDelay.setRepeats(false);
            victoryDelay.start();

            return;
        }

        gameTimeSeconds = (System.currentTimeMillis() - gameStartTimeMillis) / 1000;

        repaint();
    }

    private void showVictoryDialog() {
        String[] options;
        String message;
        if (difficulty.equals("HARD")) {
            options = new String[]{"Play again (HARD)", "Exit"};
            message = "Congratulations! You completed the hardest level!\nWhat do you want to do?";
        } else {
            String nextLevel = difficulty.equals("EASY") ? "MEDIUM" : "HARD";
            options = new String[]{"Next level (" + nextLevel + ")", "Play again (" + difficulty + ")", "Exit"};
            message = "Congratulations! You completed level " + difficulty + "!\nWhat do you want to do?";
        }
        int choice = JOptionPane.showOptionDialog(
                this,
                message,
                "Victory!",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );
        if (difficulty.equals("HARD")) {
            if (choice == 0) {
                showMessage("Restarting HARD level...");
                resetGame();
            } else {
                System.exit(0);
            }
        } else {
            if (choice == 0) {
                String nextDifficulty = difficulty.equals("EASY") ? "MEDIUM" : "HARD";
                this.difficulty = nextDifficulty;
                difficultyLevel += 1;
                showMessage("Advancing to " + nextDifficulty + " level!");
                resetGame();
            } else if (choice == 1) {
                showMessage("Restarting " + difficulty + " level...");
                resetGame();
            } else {
                System.exit(0);
            }
        }
    }

    private void activatePowerUp(PowerUp powerUp) {
        switch (powerUp.type) {
            case 0:
                if (!widePaddleActive) {
                    originalPaddleWidth = paddleWidth;
                    targetPaddleWidth = (int)(paddleWidth * 1.8f);
                    widePaddleActive = true;
                    powerUpStartTime = System.currentTimeMillis();
                    showMessage("Wide Paddle Activated!");
                } else {
                    powerUpStartTime = System.currentTimeMillis();
                    showMessage("Wide Paddle Extended!");
                }
                break;
            case 1:
                addExtraBalls();
                showMessage("Multi-Ball Activated!");
                break;
        }
    }

    private void addExtraBalls() {
        if (!balls.isEmpty()) {
            Ball firstBall = balls.get(0);

            int baseSpeed;
            switch (difficulty) {
                case "EASY":
                    baseSpeed = 3;
                    break;
                case "MEDIUM":
                    baseSpeed = 4;
                    break;
                case "HARD":
                    baseSpeed = 5;
                    break;
                default:
                    baseSpeed = 4;
            }

            Ball leftBall = new Ball(
                    firstBall.x - 5,
                    firstBall.y,
                    ballSize,
                    -baseSpeed - (int)(Math.random() * 2),
                    -baseSpeed - (int)(Math.random() * 2)
            );
            leftBall.launched = true;

            Ball rightBall = new Ball(
                    firstBall.x + 5,
                    firstBall.y,
                    ballSize,
                    baseSpeed + (int)(Math.random() * 2),
                    -baseSpeed - (int)(Math.random() * 2)
            );
            rightBall.launched = true;

            balls.add(leftBall);
            balls.add(rightBall);
        }
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT) {
            isMovingLeft = true;

            if (!balls.isEmpty() && !balls.get(0).launched) {
                Ball ball = balls.get(0);
                ball.launched = true;

                switch (difficulty) {
                    case "EASY":
                        ball.xDir = -3;
                        ball.yDir = -3;
                        break;
                    case "MEDIUM":
                        ball.xDir = -4;
                        ball.yDir = -4;
                        break;
                    case "HARD":
                        ball.xDir = -5;
                        ball.yDir = -5;
                        break;
                }
            }
        }
        if (key == KeyEvent.VK_RIGHT) {
            isMovingRight = true;

            if (!balls.isEmpty() && !balls.get(0).launched) {
                Ball ball = balls.get(0);
                ball.launched = true;

                switch (difficulty) {
                    case "EASY":
                        ball.xDir = 3;
                        ball.yDir = -3;
                        break;
                    case "MEDIUM":
                        ball.xDir = 4;
                        ball.yDir = -4;
                        break;
                    case "HARD":
                        ball.xDir = 5;
                        ball.yDir = -5;
                        break;
                }
            }
        }
        if (key == KeyEvent.VK_UP) {
            if (!balls.isEmpty() && !balls.get(0).launched) {
                Ball ball = balls.get(0);
                ball.launched = true;

                int randomDirection = (Math.random() > 0.5) ? 1 : -1;

                switch (difficulty) {
                    case "EASY":
                        ball.xDir = randomDirection;
                        ball.yDir = -4;
                        break;
                    case "MEDIUM":
                        ball.xDir = randomDirection;
                        ball.yDir = -5;
                        break;
                    case "HARD":
                        ball.xDir = randomDirection;
                        ball.yDir = -6;
                        break;
                }
            }
        }
    }

    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT) {
            isMovingLeft = false;
        }
        if (key == KeyEvent.VK_RIGHT) {
            isMovingRight = false;
        }
    }    public void keyTyped(KeyEvent e) {
    }
}

class Ball {
    int x, y;
    int size;
    int xDir, yDir;
    boolean launched = false;

    public Ball(int x, int y, int size, int xDir, int yDir) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.xDir = xDir;
        this.yDir = yDir;
    }
}
