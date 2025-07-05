import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {

    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 25;
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;
    static final int DELAY = 75;
    final int x[] = new int[GAME_UNITS];
    final int y[] = new int[GAME_UNITS];
    int bodyParts = 6;
    int applesEaten;
    int appleX;
    int appleY;
    char direction = 'R';
    boolean running = false;
    boolean gameStarted = false;
    Timer timer;
    Random random;
    Button startButton;
    Button newGameButton;

    public GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.ORANGE);
        this.setLayout(null); // disable layout manager

        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());

        // Start Game button setup

        startButton = new Button("Start Game");
        startButton.setBounds(250, 500, 100, 50); // position & size

        this.add(startButton);
        startButton.addActionListener(e -> {
            startGame();
            remove(startButton);
            repaint();
        });

        // New Game button setup (initially not added)
        newGameButton = new Button("New Game");
        newGameButton.setBounds(250, 500, 100, 40); // x=250, y=500, width=100, height=50

        newGameButton.addActionListener(e -> {
            startGame();
            remove(newGameButton);
            repaint();
        });
    }

    public void startGame() {
        gameStarted = true;
        running = true;
        bodyParts = 6;
        applesEaten = 0;

        // Initialize snake position (horizontal line in middle-ish)
        for (int i = 0; i < bodyParts; i++) {
            x[i] = 100 - i * UNIT_SIZE;
            y[i] = 100;
        }

        direction = 'R';
        newApple();

        // Remove newGameButton if visible
        if (this.isAncestorOf(newGameButton)) {
            remove(newGameButton);
        }

        timer = new Timer(DELAY, this);
        timer.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        if (!gameStarted) {
            // Show "Press Start" message before game starts
            g.setColor(Color.WHITE);
            g.setFont(new Font("Courier New", Font.BOLD, 50));
            String message = "Press Start";
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString(message, (SCREEN_WIDTH - metrics.stringWidth(message)) / 2, SCREEN_HEIGHT / 2);
        } else if (running) {
            // Draw apple
            g.setColor(Color.red);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            // Draw snake
            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(Color.green);
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                } else {
                    g.setColor(new Color(45, 180, 0));
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
            }

            // Draw score
            g.setColor(Color.BLUE);
            g.setFont(new Font("Courier New", Font.BOLD, 35));
            FontMetrics metrics1 = getFontMetrics(g.getFont());
            g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics1.stringWidth("Score: " + applesEaten)) / 2, g.getFont().getSize());
        } else {
            // Game over screen
            gameOver(g);
        }
    }

    public void newApple() {
        appleX = random.nextInt(SCREEN_WIDTH / UNIT_SIZE) * UNIT_SIZE;
        appleY = random.nextInt(SCREEN_HEIGHT / UNIT_SIZE) * UNIT_SIZE;
    }

    public void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        switch (direction) {
            case 'U' -> y[0] -= UNIT_SIZE;
            case 'D' -> y[0] += UNIT_SIZE;
            case 'L' -> x[0] -= UNIT_SIZE;
            case 'R' -> x[0] += UNIT_SIZE;
        }
    }

    public void checkApple() {
        if (x[0] == appleX && y[0] == appleY) {
            bodyParts++;
            applesEaten++;
            newApple();
        }
    }

    public void checkCollisions() {
        // Check if head collides with body
        for (int i = bodyParts - 1; i > 0; i--) {
            if (x[0] == x[i] && y[0] == y[i]) {
                running = false;
            }
        }
        // Check borders
        if (x[0] < 0) running = false;
        if (x[0] >= SCREEN_WIDTH) running = false;
        if (y[0] < 0) running = false;
        if (y[0] >= SCREEN_HEIGHT) running = false;

        if (!running) {
            timer.stop();
            // Show New Game button if not already added
            if (!this.isAncestorOf(newGameButton)) {
                this.add(newGameButton);
                revalidate();
                repaint();
            }
        }
    }

    public void gameOver(Graphics g) {
        // Score display
        g.setColor(Color.BLUE);
        g.setFont(new Font("Courier New", Font.BOLD, 35));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics1.stringWidth("Score: " + applesEaten)) / 2, g.getFont().getSize());

        // Game Over display
        g.setColor(Color.red);
        g.setFont(new Font("Courier New", Font.BOLD, 75));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - metrics2.stringWidth("Game Over")) / 2, SCREEN_HEIGHT / 2);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (direction != 'R') direction = 'L';
                    break;
                case KeyEvent.VK_RIGHT:
                    if (direction != 'L') direction = 'R';
                    break;
                case KeyEvent.VK_UP:
                    if (direction != 'D') direction = 'U';
                    break;
                case KeyEvent.VK_DOWN:
                    if (direction != 'U') direction = 'D';
                    break;
            }
        }
    }
}
