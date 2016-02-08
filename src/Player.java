public class Player {
	Ship A;
	Ship B;
	Ship C;
	Ship D1;
	Ship D2;

	int count;
	Ship[][] ships;
	boolean[][] guesses;
	
	// Player()
	Player() {
		this.A = new Ship("A", 5);
		this.B = new Ship("B", 4);
		this.C = new Ship("C", 3);
		this.D1 = new Ship("D", 2);
		this.D2 = new Ship("D", 2);
				
		this.count = 5;
		this.ships = new Ship[11][11];
		this.guesses = new boolean[11][11];
				
		for(int r = 0; r < 11; r++) {
			for(int c = 0; c < 11; c++) {
				if(r != 10 && c != 0) {
					this.ships[r][c] = null;
					this.guesses[r][c] = false;
				}
			}
		}
	}
}