import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Arkanoid");
        MenuPanel menuPanel = new MenuPanel(frame);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setMinimumSize(new Dimension(600,400));
        frame.add(menuPanel);
        frame.setVisible(true);
    }
}

class MenuPanel extends JPanel {
    private JFrame frame;

    public MenuPanel(JFrame frame) {
        this.frame = frame;
        this.setLayout(new BorderLayout());
        this.setBackground(Color.BLACK);

        JLabel titleLabel = new JLabel("Wybierz Poziom Trudności", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0xA5A5A5));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setSize(new Dimension(60,100));
        buttonPanel.setBackground(Color.BLACK);


        JButton easyButton = new JButton("Łatwy");
        JButton mediumButton = new JButton("Średni");
        JButton hardButton = new JButton("Trudny");

        easyButton.addActionListener(e -> startGame("EASY"));
        mediumButton.addActionListener(e -> startGame("MEDIUM"));
        hardButton.addActionListener(e -> startGame("HARD"));

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

class GamePanel extends JPanel implements ActionListener, KeyListener {
    Timer timer;
    int paddleX = 250;
    int paddleWidth = 100;
    int paddleHeight = 10;

    int ballX = 300;
    int ballY = 200;
    int ballSize = 10;
    int ballXDir = -2;
    int ballYDir = -3;

    Brick[] bricks;
    int numBricks = 15;

    int countdown = 3;
    boolean gameStarted = false;
    private String difficulty;

    public GamePanel(String difficulty) {
        this.difficulty = difficulty;
        this.setFocusable(true);
        this.addKeyListener(this);

        adjustDifficulty();

        timer = new Timer(10, this);
        timer.start();

        initializeBricks();
        startCountdown();
    }
    private void adjustDifficulty() {
        switch (difficulty) {
            case "EASY":
                ballXDir = -2;
                ballYDir = -3;
                break;
            case "MEDIUM":
                ballXDir = -3;
                ballYDir = -4;
                break;
            case "HARD":
                ballXDir = -4;
                ballYDir = -5;
                break;
        }
    }

    private void initializeBricks() {
        bricks = new Brick[numBricks];
        for (int i = 0; i < numBricks; i++) {
            bricks[i] = new Brick((i % 5) * 100 + 50, (i / 5) * 30 + 50, 80, 20);
        }
    }

    private void startCountdown() {
        Timer countdownTimer = new Timer(1000, e -> {
            if (countdown > 1) {
                countdown--;
            } else {
                countdown = 0;
                gameStarted = true;
                ((Timer)e.getSource()).stop();
            }
            repaint();
        });
        countdownTimer.start();
    }

    private void resetGame() {
        paddleX = 250;
        ballX = 300;
        ballY = 200;
        ballXDir = -2;
        ballYDir = -3;
        initializeBricks();
        countdown = 3;
        gameStarted = false;
        startCountdown();
        timer.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        setBackground(Color.BLACK);

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

        ballX += ballXDir;
        ballY += ballYDir;

        if (ballX <= 0 || ballX >= getWidth() - ballSize) {
            ballXDir = -ballXDir;
        }
        if (ballY <= 0) {
            ballYDir = -ballYDir;
        }

        if (ballY >= getHeight() - paddleHeight - 40 && ballX >= paddleX && ballX <= paddleX + paddleWidth) {
            ballYDir = -ballYDir;
        }

        for (Brick brick : bricks) {
            if (!brick.isDestroyed && ballX + ballSize >= brick.x && ballX <= brick.x + brick.width &&
                    ballY + ballSize >= brick.y && ballY <= brick.y + brick.height) {

                ballYDir = -ballYDir;
                brick.isDestroyed = true;
            }
        }

        if (ballY > getHeight()) {
            timer.stop();
            int response = JOptionPane.showConfirmDialog(this, "Game Over! Play Again?", "Game Over", JOptionPane.YES_NO_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                resetGame();
            }
            else {
                System.exit(0);
            }
        }

        repaint();
    }

    public void keyPressed(KeyEvent e) {
        if (!gameStarted) return;

        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT && paddleX > 0) {
            paddleX -= 20;
        }
        if (key == KeyEvent.VK_RIGHT && paddleX < getWidth() - paddleWidth) {
            paddleX += 20;
        }
    }

    public void keyReleased(KeyEvent e) {}
    public void keyTyped(KeyEvent e) {}
}