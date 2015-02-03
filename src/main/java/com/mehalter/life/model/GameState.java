package com.mehalter.life.model;

import javax.swing.*;
import java.awt.*;

public class GameState {
    private Timer gameTimer;
    private GridPanel gridPanel;
    private boolean running, contiguous;
    private int[][] grid;
    private int xSize, ySize, currentX, currentY, currentUser;
    private Color user1, user2;

    public GameState(Timer gameTimer) {
        this.gameTimer = gameTimer;
        this.running = false;
        this.contiguous = true;
        this.grid = new int[20][20];
        this.xSize = 20;
        this.ySize = 20;
        this.currentX = 0;
        this.currentY = 0;
        this.currentUser = 1;
        this.user1 = Color.GREEN.darker();
        this.user2 = Color.BLUE;

        this.gridPanel = new GridPanel(this.xSize, this.ySize, this.user1, this.user2, this.grid);
    }

    public Timer getGameTimer() {
        return gameTimer;
    }

    public GridPanel getGridPanel() {
        return gridPanel;
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

    public int[][] getGrid() {
        return grid;
    }

    public void setGrid(int[][] grid) {
        this.grid = grid;
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

    public void switchCurrentUser() {
        currentUser = getCurrentUser() == 1 ? 2 : 1;
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

    public void setGameTimer(Timer gameTimer) {
        this.gameTimer = gameTimer;
    }
}
