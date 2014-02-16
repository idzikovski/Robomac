package ivan.dzikovski.psevi.robomac;


import static android.hardware.SensorManager.SENSOR_DELAY_GAME;

import ivan.dzikovski.psevi.robomac.GameModel.Coin;

import java.util.concurrent.TimeUnit;




import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.SurfaceHolder.Callback;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

public class GameActivity extends Activity implements Callback, SensorEventListener{
	
	private static int BALL_RADIUS;
	private static int COIN_RADIUS;
	private SurfaceView surface;
	private SurfaceHolder holder;
	private GameModel model;
	private GameLoop gameLoop;
	private SensorManager sensorMgr;
	private Sensor sensor;
	private long lastSensorUpdate = -1;
	private Paint  mazePaint;
	private int ballX,ballY;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.game_layout);	
		super.onCreate(savedInstanceState);
		
		Display display = getWindowManager().getDefaultDisplay();
		int width = display.getWidth();
		int height = display.getHeight();
			
		BALL_RADIUS=width/15;
		COIN_RADIUS=(int) (BALL_RADIUS*0.66);
		
	
		model = new GameModel(BALL_RADIUS, COIN_RADIUS);
		
		model.setSize(width, height);
		model.initMaze(this);
		Log.d("Zavrsi init", "sega");
		
		sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
		sensor=sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		
		Log.d("Sensor",sensor.toString());
		
		surface = (SurfaceView) findViewById(R.id.bouncing_ball_surface);
    	holder = surface.getHolder();
    	surface.getHolder().addCallback(this);
    	
    	mazePaint=new Paint();
		mazePaint.setColor(Color.RED);
		
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		model.recycleBitMaps();
	}

	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		return super.onCreateOptionsMenu(menu);
		
		
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		//zacuvuvanje na sostojbata
		if (gameLoop!=null)
			gameLoop.onPause();
		
		ballX=model.getBallX();
		ballY=model.getBallY();
		model.setVibrator(null);
		
		//deregistracija na slusacot za senzor
		sensorMgr.unregisterListener(this);
		sensorMgr = null;
		
		model.setAccel(0, 0);
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		if (gameLoop!=null){
			model.moveBall(ballX, ballY);
			gameLoop.onResume();
		}

		sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
		sensor=sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		
		if (sensorMgr!=null){
			boolean accelSupported = sensorMgr.registerListener(this, sensor, SENSOR_DELAY_GAME);
			
			if (!accelSupported) {
				sensorMgr.unregisterListener(this);

			}
		}
		
		Vibrator vibrator = (Vibrator) getSystemService(Activity.VIBRATOR_SERVICE);
		model.setVibrator(vibrator);
	}

	public void onAccuracyChanged(int sensor, int accuracy) {		
	}

	public void onSensorChanged(SensorEvent event) {
		
		//novi podatoci od akcelerometarot
			long curTime = System.currentTimeMillis();
			if (lastSensorUpdate == -1 || (curTime - lastSensorUpdate) > 50) {
				lastSensorUpdate = curTime;
				
				model.setAccel(-event.values[0], -event.values[1]);
			}
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		
		model.setSize(width, height);
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		gameLoop = new GameLoop();
		gameLoop.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		try {
			model.setSize(0,0);
			gameLoop.safeStop();
		} finally {
			gameLoop = null;
		}
	}
	
	private void draw() {

		
		Canvas c = null;
		try {

			c = holder.lockCanvas();
			
			if (c != null) {
				doDraw(c);
			}
		} finally {
			if (c != null) {
				holder.unlockCanvasAndPost(c);
			}
		}
	}
	
	private void doDraw(Canvas c) {
		model.drawMaze(c, mazePaint, this);
	}
	
	//nitka vo koja se izvrsuva azuriranjeto na fizikata i crtanjeto
	private class GameLoop extends Thread {
		private volatile boolean running = true;
		private Object mPauseLock=new Object();
	    private boolean mPaused=false;
		
		public void run() {
			while (running) {
				try {
					//  don't like this hardcoding
					//TimeUnit.MILLISECONDS.sleep(5);
					draw();
					model.updatePhysics();

				} catch (/*Interrupted*/Exception ie) {
					running = false;
				}
				synchronized (mPauseLock) {
	                while (mPaused) {
	                    try {
	                    	Log.d("Nitka", "Spijam");
	                        mPauseLock.wait();
	                    } catch (InterruptedException e) {
	                    }
	                }
	            }
			}
		}
		
		public void safeStop() {
			running = false;
			interrupt();
		}
		
		public void onPause() {
	        synchronized (mPauseLock) {
	            mPaused = true;
	        }
	    }


	    public void onResume() {
	        synchronized (mPauseLock) {
	            mPaused = false;
	            Log.d("Nitka", "Se budam");
	            mPauseLock.notifyAll();
	        }
	    }
		
		
	}


	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}
	

}
