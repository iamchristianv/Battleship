import java.io.Serializable;

public class Ship implements Serializable {
	private static final long serialVersionUID = 1L;
	
	String letter;
	int size; 
	String direction;
	boolean isPlaced;
	boolean isSunk;
	int startRow;
	int startColumn;
	
	// Ship()
	Ship(String letter, int size) {
		this.letter = letter;
		this.size = size;
		this.direction = "";
		this.isPlaced = false;
		this.isSunk = false;
		this.startRow = 0;
		this.startColumn = 0;
	}
	
	// hit()
	void hit() {
		this.size -= 1;
		
		if(this.size == 0) {
			this.isSunk = true;
		}
	}
}