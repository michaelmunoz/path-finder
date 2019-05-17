package visual;

import javax.swing.JComponent;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

class Tile extends JComponent implements IConstants, MouseListener {
    enum STATUS {
        NORMAL, BLOCKED, START, END, OPEN, CLOSED, FAILED, TESTED
    }

    private final int size;
    private STATUS status;

    Tile(int size) {
        super();
        this.size = size;
        this.status = STATUS.NORMAL;
        addMouseListener(this);
    }

    private static Color getTileColor(STATUS status) {
        switch (status) {
            case START:
                return startColor;
            case END:
                return endColor;
            case BLOCKED:
                return blockedColor;
            case NORMAL:
                return normalColor;
            case OPEN:
                return openColor;
            case CLOSED:
                return closedColor;
            case FAILED:
                return failedColor;
            default:
                return testedColor;
        }
    }

    public STATUS getStatus() {
        return status;
    }

    public void setStatus(STATUS status) {
        this.status = status;
        this.repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        g2.setColor(getTileColor(this.status));
        Rectangle tile = new Rectangle(size, size);
        g2.fill(tile);
        g2.setColor(Color.BLACK);
        g2.draw(tile);
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getComponent() instanceof Tile) {
            switch (this.getStatus()) {
                case NORMAL:
                    this.setStatus(Tile.STATUS.BLOCKED);
                    break;
                case BLOCKED:
                    this.setStatus(Tile.STATUS.NORMAL);
                    break;
            }
        }
        this.repaint();
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
}
