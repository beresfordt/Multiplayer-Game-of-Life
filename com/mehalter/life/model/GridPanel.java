package com.mehalter.life.model;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;

public class GridPanel extends JPanel {

    private static final long serialVersionUID = - 897367256144487579L;
    private int xSize;
    private int ySize;
    private Color user1;
    private Color user2;
    private int[][] grid;

    public GridPanel(int xSize, int ySize, Color user1, Color user2, int[][] grid) {
        this.xSize = xSize;
        this.ySize = ySize;
        this.user1 = user1;
        this.user2 = user2;
        this.grid = grid;
    }

    // redefine the pant method so that the grid is painted on repaint calls
    public void paintComponent(Graphics g) {
        // get the box width and height
        float height = (float) getHeight() / ySize;
        float width = (float) getWidth() / xSize;
        Graphics2D g2 = (Graphics2D) g;
        for (int x = 0; x < xSize; x++) {
            for (int y = 0; y < ySize; y++) {
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

    public int[][] getGrid() {
        return grid;
    }

    public void setGrid(int[][] grid) {
        this.grid = grid;
    }
}
