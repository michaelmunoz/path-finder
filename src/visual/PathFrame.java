package visual;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class PathFrame extends JFrame implements IConstants, KeyListener {
    public TileGrid grid;
    private PathFrameManager vm;
    public PathFrame() {
        super();
        this.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        this.setTitle("Path Finder");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        grid = new TileGrid();
        vm = new PathFrameManager(this);
        this.add(grid);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setUndecorated(true);
        this.setVisible(true);
        addKeyListener(this);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (exitProgramKeysPressed(e)) {
            System.exit(0);
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
}
