package com.mehalter.life.ui;

import com.mehalter.life.GameOfLife;
import com.mehalter.life.model.GameState;
import com.mehalter.life.persistence.GameOfLifeFileWriter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;

//Defines the User Interface
public class GameOfLifeUi extends JFrame {

    private static final long serialVersionUID = - 6912109370693886742L;

    private GameOfLife gameOfLife;
    private GameState gameState;
    private JPopupMenu shapeMenu;
    private GameOfLifeFileWriter fileWriter;

    public GameOfLifeUi(GameOfLife gameOfLife, GameState gameState) {
        this.gameOfLife = gameOfLife;
        this.gameState = gameState;
        this.fileWriter = new GameOfLifeFileWriter(this, gameState);
    }

    public JPopupMenu getShapeMenu() {
        return shapeMenu;
    }

    public void makeMenus() {
        JMenuBar mbar = new JMenuBar();
        shapeMenu = new JPopupMenu("Shapes");
        setJMenuBar(mbar);

        // build the menus in the menu bar
        JMenu sizeMenu = buildSizeMenu();
        JMenu speedMenu = buildSpeedMenu();
        JMenu optionMenu = buildOptionMenu();
        JMenu fileMenu = buildFileMenu();

        // initialize items in the menu bar
        JMenuItem startItem = new JMenuItem("Start");
        JMenuItem stepItem = new JMenuItem("Step");
        JMenuItem userItem = new JMenuItem("User: 1");
        JMenuItem colorItem = new JMenuItem("Color");
        JMenuItem clearItem = new JMenuItem("Clear");

        // set keyboard shortcuts for menu items
        startItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0));
        stepItem.setAccelerator(KeyStroke.getKeyStroke("N"));
        userItem.setAccelerator(KeyStroke.getKeyStroke("U"));
        colorItem.setAccelerator(KeyStroke.getKeyStroke("C"));
        clearItem.setAccelerator(KeyStroke.getKeyStroke("Q"));

        // define actions of each menu item
        // stepItem increases a single step
        stepItem.addActionListener(e -> gameOfLife.nextStep());
        // userItem changes the user and resets the button text to reflect the
        // current user
        userItem.addActionListener(e -> {
            gameState.switchCurrentUser();
            userItem.setText("User: " + gameState.getCurrentUser());
        });
        // asks user to pick a color to change the current user to
        colorItem.addActionListener(e -> {
            Color tempColor = JColorChooser.showDialog(gameState, "Choose a Color",
                    gameState.getCurrentUser() == 1 ? gameState.getUser1() : gameState.getUser2());
            if (tempColor != null) {
                if (gameState.getCurrentUser() == 1) {
                    gameState.setUser1(tempColor);
                } else {
                    gameState.setUser2(tempColor);
                }
            }
            gameState.repaint();
        });
        // creates a new grid
        clearItem.addActionListener(e -> {
            int[][] grid = new int[gameState.getxSize()][gameState.getySize()];
            gameState.setGrid(grid);
            gameState.repaint();
        });
        // starts the game continuously
        startItem.addActionListener(e -> {
            // reverse the running boolean
            gameState.setRunning(! gameState.isRunning());
            startItem.setText(gameState.isRunning() ? "Stop" : "Start");

            // while running, user cannot be able to click other buttons
            stepItem.setEnabled(! gameState.isRunning());
            clearItem.setEnabled(! gameState.isRunning());
            userItem.setEnabled(! gameState.isRunning());
            colorItem.setEnabled(! gameState.isRunning());
            sizeMenu.setEnabled(! gameState.isRunning());
            speedMenu.setEnabled(! gameState.isRunning());

            // start animation
            if (gameState.isRunning()) {
                gameState.getGameTimer().start();
            } else {
                gameState.getGameTimer().stop();
            }
        });

        // add menu items and menus
        mbar.add(fileMenu);
        mbar.add(optionMenu);
        mbar.add(startItem);
        mbar.add(stepItem);
        mbar.add(userItem);
        mbar.add(colorItem);
        mbar.add(clearItem);
        mbar.add(sizeMenu);
        mbar.add(speedMenu);
    }

    // build menu of grid size options
    private JMenu buildSizeMenu() {
        JMenu sizeMenu = new JMenu("Size");
        sizeMenu.add(new SizeMenuItem(10, 10));
        sizeMenu.add(new SizeMenuItem(20, 20));
        sizeMenu.add(new SizeMenuItem(30, 30));
        sizeMenu.add(new SizeMenuItem(40, 40));
        sizeMenu.add(new SizeMenuItem(50, 50));
        sizeMenu.add(new SizeMenuItem(75, 75));
        sizeMenu.add(new SizeMenuItem(100, 100));

        // allows user to input custom width and height
        JMenuItem customSize = new JMenuItem("Custom...");
        customSize.addActionListener(e -> {
            try {
                int w = Integer.parseInt(JOptionPane.showInputDialog(this,
                        "Enter the width:"));
                int h = Integer.parseInt(JOptionPane.showInputDialog(this,
                        "Enter the height:"));
                sizeMenu.remove(customSize);
                // adds custom size to menu for selection
                sizeMenu.add(new SizeMenuItem(w, h));
                sizeMenu.add(customSize);
            } catch (java.lang.NumberFormatException n) {
                JOptionPane
                        .showMessageDialog(this, "Invalid Number");
            }
        });
        sizeMenu.add(customSize);
        return sizeMenu;
    }

    // builds menu of speed options
    private JMenu buildSpeedMenu() {
        JMenu speedMenu = new JMenu("Speed");
        speedMenu.add(new SpeedMenuItem(10));
        speedMenu.add(new SpeedMenuItem(25));
        speedMenu.add(new SpeedMenuItem(50));
        speedMenu.add(new SpeedMenuItem(100));
        speedMenu.add(new SpeedMenuItem(200));
        speedMenu.add(new SpeedMenuItem(500));

        // allows user to input custom speed in milliseconds
        JMenuItem customSpeed = new JMenuItem("Custom...");
        customSpeed.addActionListener(e -> {
            try {
                int h = Integer.parseInt(JOptionPane.showInputDialog(this,
                        "Enter the speed:"));
                speedMenu.remove(customSpeed);
                // adds custom speed to menu for selection
                speedMenu.add(new SpeedMenuItem(h));
                speedMenu.add(customSpeed);
            } catch (java.lang.NumberFormatException n) {
                JOptionPane
                        .showMessageDialog(this, "Invalid Number");
            }
        });
        speedMenu.add(customSpeed);
        return speedMenu;
    }

    // builds menu of option options
    private JMenu buildOptionMenu() {
        // initializes items
        JMenu optionMenu = new JMenu("Options");
        JMenuItem contiguousItem = new JMenuItem("Contiguous: " + (gameState.isContiguous() ? "On" : "Off"));
        JMenuItem randomItem = new JMenuItem("Random Start...");
        // sets keyboard shortcuts for option options
        contiguousItem.setAccelerator(KeyStroke.getKeyStroke("control C"));
        randomItem.setAccelerator(KeyStroke.getKeyStroke("control shift N"));
        // sets actions for item contiguous
        contiguousItem.addActionListener(e -> {
            gameState.setContiguous(! gameState.isContiguous());
            contiguousItem.setText("Contiguous: " + (gameState.isContiguous() ? "On" : "Off"));
        });
        //Sets a random configuration on the board.
        randomItem.addActionListener(e -> {
            try {
                //Prompt the user for the desired population density
                double r = Double.parseDouble(JOptionPane.showInputDialog(this,
                        "Enter population density (under 1)"));
                //Ask the user if they want a single or double strain board
                int a = JOptionPane.showOptionDialog(null,
                        "One or two cell lines?", "Feedback",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.INFORMATION_MESSAGE,
                        null, new String[]{"Single Strain", "Two Strain"},
                        "default");
                //checks if the number is valid
                if (r < 1) {
                    //clear the current grid
                    int[][] grid = new int[gameState.getxSize()][gameState.getySize()];
                    gameState.setGrid(grid);
                    //initialize new Random object to generate random numbers
                    Random rand = new Random();
                    //initialize lists of X and Y coordinates
                    ArrayList<Integer> newXs = new ArrayList<>();
                    ArrayList<Integer> newYs = new ArrayList<>();
                    for (int i = 0; i < (int) (gameState.getxSize() * gameState.getySize() * r); i++) {
                        //generates new random x and y and checks if it is used before
                        int tempX = rand.nextInt(gameState.getxSize());
                        int tempY = rand.nextInt(gameState.getySize());
                        boolean newP = ! newXs.contains(tempX);
                        if (! newP) {
                            newP = ! (newYs.get(newXs.indexOf(tempX)) == tempY);
                        }
                        if (newP) {
                            //if the point has not been used, then it is added to the list of random points
                            newXs.add(tempX);
                        }
                        newYs.add(tempY);
                        //random point is added to the grid, and moves on to the next point
                        //if the user wants a two strain random grid, then it chooses a 1 or 2 randomly
                        gameState.getGrid()[tempX][tempY] = (a == JOptionPane.OK_OPTION) ? 1 : rand.nextInt(2) + 1;
                    }
                    gameState.repaint();
                }
                //if the number is not above 1 or if the answer is not a number
                //then it prompts the user with an error message
                else
                    JOptionPane
                            .showMessageDialog(this, "Invalid Density");
            } catch (java.lang.NumberFormatException n) {
                JOptionPane
                        .showMessageDialog(this, "Invalid Density");
            }
        });
        // adds the items and returns menu
        optionMenu.add(contiguousItem);
        optionMenu.add(randomItem);
        return optionMenu;
    }

    // builds menu of file options
    private JMenu buildFileMenu() {
        // initializes items
        JMenu fileMenu = new JMenu("File");
        JMenuItem openItem = new JMenuItem("Open");
        JMenuItem saveItem = new JMenuItem("Save");
        JMenuItem quitItem = new JMenuItem("Quit");
        // sets keyboard shortcuts for file options
        openItem.setAccelerator(KeyStroke.getKeyStroke("control O"));
        saveItem.setAccelerator(KeyStroke.getKeyStroke("control S"));
        quitItem.setAccelerator(KeyStroke.getKeyStroke("control shift Q"));
        // sets actions for items open, save, and quit
        openItem.addActionListener(e -> fileWriter.openFile());
        saveItem.addActionListener(e -> cropGrid());
        quitItem.addActionListener(e -> System.exit(0));
        // adds the items and returns menu
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.add(quitItem);
        return fileMenu;
    }

    // method to resize the current grid
    private int[][] resizeGrid(int newXSize, int newYSize) {
        // initializes temporary grid of new size
        int[][] newGrid = new int[newXSize][newYSize];
        // translates the old grid to the new
        for (int x = 0; x < gameState.getxSize() && x < newXSize - 1; x++)
            for (int y = 0; y < gameState.getySize() && y < newYSize - 1; y++)
                newGrid[x][y] = gameState.getGrid()[x][y];
        // resets the x and y size variables and returns the new grid
        gameState.setxSize(newXSize);
        gameState.setySize(newYSize);
        return newGrid;
    }

    // crops grid to boundaries of current live cells and saves to file
    private void cropGrid() {
        // initializes variables used
        int firstRow = - 1;
        int lastRow = - 1;
        int firstCol = - 1;
        int lastCol = - 1;

        // loops through and marks the x and y boundaries of the live cells
        for (int y = 0; y < gameState.getxSize(); y++)
            for (int x = 0; x < gameState.getxSize(); x++)
                if (gameState.getGrid()[x][y] != 0) {
                    firstRow = ((firstRow == - 1) || (y < firstRow)) ? y : firstRow;
                    firstCol = ((firstCol == - 1) || (x < firstCol)) ? x : firstCol;
                    lastRow = (y > lastRow) ? y : lastRow;
                    lastCol = (x > lastCol) ? x : lastCol;
                }

        // checks to make sure the grid wasn't blank
        if (firstRow != - 1) {
            // creates temporary grid of the specified boundary and fills with
            // the
            // cells
            int[][] cGrid = new int[lastRow + 1][lastCol + 1];
            for (int x = firstRow; x <= lastRow; x++)
                for (int y = firstCol; y <= lastCol; y++)
                    cGrid[x - firstRow][y - firstCol] = gameState.getGrid()[y][x];
            // sends the file off for saving
            fileWriter.writeToFile(cGrid, lastRow - firstRow, lastCol - firstCol);
        }
    }

    class SizeMenuItem extends JMenuItem {

        private static final long serialVersionUID = 5500403689725866488L;
        private final int x, y;

        public SizeMenuItem(int _x, int _y) {
            // initializes name and variables
            super(_x + "x" + _y);
            x = _x;
            y = _y;
            // adds action listener to resize grid to the correct dimensions and
            // repaint
            addActionListener(e -> {
                gameState.setGrid(resizeGrid(x, y));
                gameState.repaint();
            });
        }
    }

    // speed menu item class for easy addition of new speed menu items
    class SpeedMenuItem extends JMenuItem {

        private static final long serialVersionUID = - 2630048822872351199L;

        public SpeedMenuItem(int _speed) {
            // initializes name
            super(_speed + "ms");
            // adds action listener to change the timer speed to speed up
            // animation
            addActionListener(e -> gameState.setGameTimer(new Timer(_speed,
                    (f) -> gameOfLife.nextStep())));
        }
    }
}