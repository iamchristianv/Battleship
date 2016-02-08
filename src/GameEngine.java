public class GameEngine {
	// Players
	Player user;
	Player computer;
	
	// Game Data
	boolean gameOver;
	int userShipsPlaced;
	boolean userSelectedFile;
	private FileParser fileParser;
	
	// Constructor
	GameEngine() {
		this.user = new Player();
		this.computer = new Player();
		
		this.gameOver = false;
		this.userShipsPlaced = 0;
		this.userSelectedFile = false;
		this.fileParser = new FileParser(this); 
	}
	
	// canParseLines()
	boolean canParseLines(String[] lines) {
		if(!this.fileParser.canParseLines(lines)) {
			return false;
		}
		
		return true;
	}
	
	// canParseFile()
	boolean canParseFile(String filename) {
		if(!this.fileParser.canParseFile(filename)) {
			return false;
		}
		
		return true;
	}
	
	// userCanPlaceShip() 
	boolean userCanPlaceShip(String letter, int size, String direction, int row, int column) {		
		if(direction.equals("NORTH")) {
			if((row + 1) - size < 0) {
				return false;
			}
			
			for(int r = row; r > row - size; r--) {
				if(this.user.ships[r][column] != null) {
					return false;
				}
			}
		} else if(direction.equals("SOUTH")) {
			if(row + size > 10) {
				return false;
			}
			
			for(int r = row; r < row + size; r++) {
				if(this.user.ships[r][column] != null) {
					return false;
				}
			}
		} else if(direction.equals("EAST")) {
			if(column + size > 11) {
				return false;
			}
			
			for(int c = column; c < column + size; c++) {
				if(this.user.ships[row][c] != null) {
					return false;
				}
			}
		} else if(direction.equals("WEST")) {
			if(column - size < 0) {
				return false;
			}
			
			for(int c = column; c > column - size; c--) {
				if(this.user.ships[row][c] != null) {
					return false;
				}
			}
		}		
		
		return true;
	}
	
	// canStartGame()
	boolean canStartGame() {
		if(this.userShipsPlaced == 5 && this.userSelectedFile) {
			return true;
		}
		
		return false;
	}
	
	// userAttack() 
	int userAttack(int row, int column) {		
		if(this.user.guesses[row][column] == true) {
			return 1;
		} else if(this.computer.ships[row][column] == null) {
			this.user.guesses[row][column] = true;
			return 2;
		} else if(this.computer.ships[row][column] != null) {
			this.user.guesses[row][column] = true;			
			this.computer.ships[row][column].hit();
			
			if(this.computer.ships[row][column].isSunk) {
				this.computer.count -= 1;
				
				if(this.computer.count == 0) {
					this.gameOver = true;
					return 3;
				}
			}
		}
		
		return 0;
	}
	
	// computerAttack() 
	int computerAttack(int row, int column) {
		if(this.computer.guesses[row][column] == true) {
			return 1;
		} else if(this.user.ships[row][column] == null) {
			this.computer.guesses[row][column] = true;
			return 2;
		} else if(this.user.ships[row][column] != null) {
			this.computer.guesses[row][column] = true;
			this.user.ships[row][column].hit();

			if(this.user.ships[row][column].isSunk) {
				this.user.count -= 1;
				
				if(this.user.count == 0) {
					this.gameOver = true;
					return 3;
				}
			}
		}

		return 0;
	}
}