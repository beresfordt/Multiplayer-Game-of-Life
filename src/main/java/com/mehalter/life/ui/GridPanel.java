package com.mehalter.life.ui;

import com.mehalter.life.model.GameState;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;

public class GridPanel extends JPanel {

    private static final long serialVersionUID = - 897367256144487579L;

    private GameState gameState;

    public GridPanel(GameState gameState) {
        this.gameState = gameState;
    }

    // redefine the pant method so that the grid is painted on repaint calls
    @Override
    public void paintComponent(Graphics g) {
        // get the box width and height
        float height = (float) getHeight() / gameState.getySize();
        float width = (float) getWidth() / gameState.getxSize();
        Graphics2D g2 = (Graphics2D) g;
        for (int x = 0; x < gameState.getxSize(); x++) {
            for (int y = 0; y < gameState.getySize(); y++) {
                // set the color of the cell
                g2.setColor(gameState.getGrid()[x][y] == 1 ? gameState.getUser1()
                    : gameState.getGrid()[x][y] == 2 ? gameState.getUser2() : Color.WHITE);
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
