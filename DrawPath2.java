import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class DrawPath2 {
    ArrayList<Point> points1 = new ArrayList<Point>();
    ArrayList<Point> points2 = new ArrayList<Point>();
    public String IP;
    public int Port;
    public int choice; // 1: server; 2: client
    ServerSocket ss; 
    Socket s;
    ObjectInputStream is;
    ObjectOutputStream os;
    MyPanel p;
    JFrame frame;
    
    public static void main( String[] args) {
    	DrawPath2 dp = new DrawPath2();
    	if (args.length != 3) {
    		System.out.println("Please provide arguments: <1: server / 2: client> <Server IP> <Port Number>");
    		System.exit(0);
    	}
    	dp.choice = Integer.parseInt(args[0]);
    	dp.IP = args[1];
    	dp.Port = Integer.parseInt(args[2]);
    	dp.go();	
    }

    public void go() {
    	frame = new JFrame();
    	if (choice == 1)
    		frame.setTitle("Server");
    	else
    		frame.setTitle("Client");
    	frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE);
     	p = new MyPanel();
    	frame.getContentPane().add(p);
        p.addMouseListener(p);
        p.addMouseMotionListener(p);

    	frame.setSize( 300, 300);
    	frame.setVisible( true);
    	setupNetworking();
    	Thread t = new Thread(new InReader());
    	t.start();
    }
    
    private void setupNetworking() {
    	if (choice == 1) {
    		try {
    			ss = new ServerSocket(Port);
    			s = ss.accept();
    			is = new ObjectInputStream(s.getInputStream());
    			os = new ObjectOutputStream(s.getOutputStream());
    			System.out.println("Connection established!");
    		} catch (IOException e) { e.printStackTrace(); }
    	}
    	else if (choice == 2) {
    		try {
    			s = new Socket(IP, Port);
    			os = new ObjectOutputStream(s.getOutputStream());
    			is = new ObjectInputStream(s.getInputStream());
    			System.out.println("Connection established!");
    		} catch (IOException e) { e.printStackTrace(); }
    	}
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
    		Point prevPoint1 = null;
    		for (Point p: points1) {
    			if (prevPoint1 != null) {
    				g.drawLine(prevPoint1.x, prevPoint1.y, p.x, p.y);
    			}
    			prevPoint1 = p;
    		}
    		Point prevPoint2 = null;
    		for (Point p: points2) {
    			if (prevPoint2 != null) {
    				g.drawLine(prevPoint2.x, prevPoint2.y, p.x, p.y);
    			}
    			prevPoint2 = p;
    		}
    	}
    	@Override
    	public void mouseDragged(MouseEvent event) {
    		points1.add(event.getPoint());
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
    		points1.clear();
    		points1.add(event.getPoint());
    		repaint();
    	}
    	@Override
    	public void mouseReleased(MouseEvent event) {
    	}
    }

    public class InReader implements Runnable {
    	public void run() {
    	}
    } 
    
}