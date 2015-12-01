package edu.fudan.davidgao.anothermetro.Visual;

import android.graphics.PointF;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import edu.fudan.davidgao.anothermetro.core.GameEvent;
import edu.fudan.davidgao.anothermetro.core.Line;
import edu.fudan.davidgao.anothermetro.core.Site;
import edu.fudan.davidgao.anothermetro.core.Game;
import edu.fudan.davidgao.anothermetro.tools.Broadcaster;

public class DrawSite {
	
	private FloatBuffer vertexBuffer;
	private int mPositionHandle;
	private int mColorHandle;
	private static final int COORDS_PER_VERTEX = 3;
	private final int vertexStride = COORDS_PER_VERTEX * 4;
	
	private int vertexCount = 0;
	private float[] vertexCoords;
	private static final int GTMDCoordsCount = 100000;

	static float triangleCoords[] = {   // in counterclockwise order:
			0.0f,  0.622008459f, 0.0f, // top
			-0.5f, -0.311004243f, 0.0f, // bottom left // bottom right
			0.5f, 0.2f, 0.0f,
			0.0f, 0.1f, 0.0f
	};
	
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
	private static float color[] = { 0.5f, 0.5f, 0.5f, 1.0f };; /* Config.siteColor */
	private static DrawSite instance = null;
	public static DrawSite getInstance(){
		return instance;
	}

	public DrawSite() {
		instance=this;
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

		Broadcaster b = gameMain.getCallbackBroadcaster(GameEvent.SITE_SPAWN);
		final Runnable drawSite = new Runnable() {
			@Override
			public synchronized void run(){
				DrawSite.getInstance().GTMDvertexCoords();
			}
		};
		b.addListener(drawSite);
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
	
	public static double BG2FGx(int x){
        return (double)x / Config.GRID_X * 2.0 - 1.0;
    }
    
	public static double BG2FGy(int y){
        return (Config.GRID_Y - (double)y) / Config.GRID_Y * 2.0 - 1.0;
    }
	
	private void addVertex(double x, double y) {
		vertexCoords[vertexCount ++] = (float) x;
		vertexCoords[vertexCount ++] = (float) y;
		vertexCoords[vertexCount ++] = z;
	}
	
	private void addCircleSite(double x, double y) {
		addVertex(x + r, y - hr); addVertex(x + r, y + hr); addVertex(x, y);
		addVertex(x + r, y + hr); addVertex(x + hr, y + r); addVertex(x, y);
		addVertex(x + hr, y + r); addVertex(x - hr, y + r); addVertex(x, y);
		addVertex(x - hr, y + r); addVertex(x - r, y + hr); addVertex(x, y);
		addVertex(x - r, y + hr); addVertex(x - r, y - hr); addVertex(x, y);
		addVertex(x - r, y - hr); addVertex(x - hr, y - r); addVertex(x, y);
		addVertex(x - hr, y - r); addVertex(x + hr, y - r); addVertex(x, y);
		addVertex(x + hr, y - r); addVertex(x + r, y - hr); addVertex(x, y);
	}
	
	private void addTriangleSite(double x, double y) {
		double v3 = Math.sqrt(3.0);
		addVertex(x + r * v3/2.0, y - hr); addVertex(x - r * v3/2.0, y - hr); addVertex(x, y + r);
	}
	
	private void addSquareSite(double x, double y) {
		addVertex(x - r, y - r); addVertex(x + r, y - r); addVertex(x + r, y + r);
		addVertex(x + r, y + r); addVertex(x - r, y + r); addVertex(x - r, y - r);
	}
	
	private void addU1Site(double x, double y) {
		addVertex(x + r, y - hr); addVertex(x + r, y + hr); addVertex(x, y);
		addVertex(x + r, y + hr); addVertex(x + hr, y + hr); addVertex(x, y);
		addVertex(x + hr, y + hr); addVertex(x + hr, y + r); addVertex(x, y);
		
		addVertex(x + hr, y + r); addVertex(x - hr, y + r); addVertex(x, y);
		addVertex(x - hr, y + r); addVertex(x - hr, y + hr); addVertex(x, y);
		addVertex(x - hr, y + hr); addVertex(x - r, y + hr); addVertex(x, y);
		
		addVertex(x - r, y + hr); addVertex(x - r, y - hr); addVertex(x, y);
		addVertex(x - r, y - hr); addVertex(x - hr, y - hr); addVertex(x, y);
		addVertex(x - hr, y - hr); addVertex(x - hr, y - r); addVertex(x, y);
		
		addVertex(x - hr, y - r); addVertex(x + hr, y - r); addVertex(x, y);
		addVertex(x + hr, y - r); addVertex(x + hr, y - hr); addVertex(x, y);
		addVertex(x + hr, y - hr); addVertex(x + r, y - hr); addVertex(x, y);
	}
	
	private void addU2Site(double x, double y) {
		addVertex(x + r, y);
		addVertex(x, y + r); addVertex(x - r, y);
		addVertex(x - r, y); addVertex(x, y - r); addVertex(x + r, y);
	}

	private void GTMDvertexCoords() {
		vertexCount = 0;
		ArrayList<Site> sites = gameMain.getSites();
		for (int i = 0; i < sites.size(); i ++) {
			System.out.printf("size=%d i=%d\n", sites.size(), i);
			Site site = sites.get(i);
			int ix = site.pos.x;
			int iy = site.pos.y;
			double x = Config.BG2FGx(ix);
			double y = Config.BG2FGy(iy);
			switch (site.type) {
				case CIRCLE: addCircleSite(x, y); break;
				case TRIANGLE: addTriangleSite(x, y); break;
				case SQUARE: addSquareSite(x, y); break;
				case UNIQUE1: addU1Site(x, y); break;
				case UNIQUE2: addU2Site(x, y);
					break;
				//...
				default:
			}
		}
		vertexBuffer.put(vertexCoords);
		vertexBuffer.position(0);
	}
}
