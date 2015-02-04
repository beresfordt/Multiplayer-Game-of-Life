package com.mehalter.life.persistence;

import com.mehalter.life.model.GameState;
import com.mehalter.life.ui.GameOfLifeUi;

import javax.swing.*;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

//Controls the file writing and reading module
public class GameOfLifeFileWriter {

    private GameOfLifeUi gameOfLifeUi;
    private GameState gameState;

    public GameOfLifeFileWriter(GameOfLifeUi gameOfLifeUi, GameState gameState) {
        this.gameOfLifeUi = gameOfLifeUi;
        this.gameState = gameState;
    }

    // converts the grid to a readable and savable file
    public void writeToFile(int[][] cgrid, int xSize, int ySize) {
        // builds file chooser to choose save location
        JFileChooser jfc = new JFileChooser();
        // applies the lexicon file type filter
        jfc.setFileFilter(new CellFileFilter());
        // shows the dialog and asks for save location
        int willSave = jfc.showSaveDialog(gameOfLifeUi);
        // continue if user chooses a save location
        if (willSave == JFileChooser.APPROVE_OPTION) {
            // sets the current file and adds file extension if not already
            // added
            File currentFile = jfc.getSelectedFile();
            if (! currentFile.getPath().toLowerCase().endsWith(".cells"))
                currentFile = new File(currentFile.getPath() + ".cells");
            // creates a buffered writer to save file
            BufferedWriter writer = null;
            try {
                writer = new BufferedWriter(new FileWriter(currentFile));
                // saves first two lines with name and a new line, each preceded
                // with a '!'
                writer.write("!Name: "
                        + currentFile.getName().replace(".cells", "") + "\n!\n");
                // for each line in the grid, write the cell layout to the file
                for (int x = 0; x <= xSize; x++) {
                    for (int y = 0; y <= ySize; y++) {
                        if (cgrid[x][y] == 0)
                            writer.write(".");
                        else
                            writer.write(cgrid[x][y] == 1 ? "O" : "I");
                    }
                    // move onto the new line and repeat
                    writer.newLine();
                }
                // if file error, prompt the error
            } catch (IOException e) {
                JOptionPane.showMessageDialog(gameOfLifeUi, "File error.");
            } finally {
                // try closing the file, and if can't prompt user with error
                try {
                    if (writer != null)
                        writer.close();
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(gameOfLifeUi, "Error closing file.");
                }
            }
            // add the newly saved shape to the right-click shape menu for easy
            // access
            gameOfLifeUi.getShapeMenu().add(new ShapeMenuItem(currentFile));
        }
    }

    // open file and add it to the right-click shape menu to insert into the
    // grid
    public void openFile() {
        JFileChooser jfc = new JFileChooser();
        jfc.setFileFilter(new CellFileFilter());
        jfc.setMultiSelectionEnabled(true);
        if (jfc.showOpenDialog(gameOfLifeUi) == JFileChooser.APPROVE_OPTION)
            for (File f : jfc.getSelectedFiles())
                gameOfLifeUi.getShapeMenu().add(new ShapeMenuItem(f));
    }

    // defines the shape items in the saved shapes menu
    class ShapeMenuItem extends JMenuItem {

        // initializes variables used
        private static final long serialVersionUID = 770975188019509393L;
        private final File shapeFile;
        private int[][] sGrid;
        private int cxSize, cySize;

        public ShapeMenuItem(File _shapeFile) {
            // sets name of the shape and the file to open
            super(_shapeFile.getName().replace(".cells", ""));
            shapeFile = _shapeFile;
            // creates new buffered reader and tries to read and parse it
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(shapeFile));
                // skips first two lines of file because they are not used
                reader.readLine();
                reader.readLine();
                // sets a mark so that we can reset back to this point after
                // getting shape width
                reader.mark(10000);
                // get shape size to create the grid by counting lines and
                // characters on each line
                cySize = reader.readLine().length();
                cxSize = Files.readAllLines(
                        Paths.get(shapeFile.getAbsolutePath()),
                        Charset.defaultCharset()).size() - 2;
                sGrid = new int[cySize][cxSize];
                // resets back to the top of the shape
                reader.reset();
                // loops through each line of the shape and converts the
                // characters to cell states
                String line = reader.readLine();
                for (int x = 0; x < cxSize; x++) {
                    for (int y = 0; y < cySize; y++)
                        sGrid[y][x] = line.charAt(y) == '.' ? 0 : line
                                .charAt(y) == 'O' ? 1 : 2;
                    // move to next line and repeat
                    line = reader.readLine();
                }
                // catch file issues while parsing, and prompt file error to the
                // user
            } catch (IOException e) {
                sGrid = new int[0][0];
                JOptionPane.showMessageDialog(this, shapeFile.getName()
                        + ": File Error");
            }
            // try closing file, and prompt user in case of error
            finally {
                try {
                    if (reader != null)
                        reader.close();
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(this, "Error closing file: "
                            + shapeFile.getName());
                }
            }
            // adds the action listener to put the file into the current grid
            // and repaint
            addActionListener(e -> {
                if (cxSize + gameState.getCurrentX() < gameState.getxSize() || cySize + gameState.getCurrentY() < gameState.getxSize()) {
                    for (int x = 0; x < cxSize; x++)
                        for (int y = 0; y < cySize; y++)
                            gameState.getGrid()[y + gameState.getCurrentY()][x + gameState.getCurrentX()] = sGrid[y][x] == 1 ? gameState.getCurrentUser()
                                    : sGrid[y][x] == 2 ? (gameState.getCurrentUser() == 1 ? gameState.getCurrentUser() + 1
                                    : gameState.getCurrentUser() - 1) : 0;
                    gameOfLifeUi.getGridPanel().repaint();
                    // catch array out of bounds error and prompt user that the
                    // shape is too big for the grid
                } else
                    JOptionPane.showMessageDialog(this, "Shape too big for grid.");
            });
        }
    }
}
