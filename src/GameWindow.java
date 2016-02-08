import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.TitledBorder;
import javax.swing.text.DefaultCaret;

public class GameWindow extends JFrame {
	// MAIN ENGINE
	GameEngine engine;
	
	// MENU BAR
	private JMenuBar menuBar;
	private JMenu informationMenu;
	private JMenuItem instructionsMenuItem;
	private JMenuItem aboutMenuItem;
	
	// NORTH PANEL
	private JPanel northPanel;
	private JLabel userLabel;
	private JLabel timerLabel;
	private JLabel computerLabel;
	
	// CENTER PANEL
	private JPanel centerPanel;
	private JPanel userPanel;
	JLabel[][] userWaterLabels;
	JLabel[][] userLetterLabels;
	private JPanel computerPanel;
	JLabel[][] computerWaterLabels;
	JLabel[][] computerLetterLabels;
	
	// SOUTH PANEL
	private JPanel southPanel;
	JButton startButton;
	private JTextArea statusText;
	private JScrollPane statusScroll;
	private JTextField messageTextField;
	private JButton sendButton;
	
	// IMAGES
	ShrinkIcon iA;
	ShrinkIcon iB;
	ShrinkIcon iC;
	ShrinkIcon iD;
	ShrinkIcon iH;
	ShrinkIcon iM;
	ShrinkIcon iQ;
	ShrinkIcon iWater1;
	ShrinkIcon iWater2;
	ShrinkIcon[] iExplosions;
	ShrinkIcon[] iSplashes;
	
	// THREADS
	private Timer timer;
	private Water water;
	private Explosion user_explosion;
	private Explosion computer_explosion;
	private MissSplash user_miss_splash;
	private MissSplash computer_miss_splash;
	private SinkSplash user_sink_splash;
	private SinkSplash computer_sink_splash;
	private GameSound user_game_sound;
	private GameSound computer_game_sound;
	
	// MISCELLANEOUS
	private String[] lines;
	private String user;
	private String friend;
	private int currentRound;
	private ComputerMouseAdapter[][] computerMouseAdapters;
	private GameClient client;
	private MenuWindow menuWindow;
	private static final long serialVersionUID = 1L;
	
	// GameWindow() 
	public GameWindow(String user, String friend, String[] lines, GameClient client, MenuWindow menuWindow) {
		super("Battleship");
		setSize(800, 520);
		setLocation(0, 0);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				try {
					GameWindow.this.client.disconnectFromGame();
				} catch (IOException ioe) {
					System.out.println("IOException in GameWindow Constructor");
				}

				System.exit(0);
			}
		});
		
		this.lines = lines;
		this.user = user;
		this.friend = friend;
		this.client = client;
		this.menuWindow = menuWindow;
		
		initializeComponents();
		createMenuBar();		
		createNorthPanel();
		createCenterPanel();
		createSouthPanel();	
		createActions();
		
		setJMenuBar(this.menuBar);
		add(this.northPanel, BorderLayout.NORTH);
		add(this.centerPanel, BorderLayout.CENTER);
		add(this.southPanel, BorderLayout.SOUTH);
	}
	
	// initializeComponents();
	private void initializeComponents() {
		// MAIN ENGINE
		this.engine = new GameEngine();
		
		// MENU BAR
		this.menuBar = new JMenuBar();
		this.informationMenu = new JMenu("Information");
		this.instructionsMenuItem = new JMenuItem("Instructions");
		this.aboutMenuItem = new JMenuItem("About");
		
		// NORTH PANEL
		this.northPanel = new JPanel(new GridLayout(1, 2));
		this.northPanel.setBackground(Color.BLUE.darker());
		this.userLabel = new JLabel(this.user);
		this.userLabel.setForeground(Color.WHITE);
		this.timerLabel = new JLabel("Time - 0:15");
		this.timerLabel.setForeground(Color.WHITE);
		this.computerLabel = new JLabel(this.friend);
		this.computerLabel.setForeground(Color.WHITE);
		
		// CENTER PANEL
		this.centerPanel = new JPanel();
		this.centerPanel.setBackground(Color.BLUE.darker());
		this.centerPanel.setLayout(new BoxLayout(this.centerPanel, BoxLayout.X_AXIS));
		this.userPanel = new JPanel(new GridLayout(11, 11));
		this.userPanel.setBackground(Color.BLUE.darker());
		this.userWaterLabels = new JLabel[11][11];
		this.userLetterLabels = new JLabel[11][11];
		this.computerPanel = new JPanel(new GridLayout(11, 11));
		this.computerPanel.setBackground(Color.BLUE.darker());
		this.computerWaterLabels = new JLabel[11][11];
		this.computerLetterLabels = new JLabel[11][11];
		
		// IMAGES
		this.iA = new ShrinkIcon("images/A.png");
		this.iB = new ShrinkIcon("images/B.png");
		this.iC = new ShrinkIcon("images/C.png");
		this.iD = new ShrinkIcon("images/D.png");
		this.iH = new ShrinkIcon("images/H.png");
		this.iM = new ShrinkIcon("images/M.png");
		this.iQ = new ShrinkIcon("images/Q.png");
		this.iWater1 = new ShrinkIcon("images/water1.png");
		this.iWater2 = new ShrinkIcon("images/water2.png");
		this.iExplosions = new ShrinkIcon[5];
		this.iExplosions[0] = new ShrinkIcon("animations/explosion1.png");
		this.iExplosions[1] = new ShrinkIcon("animations/explosion2.png");
		this.iExplosions[2] = new ShrinkIcon("animations/explosion3.png");
		this.iExplosions[3] = new ShrinkIcon("animations/explosion4.png");
		this.iExplosions[4] = new ShrinkIcon("animations/explosion5.png");
		this.iSplashes = new ShrinkIcon[7];
		this.iSplashes[0] = new ShrinkIcon("animations/splash1.png");
		this.iSplashes[1] = new ShrinkIcon("animations/splash2.png");
		this.iSplashes[2] = new ShrinkIcon("animations/splash3.png");
		this.iSplashes[3] = new ShrinkIcon("animations/splash4.png");
		this.iSplashes[4] = new ShrinkIcon("animations/splash5.png");
		this.iSplashes[5] = new ShrinkIcon("animations/splash6.png");
		this.iSplashes[6] = new ShrinkIcon("animations/splash7.png");

		
		for(int r = 0; r < 11; r++) {
			for(int c = 0; c < 11; c++) {
				if(c == 0) {
					if(r == 0) {
						this.userWaterLabels[r][c] = new JLabel("A");
						this.computerWaterLabels[r][c] = new JLabel("A");
					} else if(r == 1) {
						this.userWaterLabels[r][c] = new JLabel("B");
						this.computerWaterLabels[r][c] = new JLabel("B");
					} else if(r == 2) {
						this.userWaterLabels[r][c] = new JLabel("C");
						this.computerWaterLabels[r][c] = new JLabel("C");
					} else if(r == 3) {
						this.userWaterLabels[r][c] = new JLabel("D");
						this.computerWaterLabels[r][c] = new JLabel("D");
					} else if(r == 4) {
						this.userWaterLabels[r][c] = new JLabel("E");
						this.computerWaterLabels[r][c] = new JLabel("E");
					} else if(r == 5) {
						this.userWaterLabels[r][c] = new JLabel("F");
						this.computerWaterLabels[r][c] = new JLabel("F");
					} else if(r == 6) {
						this.userWaterLabels[r][c] = new JLabel("G");
						this.computerWaterLabels[r][c] = new JLabel("G");
					} else if(r == 7) {
						this.userWaterLabels[r][c] = new JLabel("H");
						this.computerWaterLabels[r][c] = new JLabel("H");
					} else if(r == 8) {
						this.userWaterLabels[r][c] = new JLabel("I");
						this.computerWaterLabels[r][c] = new JLabel("I");
					} else if(r == 9) {
						this.userWaterLabels[r][c] = new JLabel("J");
						this.computerWaterLabels[r][c] = new JLabel("J");
					} else if(r == 10) {
						this.userWaterLabels[r][c] = new JLabel("  ");
						this.computerWaterLabels[r][c] = new JLabel("  ");
					}
				} else if(r == 10) {
					if(c == 1) {
						this.userWaterLabels[r][c] = new JLabel("1");
						this.computerWaterLabels[r][c] = new JLabel("1");
					} else if(c == 2) {
						this.userWaterLabels[r][c] = new JLabel("2");
						this.computerWaterLabels[r][c] = new JLabel("2");
					} else if(c == 3) {
						this.userWaterLabels[r][c] = new JLabel("3");
						this.computerWaterLabels[r][c] = new JLabel("3");
					} else if(c == 4) {
						this.userWaterLabels[r][c] = new JLabel("4");
						this.computerWaterLabels[r][c] = new JLabel("4");
					} else if(c == 5) {
						this.userWaterLabels[r][c] = new JLabel("5");
						this.computerWaterLabels[r][c] = new JLabel("5");
					} else if(c == 6) {
						this.userWaterLabels[r][c] = new JLabel("6");
						this.computerWaterLabels[r][c] = new JLabel("6");
					} else if(c == 7) {
						this.userWaterLabels[r][c] = new JLabel("7");
						this.computerWaterLabels[r][c] = new JLabel("7");
					} else if(c == 8) {
						this.userWaterLabels[r][c] = new JLabel("8");
						this.computerWaterLabels[r][c] = new JLabel("8");
					} else if(c == 9) {
						this.userWaterLabels[r][c] = new JLabel("9");
						this.computerWaterLabels[r][c] = new JLabel("9");
					} else if(c == 10) {
						this.userWaterLabels[r][c] = new JLabel("10");
						this.computerWaterLabels[r][c] = new JLabel("10");
					}
				} else {
					this.userWaterLabels[r][c] = new JLabel();
					this.userWaterLabels[r][c].setIcon(this.iWater1);
					this.userWaterLabels[r][c].setLayout(new BorderLayout()); 
					this.userLetterLabels[r][c] = new JLabel();
					this.userLetterLabels[r][c].setIcon(this.iQ);
					this.userWaterLabels[r][c].add(this.userLetterLabels[r][c], BorderLayout.CENTER);
					
					this.computerWaterLabels[r][c] = new JLabel();
					this.computerWaterLabels[r][c].setIcon(this.iWater2);
					this.computerWaterLabels[r][c].setLayout(new BorderLayout()); 
					this.computerLetterLabels[r][c] = new JLabel();
					this.computerLetterLabels[r][c].setIcon(this.iQ);
					this.computerWaterLabels[r][c].add(this.computerLetterLabels[r][c], BorderLayout.CENTER);
				} 
				
				this.userWaterLabels[r][c].setHorizontalAlignment(JLabel.CENTER);
				this.userWaterLabels[r][c].setForeground(Color.WHITE);
				this.computerWaterLabels[r][c].setHorizontalAlignment(JLabel.CENTER);
				this.computerWaterLabels[r][c].setForeground(Color.WHITE);
			}
		}
		
		// SOUTH PANEL
		this.southPanel = new JPanel();
		this.southPanel.setBackground(Color.BLUE.darker());
		if(this.lines == null) {
			this.startButton = new JButton("Ready");
		} else {
			this.startButton = new JButton("Start");
		}
		this.statusText = new JTextArea(4, 64);
		this.statusScroll = new JScrollPane(this.statusText);
		
		this.currentRound = 0;
		this.computerMouseAdapters = new ComputerMouseAdapter[11][11];
		
		for(int r = 0; r < 11; r++) {
			for(int c = 0; c < 11; c++) {
				this.computerMouseAdapters[r][c] = new ComputerMouseAdapter(this, r, c);
			}
		}
		
		this.messageTextField = new JTextField(20);
		this.sendButton = new JButton("Send");
		
		this.water = new Water();
		this.water.start();
		
		GameWindow.this.engine.userSelectedFile = true;
		
		if(this.lines != null) {
			if(!GameWindow.this.engine.canParseLines(this.lines)) {
				JOptionPane.showMessageDialog(GameWindow.this, "You selected an invalid map.",  "Selected Map  --  Invalid", JOptionPane.ERROR_MESSAGE, null);
			}
		}
	}

	// createMenuBar()
	private void createMenuBar() {
		this.instructionsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.CTRL_MASK));
		this.informationMenu.add(this.instructionsMenuItem);
		this.aboutMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
		this.informationMenu.add(this.aboutMenuItem);
		this.menuBar.add(this.informationMenu);
	}
	
	// createNorthPanel()
	private void createNorthPanel() {
		this.userLabel.setHorizontalAlignment(JLabel.CENTER);
		this.northPanel.add(this.userLabel);
		this.timerLabel.setHorizontalAlignment(JLabel.CENTER);
		this.northPanel.add(this.timerLabel);
		this.northPanel.add(this.computerLabel);
	}

	// createCenterPanel()
	private void createCenterPanel() {		
		for(int r = 0; r < 11; r++) {
			for(int c = 0; c < 11; c++) {
				if(r != 10 && c != 0) {
					this.userWaterLabels[r][c].setBorder(BorderFactory.createLineBorder(Color.WHITE));
					this.computerWaterLabels[r][c].setBorder(BorderFactory.createLineBorder(Color.WHITE));
				}
				
				this.userPanel.add(this.userWaterLabels[r][c]);
				this.computerPanel.add(this.computerWaterLabels[r][c]);
			}
		}
		
		this.centerPanel.add(this.userPanel);
		this.centerPanel.add(new JLabel("   "));
		this.centerPanel.add(this.computerPanel);
		this.centerPanel.add(new JLabel("   "));
	}

	// createSouthPanel()
	private void createSouthPanel() {
		this.southPanel.setLayout(new BoxLayout(this.southPanel, BoxLayout.Y_AXIS));
		this.southPanel.add(Box.createGlue());
		this.startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		this.southPanel.add(this.startButton);
		this.southPanel.add(Box.createGlue());
		this.startButton.setEnabled(false);
		DefaultCaret caret = (DefaultCaret)this.statusText.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
	}
	
	// createActions()
	private void createActions() {
		this.instructionsMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				createMenuPopup("Instructions");
			}
		});
		
		this.aboutMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				createMenuPopup("About");
			}
		});
		
		for(int r = 0; r < 11; r++) {
			for(int c = 0; c < 11; c++) {
				if(r != 10 && c != 0) {
					this.userWaterLabels[r][c].addMouseListener(new UserMouseAdapter(this, r, c));
				}
			}
		}
		
		this.startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if(GameWindow.this.lines == null) {
					try {
						GameWindow.this.client.sendShips(GameWindow.this.engine.user.ships);
					} catch (IOException ioe) {
						System.out.println("IOException in startButton in GameWindow");
					}
				} else {
					GameWindow.this.startGame();
				}
				
				GameWindow.this.startButton.setEnabled(false);
			}
		});
		
		this.sendButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				String message = GameWindow.this.messageTextField.getText();
				GameWindow.this.messageTextField.setText("");
				GameWindow.this.statusText.append(GameWindow.this.user + ": " + message + "\n");
				
				try {
					GameWindow.this.client.sendMessage(message);
				} catch (IOException ioe) {
					System.out.println("IOException in sendButton in GameWindow");
				}
			}
		});
	}
	
	// receiveMessage() 
	void receiveMessage(String message) {
		this.statusText.append(this.friend + ": " + message + "\n");
	}
	
	// startGame()
	public void startGame() {
		for(int r = 0; r < 11; r++) {
			for(int c = 0; c < 11; c++) {
				if(r != 10 && c != 0) {
					GameWindow.this.computerWaterLabels[r][c].addMouseListener(GameWindow.this.computerMouseAdapters[r][c]);
				}
			}
		}

		GameWindow.this.startButton.setVisible(false);

		GameWindow.this.statusText.setVisible(true);
		GameWindow.this.statusScroll.setVisible(true);
		TitledBorder title = BorderFactory.createTitledBorder("GAME LOG");
		title.setTitleColor(Color.WHITE);
		GameWindow.this.southPanel.setBorder(title);
		GameWindow.this.statusText.setText("ROUND " + (++GameWindow.this.currentRound) + "\n");
		GameWindow.this.southPanel.add(GameWindow.this.statusScroll);
		
		if(this.lines == null) {
			JPanel messagePanel = new JPanel();
			messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.X_AXIS));
			messagePanel.add(new JLabel("  " + GameWindow.this.user + ": "));
			messagePanel.add(GameWindow.this.messageTextField);
			messagePanel.add(GameWindow.this.sendButton);

			GameWindow.this.southPanel.add(messagePanel);
		}

		GameWindow.this.timer = new Timer();
		GameWindow.this.timer.start();
	}
	
	// setShips()
	public void setShips(Ship[][] ships) {	
		int row1 = -1;
		int column1 = -1;
		int row2 = -1;
		int column2 = -1;
		
		for(int r = 0; r < 11; r++) {
			for(int c = 0; c < 11; c++) {
				if(r != 10 && c != 0) {
					if(ships[r][c] != null) {
						if(ships[r][c].letter.equals("A")) {
							this.engine.computer.ships[r][c] = this.engine.computer.A;
							this.engine.computer.ships[r][c].letter = "A";
							this.engine.computer.ships[r][c].size = 5;
						} else if(ships[r][c].letter.equals("B")) {
							this.engine.computer.ships[r][c] = this.engine.computer.B;
							this.engine.computer.ships[r][c].letter = "B";
							this.engine.computer.ships[r][c].size = 4;
						} else if(ships[r][c].letter.equals("C")) {
							this.engine.computer.ships[r][c] = this.engine.computer.C;
							this.engine.computer.ships[r][c].letter = "C";
							this.engine.computer.ships[r][c].size = 3;
						} else if(ships[r][c].letter.equals("D")) {
							if(row1 == -1 && column1 == -1) {
								row1 = ships[r][c].startRow;
								column1 = ships[r][c].startColumn;
							} else if(row2 == -1 && column2 == -1) {
								row2 = ships[r][c].startRow;
								column2 = ships[r][c].startColumn;
							}
							
							if(ships[r][c].startRow == row1 && ships[r][c].startColumn == column1) {
								this.engine.computer.ships[r][c] = this.engine.computer.D1;
							} else if(ships[r][c].startRow == row2 && ships[r][c].startColumn == column2) {
								this.engine.computer.ships[r][c] = this.engine.computer.D2;
							}
							
							this.engine.computer.ships[r][c].letter = "D";
							this.engine.computer.ships[r][c].size = 2;
						}
						
						this.engine.computer.ships[r][c].direction = ships[r][c].direction;
						this.engine.computer.ships[r][c].startRow = ships[r][c].startRow;
						this.engine.computer.ships[r][c].startColumn = ships[r][c].startColumn;
						this.engine.computer.ships[r][c].isPlaced = true;
					} else {
						this.engine.computer.ships[r][c] = null;
					}
				}
			}
		}		
	}
	
	// userAttack()
	void userAttack(int row, int column) throws IOException {
		if(this.lines == null) {
			this.client.sendCoordinates(row, column);
		}
		
		this.computer_game_sound = new GameSound("cannon.wav");
		this.computer_game_sound.start();
		
		int userOutcome = this.engine.userAttack(row, column);

		if(userOutcome == 0 || userOutcome == 3) {
			this.timer.userAttacked = true;
			
			String ship = "";
			boolean sunk = false;
			
			if(this.engine.computer.ships[row][column] == this.engine.computer.A) {
				this.computer_explosion = new Explosion("Computer", row, column, this.iA);
				this.computer_sink_splash = new SinkSplash("Computer", this.engine.computer.A.startRow, this.engine.computer.A.startColumn, 5, this.engine.computer.A.direction, this.iA);
				sunk = this.engine.computer.A.isSunk;
				ship = "an Aircraft Carrier";
			} else if(this.engine.computer.ships[row][column] == this.engine.computer.B) {
				this.computer_explosion = new Explosion("Computer", row, column, this.iB);
				this.computer_sink_splash = new SinkSplash("Computer", this.engine.computer.B.startRow, this.engine.computer.B.startColumn, 4, this.engine.computer.B.direction, this.iB);
				sunk = this.engine.computer.B.isSunk;
				ship = "a Battleship";
			} else if(this.engine.computer.ships[row][column] == this.engine.computer.C) {
				this.computer_explosion = new Explosion("Computer", row, column, this.iC);
				this.computer_sink_splash = new SinkSplash("Computer", this.engine.computer.C.startRow, this.engine.computer.C.startColumn, 3, this.engine.computer.C.direction, this.iC);
				sunk = this.engine.computer.C.isSunk;
				ship = "a Cruiser";
			} else if(this.engine.computer.ships[row][column] == this.engine.computer.D1) {
				this.computer_explosion = new Explosion("Computer", row, column, this.iD);
				this.computer_sink_splash = new SinkSplash("Computer", this.engine.computer.D1.startRow, this.engine.computer.D1.startColumn, 2, this.engine.computer.D1.direction, this.iD);
				sunk = this.engine.computer.D1.isSunk;
				ship = "a Destroyer";
			} else if(this.engine.computer.ships[row][column] == this.engine.computer.D2) {
				this.computer_explosion = new Explosion("Computer", row, column, this.iD);
				this.computer_sink_splash = new SinkSplash("Computer", this.engine.computer.D2.startRow, this.engine.computer.D2.startColumn, 2, this.engine.computer.D2.direction, this.iD);
				sunk = this.engine.computer.D2.isSunk;
				ship = "a Destroyer";
			} 
			
			this.computer_explosion.start();
			
			this.statusText.append(this.user + " attacked " + this.userWaterLabels[row][0].getText() + column + " and HIT " + ship + "! (" + this.timer.getCurrentTime() + ")\n");
			
			if(sunk) {
				this.computer_sink_splash.start();
				this.statusText.append(this.user + " sunk " + ship + "!\n");
			}
		} else if(userOutcome == 2) {
			this.timer.userAttacked = true;
			this.computer_miss_splash = new MissSplash("Computer", row, column);
			this.computer_miss_splash.start();
			this.statusText.append(this.user + " attacked " + this.userWaterLabels[row][0].getText() + column + " and MISSED! (" + this.timer.getCurrentTime() + ")\n");
		}
				
		if(this.timer.computerAttacked) {
			this.statusText.append("\nROUND " + (++this.currentRound) + "\n");
			this.timer.interrupt();
			this.timer = new Timer();
			this.timer.start();
			setComputerMouseAdapters(true);
		} else {
			setComputerMouseAdapters(false);
		}
	}
	
	// computerAttack()
	void computerAttack(int row, int column) {
		if(row == -1 && column == -1 && this.lines == null) {
			setComputerMouseAdapters(true);
			return;
		}
		
		this.user_game_sound = new GameSound("cannon.wav");
		this.user_game_sound.start();
		
		int computerRow = 0;
		int computerColumn = 0;
		int computerOutcome = 0;
		
		if(this.lines != null) {
			Random random = new Random();
			boolean valid = false;

			while(!valid) {
				computerRow = random.nextInt(10);
				computerColumn = random.nextInt(10) + 1;

				if(!this.engine.computer.guesses[computerRow][computerColumn]) {
					valid = true;
				}
			}

			computerOutcome = this.engine.computerAttack(computerRow, computerColumn);
		} else {
			computerRow = row;
			computerColumn = column;
			computerOutcome = this.engine.computerAttack(computerRow, computerColumn);
		}
		
		if(computerOutcome == 0 || computerOutcome == 3) {
			this.timer.computerAttacked = true;		

			String ship = "";
			boolean sunk = false;
			
			if(this.engine.user.ships[computerRow][computerColumn] == this.engine.user.A) {
				this.user_explosion = new Explosion("User", computerRow, computerColumn, this.iA);
				this.user_sink_splash = new SinkSplash("User", this.engine.user.A.startRow, this.engine.user.A.startColumn, 5, this.engine.user.A.direction, this.iA);
				sunk = this.engine.user.A.isSunk;
				ship = "an Aircraft Carrier";
			} else if(this.engine.user.ships[computerRow][computerColumn] == this.engine.user.B) {
				this.user_explosion = new Explosion("User", computerRow, computerColumn, this.iB);
				this.user_sink_splash = new SinkSplash("User", this.engine.user.B.startRow, this.engine.user.B.startColumn, 4, this.engine.user.B.direction, this.iB);
				sunk = this.engine.user.B.isSunk;
				ship = "a Battleship";
			} else if(this.engine.user.ships[computerRow][computerColumn] == this.engine.user.C) {
				this.user_explosion = new Explosion("User", computerRow, computerColumn, this.iC);
				this.user_sink_splash = new SinkSplash("User", this.engine.user.C.startRow, this.engine.user.C.startColumn, 3, this.engine.user.C.direction, this.iC);
				sunk = this.engine.user.C.isSunk;
				ship = "a Cruiser";
			} else if(this.engine.user.ships[computerRow][computerColumn] == this.engine.user.D1) {
				this.user_explosion = new Explosion("User", computerRow, computerColumn, this.iD);
				this.user_sink_splash = new SinkSplash("User", this.engine.user.D1.startRow, this.engine.user.D1.startColumn, 2, this.engine.user.D1.direction, this.iD);
				sunk = this.engine.user.D1.isSunk;
				ship = "a Destroyer";
			} else if(this.engine.user.ships[computerRow][computerColumn] == this.engine.user.D2) {
				this.user_explosion = new Explosion("User", computerRow, computerColumn, this.iD);
				this.user_sink_splash = new SinkSplash("User", this.engine.user.D2.startRow, this.engine.user.D2.startColumn, 2, this.engine.user.D2.direction, this.iD);
				sunk = this.engine.user.D2.isSunk;
				ship = "a Destroyer";
			} 
			
			this.user_explosion.start();
			
			this.statusText.append(this.friend + " attacked " + this.computerWaterLabels[computerRow][0].getText() + computerColumn + " and HIT " + ship + "! (" + this.timer.getCurrentTime() + ")\n");
			
			if(sunk) {
				this.user_sink_splash.start();
				this.statusText.append(this.friend + " sunk " + ship + "!\n");
			}
		} else if(computerOutcome == 2) {
			this.timer.computerAttacked = true;
			this.user_miss_splash = new MissSplash("User", computerRow, computerColumn);
			this.user_miss_splash.start();
			this.statusText.append(this.friend + " attacked " + this.computerWaterLabels[computerRow][0].getText() + computerColumn + " and MISSED! (" + this.timer.getCurrentTime() + ")\n");
		}
		
		if(this.timer.userAttacked) {
			this.statusText.append("\nROUND " + (++this.currentRound) + "\n");
			this.timer.interrupt();
			this.timer = new Timer();
			this.timer.start();
			setComputerMouseAdapters(true);
		}		
	}
	
	// setComputerMouseAdapters()
	private void setComputerMouseAdapters(boolean state) {
		for(int r = 0; r < 11; r++) {
			for(int c = 0; c < 11; c++) {
				this.computerMouseAdapters[r][c].enabled = state;
			}
		}
	}
	
	// createMenuPopup()
	private void createMenuPopup(String menuItem) {
		JDialog dialog = new JDialog();
		dialog.setTitle(menuItem);
		dialog.setSize(350, 300);
		dialog.setLocationRelativeTo(GameWindow.this);
		dialog.setModal(true);
		
		if(menuItem.equals("Instructions")) {
			JTextArea instructionsTextArea = new JTextArea();

			try	{
				FileReader fr = new FileReader("instructions.txt");
				instructionsTextArea.read(fr, null);
			} catch(IOException ioe) {
				instructionsTextArea.setText("Sorry! I cannot find the instructions file!");
			}
			
			instructionsTextArea.setLineWrap(true);
			instructionsTextArea.setEditable(false);
			JScrollPane instructionsScrollPane = new JScrollPane(instructionsTextArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			dialog.add(instructionsScrollPane);
		} else if(menuItem.equals("About")) {
			JPanel mainPanel = new JPanel();
			mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
			mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
			ImageIcon image = new ImageIcon("about.jpg");
			JLabel imageLabel = new JLabel(image);
			imageLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
			imageLabel.add(Box.createRigidArea(new Dimension(0, 5)));
			mainPanel.add(imageLabel);
			JLabel assignmentLabel = new JLabel("CSCI 201 - Assignment 3");
			assignmentLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
			mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
			mainPanel.add(assignmentLabel);
			JLabel nameLabel = new JLabel("Made by Christian Villa");
			nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
			mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
			mainPanel.add(nameLabel);
			JLabel dateLabel = new JLabel("Date Submitted: 3/1/15");
			dateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
			mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
			mainPanel.add(dateLabel);
			dialog.add(mainPanel);
		}
		
		dialog.setVisible(true);
	}
	
	// startNewRound()
	private void startNewRound() {
		this.statusText.append("\nROUND " + (++this.currentRound) + "\n");
		this.timer.interrupt();
		this.timer = new Timer();
		this.timer.start();
	}
	
	// Timer Thread
	class Timer extends Thread {
		int seconds;
		int userSeconds;
		boolean userAttacked;
		int computerSeconds;
		boolean computerAttacked;
		
		Timer() {
			this.seconds = 15;
			this.userAttacked = false;
			this.userSeconds = -1;
			this.computerAttacked = false;
			this.computerSeconds = -1;
		}

		public void run() {
			Random random = new Random();
			this.computerSeconds = random.nextInt(14) + 1;
			//this.computerSeconds = 14;
			
			for(this.seconds = 15; this.seconds > -1; this.seconds--) {
				if(this.seconds == 0 || (this.userAttacked && this.computerAttacked)) {
					GameWindow.this.startNewRound();
				} else if(this.seconds <= 9) {
					GameWindow.this.timerLabel.setText("Time - 0:0" + this.seconds);
				} else if(this.seconds >= 10){
					GameWindow.this.timerLabel.setText("Time - 0:" + this.seconds);
				}
				
				if(this.seconds == 3) {
					GameWindow.this.statusText.append("WARNING - 3 seconds remaining!\n");
				}
				
				if(this.userAttacked && this.userSeconds == -1) {
					this.userSeconds = this.seconds;
				} 
				
				if(this.computerSeconds == this.seconds) {
					GameWindow.this.computerAttack(-1, -1);
				}
				
				try {
					Thread.sleep(1000);
				} catch(InterruptedException ie) {
					return;
				}
			}
		}
		
		public String getCurrentTime() {
			String time = "";
			
			if(this.seconds <= 9) {
				time = "0:0" + this.seconds;
			} else {
				time = "0:" + this.seconds;
			}
			
			return time;
		}
	}
	
	// Water Thread
	class Water extends Thread {	
		public void run() {
			boolean water = true;
			
			while(true) {
				for(int r = 0; r < 11; r++) {
					for(int c = 0; c < 11; c++) {
						if(r != 10 && c != 0) {
							if(water == true) {
								GameWindow.this.userWaterLabels[r][c].setIcon(GameWindow.this.iWater1);
								GameWindow.this.computerWaterLabels[r][c].setIcon(GameWindow.this.iWater2);
							} else if(water == false) {
								GameWindow.this.userWaterLabels[r][c].setIcon(GameWindow.this.iWater2);
								GameWindow.this.computerWaterLabels[r][c].setIcon(GameWindow.this.iWater1);
							}
						}
					}
				}
				
				water = !water;
				
				try {
					Thread.sleep(500);
				} catch(InterruptedException ie) {
					return;
				}
			}
		}
	}
	
	// Explosion Thread
	class Explosion extends Thread {
		private String side;
		private int row;
		private int column;
		private ShrinkIcon image;
		
		Explosion(String side, int row, int column, ShrinkIcon image) {
			this.side = side;
			this.row = row;
			this.column = column;
			this.image = image;
		}
		
		public void run() {
			if(this.side.equals("User")) {
				while(GameWindow.this.user_game_sound.isAlive()) {

				}
				
				GameWindow.this.user_game_sound = new GameSound("explode.wav");
				GameWindow.this.user_game_sound.start();
			} else if(this.side.equals("Computer")) {			
				while(GameWindow.this.computer_game_sound.isAlive()) {

				}
				
				GameWindow.this.computer_game_sound = new GameSound("explode.wav");
				GameWindow.this.computer_game_sound.start();
			} 
			
			for(int i = 0; i < 5; i++) {
				if(this.side.equals("User")) {
					GameWindow.this.userLetterLabels[this.row][this.column].setIcon(GameWindow.this.iExplosions[i]);
				} else if(this.side.equals("Computer")) {
					GameWindow.this.computerLetterLabels[this.row][this.column].setIcon(GameWindow.this.iExplosions[i]);
				}
				
				try {
					Thread.sleep(330);
				} catch(InterruptedException ie) {
					return;
				}
			}
			
			if(this.side.equals("User")) {
				GameWindow.this.userLetterLabels[this.row][this.column].setIcon(GameWindow.this.iH);
			} else if(this.side.equals("Computer")) {
				GameWindow.this.computerLetterLabels[this.row][this.column].setIcon(this.image);
			}
			
			interrupt();
		}
	}
	
	// MissSplash Thread
	class MissSplash extends Thread {
		private String side;
		private int row;
		private int column;
		
		MissSplash(String side, int row, int column) {
			this.side = side;
			this.row = row;
			this.column = column;
		}
		
		public void run() {
			if(this.side.equals("User")) {
				while(GameWindow.this.user_game_sound.isAlive()) {

				}
				
				GameWindow.this.user_game_sound = new GameSound("splash.wav");
				GameWindow.this.user_game_sound.start();
			} else if(this.side.equals("Computer")) {			
				while(GameWindow.this.computer_game_sound.isAlive()) {

				}
				
				GameWindow.this.computer_game_sound = new GameSound("splash.wav");
				GameWindow.this.computer_game_sound.start();
			} 
			
			for(int i = 0; i < 7; i++) {
				if(this.side.equals("User")) {
					GameWindow.this.userLetterLabels[this.row][this.column].setIcon(GameWindow.this.iSplashes[i]);
				} else if(this.side.equals("Computer")) {
					GameWindow.this.computerLetterLabels[this.row][this.column].setIcon(GameWindow.this.iSplashes[i]);
				}
				
				try {
					Thread.sleep(100);
				} catch(InterruptedException ie) {
					return;
				}
			}
			
			if(this.side.equals("User")) {
				GameWindow.this.userLetterLabels[this.row][this.column].setIcon(GameWindow.this.iM);
			} else if(this.side.equals("Computer")) {
				GameWindow.this.computerLetterLabels[this.row][this.column].setIcon(GameWindow.this.iM);
			}
			
			interrupt();
		}
	}
	
	// SinkSplash Thread
	class SinkSplash extends Thread {
		private String side;
		private int row;
		private int column;
		private int size;
		private String direction;
		private ShrinkIcon image;
		
		SinkSplash(String side, int row, int column, int size, String direction, ShrinkIcon image) {
			this.side = side;
			this.row = row;
			this.column = column;
			this.size = size;
			this.direction = direction;
			this.image = image;
		}
		
		public void run() {
			if(this.side.equals("User")) {
				while(GameWindow.this.user_explosion.isAlive() && GameWindow.this.user_game_sound.isAlive()) {
					
				}
			} else if(this.side.equals("Computer")) {
				while(GameWindow.this.computer_explosion.isAlive() && GameWindow.this.computer_game_sound.isAlive()) {
					
				}
			}
			
			try {
				Thread.sleep(2000);
			} catch(InterruptedException ie) {
				
			}
			
			if(this.side.equals("User")) {
				GameWindow.this.user_game_sound = new GameSound("sinking.wav");
				GameWindow.this.user_game_sound.start();
			} else if(this.side.equals("Computer")) {
				GameWindow.this.computer_game_sound = new GameSound("sinking.wav");
				GameWindow.this.computer_game_sound.start();
			} 
			
			for(int i = 0; i < 7; i++) {
				if(this.direction.equals("NORTH")) {
					for(int k = this.row; k > this.row - this.size; k--) {
						if(this.side.equals("User")) {
							GameWindow.this.userLetterLabels[k][this.column].setIcon(GameWindow.this.iSplashes[i]);
						} else if(this.side.equals("Computer")) {
							GameWindow.this.computerLetterLabels[k][this.column].setIcon(GameWindow.this.iSplashes[i]);
						}
					}
				} else if(this.direction.equals("SOUTH")) {
					for(int k = this.row; k < this.row + this.size; k++) {
						if(this.side.equals("User")) {
							GameWindow.this.userLetterLabels[k][this.column].setIcon(GameWindow.this.iSplashes[i]);
						} else if(this.side.equals("Computer")) {
							GameWindow.this.computerLetterLabels[k][this.column].setIcon(GameWindow.this.iSplashes[i]);
						}
					}
				} else if(this.direction.equals("EAST")) {
					for(int k = this.column; k < this.column + this.size; k++) {
						if(this.side.equals("User")) {
							GameWindow.this.userLetterLabels[this.row][k].setIcon(GameWindow.this.iSplashes[i]);
						} else if(this.side.equals("Computer")) {
							GameWindow.this.computerLetterLabels[this.row][k].setIcon(GameWindow.this.iSplashes[i]);
						}
					}
				} else if(this.direction.equals("WEST")) {
					for(int k = this.column; k > this.column - this.size; k--) {
						if(this.side.equals("User")) {
							GameWindow.this.userLetterLabels[this.row][k].setIcon(GameWindow.this.iSplashes[i]);
						} else if(this.side.equals("Computer")) {
							GameWindow.this.computerLetterLabels[this.row][k].setIcon(GameWindow.this.iSplashes[i]);
						}
					}
				}
				
				try {
					Thread.sleep(225);
				} catch(InterruptedException ie) {
					return;
				}
			}
			
			if(this.direction.equals("NORTH")) {
				for(int k = this.row; k > this.row - this.size; k--) {
					if(this.side.equals("User")) {
						GameWindow.this.userLetterLabels[k][this.column].setIcon(GameWindow.this.iH);
					} else if(this.side.equals("Computer")) {
						GameWindow.this.computerLetterLabels[k][this.column].setIcon(this.image);
					}
				}
			} else if(this.direction.equals("SOUTH")) {
				for(int k = this.row; k < this.row + this.size; k++) {
					if(this.side.equals("User")) {
						GameWindow.this.userLetterLabels[k][this.column].setIcon(GameWindow.this.iH);
					} else if(this.side.equals("Computer")) {
						GameWindow.this.computerLetterLabels[k][this.column].setIcon(this.image);
					}
				}
			} else if(this.direction.equals("EAST")) {
				for(int k = this.column; k < this.column + this.size; k++) {
					if(this.side.equals("User")) {
						GameWindow.this.userLetterLabels[this.row][k].setIcon(GameWindow.this.iH);
					} else if(this.side.equals("Computer")) {
						GameWindow.this.computerLetterLabels[this.row][k].setIcon(this.image);
					}
				}
			} else if(this.direction.equals("WEST")) {
				for(int k = this.column; k > this.column - this.size; k--) {
					if(this.side.equals("User")) {
						GameWindow.this.userLetterLabels[this.row][k].setIcon(GameWindow.this.iH);
					} else if(this.side.equals("Computer")) {
						GameWindow.this.computerLetterLabels[this.row][k].setIcon(this.image);
					}
				}
			}
			
			if(GameWindow.this.engine.computer.count == 0 && GameWindow.this.lines != null) {
				JOptionPane.showMessageDialog(GameWindow.this.menuWindow, "You win!", "Game Over", JOptionPane.PLAIN_MESSAGE);
			}
			
			if(GameWindow.this.engine.user.count == 0 && GameWindow.this.lines != null) {
				JOptionPane.showMessageDialog(GameWindow.this.menuWindow, "You lose.", "Game Over", JOptionPane.PLAIN_MESSAGE);
			}
			
			if(GameWindow.this.engine.computer.count == 0 && GameWindow.this.lines == null) {
				int selection = JOptionPane.showConfirmDialog(GameWindow.this, "You win! Would you like to have a rematch?", "Game Over", JOptionPane.YES_NO_OPTION);
				
				if(selection == JOptionPane.YES_OPTION) {
					try {
						GameWindow.this.client.restartGame();
					} catch (IOException ioe) {
						System.out.println("IOException in SinkSplash in GameWindow");
					}
				} else if(selection == JOptionPane.NO_OPTION) {
					try {
						GameWindow.this.client.doNotRestartGame();
					} catch (IOException ioe) {
						System.out.println("IOException in SinkSplash in GameWindow");
					}
				}
			}
			
			if(GameWindow.this.engine.user.count == 0 && GameWindow.this.lines == null) {
				int selection = JOptionPane.showConfirmDialog(GameWindow.this, "You lose. Would you like to have a rematch?", "Game Over", JOptionPane.YES_NO_OPTION);
				
				if(selection == JOptionPane.YES_OPTION) {
					try {
						GameWindow.this.client.restartGame();
					} catch (IOException ioe) {
						System.out.println("IOException in SinkSplash in GameWindow");
					}
				} else if(selection == JOptionPane.NO_OPTION) {
					try {
						GameWindow.this.client.doNotRestartGame();
					} catch (IOException ioe) {
						System.out.println("IOException in SinkSplash in GameWindow");
					}
				}
			}
			
			interrupt();
		}
	}
	
	// GameSound Thread
	class GameSound extends Thread {
		String sound;

		GameSound(String sound) {
			this.sound = sound;
		}

		public void run() {
			SoundLibrary.playSound(this.sound);
			interrupt();
		}
	}
}

