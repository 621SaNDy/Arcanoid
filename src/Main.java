import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Arkanoid");
        GamePanel gamePanel = new GamePanel();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.add(gamePanel);
        frame.setVisible(true);
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

    public GamePanel() {
        this.setFocusable(true);
        this.addKeyListener(this);

        timer = new Timer(10, this);
        timer.start();

        initializeBricks();
        startCountdown();
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