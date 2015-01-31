import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.filechooser.FileFilter;

public class gameoflife extends JFrame implements Runnable {

    private static final long serialVersionUID = -6912109370693886742L;
    private boolean edit;
    private Timer gameTimer;
	private gridPanel p;
	private boolean running;
	private int[][] grid;
	private int xSize;
	private int ySize;
	private int currentX;
	private int currentY;
	private int user;
	private Color user1;
	private Color user2;
	private JPopupMenu shapeMenu;

	public gameoflife() {
	    setTitle("Multiplayer Game of Life");
	    user1 = Color.GREEN.darker();
	    user2 = Color.BLUE;
	    gameTimer = new Timer(100, (e) -> nextStep());
	    edit = true;
		p = new gridPanel();
		xSize = 20;
		ySize = 20;
		user = 1;
		grid = new int[xSize][ySize];
		for (int x = 0; x < xSize; x++)
		    for (int y = 0; y < ySize; y++)
		        grid[x][y] = 0;
	}

	private void makeMenus() {
	    JMenuBar mbar = new JMenuBar();
	    setJMenuBar(mbar);
	    JMenu sizeMenu = buildSizeMenu();
	    JMenu speedMenu = buildSpeedMenu();
	    JMenu fileMenu = buildFileMenu();
	    JMenuItem startItem = new JMenuItem("Start");
	    JMenuItem stepItem = new JMenuItem("Step");
	    JMenuItem userItem = new JMenuItem("User: 1");
	    JMenuItem colorItem = new JMenuItem("Color");
	    JMenuItem clearItem = new JMenuItem("Clear");
	    startItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0));
	    stepItem.setAccelerator(KeyStroke.getKeyStroke("N"));
	    userItem.setAccelerator(KeyStroke.getKeyStroke("U"));
	    colorItem.setAccelerator(KeyStroke.getKeyStroke("C"));
	    clearItem.setAccelerator(KeyStroke.getKeyStroke("Q"));
	    stepItem.addActionListener(e -> nextStep());
	    userItem.addActionListener(e ->  { user = changeUser();          userItem.setText("User: " + user); });
	    colorItem.addActionListener(e ->  {
	        if (user == 1)
	            user1 = JColorChooser.showDialog(p, "Choose a Color", user1);
	        else
	            user2 = JColorChooser.showDialog(p, "Choose a Color", user2);
	        p.repaint();
	    });
	    clearItem.addActionListener(e -> { grid = new int[xSize][ySize]; p.repaint();                       });
	    startItem.addActionListener(e -> {
            running = !running;
            startItem.setText(running ? "Stop" : "Start");
            edit = running ? false : true;
            stepItem.setEnabled(!running);
            clearItem.setEnabled(!running);
            userItem.setEnabled(!running);
            colorItem.setEnabled(!running);
            sizeMenu.setEnabled(!running);
            speedMenu.setEnabled(!running);
            if (running)
                gameTimer.start();
            else
                gameTimer.stop();
	    });
	    mbar.add(fileMenu);
	    mbar.add(startItem);
	    mbar.add(stepItem);
	    mbar.add(userItem);
	    mbar.add(colorItem);
	    mbar.add(clearItem);
	    mbar.add(sizeMenu);
	    mbar.add(speedMenu);
	}

	private int changeUser() { return user == 1 ? 2 : 1; }

	private JMenu buildSizeMenu() {
	    JMenu sizeMenu = new JMenu("Size");
	    sizeMenu.add(new SizeMenuItem(10, 10));
	    sizeMenu.add(new SizeMenuItem(20, 20));
	    sizeMenu.add(new SizeMenuItem(30, 30));
	    sizeMenu.add(new SizeMenuItem(40, 40));
	    sizeMenu.add(new SizeMenuItem(50, 50));
	    sizeMenu.add(new SizeMenuItem(75, 75));
	    sizeMenu.add(new SizeMenuItem(100, 100));
	    JMenuItem customSize = new JMenuItem("Custom...");
	    customSize.addActionListener(e -> {
	        try {
	            int w = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter the width:"));
	            int h = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter the height:"));
	            sizeMenu.remove(customSize);
	            sizeMenu.add(new SizeMenuItem(w, h));
	            sizeMenu.add(customSize);
            } catch (java.lang.NumberFormatException n) { }
        });
	    sizeMenu.add(customSize);
	    return sizeMenu;
	}
	
	private JMenu buildSpeedMenu() {
	    JMenu speedMenu = new JMenu("Speed");
	    speedMenu.add(new SpeedMenuItem(10));
	    speedMenu.add(new SpeedMenuItem(25));
	    speedMenu.add(new SpeedMenuItem(50));
	    speedMenu.add(new SpeedMenuItem(100));
	    speedMenu.add(new SpeedMenuItem(200));
	    speedMenu.add(new SpeedMenuItem(500));
	    JMenuItem customSpeed = new JMenuItem("Custom...");
        customSpeed.addActionListener(e -> {
            try {
                int h = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter the speed:"));
                speedMenu.remove(customSpeed);
                speedMenu.add(new SpeedMenuItem(h));
                speedMenu.add(customSpeed);
            } catch (java.lang.NumberFormatException n) { }
        });
        speedMenu.add(customSpeed);
	    return speedMenu;
	}
	
	private JMenu buildFileMenu() {
	    JMenu fileMenu = new JMenu("File");
	    JMenuItem openItem = new JMenuItem("Open");
	    JMenuItem saveItem = new JMenuItem("Save");
	    JMenuItem quitItem = new JMenuItem("Quit");
	    openItem.setAccelerator(KeyStroke.getKeyStroke("control O"));
	    openItem.addActionListener(e -> openFile());
	    saveItem.setAccelerator(KeyStroke.getKeyStroke("control S"));
	    saveItem.addActionListener(e -> cropGrid());
	    quitItem.setAccelerator(KeyStroke.getKeyStroke("control shift Q"));
	    quitItem.addActionListener(e -> System.exit(0));
	    fileMenu.add(openItem);
	    fileMenu.add(saveItem);
	    fileMenu.add(quitItem);
	    return fileMenu;
	}
	
	private void nextStep() {
	    int[][] newGrid = new int[xSize][ySize];
	    for (int x = 0; x < xSize; x++) {
	        for (int y = 0; y < ySize; y++) {
	            ArrayList<Integer> neighbors = gatherNeighbors(x, y);
	            int oneCount = Collections.frequency(neighbors, 1);
	            int twoCount = Collections.frequency(neighbors, 2);
	            if (grid[x][y] == 0 && neighbors.size() == 3) newGrid[x][y] = oneCount > twoCount ? 1 : 2;
	            else if (grid[x][y] == 1 || grid[x][y] == 2)  newGrid[x][y] = (neighbors.size() < 2 || neighbors.size() > 3) ? 0 : grid[x][y];
	            else                                          newGrid[x][y] = 0;
	        }
	    }
	    grid = newGrid;
	    p.repaint();
	}

	private int[][] resizeGrid(int newXSize, int newYSize) {
	    int[][] newGrid = new int[newXSize][newYSize];
	    for (int x = 0; x < xSize; x++)
	        for (int y = 0; y < ySize; y++)
	            if (x < newXSize - 1 && y < newYSize - 1)
	                newGrid[x][y] = grid[x][y];
	    xSize = newXSize;
	    ySize = newYSize;
	    return newGrid;
	}

	private ArrayList<Integer> gatherNeighbors(int x, int y) {
	    ArrayList<Integer> count = new ArrayList<Integer>();

	    int right     = grid[x == xSize - 1 ? 0 : x + 1][y];
	    int left      = grid[x == 0 ? xSize - 1 : x - 1][y];
	    int up        = grid[x][y == ySize - 1 ? 0 : y + 1];
	    int down      = grid[x][y == 0 ? ySize - 1 : y - 1];

	    int rightup   = grid[x == xSize - 1 ? 0 : x + 1][y == ySize - 1 ? 0 : y + 1];
	    int rightdown = grid[x == xSize - 1 ? 0 : x + 1][y == 0 ? ySize - 1 : y - 1];
	    int leftup    = grid[x == 0 ? xSize - 1 : x - 1][y == ySize - 1 ? 0 : y + 1];
	    int leftdown  = grid[x == 0 ? xSize - 1 : x - 1][y == 0 ? ySize - 1 : y - 1];

	    if (right != 0)     count.add(right);
	    if (left != 0)      count.add(left);
	    if (up != 0)        count.add(up);
	    if (down != 0)      count.add(down);
	    if (rightup != 0)   count.add(rightup);
	    if (rightdown != 0) count.add(rightdown);
	    if (leftup != 0)    count.add(leftup);
	    if (leftdown != 0)  count.add(leftdown);

	    return count;
	}
	
	private void cropGrid() {
	    int firstRow = -1;
	    int lastRow = -1;
	    int firstCol = -1;
	    int lastCol = -1;
	    for (int y = 0; y < ySize; y++) {
	        for (int x = 0; x < xSize; x++) {
	            if (grid[x][y] != 0) {
	                if (firstRow == -1)    firstRow = y;
	                else if (y < firstRow) firstRow = y;
	                if (y > lastRow)       lastRow = y;
	                if (firstCol == -1)    firstCol = x;
	                else if (x < firstCol) firstCol = x;
	                if (x > lastCol)       lastCol = x;
	            }
	        }
	    }
	    if (firstRow != -1) {
	        int[][] cGrid = new int[lastRow+1][lastCol+1];
	        for (int x = firstRow; x <= lastRow; x++)
	            for (int y = firstCol; y <= lastCol; y++)
	                cGrid[x-firstRow][y-firstCol] = grid[y][x];
	        writeToFile(cGrid, lastRow - firstRow, lastCol - firstCol);
	    }
	}
	
	private void writeToFile(int[][] cgrid, int xSize, int ySize) {
	    JFileChooser jfc = new JFileChooser();
        jfc.setFileFilter(new CellFileFilter());
        int willSave = jfc.showSaveDialog(gameoflife.this);
        if (willSave == JFileChooser.APPROVE_OPTION) {
            File currentFile = jfc.getSelectedFile();
            if (!currentFile.getPath().toLowerCase().endsWith(".cells"))
                currentFile = new File(currentFile.getPath() + ".cells");
            BufferedWriter writer = null;
            try { 
                writer = new BufferedWriter(new FileWriter(currentFile));
                writer.write("!Name: " + currentFile.getName().replace(".cells", "") + "\n!\n");
                for(int x = 0; x <= xSize; x++ ) {
                    for (int y = 0; y <= ySize; y++) {
                        if (cgrid[x][y] == 0)
                            writer.write(".");
                        else
                            writer.write(cgrid[x][y] == 1 ? "O" : "I");
                    }
                    writer.newLine();
                }
            } catch (IOException e)   { JOptionPane.showMessageDialog(this, "File error.");         }
            finally {
                try                   { if (writer != null) writer.close();                         }
                catch (IOException e) { JOptionPane.showMessageDialog(this, "Error closing file."); }
            }
            shapeMenu.add(new ShapeMenuItem(currentFile));
        }
	}

	private void openFile() {
	    JFileChooser jfc = new JFileChooser();
	    jfc.setFileFilter(new CellFileFilter());
	    int willOpen = jfc.showOpenDialog(gameoflife.this);
	    if (willOpen == JFileChooser.APPROVE_OPTION)
	        shapeMenu.add(new ShapeMenuItem(jfc.getSelectedFile()));
	}
	
    public void run() {
        setSize(700,726);
        makeMenus();
        shapeMenu = new JPopupMenu("Shapes");
        getContentPane().add(p);
        p.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                currentY = new BigDecimal((float) e.getX()/((float) p.getWidth()/xSize)).setScale(0, RoundingMode.DOWN).intValue();
                currentX = new BigDecimal((float) e.getY()/((float) p.getHeight()/ySize)).setScale(0, RoundingMode.DOWN).intValue();
                if (SwingUtilities.isRightMouseButton(e))
                    shapeMenu.show(e.getComponent(), e.getX(), e.getY());
                else if (edit) {
                    grid[currentY][currentX] = (grid[currentY][currentX] == user ? 0 : user);
                    p.repaint();
                }
            }
            @Override
            public void mousePressed(MouseEvent e)  { }
            @Override
            public void mouseReleased(MouseEvent e) { }
            @Override
            public void mouseEntered(MouseEvent e)  { }
            @Override
            public void mouseExited(MouseEvent e)   { }
        });
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    public static void main(String[] args) { javax.swing.SwingUtilities.invokeLater(new gameoflife()); }
    

	class gridPanel extends JPanel {

        private static final long serialVersionUID = -897367256144487579L;

        public void paintComponent(Graphics g) {
		    g.setColor(Color.WHITE);
		    g.fillRect(0, 0, p.getWidth(), p.getHeight());
			for (int x = 0; x < xSize; x++) {
			    for (int y = 0; y < ySize; y++) {
			        float height = (float) p.getHeight()/ySize;
			        float width = (float) p.getWidth()/xSize;
			        Graphics2D g2 = (Graphics2D) g;
			        g2.setColor(grid[x][y] == 1 ? user1 : grid[x][y] == 2 ? user2 : Color.WHITE);
			        Rectangle2D r2d = new Rectangle2D.Float(x*width, y*height, width, height);
			        g2.fill(r2d);
			        g2.setStroke(new BasicStroke(1));
			        g2.setColor(Color.BLACK);
			        g2.draw(r2d);
			    }
			}
		}
	}
	

	class SizeMenuItem extends JMenuItem {

        private static final long serialVersionUID = 5500403689725866488L;
        private final int x, y;

        public SizeMenuItem(int _x, int _y) {
            super(_x + "x"+_y);
            x = _x;
            y = _y;
            addActionListener(e -> { grid = resizeGrid(x, y); p.repaint(); });
        }
    }
	
	
	class SpeedMenuItem extends JMenuItem {

        private static final long serialVersionUID = -2630048822872351199L;

        public SpeedMenuItem(int _speed) {
	        super (_speed + "ms");
	        addActionListener(e -> gameTimer = new Timer(_speed, (f) -> nextStep()));
	    }
	}
	

	class ShapeMenuItem extends JMenuItem {

        private static final long serialVersionUID = 770975188019509393L;
        private final File shapeFile;
	    private int[][] sGrid;
	    private int xSize;
	    private int ySize;

	    public ShapeMenuItem(File _shapeFile) {
	        super(_shapeFile.getName().replace(".cells", ""));
	        shapeFile = _shapeFile;
	        BufferedReader reader = null;
	        try {
	            reader = new BufferedReader(new FileReader(shapeFile));
	            reader.readLine();
	            reader.readLine();
	            reader.mark(10000);
	            ySize = reader.readLine().length();
	            xSize = Files.readAllLines(Paths.get(shapeFile.getAbsolutePath()), Charset.defaultCharset()).size() - 2;
	            sGrid = new int[ySize][xSize];
	            reader.reset();
	            String line = reader.readLine();
	            for (int x = 0; x < xSize; x++) {
	                for (int y = 0; y < ySize; y++)
	                    sGrid[y][x] = line.charAt(y) == '.' ? 0 : line.charAt(y) == 'O' ? 1 : 2;
	                line = reader.readLine();
	            }
	        } catch (IOException e) {
	            sGrid = new int[0][0];
	            JOptionPane.showMessageDialog(this, "File Error");
	        }
	        finally {
	            try                   { reader.close();      }
	            catch (IOException e) { JOptionPane.showMessageDialog(this, "Error closing file."); }
	        }
	        addActionListener(e -> {
	            try {
	                for (int x = 0; x < xSize; x++)
	                    for (int y = 0; y < ySize; y++)
	                        grid[y + currentY][x + currentX] = sGrid[y][x];
	                p.repaint();
	            } catch (ArrayIndexOutOfBoundsException ex) { JOptionPane.showMessageDialog(this, "Shape to big for grid"); }
	        });
	    }
	}
	

	class CellFileFilter extends FileFilter {

        public boolean accept(File f)  { return f.isDirectory() ? true : f.getName().endsWith(".cells"); }

        public String getDescription() { return "Lexicon files (*.cells)";                               }
    }
}
