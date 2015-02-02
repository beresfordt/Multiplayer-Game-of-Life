import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;

public class GameOfLife extends JFrame implements Runnable {

    // define needed state variable
    private static final long serialVersionUID = -6912109370693886742L;
    private Timer gameTimer;
    private GridPanel p;
    private boolean running;
    private int[][] grid;
    private int xSize, ySize, currentX, currentY, user;
    private Color user1, user2;
    private JPopupMenu shapeMenu;

    // initialize state variables
    public GameOfLife() {
        setTitle("Multiplayer Game of Life");
        user1 = Color.GREEN.darker();
        user2 = Color.BLUE;
        gameTimer = new Timer(100, (e) -> nextStep());
        p = new GridPanel();
        xSize = 20;
        ySize = 20;
        user = 1;

        // build new blank grid
        grid = new int[xSize][ySize];
    }

    // initialize the program entirely and run the runnable method
    // to start the game
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new GameOfLife());
    }

    // build the top menu bar
    private void makeMenus() {
        JMenuBar mbar = new JMenuBar();
        setJMenuBar(mbar);

        // build the menus in the menu bar
        JMenu sizeMenu = buildSizeMenu();
        JMenu speedMenu = buildSpeedMenu();
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
        stepItem.addActionListener(e -> nextStep());
        // userItem changes the user and resets the button text to reflect the
        // current user
        userItem.addActionListener(e -> {
            user = changeUser();
            userItem.setText("User: " + user);
        });
        // asks user to pick a color to change the current user to
        colorItem.addActionListener(e -> {
            Color tempColor = JColorChooser.showDialog(p, "Choose a Color",
                    user == 1 ? user1 : user2);
            if (tempColor != null) {
                if (user == 1)
                    user1 = tempColor;
                else
                    user2 = tempColor;
            }
            p.repaint();
        });
        // creates a new grid
        clearItem.addActionListener(e -> {
            grid = new int[xSize][ySize];
            p.repaint();
        });
        // starts the game continuously
        startItem.addActionListener(e -> {
            // reverse the running boolean
            running = !running;
            startItem.setText(running ? "Stop" : "Start");

            // while running, user cannot be able to click other buttons
            stepItem.setEnabled(!running);
            clearItem.setEnabled(!running);
            userItem.setEnabled(!running);
            colorItem.setEnabled(!running);
            sizeMenu.setEnabled(!running);
            speedMenu.setEnabled(!running);

            // start animation
            if (running)
                gameTimer.start();
            else
                gameTimer.stop();
        });

        // add menu items and menus
        mbar.add(fileMenu);
        mbar.add(startItem);
        mbar.add(stepItem);
        mbar.add(userItem);
        mbar.add(colorItem);
        mbar.add(clearItem);
        mbar.add(sizeMenu);
        mbar.add(speedMenu);
    }

    // change user variable
    private int changeUser() {
        return user == 1 ? 2 : 1;
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
        openItem.addActionListener(e -> openFile());
        saveItem.addActionListener(e -> cropGrid());
        quitItem.addActionListener(e -> System.exit(0));
        // adds the items and returns menu
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.add(quitItem);
        return fileMenu;
    }

    // calculates the next step of the grid based on the current grid layout
    private void nextStep() {
        // creates new temporary grid for new layout
        int[][] newGrid = new int[xSize][ySize];
        for (int x = 0; x < xSize; x++) {
            for (int y = 0; y < ySize; y++) {
                // calculates number of neighbors and how many are of each
                // user
                ArrayList<Integer> neighbors = gatherNeighbors(x, y);
                int oneCount = Collections.frequency(neighbors, 1);
                int twoCount = Collections.frequency(neighbors, 2);

                // applies the three rules of life Conway developed
                // checks if a dead cell has exactly 3 neighbors, and sets the
                // color to whichever user accounts for more of those three
                // cells
                if (grid[x][y] == 0 && neighbors.size() == 3)
                    newGrid[x][y] = oneCount > twoCount ? 1 : 2;
                    // checks if a lives cell has too few or too many neighbors and
                    // sets it dead or alive accordingly
                else if (grid[x][y] == 1 || grid[x][y] == 2)
                    newGrid[x][y] = (neighbors.size() < 2 || neighbors.size() > 3) ? 0
                            : grid[x][y];
                    // if none of previous rules, set cell to dead
                else
                    newGrid[x][y] = 0;
            }
        }
        // moves the temporary grid to the used grid and repaints
        grid = newGrid;
        p.repaint();
    }

    // method to resize the current grid
    private int[][] resizeGrid(int newXSize, int newYSize) {
        // initializes temporary grid of new size
        int[][] newGrid = new int[newXSize][newYSize];
        // translates the old grid to the new
        for (int x = 0; x < xSize; x++)
            for (int y = 0; y < ySize; y++)
                if (x < newXSize - 1 && y < newYSize - 1)
                    newGrid[x][y] = grid[x][y];
        // resets the x and y size variables and returns the new grid
        xSize = newXSize;
        ySize = newYSize;
        return newGrid;
    }

    // method to return array of a cell's neighbors
    private ArrayList<Integer> gatherNeighbors(int x, int y) {
        // initializes an array list of integers to house the neighbors
        ArrayList<Integer> count = new ArrayList<>();

        // defines the different cell positions around the current cell
        // the ternary operators are to make sure that the grid wraps around and
        // doesn't hit an edge
        int right = grid[x == xSize - 1 ? 0 : x + 1][y];
        int left = grid[x == 0 ? xSize - 1 : x - 1][y];
        int up = grid[x][y == ySize - 1 ? 0 : y + 1];
        int down = grid[x][y == 0 ? ySize - 1 : y - 1];

        int rightup = grid[x == xSize - 1 ? 0 : x + 1][y == ySize - 1 ? 0
                : y + 1];
        int rightdown = grid[x == xSize - 1 ? 0 : x + 1][y == 0 ? ySize - 1
                : y - 1];
        int leftup = grid[x == 0 ? xSize - 1 : x - 1][y == ySize - 1 ? 0
                : y + 1];
        int leftdown = grid[x == 0 ? xSize - 1 : x - 1][y == 0 ? ySize - 1
                : y - 1];

        // adds the neighbor value if it is alive
        if (right != 0)
            count.add(right);
        if (left != 0)
            count.add(left);
        if (up != 0)
            count.add(up);
        if (down != 0)
            count.add(down);
        if (rightup != 0)
            count.add(rightup);
        if (rightdown != 0)
            count.add(rightdown);
        if (leftup != 0)
            count.add(leftup);
        if (leftdown != 0)
            count.add(leftdown);

        return count;
    }

    // crops grid to boundaries of current live cells and saves to file
    private void cropGrid() {
        // initializes variables used
        int firstRow = -1;
        int lastRow = -1;
        int firstCol = -1;
        int lastCol = -1;

        // loops through and marks the x and y boundaries of the live cells
        for (int y = 0; y < ySize; y++) {
            for (int x = 0; x < xSize; x++) {
                if (grid[x][y] != 0) {
                    if (firstRow == -1)
                        firstRow = y;
                    else if (y < firstRow)
                        firstRow = y;
                    if (y > lastRow)
                        lastRow = y;
                    if (firstCol == -1)
                        firstCol = x;
                    else if (x < firstCol)
                        firstCol = x;
                    if (x > lastCol)
                        lastCol = x;
                }
            }
        }

        // checks to make sure the grid wasn't blank
        if (firstRow != -1) {
            // creates temporary grid of the specified boundary and fills with
            // the
            // cells
            int[][] cGrid = new int[lastRow + 1][lastCol + 1];
            for (int x = firstRow; x <= lastRow; x++)
                for (int y = firstCol; y <= lastCol; y++)
                    cGrid[x - firstRow][y - firstCol] = grid[y][x];
            // sends the file off for saving
            writeToFile(cGrid, lastRow - firstRow, lastCol - firstCol);
        }
    }

    // converts the grid to a readable and savable file
    private void writeToFile(int[][] cgrid, int xSize, int ySize) {
        // builds file chooser to choose save location
        JFileChooser jfc = new JFileChooser();
        // applies the lexicon file type filter
        jfc.setFileFilter(new CellFileFilter());
        // shows the dialog and asks for save location
        int willSave = jfc.showSaveDialog(GameOfLife.this);
        // continue if user chooses a save location
        if (willSave == JFileChooser.APPROVE_OPTION) {
            // sets the current file and adds file extension if not already
            // added
            File currentFile = jfc.getSelectedFile();
            if (!currentFile.getPath().toLowerCase().endsWith(".cells"))
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
                JOptionPane.showMessageDialog(this, "File error.");
            } finally {
                // try closing the file, and if can't prompt user with error
                try {
                    if (writer != null)
                        writer.close();
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(this, "Error closing file.");
                }
            }
            // add the newly saved shape to the right-click shape menu for easy
            // access
            shapeMenu.add(new ShapeMenuItem(currentFile));
        }
    }

    // open file and add it to the right-click shape menu to insert into the
    // grid
    private void openFile() {
        JFileChooser jfc = new JFileChooser();
        jfc.setFileFilter(new CellFileFilter());
        jfc.setMultiSelectionEnabled(true);
        if (jfc.showOpenDialog(GameOfLife.this) == JFileChooser.APPROVE_OPTION)
            for (File f : jfc.getSelectedFiles())
                shapeMenu.add(new ShapeMenuItem(f));
    }

    // run method to start the game
    public void run() {
        // set the initial size of the window
        setSize(700, 726);
        // build the top menu bar
        makeMenus();
        // initialize the right-click shape pop-up menu
        shapeMenu = new JPopupMenu("Shapes");
        // add grid panel to the window for viewing
        getContentPane().add(p);
        // add mouse listener to listen for clicks on the grid
        p.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // gets current location on the panel and divides it by the size
                // of the panel and size of the grid to get the box number
                // uses a double in the denominator to force an integer divide
                // to force the final value to round down
                currentY = (int) (e.getX() / ((double) p.getWidth() / xSize));
                currentX = (int) (e.getY() / ((double) p.getHeight() / ySize));
                // if the click is a right click, don't toggle cell state, but
                // open shape menu
                if (SwingUtilities.isRightMouseButton(e))
                    shapeMenu.show(e.getComponent(), e.getX(), e.getY());
                    // if it isn't a right click, and the grid is editable, toggle
                    // the cell state and repaint
                else if (!running) {
                    grid[currentY][currentX] = (grid[currentY][currentX] == user ? 0
                            : user);
                    p.repaint();
                }
            }

            // blank unused mouse event methods that java forces me to put here
            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });
        p.addMouseMotionListener(new MouseMotionListener() {

            @Override
            public void mouseDragged(MouseEvent e) {
                // Allow user to click and drag mouse to toggle cell states
                currentY = (int) (e.getX() / ((double) p.getWidth() / xSize));
                currentX = (int) (e.getY() / ((double) p.getHeight() / ySize));

                // checks if the board is editable, and if the mouse is on the
                // screen
                if (!running && (currentY < xSize && currentY >= 0)
                        && (currentX < ySize && currentX >= 0)) {
                    // if the button used is the left click, then it turns cells
                    // on
                    // Checks to make sure that the cell it is changing is 0 so
                    // it
                    // doesn't overwrite the other user's information
                    // accidentally
                    grid[currentY][currentX] = SwingUtilities
                            .isLeftMouseButton(e) ? grid[currentY][currentX] == 0 ? user
                            : grid[currentY][currentX]
                            // if the button isn't the left click, then it
                            // becomes an eraser
                            // and deletes everything
                            : 0;
                    p.repaint();
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
            }

        });
        // set the default close operation to fully close the program when the
        // user presses the 'x' in the corner
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        // sets the window contents to visible
        setVisible(true);
    }

    /*
     * = INNER CLASSES =
     */

    // defines the panel the grid is drawn
    class GridPanel extends JPanel {

        private static final long serialVersionUID = -897367256144487579L;

        // redefine the pant method so that the grid is painted on repaint calls
        public void paintComponent(Graphics g) {
            // get the box width and height
            float height = (float) p.getHeight() / ySize;
            float width = (float) p.getWidth() / xSize;
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

    // size menu item class for easy addition of new size menu items
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
                grid = resizeGrid(x, y);
                p.repaint();
            });
        }
    }

    // speed menu item class for easy addition of new speed menu items
    class SpeedMenuItem extends JMenuItem {

        private static final long serialVersionUID = -2630048822872351199L;

        public SpeedMenuItem(int _speed) {
            // initializes name
            super(_speed + "ms");
            // adds action listener to change the timer speed to speed up
            // animation
            addActionListener(e -> gameTimer = new Timer(_speed,
                    (f) -> nextStep()));
        }
    }

    // defines the shape items in the saved shapes menu
    class ShapeMenuItem extends JMenuItem {

        // initializes variables used
        private static final long serialVersionUID = 770975188019509393L;
        private final File shapeFile;
        private int[][] sGrid;
        private int xSize;
        private int ySize;

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
                ySize = reader.readLine().length();
                xSize = Files.readAllLines(
                        Paths.get(shapeFile.getAbsolutePath()),
                        Charset.defaultCharset()).size() - 2;
                sGrid = new int[ySize][xSize];
                // resets back to the top of the shape
                reader.reset();
                // loops through each line of the shape and converts the
                // characters to cell states
                String line = reader.readLine();
                for (int x = 0; x < xSize; x++) {
                    for (int y = 0; y < ySize; y++)
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
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(this, "Error closing file: "
                            + shapeFile.getName());
                }
            }
            // adds the action listener to put the file into the current grid
            // and repaint
            addActionListener(e -> {
                try {
                    for (int x = 0; x < xSize; x++)
                        for (int y = 0; y < ySize; y++)
                            grid[y + currentY][x + currentX] = sGrid[y][x] == 1 ? user
                                    : sGrid[y][x] == 2 ? (user == 1 ? user + 1
                                    : user - 1) : 0;
                    p.repaint();
                    // catch array out of bounds error and prompt user that the
                    // shape is too big for the grid
                } catch (ArrayIndexOutOfBoundsException ex) {
                    JOptionPane
                            .showMessageDialog(this, "Shape to big for grid");
                }
            });
        }
    }

    // file filter class to filter out non-lexicon files in the file choosers
    // used in the program
    class CellFileFilter extends FileFilter {

        // defines the files allowed
        public boolean accept(File f) {
            return f.isDirectory() || f.getName().endsWith(".cells");
        }

        // define the description to be shows in the file chooser
        public String getDescription() {
            return "Lexicon files (*.cells)";
        }
    }
}
