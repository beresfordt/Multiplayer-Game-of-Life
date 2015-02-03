package com.mehalter.life.model;

import javax.swing.*;

public class GameState {
    private Timer gameTimer;
    private GridPanel gridPanel;
    private boolean running, contiguous;
    private int currentX, currentY, currentUser;

    public GameState(Timer gameTimer, GridPanel gridPanel) {
        this.gameTimer = gameTimer;
        this.running = false;
        this.contiguous = true;
        this.currentX = 0;
        this.currentY = 0;
        this.currentUser = 1;
        this.gridPanel = gridPanel;
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

    public void setGameTimer(Timer gameTimer) {
        this.gameTimer = gameTimer;
    }
}
