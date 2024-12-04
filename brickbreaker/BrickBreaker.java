import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BrickBreaker extends JPanel implements KeyListener, ActionListener {
    private final int screenWidth = 700;
    private final int screenHeight = 600;
    private final int paddleWidth = 100;
    private final int paddleHeight = 10;
    private final int ballSize = 20;

    private boolean play = false;
    private int score = 0;
    private int totalBricks = 21;

    private int paddleX = (screenWidth - paddleWidth) / 2;
    private int ballX = 120;
    private int ballY = 350;
    private int ballXDir = -1;
    private int ballYDir = -2;

    private Timer timer;
    private final int delay = 8;

    private int[][] bricks;
    private int brickWidth;
    private int brickHeight;

    public BrickBreaker() {
        this.addKeyListener(this);
        this.setFocusable(true);
        this.setFocusTraversalKeysEnabled(false);
        this.timer = new Timer(delay, this);
        this.timer.start();

        // Initialize bricks
        this.bricks = new int[3][7]; // 3 rows, 7 columns
        this.brickWidth = screenWidth / bricks[0].length;
        this.brickHeight = 50;

        for (int i = 0; i < bricks.length; i++) {
            for (int j = 0; j < bricks[0].length; j++) {
                bricks[i][j] = 1; // 1 means the brick is visible
            }
        }
    }

    @Override
    public void paint(Graphics g) {
        // Background
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, screenWidth, screenHeight);

        // Bricks
        for (int i = 0; i < bricks.length; i++) {
            for (int j = 0; j < bricks[0].length; j++) {
                if (bricks[i][j] > 0) {
                    g.setColor(Color.YELLOW);
                    g.fillRect(j * brickWidth + 10, i * brickHeight + 50, brickWidth - 10, brickHeight - 10);
                }
            }
        }

        // Paddle
        g.setColor(Color.GREEN);
        g.fillRect(paddleX, screenHeight - 50, paddleWidth, paddleHeight);

        // Ball
        g.setColor(Color.RED);
        g.fillOval(ballX, ballY, ballSize, ballSize);

        // Score
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Score: " + score, 20, 30);

        // Game Over
        if (totalBricks <= 0) {
            play = false;
            ballXDir = 0;
            ballYDir = 0;
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.drawString("You Won! Score: " + score, screenWidth / 2 - 150, screenHeight / 2);
            g.drawString("Press Enter to Restart", screenWidth / 2 - 150, screenHeight / 2 + 50);
        }

        if (ballY > screenHeight) {
            play = false;
            ballXDir = 0;
            ballYDir = 0;
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.drawString("Game Over, Score: " + score, screenWidth / 2 - 150, screenHeight / 2);
            g.drawString("Press Enter to Restart", screenWidth / 2 - 150, screenHeight / 2 + 50);
        }

        g.dispose();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (play) {
            // Ball collision with paddle
            if (new Rectangle(ballX, ballY, ballSize, ballSize).intersects(new Rectangle(paddleX, screenHeight - 50, paddleWidth, paddleHeight))) {
                ballYDir = -ballYDir;
            }

            // Ball collision with bricks
            A:
            for (int i = 0; i < bricks.length; i++) {
                for (int j = 0; j < bricks[0].length; j++) {
                    if (bricks[i][j] > 0) {
                        int brickX = j * brickWidth + 10;
                        int brickY = i * brickHeight + 50;
                        int brickWidthAdjusted = brickWidth - 10;
                        int brickHeightAdjusted = brickHeight - 10;

                        Rectangle brickRect = new Rectangle(brickX, brickY, brickWidthAdjusted, brickHeightAdjusted);
                        Rectangle ballRect = new Rectangle(ballX, ballY, ballSize, ballSize);

                        if (ballRect.intersects(brickRect)) {
                            bricks[i][j] = 0;
                            totalBricks--;
                            score += 5;

                            if (ballX + ballSize - 1 <= brickX || ballX + 1 >= brickX + brickWidthAdjusted) {
                                ballXDir = -ballXDir;
                            } else {
                                ballYDir = -ballYDir;
                            }
                            break A;
                        }
                    }
                }
            }

            ballX += ballXDir;
            ballY += ballYDir;

            // Ball collision with walls
            if (ballX < 0 || ballX > screenWidth - ballSize) {
                ballXDir = -ballXDir;
            }
            if (ballY < 0) {
                ballYDir = -ballYDir;
            }
        }

        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            if (paddleX >= screenWidth - paddleWidth) {
                paddleX = screenWidth - paddleWidth;
            } else {
                paddleX += 20;
            }
        }
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            if (paddleX <= 0) {
                paddleX = 0;
            } else {
                paddleX -= 20;
            }
        }
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            if (!play) {
                resetGame();
            }
        }
    }

    private void resetGame() {
        play = true;
        ballX = 120;
        ballY = 350;
        ballXDir = -1;
        ballYDir = -2;
        paddleX = (screenWidth - paddleWidth) / 2;
        score = 0;
        totalBricks = 21;

        // Reset bricks
        for (int i = 0; i < bricks.length; i++) {
            for (int j = 0; j < bricks[0].length; j++) {
                bricks[i][j] = 1;
            }
        }
        repaint();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Brick Breaker");
        BrickBreaker game = new BrickBreaker();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 600);
        frame.setResizable(false);
        frame.add(game);
        frame.setVisible(true);
    }
}
