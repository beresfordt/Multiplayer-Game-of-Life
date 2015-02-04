package com.mehalter.life.model;

import javax.swing.*;
import java.awt.*;
<<<<<<< HEAD
import java.awt.geom.Rectangle2D;

//Holds attributes pertaining to the current state of the game
public class GameState extends JPanel {
    private Timer gameTimer;
    private boolean running, contiguous;
    private int xSize, ySize, currentX, currentY, currentUser;
    private Color user1, user2;
    private int[][] grid;

    public GameState(Timer gameTimer, int xSize, int ySize, Color user1, Color user2) {
=======

public class GameState {

    private Timer gameTimer;
    private boolean running, contiguous;
    private int currentX, currentY, currentUser;
    private int xSize;
    private int ySize;
    private Color user1;
    private Color user2;
    private int[][] grid;

    public GameState(Timer gameTimer, int xSize, int ySize, Color user1, Color user2, int[][] grid) {
>>>>>>> 3751e392785e6f25f9070c09c3874360bc74713b
        this.gameTimer = gameTimer;
        this.xSize = xSize;
        this.ySize = ySize;
        this.user1 = user1;
        this.user2 = user2;
        this.grid = grid;
        this.running = false;
        this.contiguous = true;
        this.currentX = 0;
        this.currentY = 0;
        this.xSize = xSize;
        this.ySize = ySize;
        this.currentUser = 1;
<<<<<<< HEAD
        this.user1 = user1;
        this.user2 = user2;
        this.grid = new int[xSize][ySize];
    }

    public void paintComponent(Graphics g) {
        // get the box width and height
        float height = (float) getHeight() / ySize;
        float width = (float) getWidth() / xSize;
        Graphics2D g2 = (Graphics2D) g;
        for (int x = 0; x < xSize; x++) {
            for (int y = 0; y < xSize; y++) {
                // set the color of the cell
                g2.setColor(grid[x][y] == 1 ? user1
                        : grid[x][y] == 2 ? user2 : Color.WHITE);
                // defines the rectangle painted
                Rectangle2D r2d = new Rectangle2D.Float(x * width, y
                        * height, width, height);
                // fills the rectangle, sets the color to black and then
                // draws the grid
                g2.fill(r2d);
                g2.setStroke(new BasicStroke(1));
                g2.setColor(Color.BLACK);
                g2.draw(r2d);
            }
        }
=======
>>>>>>> 3751e392785e6f25f9070c09c3874360bc74713b
    }

    //Methods allow for retrieving of the current state, and changing the current state
    public Timer getGameTimer() {
        return gameTimer;
    }

    public void setGameTimer(Timer gameTimer) {
        this.gameTimer = gameTimer;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public boolean isContiguous() {
        return contiguous;
    }

    public void setContiguous(boolean contiguous) {
        this.contiguous = contiguous;
    }

    public int getxSize() {
        return xSize;
    }

    public void setxSize(int xSize) {
        this.xSize = xSize;
    }

    public int getySize() {
        return ySize;
    }

    public void setySize(int ySize) {
        this.ySize = ySize;
    }

    public int getCurrentX() {
        return currentX;
    }

    public void setCurrentX(int currentX) {
        this.currentX = currentX;
    }

    public int getCurrentY() {
        return currentY;
    }

    public void setCurrentY(int currentY) {
        this.currentY = currentY;
    }

    public int getCurrentUser() {
        return currentUser;
    }

    public Color getUser1() {
        return user1;
    }

    public void setUser1(Color user1) {
        this.user1 = user1;
    }

    public Color getUser2() {
        return user2;
    }

    public void setUser2(Color user2) {
        this.user2 = user2;
    }

    public void switchCurrentUser() {
        currentUser = getCurrentUser() == 1 ? 2 : 1;
    }

<<<<<<< HEAD
=======
    public int getxSize() {
        return xSize;
    }

    public void setxSize(int xSize) {
        this.xSize = xSize;
    }

    public int getySize() {
        return ySize;
    }

    public void setySize(int ySize) {
        this.ySize = ySize;
    }

    public Color getUser1() {
        return user1;
    }

    public void setUser1(Color user1) {
        this.user1 = user1;
    }

    public Color getUser2() {
        return user2;
    }

    public void setUser2(Color user2) {
        this.user2 = user2;
    }

>>>>>>> 3751e392785e6f25f9070c09c3874360bc74713b
    public int[][] getGrid() {
        return grid;
    }

    public void setGrid(int[][] grid) {
        this.grid = grid;
    }
}
