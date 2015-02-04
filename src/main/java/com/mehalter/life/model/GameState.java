package com.mehalter.life.model;

import javax.swing.*;
import java.awt.*;

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

    public int[][] getGrid() {
        return grid;
    }

    public void setGrid(int[][] grid) {
        this.grid = grid;
    }
}
