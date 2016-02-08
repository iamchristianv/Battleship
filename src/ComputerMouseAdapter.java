import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class ComputerMouseAdapter extends MouseAdapter {
	// Parent Window
	GameWindow gameWindow;
	
	// Game Data
	int row;
	int column;
	boolean enabled;
	
	// Constructor
	ComputerMouseAdapter(GameWindow gameWindow, int row, int column) {
		this.gameWindow = gameWindow;
		this.row = row;
		this.column = column;
		this.enabled = true;
	}
	
	// mouseClicked()
	public void mouseClicked(MouseEvent me) {
		// if the game is not over and the user has not already guessed this coordinate and the mouse adapter is enabled
		if(!this.gameWindow.engine.gameOver && !this.gameWindow.engine.user.guesses[this.row][this.column] && this.enabled) {
			try {
				this.gameWindow.userAttack(this.row, this.column);
			} catch (IOException ioe) {
				System.out.println("IOException in mouseClicked() in ComputerMouseAdapter");
			}
		} 
	}
}