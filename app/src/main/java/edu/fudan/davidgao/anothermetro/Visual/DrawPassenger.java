package edu.fudan.davidgao.anothermetro.Visual;

import android.graphics.PointF;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import edu.fudan.davidgao.anothermetro.Line;
import edu.fudan.davidgao.anothermetro.Site;
import edu.fudan.davidgao.anothermetro.core.Game;

public class DrawPassenger {
	
	private FloatBuffer vertexBuffer;
	private FloatBuffer vertexBuffer;
	private int mPositionHandle;
	private int mColorHandle;
	private static final int COORDS_PER_VERTEX = 3;
	private final int vertexStride = COORDS_PER_VERTEX * 4;
	
	private int vertexCount = 0;
	private float[] vertexCoords;
	private static final int GTMDCoordsCount = 200000;
	
	private final String vertexShaderCode = 
		"attribute vec4 vPosition;" + 
		"void main() {" + 
		"	gl_Position = vPosition;" + 
		"}";
	private final String fragmentShaderCode = 
		"precision mediump float;" + 
		"uniform vec4 vColor;" + 
		"void main() {" + 
		"	gl_FragColor = vColor;" + 
		"}";
	
	private static int loadShader(int type, String shaderCode) {
		int shader = CLES20.glCreateShader(type);
		CLES20.glShaderSource(shader, shaderCode);
		GLES20.glCompileShader(shader);
		return shader;
	}
	
	private Game gameMain;
	private final int mProgram;
	private static final float color = 1.0f; /* Config.passengerColor */
		
	public DrawPassenger {
		gameMain = Game.getInstance();
		vertexCoords = new float[GTMDCoordsCount];
		ByteBuffer bb = ByteBuffer.allocateDirect(vertexCoords.length * 4);
		bb.order(ByteOrder.nativeOrder());
		vertexBuffer = bb.asFloatBuffer();
		vertexBuffer.put(vertexCoords);
		vertexBuffer.position(0);
		int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
		int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, framentShaderCode);
		mProgram = GLES20.glCreateProgram();
		GLES20.glAttachShader(mProgram, vertexShader);
		GLES20.glAttachShader(mProgram, fragmentShader);
		GLES20.glLinkProgram(mProgram);
	}
	
	public void draw() {
		GTMDvertexCoords();
		GLES20.glUseProgram(mProgram);
		mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
		GLES20.glEnableVertexAttribArray(mPositionHandle);
		GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, 
			GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);
		mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
		GLES20.glUniform4fv(mColorHandle, 1, color, 0);
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
		GLES20.glDisableVertexAttribArray(mPositionHandle);
	}
	
	private static final double r = 0.05; /* Config.siteRadius */
	private static final double hr = r / 2.0; /* Config.siteHalfRadius */
	private static final float z = 0.0f; /* Config.siteZ */
	
	private static final double pr = 0.01; /* Config.passengerRadius */
	private static final double phr = r / 2.0; /* Config.passengerHalfRadius */
	private static final float pz = 0.0f; /* Config.passengerZ */
	
	public static double BG2FGx(int x){
		return (double)x / Config.GRID_X * 2.0 - 1.0;
	}
    
	public static double BG2FGy(int y){
		return (Config.GRID_Y - (double)y) / Config.GRID_Y * 2.0 - 1.0;
	}
	
	private addVertex(double x, double y) {
		vertexCoords[vertexCount ++] = (float) x;
		vertexCoords[vertexCount ++] = (float) y;
		vertexCoords[vertexCount ++] = pz;
	}
	
	private void addCircle(double x, double y) {
		addVertex(x + pr, y - phr); addVertex(x + pr, y + phr); addVertex(x, y);
		addVertex(x + pr, y + phr); addVertex(x + phr, y + pr); addVertex(x, y);
		addVertex(x + phr, y + pr); addVertex(x - phr, y + pr); addVertex(x, y);
		addVertex(x - phr, y + pr); addVertex(x - pr, y + phr); addVertex(x, y);
		addVertex(x - pr, y + phr); addVertex(x - pr, y - phr); addVertex(x, y);
		addVertex(x - pr, y - phr); addVertex(x - phr, y - pr); addVertex(x, y);
		addVertex(x - phr, y - pr); addVertex(x + phr, y - pr); addVertex(x, y);
		addVertex(x + phr, y - pr); addVertex(x + pr, y - phr); addVertex(x, y);
	}
	
	private void addTriangle(double x, double y) {
		double v3 = Math.sqrt(3.0);
		addVertex(x + pr * v3, y - phr); addVertex(x - pr * v3, y - phr); addVertex(x, y + pr);
	}
	
	private void addSquare(double x, double y) {
		addVertex(x - pr, y - pr); addVertex(x + pr, y - pr); addVertex(x + pr, y + pr);
		addVertex(x + pr, y + pr); addVertex(x - pr, y + pr); addVertex(x - pr, y - pr);
	}
	
	private void addU1(double x, double y) {
		addVertex(x + pr, y - phr); addVertex(x + pr, y + phr); addVertex(x, y);
		addVertex(x + pr, y + phr); addVertex(x + phr, y + phr); addVertex(x, y);
		addVertex(x + phr, y + phr); addVertex(x + phr, y + pr); addVertex(x, y);
		
		addVertex(x + phr, y + pr); addVertex(x - phr, y + pr); addVertex(x, y);
		addVertex(x - phr, y + pr); addVertex(x - phr, y + phr); addVertex(x, y);
		addVertex(x - phr, y + phr); addVertex(x - pr, y + phr); addVertex(x, y);
		
		addVertex(x - pr, y + phr); addVertex(x - pr, y - phr); addVertex(x, y);
		addVertex(x - pr, y - phr); addVertex(x - phr, y - phr); addVertex(x, y);
		addVertex(x - phr, y - phr); addVertex(x - phr, y - pr); addVertex(x, y);
		
		addVertex(x - phr, y - pr); addVertex(x + phr, y - pr); addVertex(x, y);
		addVertex(x + phr, y - pr); addVertex(x + phr, y - phr); addVertex(x, y);
		addVertex(x + phr, y - phr); addVertex(x + pr, y - phr); addVertex(x, y);
	}
	
	private void addU2(double x, double y) {
		addVertex(x + pr, y); addVertex(x, y + pr); addVertex(x - pr, y);
		addVertex(x - pr, y); addVertex(x, y - pr); addVertex(x + pr, y);
	}
	
	private static final row = 5; /* Config.passengerSiteboxRow */
	private static final col = 5; /* Config.passengerSiteboxColumn */
	private static final gap = pr * 1.0; /* Config.passgerSiteboxGap */
	
	/*
	
	+--                                                 -
	|   |   |                                           |
	+---+---+--                                         |
	|   |   |   |   |                                   |
	+---+---+---+---+---+                               |
	|   |   |   |   |   |  <- (r = 2, c = 0 ~ col - 1)  | passengerSitebox
	+---+---+---+---+---+                               |
	|   |   |   |   |   |  <- (r = 1, c = 0 ~ col - 1)  |
	+---+---+---+---+---+                               |
	|   |   |   |   |   |  <- (r = 0, c = 0 ~ col - 1)  |
	+---+---+---+---+2pr+                               -
	|       |                                           |
	| aSite |                                           | site
	|       |                                           |
	+--2*r--+                                           -
	
	(gap = 0)
	
	*/
	
	private void GTMDvertexCoords() {
		ArrayList<Site> sites = gameMain.getSites();
		for (int i = 0; i < sites.length; i ++) {
			Site site = sites.get(i);
			int ix = site.pos.x;
			int iy = site.pos.y;
			double x = Config.BG2FGx(ix);
			double y = Config.BG2FGy(iy);
			ArrayList<Passenger> passengers = site.getPassengers();
			int r = 0, c = 0;
			for (int j = 0; j < passengers.length; j ++) {
				Passenger passenger = passengers.get(j);
				double px = x - r + (pr + gap) * (2 * c + 1);
				double py = y + r + (pr + gap) * (2 * r + 1);
				switch (passenger.type) {
					case CIRCLE: addCircle(px, py); break;
					case TRIANGLE: addTriangle(px, py); break;
					case SQUARE: addSquare(px, py); break;
					case UNIQUE1: addU1(px, py); break;
					case UNIQUE2: addU2(px, py); break;
					//...
					default: 
				}
				c ++;
				if (c == col) {
					r ++;
					c = 0;
				}
			}
		}
		
		ArrayList<Line> lines = gameMain.getLines();
		for (int i = 0; i < lines.length; i ++) {
			Line line = lines.get(i);
			Train train = line.getTrain();
			ArrayList<Passenger> passengers = train.getPassengers();
			for (int j = 0; j < passengers.length; j ++) {
				
			}
		}
	}
}
