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
    private final JFrame frame;

    public MenuPanel(JFrame frame) {
        this.frame = frame;
        this.setLayout(new BorderLayout());
        this.setBackground(new Color(20, 20, 40));

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(0, 0, 0, 0));
        titlePanel.setBorder(createEmptyBorder(50, 0, 50, 0));

        JLabel titleLabel = new JLabel("ARKANOID", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 48));
        titleLabel.setForeground(new Color(255, 215, 0));
        titleLabel.setBorder(createEmptyBorder(0, 0, 10, 0));

        JLabel subtitleLabel = new JLabel("Wybierz poziom trudności", JLabel.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        subtitleLabel.setForeground(Color.WHITE);

        titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(subtitleLabel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 0, 20));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(createEmptyBorder(0, 250, 100, 250));

        JButton easyButton = createStyledButton("ŁATWY", new Color(76, 175, 80));
        JButton mediumButton = createStyledButton("ŚREDNI", new Color(255, 152, 0));
        JButton hardButton = createStyledButton("TRUDNY", new Color(244, 67, 54));

        easyButton.addActionListener(_ -> startGame("EASY"));
        mediumButton.addActionListener(_ -> startGame("MEDIUM"));
        hardButton.addActionListener(_ -> startGame("HARD"));

        buttonPanel.add(easyButton);
        buttonPanel.add(mediumButton);
        buttonPanel.add(hardButton);

        this.add(titlePanel, BorderLayout.NORTH);
        this.add(buttonPanel, BorderLayout.CENTER);
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 20));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(color.darker());
            }
            public void mouseExited(MouseEvent evt) {
                button.setBackground(color);
            }
        });

        return button;
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
    int paddleSpeed = 15;

    int ballX = 450;
    int ballY = 300;
    int ballSize = 12;
    int ballXDir = -3;
    int ballYDir = -4;

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

    private BufferedImage backgroundImage;

    public void startGame(String difficulty) {
        gameStarted = true;
        startTime = System.currentTimeMillis();

        if (difficulty.equals("Medium")) {
            speedIncreaseInterval = 3000;
        } else if (difficulty.equals("Hard")) {
            speedIncreaseInterval = 2000;
        }
    }

    public GamePanel(String difficulty) {
        this.difficulty = difficulty;
        this.setFocusable(true);
        this.addKeyListener(this);

        File file = new File("img/background.jpg");
        if (!file.exists()) {
            System.out.println("Plik nie istnieje: " + file.getAbsolutePath());
        } else {
            try {
                backgroundImage = ImageIO.read(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        adjustDifficulty();
        timer = new Timer(10, this);
        timer.start();

        moveTimer = new Timer(10, _ -> movePaddle());
        moveTimer.start();

        initializeBricks();
        startCountdown();
    }

    private void movePaddle() {
        if (isMovingLeft && paddleX > 0) {
            paddleX -= paddleSpeed;
        }
        if (isMovingRight && paddleX < getWidth() - paddleWidth) {
            paddleX += paddleSpeed;
        }
        repaint();
    }

    private void adjustDifficulty() {
        switch (difficultyLevel) {
            case 1:
                difficulty = "EASY";
                break;
            case 2:
                difficulty = "MEDIUM";
                break;
            case 3:
                difficulty = "HARD";
                break;
        }
        switch (difficulty) {
            case "EASY":
                difficultyLevel = 1;
                ballXDir = -3;
                ballYDir = -4;
                break;
            case "MEDIUM":
                difficultyLevel = 2;
                ballXDir = -4;
                ballYDir = -5;
                break;
            case "HARD":
                difficultyLevel = 3;
                ballXDir = -5;
                ballYDir = -6;
                break;
        }
    }

    private void initializeBricks() {
        int brickWidth = 80;
        int brickHeight = 30;
        int cols = 9;
        int spacing = 5;
        int totalWidth = cols * (brickWidth + spacing) - spacing;
        int startX = (900 - totalWidth) / 2;

        bricks = new Brick[numBricks];
        for (int i = 0; i < numBricks; i++) {
            bricks[i] = new Brick(startX - 5 + (i % cols) * (brickWidth + spacing), 50 + (i / cols) * (brickHeight + spacing), brickWidth, brickHeight);
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
        ballX = 450;
        ballY = 300;
        ballXDir = -2;
        ballYDir = -3;
        initializeBricks();
        adjustDifficulty();

        countdown = 3;
        gameStarted = false;
        startCountdown();
        timer.start();
    }

    private boolean checkWinCondition() {
        for (Brick brick : bricks) {
            if (!brick.isDestroyed) {
                return false;
            }
        }
        return true;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Point hotSpot = new Point(0,0);
        BufferedImage cursorImage = new BufferedImage(1, 1, BufferedImage.TRANSLUCENT);
        Cursor invisibleCursor = toolkit.createCustomCursor(cursorImage, hotSpot, "InvisibleCursor");
        setCursor(invisibleCursor);

        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        if (!gameStarted) {
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, 0, getWidth(), getHeight());

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 48));
            String countdownText = String.valueOf(countdown);
            int textWidth = g.getFontMetrics().stringWidth(countdownText);
            g.drawString(countdownText, (getWidth() - textWidth) / 2, getHeight() / 2);

            return;
        }

        g.setColor(Color.BLUE);
        g.fillRect(paddleX, getHeight() - paddleHeight - 30, paddleWidth, paddleHeight);

        g.setColor(Color.RED);
        g.fillOval(ballX, ballY, ballSize, ballSize);

        for (Brick brick : bricks) {
            if (!brick.isDestroyed) {
                g.setColor(Color.GREEN);
                g.fillRect(brick.x, brick.y, brick.width, brick.height);
            }
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (!gameStarted) return;

        long elapsedTime = System.currentTimeMillis() - startTime;

        if (elapsedTime / speedIncreaseInterval > 0) {
            ballSpeed = (float) (ballSpeed + 0.1);
            startTime = System.currentTimeMillis();
        }

        double magnitude = Math.sqrt(ballXDir * ballXDir + ballYDir * ballYDir);
        ballX += (int) ((ballXDir / magnitude) * ballSpeed);
        ballY += (int) ((ballYDir / magnitude) * ballSpeed);

        ballX += ballXDir;
        ballY += ballYDir;

        if (ballX <= 0 || ballX >= getWidth() - ballSize) {
            ballXDir = -ballXDir;
        }

        if (ballY <= 0) {
            ballYDir = -ballYDir;
        }

        if (ballY + ballSize >= getHeight() - paddleHeight - 40 &&
                ballY + ballSize <= getHeight() - 30 &&
                ballX + ballSize >= paddleX &&
                ballX <= paddleX + paddleWidth) {
            int paddleCenter = paddleX + paddleWidth / 2;
            int ballCenter = ballX + ballSize / 2;
            int distanceFromCenter = ballCenter - paddleCenter;
            double normalizedHit = distanceFromCenter / (double) (paddleWidth / 2);

            ballXDir = (int) (normalizedHit * 5);
            ballYDir = -Math.abs(ballYDir);

            if (ballXDir == 0) {
                ballXDir = (Math.random() > 0.5) ? 1 : -1;
            }
        }

        for (Brick brick : bricks) {
            if (!brick.isDestroyed &&
                    ballX + ballSize >= brick.x &&
                    ballX <= brick.x + brick.width &&
                    ballY + ballSize >= brick.y &&
                    ballY <= brick.y + brick.height) {

                int ballCenterX = ballX + ballSize / 2;
                int ballCenterY = ballY + ballSize / 2;
                int brickCenterX = brick.x + brick.width / 2;
                int brickCenterY = brick.y + brick.height / 2;

                int deltaX = ballCenterX - brickCenterX;
                int deltaY = ballCenterY - brickCenterY;

                int overlapX = (brick.width + ballSize) / 2 - Math.abs(deltaX);
                int overlapY = (brick.height + ballSize) / 2 - Math.abs(deltaY);

                if (overlapX < overlapY) {
                    ballXDir = -ballXDir;
                } else {
                    ballYDir = -ballYDir;
                }

                brick.isDestroyed = true;
                break;
            }
        }

        if (ballY > getHeight() - ballSize) {
            timer.stop();
            moveTimer.stop();
            isMovingLeft = false;
            isMovingRight = false;
            int response = JOptionPane.showConfirmDialog(this, "Game Over! Play Again?", "Game Over", JOptionPane.YES_NO_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                resetGame();
            } else {
                System.exit(0);
            }
            return;
        }

        if (checkWinCondition()) {
            timer.stop();
            moveTimer.stop();
            isMovingLeft = false;
            isMovingRight = false;
            int response = JOptionPane.showConfirmDialog(this, "You Win! Play Again?", "Victory", JOptionPane.YES_NO_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                difficultyLevel += 1;
                resetGame();
            } else {
                System.exit(0);
            }
            return;
        }

        repaint();
    }

    public void keyPressed(KeyEvent e) {
        if (!gameStarted) return;

        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT) {
            isMovingLeft = true;
        }
        if (key == KeyEvent.VK_RIGHT) {
            isMovingRight = true;
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
    }

    public void keyTyped(KeyEvent e) {}
}