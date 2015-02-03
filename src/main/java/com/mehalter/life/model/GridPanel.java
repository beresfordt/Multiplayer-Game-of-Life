package com.mehalter.life.model;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;

public class GridPanel extends JPanel {

    private static final long serialVersionUID = - 897367256144487579L;
    private final int xSize;
    private final int ySize;
    private final Color user1;
    private final Color user2;
    private final int[][] grid;

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
}
