package visual;

import core.Grid;
import core.Node;
import core.SearchData;

import finders.AStarFinder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that provides an overlay of JPanels and JComponents.
 * Handles key inputs.
 */
public class PathPanel extends JPanel implements IConstants, KeyListener, ActionListener {
    private TileGrid tileGrid;
    private Grid grid;
    private ControllerMenu menu;
    private List<Line> lines;
    private boolean isRunning;
    private boolean isPaused;
    private boolean linesDrawn;
    private SearchData results;
    private int iterationIndex;
    private Timer timer;

    PathPanel() {
        super();
        this.setLayout(new OverlayLayout(this));

        tileGrid = new TileGrid();
        menu = new ControllerMenu();

        this.add(menu);
        this.add(tileGrid);
        this.setFocusable(true);
        this.requestFocus();
        this.addKeyListener(this);

        isRunning = false;
        isPaused = false;
        linesDrawn = false;
        iterationIndex = 0;
        results = null;
        timer = new Timer(2, this);
    }

    public void startSearch() {
        clearPath();
        isRunning = true;
        isPaused = false;
        linesDrawn = false;
        // Reset animation variables.
        iterationIndex = 0;
        results = null;
        // Perform path finding.
        grid = new Grid(tileGrid);
        AStarFinder aStar = new AStarFinder(grid);
        results = aStar.findPath();
        // Set timer speed based on whether a path is found.
        if (results != null) {
            System.out.println("Path Found");
            timer.setDelay(BASE_DELAY);
        } else {
            System.out.println("No Path Exists");
            timer.setDelay(FAIL_DELAY);
        }
        // Animate search.
        timer.start();
    }

    /**
     * Toggle pause during animation of search.
     */
    public void pauseSearch() {
        if (isPaused) {
            timer.start();
        } else {
            timer.stop();
        }
        isPaused = !isPaused;
    }

    /**
     * Stop current run and reset grid to initial state.
     */
    public void cancelSearch() {
        isRunning = false;
        isPaused = false;
        linesDrawn = false;
        iterationIndex = 0;
        results = null;
        timer.stop();
        clearPath();
    }

    /**
     * Get the list of lines that correspond to the path between nodes.
     * @param tiles List of tiles that correspond to the path.
     * @return List of lines.
     */
    private List<Line> getLines(List<Tile> tiles) {
        ArrayList<Line> lines = new ArrayList<>();
        for (int i = 0; i < tiles.size()-1; i++) {
            lines.add(new Line(tiles.get(i), tiles.get(i+1)));
        }
        return lines;
    }

    /**
     * Add lines from path starting from the start node to the end node.
     * @param lines List of lines to add to the canvas
     */
    private void drawPath(List<Line> lines) {
        this.remove(tileGrid);
        for (Line line : lines) {
            this.add(line);
        }
        this.repaint();
        this.add(tileGrid);
        this.updateUI();
    }

    /**
     * Clear all lines and reset all open and closed nodes in the tile grid.
     */
    private void clearPath() {
        this.removeAll();
        // Clear lines from grid.
        if (lines != null) {
            lines.clear();
        }
        // Reset open and closed nodes back to unblocked nodes.
        tileGrid.clearPath();
        // Reflect changes on grid UI.
        this.add(tileGrid);
        repaint();
        updateUI();
    }

    /**
     * Reset the grid to its initial state.
     */
    private void clearWalls() {
        this.removeAll();
        // Clear lines from grid.
        if (lines != null) {
            lines.clear();
        }
        // Reset nodes back to normal.
        tileGrid.clearWalls();
        // Reflect changes on grid UI.
        this.add(tileGrid);
        repaint();
        updateUI();
    }

    /**
     * Indicate failed path search by setting tiles to failed color.
     */
    private void failPath() {
        this.removeAll();
        // Reset open and closed nodes back to unblocked nodes.
        tileGrid.failPath();
        // Reflect changes on grid UI.
        this.add(tileGrid);
        repaint();
        updateUI();
    }

    /**
     * Performs a rainbow effect on tiles that are normal.
     */
    private void rainbow() {
        tileGrid.rainbowBoard();
    }

    /**
     * Set timer delay based on progress in the animation.
     * Start animation off fast then slow towards middle and then speed up towards the end.
     */
    private void setTimerSpeed() {
        int delay;
        double percentDone = (double)(iterationIndex+1)/results.getOpenSetList().size();
        if (percentDone <= 0.2) {
            delay = BASE_DELAY;
        } else if (0.2 < percentDone && percentDone <= 0.4) {
            delay = 2*BASE_DELAY;
        } else if (0.4 < percentDone && percentDone <= 0.6) {
            delay = 3*BASE_DELAY;
        } else if (0.6 < percentDone && percentDone <= 0.8) {
            delay = 2*BASE_DELAY;
        } else if (0.8 < percentDone) {
            delay = BASE_DELAY;
        } else {
            delay = BASE_DELAY;
        }
        timer.setDelay(delay);
    }

    /**
     * Animates the path finder searching.
     * Method invoked by timer.
     * @param actionEvent event
     */
    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        // If path finder is done and has results.
        if (results != null) {
            // If there are "frames" to animate.
            if (iterationIndex < results.getOpenSetList().size()) {
                // Paint the current iteration of open neighbor nodes.
                Node[] openSet = results.getOpenSetList().get(iterationIndex);
                Tile[][] tileMatrix = tileGrid.getTileMatrix();
                for (Node openNode : openSet) {
                    if (!(openNode.equals(grid.getStartNode()) || openNode.equals(grid.getEndNode()))) {
                        tileMatrix[openNode.getRow()][openNode.getCol()].setStatus(Tile.Status.OPEN);
                    }
                }
                // Paint the current iteration of closed nodes.
                Node closedNode = results.getClosedSetList().get(iterationIndex);
                if (!(closedNode.equals(grid.getStartNode()) || closedNode.equals(grid.getEndNode()))) {
                    tileMatrix[closedNode.getRow()][closedNode.getCol()].setStatus(Tile.Status.CLOSED);
                }
                setTimerSpeed();
                iterationIndex++;
            } else {
                // Done animating, draw the lines that show the resulting path.
                if (!linesDrawn) {
                    lines = this.getLines(tileGrid.getTiles(results.getPath()));
                    drawPath(lines);
                    linesDrawn = true;
                } else {
                    isRunning = false;
                    timer.stop();
                    rainbow();
                    // Reset animation variables.
                    iterationIndex = 0;
                    results = null;
                }
            }
        } else {
            if (iterationIndex < (FAIL_BLINKS * 2)) {
                if (iterationIndex % 2 == 0) {
                    failPath();
                } else {
                    clearPath();
                }
                iterationIndex++;
            } else {
                isRunning = false;
                timer.stop();
                // Reset animation variables.
                iterationIndex = 0;
                results = null;
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (exitProgramKeysPressed(e)) {
            System.out.println("Exit");
            System.exit(0);
        } else if (resetKeysPressed(e)) {
            System.out.println("Clear Walls");
            clearWalls();
        } else if (clearKeysPressed(e)) {
            System.out.println("Clear Path");
            clearPath();
        } else if (startKeyPressed(e)) {
            System.out.println("Run");
            startSearch();
        } else if (pauseKeyPressed(e)) {
            System.out.println("Toggle Pause");
            pauseSearch();
        } else if (cancelKeyPressed(e)) {
            System.out.println("Cancel");
            cancelSearch();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    /**
     * Check if a key event is an exit event.
     * @param e KeyEvent
     * @return Returns if any of the escape program keys are hit.
     */
    private boolean exitProgramKeysPressed(KeyEvent e) {
        return e.getKeyChar() == KeyEvent.VK_ESCAPE || e.getKeyChar() == KeyEvent.VK_Q;
    }

    /**
     * Check if a key event is a reset event.
     * @param e KeyEvent
     * @return Returns if any of the reset keys are hit.
     */
    private boolean resetKeysPressed(KeyEvent e) {
        return !isRunning && (e.getKeyChar() == KeyEvent.VK_R);
    }

    /**
     * Check if a key event is a clear event.
     * @param e KeyEvent
     * @return Returns if any of the clear keys are hit.
     */
    private boolean clearKeysPressed(KeyEvent e) {
        return !isRunning && (e.getKeyChar() == KeyEvent.VK_C);
    }

    /**
     * Check if key event is a start search event.
     * @param e KeyEvent
     * @return Returns if a start key is hit.
     */
    private boolean startKeyPressed(KeyEvent e) {
        return !isRunning && (e.getKeyChar() == KeyEvent.VK_S);
    }

    /**
     * Check if key event is a pause search event.
     * @param e KeyEvent
     * @return Returns if a pause key is hit.
     */
    private boolean pauseKeyPressed(KeyEvent e) {
        return (isRunning || isPaused) && e.getKeyChar() == KeyEvent.VK_P;
    }

    /**
     * Check if key event is a cancel search event.
     * @param e KeyEvent
     * @return Returns if a cancel key is hit.
     */
    private boolean cancelKeyPressed(KeyEvent e) {
        return isRunning && (e.getKeyChar() == KeyEvent.VK_C);
    }
}
