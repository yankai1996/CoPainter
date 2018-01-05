import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.net.*;

public class CoPainter implements Serializable{
	
	class Path implements Serializable {
		private static final long serialVersionUID = 3035141231L;
		ArrayList<Point> points = new ArrayList<Point>();
		Color pathColor;
		int pathWidth;
	}
	
	ArrayList<Path> paths = new ArrayList<Path>();
	Color penColor;
	int penWidth;
	
	Path currentPath = null;
	
	String IP;
	int Port;
	int choice;	//1.server; 2.client
	transient ServerSocket serverSocket;
	transient Socket socket;
	transient ObjectInputStream istream;
	transient ObjectOutputStream ostream;
	
	transient MyPanel drawingBoard;
	
	class OutputStreamList extends ArrayList<ObjectOutputStream> {
		public synchronized boolean add(ObjectOutputStream os) {
			return super.add(os);
		}
		public synchronized boolean remove(ObjectOutputStream os) {
			return super.remove(os);
		}
	}
	transient OutputStreamList clientOutputStream = new OutputStreamList();
	
    public static void main(String[] args) {
    	CoPainter cp = new CoPainter();
    	cp.start();
    }
    

    public void start() {
		JFrame startup = new JFrame();
		JPanel mainPanel = new JPanel();
    	startup.getContentPane().add(mainPanel);
    	JPanel hostPanel = new JPanel();
    	JPanel portPanel = new JPanel();
    	JPanel btnPanel = new JPanel();
    	JLabel host = new JLabel("Host:");
    	JLabel port = new JLabel("Port:");
    	JTextField getHost = new JTextField(20);
    	JTextField getPort = new JTextField(20);
    	JButton start = new JButton("Start as a host");
    	JButton connect = new JButton("Connect to a host");
    	mainPanel.setLayout(new GridLayout(3,1));
    	mainPanel.add(hostPanel);
    	mainPanel.add(portPanel);
    	mainPanel.add(btnPanel);
    	hostPanel.add(host);
    	hostPanel.add(getHost);
    	portPanel.add(port);
    	portPanel.add(getPort);
    	btnPanel.add(start);
    	btnPanel.add(connect);
    	startup.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	startup.setSize(300,150);
    	startup.setResizable(false);
    	startup.setLocationRelativeTo(null);
    	startup.setVisible(true);
    	
    	start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Port = Integer.parseInt(getPort.getText());
					choice = 1;

					serverSocket = new ServerSocket(Port);
			    	clientOutputStream = new OutputStreamList();
			    	
					Thread connection = new Thread(new MultiConnection());
		    		connection.start();
		    		
					startup.setVisible(false);
					startup.dispose();
			    	go();
				} catch(NumberFormatException NFe) {
					JOptionPane.showMessageDialog(new JFrame(), "Please input an appropriate TCP Port number!",
							"Fail to start",JOptionPane.ERROR_MESSAGE);
				} catch(Exception ex) {
					JOptionPane.showMessageDialog(new JFrame(), "Unable to listen to port " + getPort.getText() +"!",
							"Fail to start",JOptionPane.ERROR_MESSAGE);
				}
			}
    	});
    	
    	connect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Port = Integer.parseInt(getPort.getText());
					IP = getHost.getText();
					choice = 2;
					
					socket = new Socket(IP, Port);
					ostream = new ObjectOutputStream(socket.getOutputStream());
					clientOutputStream = new OutputStreamList();
			    	clientOutputStream.add(ostream);
			    	istream = new ObjectInputStream(socket.getInputStream());
			    	
				    Thread t = new Thread(new ClientHandler(socket, ostream, istream));
				   	t.start();
				   	
					startup.setVisible(false);
					startup.dispose();
			    	go();
				} catch(NumberFormatException NFe) {
					JOptionPane.showMessageDialog(new JFrame(), "Please input an appropriate TCP Port number!",
							"Fail to start",JOptionPane.ERROR_MESSAGE);
				} catch(Exception ex) {
					JOptionPane.showMessageDialog(new JFrame(), "Unable to connect to host!",
							"Fail to start",JOptionPane.ERROR_MESSAGE);
				}
			}
    	});
	}
		
    class MultiConnection implements Runnable {
    	public void run(){
    		try {
		    	while (true) {
		    		socket = serverSocket.accept();
		    		
			    	ostream = new ObjectOutputStream(socket.getOutputStream());
			    	clientOutputStream.add(ostream);
			    	istream = new ObjectInputStream(socket.getInputStream());
			    	Thread t = new Thread(new ClientHandler(socket, ostream, istream));
			    	t.start();
		    	}
    		} catch (IOException IOe) {
 		    	IOe.printStackTrace();
 		    }
    	}
    }
    
    
    public void go() {
    	JFrame frame = new JFrame();
    	frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    	
    	if (choice == 1)
    		frame.setTitle("Collaborative Painter - Server"); 
    	else if (choice == 2)
    		frame.setTitle("Collaborative Painter - Client");
    	
    	JPanel mainPanel = new JPanel();
    	mainPanel.setLayout(new BorderLayout());
    	frame.getContentPane().add(mainPanel);
    	
    	drawingBoard = new MyPanel();
    	mainPanel.add(BorderLayout.CENTER, drawingBoard);
    	drawingBoard.addMouseListener(drawingBoard);
    	drawingBoard.addMouseMotionListener(drawingBoard);
        
    	JPanel penPicker = new JPanel();
        penPicker.setPreferredSize(new Dimension(750, 60));
        penPicker.setLayout(new BorderLayout());
        mainPanel.add(BorderLayout.SOUTH, penPicker);
        
        JPanel colorPanel = new JPanel();
        JPanel sizePanel = new JPanel();
        penPicker.add(BorderLayout.WEST, colorPanel);
        penPicker.add(BorderLayout.EAST, sizePanel);
       
        Dimension square = new Dimension(50, 50);
        
        ColorPicker Black = new ColorPicker(Color.black);
        ColorPicker Red = new ColorPicker(Color.red);
        ColorPicker Blue = new ColorPicker(Color.blue);
        ColorPicker Green = new ColorPicker(Color.green);
        ColorPicker White = new ColorPicker(Color.white);
        Black.setPreferredSize(square);
        Red.setPreferredSize(square);
        Blue.setPreferredSize(square);
        Green.setPreferredSize(square);
        White.setPreferredSize(square);
        colorPanel.add(Black);
        colorPanel.add(Red);
        colorPanel.add(Blue);
        colorPanel.add(Green);
        colorPanel.add(White);
        
        SizePicker Size1 = new SizePicker(5);
        SizePicker Size2 = new SizePicker(9);
        SizePicker Size3 = new SizePicker(15);
        SizePicker Size4 = new SizePicker(19);
        SizePicker Size5 = new SizePicker(25);
        Size1.setPreferredSize(square);
        Size2.setPreferredSize(square);
        Size3.setPreferredSize(square);
        Size4.setPreferredSize(square);
        Size5.setPreferredSize(square);
        sizePanel.add(Size1);
        sizePanel.add(Size2);
        sizePanel.add(Size3);
        sizePanel.add(Size4);
        sizePanel.add(Size5);
        
        penColor = Black.c;		//initialized here
        penWidth = Size3.d;		//
        
    	JMenuBar MenuBar = new JMenuBar();
    	JMenu Action = new JMenu("Action");
    	JMenuItem Clear = new JMenuItem("Clear");
    	JMenuItem Save = new JMenuItem("Save");
    	JMenuItem Load = new JMenuItem("Load");
    	JMenuItem Exit = new JMenuItem("Exit");
    	if (choice == 1){
    		Action.add(Clear);
    		Action.add(Load);
    	}
    	Action.add(Save);
    	Action.add(Exit);
    	MenuBar.add(Action);
    	frame.setJMenuBar(MenuBar);
    
    	Clear.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				paths.clear();
				drawingBoard.repaint();
				tellEveryone();
			}	
    	});
    	
    	Exit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				for (ObjectOutputStream os : clientOutputStream){
					try {
						os.reset();
						os.writeObject(new Path());
						os.flush();
						os.close();
					} catch(IOException IOe) {
						IOe.printStackTrace();
					}
				}
				
				try {
					socket.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				System.exit(0);
			}
    	});
    	
    	Save.addActionListener(new ActionListener(){
    		public void actionPerformed(ActionEvent e){
    			JFileChooser chooser = new JFileChooser();
				int returnVal = chooser.showSaveDialog(frame);
				if (returnVal == JFileChooser.APPROVE_OPTION){
					try {
						FileOutputStream f = new FileOutputStream(chooser.getSelectedFile());
						BufferedOutputStream bf = new BufferedOutputStream(f);
						ObjectOutputStream os = new ObjectOutputStream(bf);
						os.writeObject(paths);
						os.close();
					} catch (FileNotFoundException FNFe) {
						FNFe.printStackTrace();
					} catch (IOException IOe) {
						IOe.printStackTrace();
					}
    			} 
			}
    	});
    	
    	Load.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				int returnVal = chooser.showOpenDialog(frame);
				if (returnVal == JFileChooser.APPROVE_OPTION){
					try{
						FileInputStream f = new FileInputStream(chooser.getSelectedFile());
						BufferedInputStream bf = new BufferedInputStream(f); 
						ObjectInputStream os = new ObjectInputStream(bf);
						Object obj = os.readObject();
						paths = (ArrayList<Path>)obj;
						os.close();
						drawingBoard.repaint();
						tellEveryone();
					} catch(FileNotFoundException FNFe) {
						FNFe.printStackTrace();
						JOptionPane.showMessageDialog(new JFrame(), "File not found!",
								"Exception",JOptionPane.ERROR_MESSAGE);
					} catch (IOException IOe) {
						JOptionPane.showMessageDialog(new JFrame(), "Coperation Paniter cannot open this file!",
								"Exception",JOptionPane.ERROR_MESSAGE);
						IOe.printStackTrace();
					} catch (ClassNotFoundException CNFe) {
						CNFe.printStackTrace();
					} 
				}
			}
    	});
        
        frame.setSize(750, 600);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
    	frame.setVisible(true);
    }
    
    class MyPanel extends JPanel implements MouseListener, MouseMotionListener {
    	public void paintComponent(Graphics g) {
    		g.setColor(Color.white);
    		g.fillRect(0, 0, getWidth(), getHeight());
    		
    		for (Path path : paths){
    			if (g instanceof Graphics2D) {
        			Graphics2D g2D = (Graphics2D) g;
        			g2D.setStroke(new BasicStroke(path.pathWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        		}
    			g.setColor(path.pathColor);
    			Point prevPoint = null;
    			for (Point p : path.points) {
    				if (p == path.points.get(0))
    					prevPoint = p;
    				if (prevPoint != null) {
    					g.drawLine(prevPoint.x, prevPoint.y, p.x, p.y);
    				}
    				prevPoint = p;
    			}
    		}
    		
    		if (currentPath != null)
    		try {
	    		Point prevPoint = null;
	    		g.setColor(currentPath.pathColor);
	    		if (g instanceof Graphics2D) {
	    			Graphics2D g2D = (Graphics2D) g;
	    			g2D.setStroke(new BasicStroke(currentPath.pathWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
	    		}
	    		for (Point p : currentPath.points) {
					if (p == currentPath.points.get(0))
						prevPoint = p;
					if (prevPoint != null) {
						g.drawLine(prevPoint.x, prevPoint.y, p.x, p.y);
					}
					prevPoint = p;
				}
    		} catch(Exception e) {
    			e.printStackTrace();
    		}
    	}
    	
    	@Override
    	public void mouseDragged(MouseEvent event) {
    		currentPath.points.add(event.getPoint());
    		repaint();
    	}
    	public void mousePressed(MouseEvent event) {
    		currentPath = new Path();
    		currentPath.points.add(event.getPoint());
    		currentPath.pathColor = penColor;
    		currentPath.pathWidth = penWidth;
    		repaint();
    	}
    	
    	public void mouseReleased(MouseEvent event) {
    		paths.add(currentPath);
    		tellEveryone();
    		currentPath = null;
    	}
    	
    	public void mouseMoved(MouseEvent event) {}
    	public void mouseClicked(MouseEvent event) {}
    	public void mouseEntered(MouseEvent event) {}
    	public void mouseExited(MouseEvent event) {}
    }
    
    class ColorPicker extends JButton {
    	Color c;
    	ColorPicker(Color c){
    		this.c = c;
    		this.addActionListener(new ActionListener(){
    			public void actionPerformed(ActionEvent e){
    	    		penColor = c;
    				repaint();
    			}
    		});
    	}
    	
    	public void paintComponent(Graphics g){
    		if (c == penColor) {
    			g.setColor(Color.lightGray);
    			g.fillRoundRect(2, 2, 48, 48, 16, 16);
    			g.setColor(c);
        		g.fillRoundRect(3, 3, 46, 46, 12, 12);
    		} else {
    			g.setColor(Color.lightGray);
    			g.fillRoundRect(0, 0, 50, 50, 16, 16);
    			g.setColor(c);
        		g.fillRoundRect(1, 1, 46, 46, 12, 12);
    		}
    		this.repaint();    		
    	}
    }
    
    class SizePicker extends JButton {
    	int d;
    	SizePicker(int d){
    		this.d = d;
    		this.addActionListener(new ActionListener(){
    			public void actionPerformed(ActionEvent e){
    				penWidth = d;
    			}
    		});
    	}
    	
    	public void paintComponent(Graphics g) {
    		if (d == penWidth) {
    			g.setColor(Color.lightGray);
    			g.fillRoundRect(2, 2, 48, 48, 16, 16);
    			g.setColor(Color.white);
        		g.fillRoundRect(3, 3, 46, 46, 12, 12);
        		g.setColor(Color.black);
        		g.fillOval(25-d/2, 25-d/2, d, d);
    		} else {
    			g.setColor(Color.lightGray);
    			g.fillRoundRect(0, 0, 50, 50, 16, 16);
    			g.setColor(Color.white);
        		g.fillRoundRect(1, 1, 46, 46, 12, 12);
        		g.setColor(Color.black);
        		g.fillOval(23-d/2, 23-d/2, d, d);
    		}
    		this.repaint();
    	}
    }
    
    class ClientHandler implements Runnable {
    	Socket socket;
    	ObjectOutputStream ostream;
    	ObjectInputStream istream;
    	
    	ClientHandler(Socket socket, ObjectOutputStream ostream, ObjectInputStream istream){
    		this.socket = socket;
    		this.ostream = ostream;
    		this.istream = istream;
    	}
    	
		@SuppressWarnings("unchecked")
		public void run() {
			if (choice == 1) 
				tellEveryone();
			
			try {
				while (true) {
					Object obj = istream.readObject();
					try {
						paths = (ArrayList<Path>)obj;
						drawingBoard.repaint();
						if (choice == 1)
							tellEveryone();
					} catch(ClassCastException CCe) {	
						if (choice == 1) {
							clientOutputStream.remove(ostream);
						}
						else if (choice == 2){
							JOptionPane.showMessageDialog(new JFrame(), "Host is gone!",
									"Connection dropped",JOptionPane.ERROR_MESSAGE);
							System.exit(0);
						}
						break;
					} catch(NullPointerException NPe) {
					}
				}
			} catch(EOFException EOFe) {
			} catch (ClassNotFoundException e) {
			} catch (IOException e) {
			}
		}
    }
    
    public void tellEveryone () {
		for (ObjectOutputStream os : clientOutputStream){
			try {
				os.reset();				//reset! reset! reset!
				os.writeObject(paths);
				os.flush();
			} catch(IOException IOe) {
				IOe.printStackTrace();
			} 
		}
	}
    
}