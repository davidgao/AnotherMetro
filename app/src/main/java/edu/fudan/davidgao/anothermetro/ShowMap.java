
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Color;

public class ShowMap {
	
	private Game game;
	private int screenWidth, screenHeight;
	private int gridWidth, gridHeight;
	private Paint landPaint, waterPaint;
	
	public ShowMap(Game mainclass, int screenWidth, int screenHeight) {
		this.game = mainclass;
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		int temp[] = game.getSize();
		gridWidth = temp[0];
		gridHeight = temp[1];
		
		landPaint = new Paint();
		landPaint.setColor(Color.BLACK);
		landPaint.setStyle(Style.FILL);
		waterPaint = new Paint();
		waterPaint.setColor(Color.CYAN);
		waterPaint.setStyle(Style.FILL);
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
	
	public Canvas showMap() {
		Canvas cacheCanvas = new Canvas();
		MapDatum map[][] = game.getMap();
		int i, j;
		for (i = 0; i < gridWidth; i ++) {
			for (j = 0; j < gridHeight; j ++) {
				double X0 = grid2screen(i, gridWidth, screenWidth);
				double X1 = grid2screen(i + 1, gridWidth, screenWidth);
				double Y0 = grid2screen(j, gridHeight, screenHeight);
				double Y1 = grid2screen(j + 1, gridHeight, screenHeight);
				switch (map[i][j]) {
					case LAND : 
						cacheCanvas.drawRect(X0, Y0, X1, Y1, landPaint);
						break;
					case WATER : 
						cacheCanvas.drawRect(X0, Y0, X1, Y1, waterPaint);
						break;
					default :
				}
			}
		}
		return cacheCanvas;
	}
	
	private double grid2screen(int gridCoord, int gridLength, int screenLength) {
		double len = screenLength / (double)gridLength;
		return len * gridCoord;
	}
	
}
