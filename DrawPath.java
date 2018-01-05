import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class DrawPath {
    ArrayList<Point> points = new ArrayList<Point>();

    public static void main( String[] args) {
    	DrawPath dp = new DrawPath();
    	dp.go();	
    }

    public void go() {
    	JFrame frame = new JFrame();
    	frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE);
     	MyPanel p = new MyPanel();
    	frame.getContentPane().add(p);
        p.addMouseListener(p);
        p.addMouseMotionListener(p);
    	frame.setSize( 300, 300);
    	frame.setVisible( true);
    }
    
    class MyPanel extends JPanel implements MouseListener, MouseMotionListener {   //An inner class
    	public void paintComponent( Graphics g) {
    		g.setColor(Color.white);   //Erase the previous figures
    		g.fillRect(0, 0, getWidth(), getHeight());
    		g.setColor(Color.black);
    		if(g instanceof Graphics2D) {
    			Graphics2D g2D = (Graphics2D) g;
    			g2D.setStroke(new BasicStroke(20, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
    		}
    		Point prevPoint = null;
    		for (Point p: points) {
    			if (prevPoint != null) {
    				g.drawLine(prevPoint.x, prevPoint.y, p.x, p.y);
    			}
    			prevPoint = p;
    		}
    	}
    	@Override
    	public void mouseDragged(MouseEvent event) {
    		points.add(event.getPoint());
    		repaint();
    	}
    	@Override
    	public void mouseMoved(MouseEvent event) {
    	}
    	@Override
    	public void mouseClicked(MouseEvent event) {
    	}
    	@Override
    	public void mouseEntered(MouseEvent event) {
    	}
    	@Override
    	public void mouseExited(MouseEvent event) {
    	}
    	@Override
    	public void mousePressed(MouseEvent event) {
    		points.clear();
    		points.add(event.getPoint());
    		repaint();
    	}
    	@Override
    	public void mouseReleased(MouseEvent event) {
    	}
    }

}
