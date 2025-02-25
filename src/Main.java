import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Arkanoid");
        MenuPanel menuPanel = new MenuPanel(frame);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 600);
        frame.add(menuPanel);
        frame.setResizable(false);
        frame.setVisible(true);
    }
}

class MenuPanel extends JPanel {
    private final JFrame frame;

    public MenuPanel(JFrame frame) {
        this.frame = frame;
        this.setLayout(new BorderLayout());
        this.setBackground(Color.BLACK);

        JLabel titleLabel = new JLabel("Wybierz Poziom Trudności", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0xA5A5A5));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.BLACK);

        JButton easyButton = new JButton("Łatwy");
        JButton mediumButton = new JButton("Średni");
        JButton hardButton = new JButton("Trudny");

        easyButton.addActionListener(_ -> startGame("EASY"));
        mediumButton.addActionListener(_ -> startGame("MEDIUM"));
        hardButton.addActionListener(_ -> startGame("HARD"));

        buttonPanel.add(easyButton);
        buttonPanel.add(mediumButton);
        buttonPanel.add(hardButton);

        this.add(titleLabel, BorderLayout.NORTH);
        this.add(buttonPanel, BorderLayout.CENTER);
    }

    private void startGame(String difficulty) {
        GamePanel gamePanel = new GamePanel(difficulty);
        frame.getContentPane().removeAll();
        frame.add(gamePanel);
        frame.revalidate();
        frame.repaint();
    }
}

class GamePanel extends JPanel implements KeyListener, ActionListener {
    Timer timer;
    Timer moveTimer;
    int paddleX = 400;
    int paddleWidth = 150;
    int paddleHeight = 10;
    int paddleSpeed = 8;

    int ballX = 450;
    int ballY = 300;
    int ballSize = 12;
    int ballXDir = -3;
    int ballYDir = -4;

    boolean isMovingLeft = false;
    boolean isMovingRight = false;

    Brick[] bricks;
    int numBricks = 27;

    int countdown = 3;
    boolean gameStarted = false;
    private final String difficulty;

    private int ballSpeed = 1;
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
        switch (difficulty) {
            case "EASY":
                ballXDir = -3;
                ballYDir = -4;
                break;
            case "MEDIUM":
                ballXDir = -4;
                ballYDir = -5;
                break;
            case "HARD":
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
            ballSpeed = (int) (ballSpeed + 0.1);
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

        if (ballY >= getHeight() - paddleHeight - 40 && ballX + ballSize >= paddleX && ballX <= paddleX + paddleWidth) {
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

        if (ballY > getHeight()) {
            timer.stop();
            int response = JOptionPane.showConfirmDialog(this, "Game Over! Play Again?", "Game Over", JOptionPane.YES_NO_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                resetGame();
            } else {
                System.exit(0);
            }
        }

        if (checkWinCondition()) {
            timer.stop();
            int response = JOptionPane.showConfirmDialog(this, "You Win! Play Again?", "Victory", JOptionPane.YES_NO_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                resetGame();
            } else {
                System.exit(0);
            }
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