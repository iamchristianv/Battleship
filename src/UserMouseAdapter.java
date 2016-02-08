import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class UserMouseAdapter extends MouseAdapter {
	// MAIN WINDOW
	GameWindow window;
			
	// NORTH PANEL
	private JPanel northPanel;
	private JLabel selectShipLabel;
	private String[] shipNames;
	private JComboBox<String> shipComboBox;
			
	// CENTER PANEL
	private JPanel centerPanel;
	private JRadioButton northButton;
	private JRadioButton southButton;
	private JRadioButton eastButton;
	private JRadioButton westButton;
	private ButtonGroup buttonGroup;
			
	// SOUTH PANEL
	private JButton placeShipButton;
	
	// MISCELLANEOUS
	private int row;
	private int column;
	private JDialog dialog;
	boolean enabled;
	
	// UserMouseAdapter()
	UserMouseAdapter(GameWindow window, int row, int column) {
		this.window = window;
		this.row = row;
		this.column = column;
		this.enabled = true;
	}

	// initializeComponents() 
	private void initializeComponents() {
		this.dialog = new JDialog();
		
		// NORTH PANEL
		this.northPanel = new JPanel();
		this.selectShipLabel = new JLabel("Select Ship:");
		this.shipNames = new String[5 - this.window.engine.userShipsPlaced];
		
		int index = 0;
		
		if(!this.window.engine.user.A.isPlaced) {
			this.shipNames[index] = "Aircraft Carrier";
			index++;
		}
		
		if(!this.window.engine.user.B.isPlaced) {
			this.shipNames[index] = "Battleship";
			index++;
		}
		
		if(!this.window.engine.user.C.isPlaced) {
			this.shipNames[index] = "Cruiser";
			index++;
		}
		
		if(!this.window.engine.user.D1.isPlaced) {
			this.shipNames[index] = "Destroyer 1";
			index++;
		}
		
		if(!this.window.engine.user.D2.isPlaced) {
			this.shipNames[index] = "Destroyer 2";
		}
		
		this.shipComboBox = new JComboBox<String>(shipNames);

		// CENTER PANEL
		this.centerPanel = new JPanel(new GridLayout(2, 2));
		this.northButton = new JRadioButton("Heading North");
		this.southButton = new JRadioButton("Heading South");
		this.eastButton = new JRadioButton("Heading East  ");
		this.westButton = new JRadioButton("Heading West");

		// SOUTH PANEL
		this.placeShipButton = new JButton("Place Ship");
	}
	
	// createDialog() 
	private void createDialog() {
		this.dialog.setTitle("Ship Placement  --" + this.window.userWaterLabels[this.row][0].getText() + this.column);
		this.dialog.setSize(300, 200);
		this.dialog.setLocationRelativeTo(this.window);
		this.dialog.setModal(true);
	}
	
	// createNorthPanel() 
	private void createNorthPanel() {
		this.northPanel.add(this.selectShipLabel);
		this.northPanel.add(this.shipComboBox);
	}

	// createCenterPanel() 
	private void createCenterPanel() {
		this.northButton.setHorizontalAlignment(AbstractButton.RIGHT);
		this.northButton.setActionCommand("NORTH");
		this.northButton.setSelected(true);
		this.southButton.setHorizontalAlignment(AbstractButton.LEFT);
		this.southButton.setActionCommand("SOUTH");
		this.eastButton.setHorizontalAlignment(AbstractButton.RIGHT);
		this.eastButton.setActionCommand("EAST");
		this.westButton.setHorizontalAlignment(AbstractButton.LEFT);
		this.westButton.setActionCommand("WEST");
		this.buttonGroup = new ButtonGroup();
		this.buttonGroup.add(this.northButton);
		this.buttonGroup.add(this.southButton);
		this.buttonGroup.add(this.eastButton);
		this.buttonGroup.add(this.westButton);
		this.centerPanel.add(this.northButton);
		this.centerPanel.add(this.southButton);
		this.centerPanel.add(this.eastButton);
		this.centerPanel.add(this.westButton);
	}
	
	// createActions()
	private void createActions() {
		this.placeShipButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				int size = 0;
				String letter = "";
				ImageIcon image = null;
				boolean error = false;

				if(UserMouseAdapter.this.shipComboBox.getSelectedItem().toString().equals("Aircraft Carrier")) {
					size = 5;
					letter = "A";
					image = UserMouseAdapter.this.window.iA;
				} else if(UserMouseAdapter.this.shipComboBox.getSelectedItem().toString().equals("Battleship")) {
					size = 4;
					letter = "B";
					image = UserMouseAdapter.this.window.iB;
				} else if(UserMouseAdapter.this.shipComboBox.getSelectedItem().toString().equals("Cruiser")) {
					size = 3;
					letter = "C";
					image = UserMouseAdapter.this.window.iC;
				} else if(UserMouseAdapter.this.shipComboBox.getSelectedItem().toString().equals("Destroyer 1")) {
					size = 2;
					letter = "D1";
					image = UserMouseAdapter.this.window.iD;
				} else if(UserMouseAdapter.this.shipComboBox.getSelectedItem().toString().equals("Destroyer 2")) {
					size = 2;
					letter = "D2";
					image = UserMouseAdapter.this.window.iD;
				}
				
				if(UserMouseAdapter.this.buttonGroup.getSelection().getActionCommand().equals("NORTH")) {
					if(UserMouseAdapter.this.window.engine.userCanPlaceShip(letter, size, "NORTH", UserMouseAdapter.this.row, UserMouseAdapter.this.column)) {
						UserMouseAdapter.this.window.engine.userShipsPlaced++;
						
						for(int i = UserMouseAdapter.this.row; i > UserMouseAdapter.this.row - size; i--) {
							UserMouseAdapter.this.window.userLetterLabels[i][UserMouseAdapter.this.column].setIcon(image);
							
							if(letter.equals("A")) {
								UserMouseAdapter.this.window.engine.user.ships[i][UserMouseAdapter.this.column] = UserMouseAdapter.this.window.engine.user.A;
								UserMouseAdapter.this.window.engine.user.A.isPlaced = true;
								UserMouseAdapter.this.window.engine.user.A.direction = "NORTH";
								UserMouseAdapter.this.window.engine.user.A.startRow = UserMouseAdapter.this.row;
								UserMouseAdapter.this.window.engine.user.A.startColumn = UserMouseAdapter.this.column;
							} else if(letter.equals("B")) {
								UserMouseAdapter.this.window.engine.user.ships[i][UserMouseAdapter.this.column] = UserMouseAdapter.this.window.engine.user.B;
								UserMouseAdapter.this.window.engine.user.B.isPlaced = true;
								UserMouseAdapter.this.window.engine.user.B.direction = "NORTH";
								UserMouseAdapter.this.window.engine.user.B.startRow = UserMouseAdapter.this.row;
								UserMouseAdapter.this.window.engine.user.B.startColumn = UserMouseAdapter.this.column;
							} else if(letter.equals("C")) {
								UserMouseAdapter.this.window.engine.user.ships[i][UserMouseAdapter.this.column] = UserMouseAdapter.this.window.engine.user.C;
								UserMouseAdapter.this.window.engine.user.C.isPlaced = true;
								UserMouseAdapter.this.window.engine.user.C.direction = "NORTH";
								UserMouseAdapter.this.window.engine.user.C.startRow = UserMouseAdapter.this.row;
								UserMouseAdapter.this.window.engine.user.C.startColumn = UserMouseAdapter.this.column;
							} else if(letter.equals("D1")) {
								UserMouseAdapter.this.window.engine.user.ships[i][UserMouseAdapter.this.column] = UserMouseAdapter.this.window.engine.user.D1;
								UserMouseAdapter.this.window.engine.user.D1.isPlaced = true;
								UserMouseAdapter.this.window.engine.user.D1.direction = "NORTH";
								UserMouseAdapter.this.window.engine.user.D1.startRow = UserMouseAdapter.this.row;
								UserMouseAdapter.this.window.engine.user.D1.startColumn = UserMouseAdapter.this.column;
							} else if(letter.equals("D2")) {
								UserMouseAdapter.this.window.engine.user.ships[i][UserMouseAdapter.this.column] = UserMouseAdapter.this.window.engine.user.D2;
								UserMouseAdapter.this.window.engine.user.D2.isPlaced = true;
								UserMouseAdapter.this.window.engine.user.D2.direction = "NORTH";
								UserMouseAdapter.this.window.engine.user.D2.startRow = UserMouseAdapter.this.row;
								UserMouseAdapter.this.window.engine.user.D2.startColumn = UserMouseAdapter.this.column;
							}
						}
						
						if(UserMouseAdapter.this.window.engine.canStartGame()) {
							UserMouseAdapter.this.window.startButton.setEnabled(true);
						}
					} else {
						showErrorMessage("Sorry! I was not able to place that ship in that direction!", "Ship Placement  --  Invalid");
						error = true;
					}
				} else if(UserMouseAdapter.this.buttonGroup.getSelection().getActionCommand().equals("SOUTH")) {
					if(UserMouseAdapter.this.window.engine.userCanPlaceShip(letter, size, "SOUTH", UserMouseAdapter.this.row, UserMouseAdapter.this.column)) {
						UserMouseAdapter.this.window.engine.userShipsPlaced++;
						
						for(int i = UserMouseAdapter.this.row; i < UserMouseAdapter.this.row + size; i++) {
							UserMouseAdapter.this.window.userLetterLabels[i][UserMouseAdapter.this.column].setIcon(image);
							
							if(letter.equals("A")) {
								UserMouseAdapter.this.window.engine.user.ships[i][UserMouseAdapter.this.column] = UserMouseAdapter.this.window.engine.user.A;
								UserMouseAdapter.this.window.engine.user.A.isPlaced = true;
								UserMouseAdapter.this.window.engine.user.A.direction = "SOUTH";
								UserMouseAdapter.this.window.engine.user.A.startRow = UserMouseAdapter.this.row;
								UserMouseAdapter.this.window.engine.user.A.startColumn = UserMouseAdapter.this.column;
							} else if(letter.equals("B")) {
								UserMouseAdapter.this.window.engine.user.ships[i][UserMouseAdapter.this.column] = UserMouseAdapter.this.window.engine.user.B;
								UserMouseAdapter.this.window.engine.user.B.isPlaced = true;
								UserMouseAdapter.this.window.engine.user.B.direction = "SOUTH";
								UserMouseAdapter.this.window.engine.user.B.startRow = UserMouseAdapter.this.row;
								UserMouseAdapter.this.window.engine.user.B.startColumn = UserMouseAdapter.this.column;
							} else if(letter.equals("C")) {
								UserMouseAdapter.this.window.engine.user.ships[i][UserMouseAdapter.this.column] = UserMouseAdapter.this.window.engine.user.C;
								UserMouseAdapter.this.window.engine.user.C.isPlaced = true;
								UserMouseAdapter.this.window.engine.user.C.direction = "SOUTH";
								UserMouseAdapter.this.window.engine.user.C.startRow = UserMouseAdapter.this.row;
								UserMouseAdapter.this.window.engine.user.C.startColumn = UserMouseAdapter.this.column;
							} else if(letter.equals("D1")) {
								UserMouseAdapter.this.window.engine.user.ships[i][UserMouseAdapter.this.column] = UserMouseAdapter.this.window.engine.user.D1;
								UserMouseAdapter.this.window.engine.user.D1.isPlaced = true;
								UserMouseAdapter.this.window.engine.user.D1.direction = "SOUTH";
								UserMouseAdapter.this.window.engine.user.D1.startRow = UserMouseAdapter.this.row;
								UserMouseAdapter.this.window.engine.user.D1.startColumn = UserMouseAdapter.this.column;
							} else if(letter.equals("D2")) {
								UserMouseAdapter.this.window.engine.user.ships[i][UserMouseAdapter.this.column] = UserMouseAdapter.this.window.engine.user.D2;
								UserMouseAdapter.this.window.engine.user.D2.isPlaced = true;
								UserMouseAdapter.this.window.engine.user.D2.direction = "SOUTH";
								UserMouseAdapter.this.window.engine.user.D2.startRow = UserMouseAdapter.this.row;
								UserMouseAdapter.this.window.engine.user.D2.startColumn = UserMouseAdapter.this.column;
							}
						}
						
						if(UserMouseAdapter.this.window.engine.canStartGame()) {
							UserMouseAdapter.this.window.startButton.setEnabled(true);
						}
					} else {
						showErrorMessage("Sorry! I was not able to place that ship in that direction!", "Ship Placement  --  Invalid");
						error = true;
					}
				} else if(UserMouseAdapter.this.buttonGroup.getSelection().getActionCommand().equals("EAST")) {
					if(UserMouseAdapter.this.window.engine.userCanPlaceShip(letter, size, "EAST", UserMouseAdapter.this.row, UserMouseAdapter.this.column)) {
						UserMouseAdapter.this.window.engine.userShipsPlaced++;
						
						for(int i = UserMouseAdapter.this.column; i < UserMouseAdapter.this.column + size; i++) {
							UserMouseAdapter.this.window.userLetterLabels[UserMouseAdapter.this.row][i].setIcon(image);
							
							if(letter.equals("A")) {
								UserMouseAdapter.this.window.engine.user.ships[UserMouseAdapter.this.row][i] = UserMouseAdapter.this.window.engine.user.A;
								UserMouseAdapter.this.window.engine.user.A.isPlaced = true;
								UserMouseAdapter.this.window.engine.user.A.direction = "EAST";
								UserMouseAdapter.this.window.engine.user.A.startRow = UserMouseAdapter.this.row;
								UserMouseAdapter.this.window.engine.user.A.startColumn = UserMouseAdapter.this.column;
							} else if(letter.equals("B")) {
								UserMouseAdapter.this.window.engine.user.ships[UserMouseAdapter.this.row][i] = UserMouseAdapter.this.window.engine.user.B;
								UserMouseAdapter.this.window.engine.user.B.isPlaced = true;
								UserMouseAdapter.this.window.engine.user.B.direction = "EAST";
								UserMouseAdapter.this.window.engine.user.B.startRow = UserMouseAdapter.this.row;
								UserMouseAdapter.this.window.engine.user.B.startColumn = UserMouseAdapter.this.column;
							} else if(letter.equals("C")) {
								UserMouseAdapter.this.window.engine.user.ships[UserMouseAdapter.this.row][i] = UserMouseAdapter.this.window.engine.user.C;
								UserMouseAdapter.this.window.engine.user.C.isPlaced = true;
								UserMouseAdapter.this.window.engine.user.C.direction = "EAST";
								UserMouseAdapter.this.window.engine.user.C.startRow = UserMouseAdapter.this.row;
								UserMouseAdapter.this.window.engine.user.C.startColumn = UserMouseAdapter.this.column;
							} else if(letter.equals("D1")) {
								UserMouseAdapter.this.window.engine.user.ships[UserMouseAdapter.this.row][i] = UserMouseAdapter.this.window.engine.user.D1;
								UserMouseAdapter.this.window.engine.user.D1.isPlaced = true;
								UserMouseAdapter.this.window.engine.user.D1.direction = "EAST";
								UserMouseAdapter.this.window.engine.user.D1.startRow = UserMouseAdapter.this.row;
								UserMouseAdapter.this.window.engine.user.D1.startColumn = UserMouseAdapter.this.column;
							} else if(letter.equals("D2")) {
								UserMouseAdapter.this.window.engine.user.ships[UserMouseAdapter.this.row][i] = UserMouseAdapter.this.window.engine.user.D2;
								UserMouseAdapter.this.window.engine.user.D2.isPlaced = true;
								UserMouseAdapter.this.window.engine.user.D2.direction = "EAST";
								UserMouseAdapter.this.window.engine.user.D2.startRow = UserMouseAdapter.this.row;
								UserMouseAdapter.this.window.engine.user.D2.startColumn = UserMouseAdapter.this.column;
							}
						}
						
						if(UserMouseAdapter.this.window.engine.canStartGame()) {
							UserMouseAdapter.this.window.startButton.setEnabled(true);
						}
					} else {
						showErrorMessage("Sorry! I was not able to place that ship in that direction!", "Ship Placement  --  Invalid");
						error = true;
					}
				} else if(UserMouseAdapter.this.buttonGroup.getSelection().getActionCommand().equals("WEST")) {
					if(UserMouseAdapter.this.window.engine.userCanPlaceShip(letter, size, "WEST", UserMouseAdapter.this.row, UserMouseAdapter.this.column)) {
						UserMouseAdapter.this.window.engine.userShipsPlaced++;
						
						for(int i = UserMouseAdapter.this.column; i > UserMouseAdapter.this.column - size; i--) {
							UserMouseAdapter.this.window.userLetterLabels[UserMouseAdapter.this.row][i].setIcon(image);
							
							if(letter.equals("A")) {
								UserMouseAdapter.this.window.engine.user.ships[UserMouseAdapter.this.row][i] = UserMouseAdapter.this.window.engine.user.A;
								UserMouseAdapter.this.window.engine.user.A.isPlaced = true;
								UserMouseAdapter.this.window.engine.user.A.direction = "WEST";
								UserMouseAdapter.this.window.engine.user.A.startRow = UserMouseAdapter.this.row;
								UserMouseAdapter.this.window.engine.user.A.startColumn = UserMouseAdapter.this.column;
							} else if(letter.equals("B")) {
								UserMouseAdapter.this.window.engine.user.ships[UserMouseAdapter.this.row][i] = UserMouseAdapter.this.window.engine.user.B;
								UserMouseAdapter.this.window.engine.user.B.isPlaced = true;
								UserMouseAdapter.this.window.engine.user.B.direction = "WEST";
								UserMouseAdapter.this.window.engine.user.B.startRow = UserMouseAdapter.this.row;
								UserMouseAdapter.this.window.engine.user.B.startColumn = UserMouseAdapter.this.column;
							} else if(letter.equals("C")) {
								UserMouseAdapter.this.window.engine.user.ships[UserMouseAdapter.this.row][i] = UserMouseAdapter.this.window.engine.user.C;
								UserMouseAdapter.this.window.engine.user.C.isPlaced = true;
								UserMouseAdapter.this.window.engine.user.C.direction = "WEST";
								UserMouseAdapter.this.window.engine.user.C.startRow = UserMouseAdapter.this.row;
								UserMouseAdapter.this.window.engine.user.C.startColumn = UserMouseAdapter.this.column;
							} else if(letter.equals("D1")) {
								UserMouseAdapter.this.window.engine.user.ships[UserMouseAdapter.this.row][i] = UserMouseAdapter.this.window.engine.user.D1;
								UserMouseAdapter.this.window.engine.user.D1.isPlaced = true;
								UserMouseAdapter.this.window.engine.user.D1.direction = "WEST";
								UserMouseAdapter.this.window.engine.user.D1.startRow = UserMouseAdapter.this.row;
								UserMouseAdapter.this.window.engine.user.D1.startColumn = UserMouseAdapter.this.column;
							} else if(letter.equals("D2")) {
								UserMouseAdapter.this.window.engine.user.ships[UserMouseAdapter.this.row][i] = UserMouseAdapter.this.window.engine.user.D2;
								UserMouseAdapter.this.window.engine.user.D2.isPlaced = true;
								UserMouseAdapter.this.window.engine.user.D2.direction = "WEST";
								UserMouseAdapter.this.window.engine.user.D2.startRow = UserMouseAdapter.this.row;
								UserMouseAdapter.this.window.engine.user.D2.startColumn = UserMouseAdapter.this.column;
							}
						}
						
						if(UserMouseAdapter.this.window.engine.canStartGame()) {
							UserMouseAdapter.this.window.startButton.setEnabled(true);
						}
					} else {
						showErrorMessage("Sorry! I was not able to place that ship in that direction!", "Ship Placement  --  Invalid");
						error = true;
					}
				}
				
				if(!error) {
					UserMouseAdapter.this.dialog.dispose();
				}
			}
		});
	}
	
	// showErrorMessage() 
	private void showErrorMessage(String errorMessage, String errorTitle) {
		JOptionPane.showMessageDialog(UserMouseAdapter.this.dialog, errorMessage, errorTitle, JOptionPane.ERROR_MESSAGE, null);
	}
	
	// mouseClicked()
	public void mouseClicked(MouseEvent me) {
		if(this.enabled == true) {
			if(this.window.engine.user.ships[this.row][this.column] != null) {
				int r = this.window.engine.user.ships[this.row][this.column].startRow;
				int c = this.window.engine.user.ships[this.row][this.column].startColumn;
				String direction = this.window.engine.user.ships[this.row][this.column].direction;
				int size = this.window.engine.user.ships[this.row][this.column].size;

				this.window.engine.user.ships[this.row][this.column].isPlaced = false;
				this.window.engine.user.ships[this.row][this.column].direction = "";

				if(direction.equals("NORTH")) {
					for(int i = r; i > r - size; i--) {
						this.window.userLetterLabels[i][c].setIcon(this.window.iQ);
						this.window.engine.user.ships[i][c] = null;
					}
				} else if(direction.equals("SOUTH")) {
					for(int i = r; i < r + size; i++) {
						this.window.userLetterLabels[i][c].setIcon(this.window.iQ);
						this.window.engine.user.ships[i][c] = null;
					}
				} else if(direction.equals("EAST")) {
					for(int i = c; i < c + size; i++) {
						this.window.userLetterLabels[r][i].setIcon(this.window.iQ);
						this.window.engine.user.ships[r][i] = null;
					}
				} else if(direction.equals("WEST")) {
					for(int i = c; i > c - size; i--) {
						this.window.userLetterLabels[r][i].setIcon(this.window.iQ);
						this.window.engine.user.ships[r][i] = null;
					}
				}

				this.window.engine.userShipsPlaced--;
			} else if(this.window.engine.userShipsPlaced != 5) {
				initializeComponents();
				createDialog();
				createNorthPanel();
				createCenterPanel();
				createActions();

				this.dialog.add(this.northPanel, BorderLayout.NORTH);
				this.dialog.add(this.centerPanel, BorderLayout.CENTER);
				this.dialog.add(this.placeShipButton, BorderLayout.SOUTH);
				this.dialog.setVisible(true);
			}
		}
	}
}