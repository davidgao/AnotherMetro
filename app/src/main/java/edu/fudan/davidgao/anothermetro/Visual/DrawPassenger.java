package edu.fudan.davidgao.anothermetro.Visual;

import android.graphics.PointF;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import edu.fudan.davidgao.anothermetro.core.GameEvent;
import edu.fudan.davidgao.anothermetro.core.Line;
import edu.fudan.davidgao.anothermetro.core.Passenger;
import edu.fudan.davidgao.anothermetro.core.RunningTrainState;
import edu.fudan.davidgao.anothermetro.core.Site;
import edu.fudan.davidgao.anothermetro.core.Game;
import edu.fudan.davidgao.anothermetro.core.StandbyTrainState;
import edu.fudan.davidgao.anothermetro.core.Train;
import edu.fudan.davidgao.anothermetro.core.TrainState;
import edu.fudan.davidgao.anothermetro.tools.Broadcaster;

public class DrawPassenger {

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
		int shader = GLES20.glCreateShader(type);
		GLES20.glShaderSource(shader, shaderCode);
		GLES20.glCompileShader(shader);
		return shader;
	}
	
	private Game gameMain;
	private final int mProgram;
	private static float color[] = { 0.2f, 0.2f, 0.2f, 1.0f };; /* Config.siteColor */
	private static DrawPassenger instance = null;
	public static DrawPassenger getInstance(){
		return instance;
	}
	public DrawPassenger() {
		instance = this;
		gameMain = Game.getInstance();
		vertexCoords = new float[GTMDCoordsCount];
		ByteBuffer bb = ByteBuffer.allocateDirect(vertexCoords.length * 4);
		bb.order(ByteOrder.nativeOrder());
		vertexBuffer = bb.asFloatBuffer();
		vertexBuffer.put(vertexCoords);
		vertexBuffer.position(0);
		int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
		int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
		mProgram = GLES20.glCreateProgram();
		GLES20.glAttachShader(mProgram, vertexShader);
		GLES20.glAttachShader(mProgram, fragmentShader);
		GLES20.glLinkProgram(mProgram);

		Broadcaster b = gameMain.getCallbackBroadcaster(GameEvent.PASSENGER_CHANGE);
		Runnable drawPassenger = new Runnable() {
			@Override
			public synchronized void run(){
				DrawPassenger.getInstance().GTMDvertexCoords();
			}
		};
		b.addListener(drawPassenger);
	}
	
	public void draw() {
		//GTMDvertexCoords();
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
	private static final double phr = pr / 2.0; /* Config.passengerHalfRadius */
	private static final float pz = 0.0f; /* Config.passengerZ */
	
	public static double BG2FGx(int x){
		return (double)x / Config.GRID_X * 2.0 - 1.0;
	}
    
	public static double BG2FGy(int y){
		return (Config.GRID_Y - (double)y) / Config.GRID_Y * 2.0 - 1.0;
	}
	
	private void addVertex(double x, double y) {
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
	
	private static final int row = 5; /* Config.passengerSiteboxRow */
	private static final int col = 5; /* Config.passengerSiteboxColumn */
	private static final double gap = pr * 1.0; /* Config.passgerSiteboxGap */
	
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
	|   #   |                                           | site
	|       |                                           |
	+--2*r--+                                           -
	
	(gap = 0)
	
	*/
	
	/*
	
	+---+---+---+                         -
	|   |   |   |  <- (r = 1, c = 0 ~ 2)  |
	+---+-#-+---+                         | passengerTrainbox
	|   |   |   |  <- (r = 0, c = 0 ~ 2)  |
	+---+---+---+                         -
	
	*/
		
	private void GTMDvertexCoords() {
		ArrayList<Site> sites = gameMain.getSites();
		vertexCount = 0;
		System.out.println("Draw passenger");
		for (int i = 0; i < sites.size(); i ++) {
			Site site = sites.get(i);
			int ix = site.pos.x;
			int iy = site.pos.y;
			double x = Config.BG2FGx(ix);
			double y = Config.BG2FGy(iy);
			ArrayList<Passenger> passengers = site.getPassengers();
			int _r = 0, _c = 0;
			for (int j = 0; j < passengers.size(); j ++) {
				System.out.printf("%d %d %d %d\n", sites.size(), i, passengers.size(), j);
				Passenger passenger = passengers.get(j);
				double px = x - r + (pr + gap) * (2 * _c + 1);
				double py = y + r + (pr + gap) * (2 * _r + 1);
				switch (passenger.type) {
					case CIRCLE: addCircle(px, py); break;
					case TRIANGLE: addTriangle(px, py); break;
					case SQUARE: addSquare(px, py); break;
					case UNIQUE1: addU1(px, py); break;
					case UNIQUE2: addU2(px, py); break;
					//...
					default: 
				}
				_c ++;
				if (_c == col) {
					_r ++;
					_c = 0;
				}
			}
		}
		
		ArrayList<Line> lines = gameMain.getLines();
		for (int i = 0; i < lines.size(); i ++) {
			Line line = lines.get(i);
			Train train = line.train;
			TrainState trainState = train.getState();
			double tx = 0, ty = 0;
			int angle;
			if (trainState instanceof StandbyTrainState) {
				StandbyTrainState standbyTrainState = (StandbyTrainState)trainState;
				int ix = standbyTrainState.site.pos.x;
				int iy = standbyTrainState.site.pos.y;
				tx = Config.BG2FGx(ix);
				ty = Config.BG2FGy(iy);
				angle = 0;
			}
			if (trainState instanceof RunningTrainState) {
				RunningTrainState runningTrainState = (RunningTrainState)trainState;
				VsSegment vsSegment;
				//if (trainState.direction==1) {
					vsSegment = DrawLine.getInstance().findSegment(runningTrainState.s1, runningTrainState.s2, trainState.line);
				//}else{
				//	vsSegment = DrawLine.getInstance().findSegment(runningTrainState.s2, runningTrainState.s1, trainState.line);
				//}
				long depart = runningTrainState.departure;
				long arrival = runningTrainState.arrival;
				long currentTick = gameMain.getTickCounter();
				float fraction = (float)(currentTick - depart) / (arrival - depart);
				VsTrainState vsTrainState = vsSegment.getTrainState(fraction, trainState.direction);
				tx = vsTrainState.coordinate.x;
				ty = vsTrainState.coordinate.y;
				angle = vsTrainState.angle;
			}
			ArrayList<Passenger> passengers = train.getPassengers();
			int _r = 0, _c = 0;
			for (int j = 0; j < passengers.size(); j ++) {
				Passenger passenger = passengers.get(j);
				double px = tx - 3 * (pr + gap) + (pr + gap) * (2 * _c + 1);
				double py = ty - 2 * (pr + gap) + (pr + gap) * (2 * _r + 1);
				switch (passenger.type) {
					case CIRCLE: addCircle(px, py); break;
					case TRIANGLE: addTriangle(px, py); break;
					case SQUARE: addSquare(px, py); break;
					case UNIQUE1: addU1(px, py); break;
					case UNIQUE2: addU2(px, py); break;
					//...
					default: 
				}
				_c ++;
				if (_c == 3) {
					_r ++;
					_c = 0;
				}
			}
		}
		vertexBuffer.put(vertexCoords);
		vertexBuffer.position(0);
	}
}
