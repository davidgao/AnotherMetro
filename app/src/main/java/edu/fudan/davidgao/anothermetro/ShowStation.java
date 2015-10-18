
public class ShowStation {
	
	private pointDrawer skqDrawer;
	private Game game;
	
	public ShowStation(pointDrawer SKQ, Game mainClass) {
		this.skqDrawer = SKQ;
		this.game = mainClass;
	}
	
	/*
	                         gridWidth = 5
                       0     1     2     3     4
                    +-----+-----+-----+-----+-----+  -
                    |     |     |     |     |     |  |
                  0 |  X  |  X  |  X  |  X  |  X  |  |
                    |     |     |     |     |     |  |
                    +-----+-----+-----+-----+-----+  |
                    |     |     |     |     |     |  |
  gridHeight = 3  1 |  X  |  X  |  X  |  X  |  X  | screenHeight
                    |     |     |     |     |     |  |
                    +-----+-----+-----+-----+-----+  |
                    |     |     |     |     |     |  |
                  2 |  X  |  X  |  X  |  X  |  X  |  |
                    |     |     |     |     |     |  |
                    +-----+-----+-----+-----+-----+  -

                    | - - - - screenWidth - - - - |
	*/
	
	public void showStation(Station station) {
		int gX = station.getX();
		int gY = station.getY();
		double X = grid2screen(gX, game.gridWidth, game.screenWidth);
		double Y = grid2screen(gY, game.gridHeight, game.screenHeight);
		double R = game.stationSize;
		switch (station.getType()) {
			case 0: skqDrawer.drawCircle(X, Y, R); break;
			case 1: skqDrawer.drawTriangle(X, Y, R); break;
			case 2: skqDrawer.drawSquare(X, Y, R); break;
			default: 
		}
	}
	
	private double grid2screen(int gridCoord, int gridLength, int screenLength) {
		double len = screenLength / (double)gridLength;
		return len * gridCoord + len / 2.0;
	}
	
}
