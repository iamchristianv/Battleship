import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class FileParser {
	private GameEngine engine;
	private String[][] coordinateValues;
	
	// Constructor
	FileParser(GameEngine engine) {
		this.engine = engine;
		this.coordinateValues = new String[11][11];
	}
	
	// canParseLines()
	boolean canParseLines(String[] lines) {
		for(int i = 0; i < 10; i++) {
			if(!canParseCoordinates(lines[i], i)) {
				return false;
			}
		}
		
		if(!canParseShips()) {
			return false;
		}
		
		return true;
	}
		
	// canParseFile()
	boolean canParseFile(String filename) {
		String lineContent = "";
		int lineNumber = 1;
		int row = 0;
		
		try {
			FileReader fr = new FileReader(filename);
			BufferedReader br = new BufferedReader(fr);
			
			while(lineContent != null) {
				lineContent = br.readLine();
				
				if(lineNumber > 0 && lineNumber < 11) {
					if(!canParseCoordinates(lineContent, row)) {
						br.close();
						fr.close();
						return false;
					}
					
					row++;
					lineNumber++;
				}
			}
			
			if(!canParseShips()) {
				br.close();
				fr.close();
				return false;
			}
			
			br.close();
			fr.close();
		} catch(FileNotFoundException fnfe) {
			return false;
		} catch(IOException ioe) {
			return false;
		}
		
		return true;
	}
	
	// canParseCoordinates()
	private boolean canParseCoordinates(String lineContent, int row) {
		if(lineContent.length() != 10) {
			return false;
		}
				
		for(int i = 1; i < 11; i++) {
			if(lineContent.charAt(i - 1) == 'X') {
				this.coordinateValues[row][i] = "X";
			} else if(lineContent.charAt(i - 1) == 'A') {
				this.coordinateValues[row][i] = "A";
			} else if(lineContent.charAt(i - 1) == 'B') {
				this.coordinateValues[row][i] = "B";
			} else if(lineContent.charAt(i - 1) == 'C') {
				this.coordinateValues[row][i] = "C";
			} else if(lineContent.charAt(i - 1) == 'D') {
				this.coordinateValues[row][i] = "D";
			} else {
				return false;
			}
		}
		
		return true;
	}
	
	// canParseShips()
	private boolean canParseShips() {
		boolean[][] visited = new boolean[11][11];
		
		for(int r = 0; r < 11; r++) {
			for(int c = 0; c < 11; c++) {
				if(r != 10 && c != 0) {
					visited[r][c] = false;
				}
			}				
		}
		
		int shipSize = 0;
		String shipValue = "";
		String shipDirection = "";
		int[] shipCount = new int[5];
		
		for(int i = 0; i < 5; i++) {
			shipCount[i] = 0;
		}
		
		for(int r = 0; r < 11; r++) {
			for(int c = 0; c < 11; c++) {
				if(r != 10 && c != 0) {
					if(visited[r][c] == false && !this.coordinateValues[r][c].equals("X")) {
						shipSize = 0;
						shipValue = "";
						shipDirection = "";

						if(this.coordinateValues[r][c].equals("A") && shipCount[0] == 0) {
							shipSize = 5;
							shipValue = "A";
							shipCount[0]++;
						} else if(this.coordinateValues[r][c].equals("B") && shipCount[1] == 0) {
							shipSize = 4;
							shipValue = "B";
							shipCount[1]++;
						} else if(this.coordinateValues[r][c].equals("C") && shipCount[2] == 0) {
							shipSize = 3;
							shipValue = "C";
							shipCount[2]++;
						} else if(this.coordinateValues[r][c].equals("D") && (shipCount[3] == 0 || shipCount[4] == 0)) {
							shipSize = 2;
							shipValue = "D";

							if(shipCount[3] == 0) {
								shipCount[3]++;
							} else if(shipCount[4] == 0) {
								shipCount[4]++;
							}
						}

						if(c + 1 < 11 && this.coordinateValues[r][c + 1].equals(shipValue)) {
							shipDirection = "EAST";
						} else if(r + 1 < 10 && this.coordinateValues[r + 1][c].equals(shipValue)) {
							shipDirection = "SOUTH";
						} else {
							System.out.println("1");
							return false;
						}

						for(int i = 0; i < shipSize; i++) {
							if(shipDirection.equals("EAST")) {
								if(!this.coordinateValues[r][c + i].equals(this.coordinateValues[r][c])) {
									return false;
								}

								if(this.coordinateValues[r][c + i].equals("A")) {
									this.engine.computer.ships[r][c + i] = this.engine.computer.A;
									this.engine.computer.A.direction = "EAST";
									this.engine.computer.A.startRow = r;
									this.engine.computer.A.startColumn = c;
								} else if(this.coordinateValues[r][c + i].equals("B")) {
									this.engine.computer.ships[r][c + i] = this.engine.computer.B;
									this.engine.computer.B.direction = "EAST";
									this.engine.computer.B.startRow = r;
									this.engine.computer.B.startColumn = c;
								} else if (this.coordinateValues[r][c + i].equals("C")) {
									this.engine.computer.ships[r][c + i] = this.engine.computer.C;
									this.engine.computer.C.direction = "EAST";
									this.engine.computer.C.startRow = r;
									this.engine.computer.C.startColumn = c;
								} else if (this.coordinateValues[r][c + i].equals("D")) {
									if (shipCount[4] == 0) {
										this.engine.computer.ships[r][c + i] = this.engine.computer.D1;
										this.engine.computer.D1.direction = "EAST";
										this.engine.computer.D1.startRow = r;
										this.engine.computer.D1.startColumn = c;
									} else if (shipCount[4] == 1) {
										this.engine.computer.ships[r][c + i] = this.engine.computer.D2;
										this.engine.computer.D2.direction = "EAST";
										this.engine.computer.D2.startRow = r;
										this.engine.computer.D2.startColumn = c;
									}
								}

								visited[r][c + i] = true;
							} else if(shipDirection.equals("SOUTH")) {
								if(!this.coordinateValues[r + i][c].equals(this.coordinateValues[r][c])) {
									return false;
								}

								if(this.coordinateValues[r + i][c].equals("A")) {
									this.engine.computer.ships[r + i][c] = this.engine.computer.A;
									this.engine.computer.A.direction = "SOUTH";
									this.engine.computer.A.startRow = r;
									this.engine.computer.A.startColumn = c;
								} else if (this.coordinateValues[r + i][c].equals("B")) {
									this.engine.computer.ships[r + i][c] = this.engine.computer.B;
									this.engine.computer.B.direction = "SOUTH";
									this.engine.computer.B.startRow = r;
									this.engine.computer.B.startColumn = c;
								} else if (this.coordinateValues[r + i][c].equals("C")) {
									this.engine.computer.ships[r + i][c] = this.engine.computer.C;
									this.engine.computer.C.direction = "SOUTH";
									this.engine.computer.C.startRow = r;
									this.engine.computer.C.startColumn = c;
								} else if (this.coordinateValues[r + i][c].equals("D")) {
									if (shipCount[4] == 0) {
										this.engine.computer.ships[r + i][c] = this.engine.computer.D1;
										this.engine.computer.D1.direction = "SOUTH";
										this.engine.computer.D1.startRow = r;
										this.engine.computer.D1.startColumn = c;
									} else if (shipCount[4] == 1) {
										this.engine.computer.ships[r + i][c] = this.engine.computer.D2;
										this.engine.computer.D2.direction = "SOUTH";
										this.engine.computer.D2.startRow = r;
										this.engine.computer.D2.startColumn = c;
									}
								}

								visited[r + i][c] = true;
							}
						}
					}
				}
			}
		}
		
		if(shipCount[0] != 1 || shipCount[1] != 1 || shipCount[2] != 1 || shipCount[3] != 1 || shipCount[4] != 1) {
			return false;
		}
		
		return true;
	}
}