import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

class Obstacle {
    private int oHeight;
    private int OWidth;
    final private char symbol = '-';

    public Obstacle(int oHeight, int OWidth) {
        this.oHeight = oHeight;
        this.OWidth = OWidth;
    }

    public int getoHeight() {
        return oHeight;
    }

    public void setoHeight(int oHeight) {
        this.oHeight = oHeight;
    }

    public int getOWidth() {
        return OWidth;
    }

    public void setOWidth(int OWidth) {
        this.OWidth = OWidth;
    }

    public char getSymbol() {
        return symbol;
    }
    public void render(char[][] board) {
        board[oHeight][OWidth] = getSymbol();
    }
}


class Player {
    int pHeight;
    int pWidth;
    final char symbol = '*';
    public Player(int pHeight, int pWidth) {
        this.pHeight = pHeight;
        this.pWidth = pWidth;
    }

    public int getpHeight() {
        return pHeight;
    }

    public void setpHeight(int pHeight) {
        this.pHeight = pHeight;
    }

    public int getpWidth() {
        return pWidth;
    }

    public void setpWidth(int pWidth) {
        this.pWidth = pWidth;
    }
    public void move(int amount) {
        this.pWidth += amount;
    }
    public void render(char[][] board) {
        board[pHeight][pWidth] = symbol;
    }
}


public class windows extends JFrame {
    public Random random;
    public char[][] board;
    public int score = 0;
    public int speed = 0;
    public boolean gameOver = false;
    public char dir = '0'; // dir for player to move left or right
    public Player player;


    public void update() {
        //player move
        playerOperation(player);
        // generate obstacles and move
        generateObstacle();
        obstaclesMove(player);
        //check obstacles position if out of bound, and compare player.height and count scores
        checkObstacles(player);
        dir = 'q';
    }
    public synchronized void checkObstacles(Player player){
        int pass = 0;
        for (int i = 0; i < this.board[0].length; i++) {
            if (this.board[this.player.getpHeight()][i] == '-') {
                pass++;
            }
        }
       if (pass > 0) {
           score++;
       }
    }
    public synchronized void obstaclesMove(Player player){
        for (int i = board.length - 1; i >0; i--) {
            for (int j = 0; j < board[0].length; j++) {
                if (board[i][j] != '*') {
                    this.board[i][j] = this.board[i-1][j];
                } else {
                    if (this.board[i-1][j] == '-') {
                        gameOver = true;
                    }
                }
            }
        }
        Arrays.fill(board[0], ' ');
    }
    public synchronized void generateObstacle() {
        int i = board[0].length / 3;
        while (i > 0) {
            int min = 0;
            int max = board[0].length - 1;
            int col = (int)(Math.random() * (max-min)) + min;
            Obstacle obstacle = new Obstacle(0, col);
            obstacle.render(board);
            i--;
        }
    }
    public synchronized void checkPlayer(Player player) {
        int positionCol = this.player.getpWidth();
        if (positionCol < 0) {
            positionCol = this.board[0].length - 1;
        } else if (positionCol == this.board[0].length) {
            positionCol = 0;
        }
        player.setpWidth(positionCol);
    }
    public synchronized void playerOperation(Player player) {
        if (dir == 'a') {
            this.board[player.getpHeight()][player.getpWidth()] = ' ';
            this.player.move(-1);

        } else if (dir == 'd') {
            this.board[player.getpHeight()][player.getpWidth()] = ' ';
            this.player.move(1);
        }
        checkPlayer(player);
        if (board[player.getpHeight()][player.getpWidth()] == '-'){ // player move and hit obstacles
            gameOver = true;
        } else {
            player.render(board);
        }
    }
    public int updateSpeed(int score) throws InterruptedException {
        if((270 - this.score)>100) {
            Thread.sleep(270 - this.score);
            return (int) (this.score /(1.7));
        }else {
            Thread.sleep(100);
            return 100;
        }
    }
    private synchronized void render(){
        if (!gameOver) {
            System.out.println("scores: " + score);
            System.out.println();
            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board[0].length; j++) {
                    System.out.print("    " +board[i][j]);
                }
                System.out.println();
                System.out.println();
            }
        } else {
            System.out.println("GAME OVER! The final scores is " + this.score);
            System.out.println();
        }
    }

    void cleanScreen() throws IOException, InterruptedException {
        new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();

//        try {
//
//            if (System.getProperty("os.name").contains("Windows")){
//                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
//            }else {
//                String[] command = {"clear"};
//                ProcessBuilder processBuilder = new ProcessBuilder(command);
//                processBuilder.directory(new File(System.getProperty("user.home")));
//                processBuilder.start();
//            }
//        } catch (IOException | InterruptedException ex) {
//            ex.printStackTrace();
//        }
    }
    //constructor
    public windows(int height, int width, Player player) {
        this.board = new char[height][width];
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                board[i][j] = ' ';
            }
        }
        this.player = player;
        this.setSize(250, 150);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("keyboard input");
        this.setVisible(true);
        this.addKeyListener(new KeyAdapter() {
            public void keyTyped (KeyEvent e) {
                dir = e.getKeyChar();
            }
        });
    }
    public static void main(String[] args) throws IOException, InterruptedException {
        // args[0[ and args[1] have to be bigger than 4
        int height = Integer.valueOf(args[0]);
        int width = Integer.valueOf(args[1]);


        Player player = new Player(height - 1, width / 2);
        windows window = new windows(height, width ,player);
        while (!window.gameOver) {
            window.cleanScreen();
            window.update();
            Thread.sleep(7000);
            window.render();
            window.speed = window.updateSpeed(window.score);
        }
    }
}
