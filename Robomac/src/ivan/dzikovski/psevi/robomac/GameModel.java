package ivan.dzikovski.psevi.robomac;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;
import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Vibrator;
import android.util.Log;

public class GameModel {
	
		public class Coin{
			public int x,y;
			public Coin(int x, int y){
				this.x=x;
				this.y=y;
			}
		}
		
		
		private static final double sin22=0.382,cos22=0.923;
	
		//brzina na robotot
		private final float pixelsPerMeter = 10;
		
		//radiusi na robotot i parickite
		private final int ballRadius;
		private final int coinRadius;
		
		//pozicija na robotot
		public float ballPixelX, ballPixelY;
		
		//pozicija na kukata
		public float exitTileX, exitTileY;
		
		//dimenzii na platnoto
		private int pixelWidth, pixelHeight;
		
		// momentalni brzini po oski
		private float velocityX, velocityY;
		
		//vrednosti dobieno od akcelerometar
		private float accelX, accelY;
		
		
		private final int brParicki=7;
		
		private boolean sobrani=false;
		
		private ArrayList<Coin> coinList=new ArrayList<Coin>();
		
		//zvuk za paricka
		private SoundPool soundPool;
		private int coinSoundID,finishSoundID;
		private boolean coinSoundLoaded = false;
		private boolean finishSoundLoaded = false;
		private float actualVolume;
        private float maxVolume;
        private float volume;
		
		
		
		// golemina na plocki za lavirintot
	    private int TILE_HEIGHT ;
	    private int TILE_WIDTH ;
	    private final static int MAZE_COLS = 40;
	    private final static int MAZE_ROWS = 60;

	    // tipovi na objekti
	    public final static int PATH_TILE = 0;
	    public final static int MAZE_TILE = 1;
	    public final static int EXIT_TILE = 2;
	    public final static int START_TILE = 3;
		
	    private static int[] mMazeData;
	    
	    private Rect mRect = new Rect();
	    private int mRow;
	    private int mCol;
	    private int mX;
	    private int mY;
	    
	    
	    //sliki za crtanje
	    
	    

	    private Bitmap robotStripes;
	    private Bitmap pozadinaIgraBitMap;
	    private Bitmap coinHomeBitMap;
	    
	    private Rect rectRobotStripeS[];
	    
        private Rect rectRobotD=new Rect(), rectParickaD=new Rect();
        private Rect rectPozadinaS, rectPozadinaD, rectParickaS,rectHomeD=new Rect(), rectHomeRedS, rectHomeGreenS;
        private int currentRobotBitMap=0;
        private int skipFrameCounter=0;
        private int rotationDirection=0;
        private int robotStripeWidth,robotStripeHeight, coinHomeStripeWidth,coinHomeStripeHeight;
        
	    
	    
	    //cetki
	    private Paint backgroundPaint, mazePaint;
		private Paint ballPaint;
		
		private Robomac appState;

		
		
		//faktor na odbivanje
	    private static final float rebound = 0.2f;

	    //brzina pri koja prestanuva odbivanjeto
	    private static final float STOP_BOUNCING_VELOCITY = 2f;

	    private volatile long lastTimeMs = -1;
	    private long totalElapsedMs;
		
		public final Object LOCK = new Object();
		
		private AtomicReference<Vibrator> vibratorRef =
			new AtomicReference<Vibrator>();
		
		/**
		 * Konstruktor za modelot na igrata
		 * @param ballRadius
		 * @param coinRadius
		 */
		
		public GameModel(int ballRadius, int coinRadius) {
			this.ballRadius = ballRadius;
			this.coinRadius= coinRadius;
			
			
		}
		/**
		 * Set za parametri od akcelerometar
		 * @param ax
		 * @param ay
		 */
		public void setAccel(float ax, float ay) {
			synchronized (LOCK) {
				this.accelX = ax;
				this.accelY = ay;
			}
		}
		
		/**
		 * Inicijalizacija na potrebnite resursi za igrata
		 * @param activity
		 */
		public void initMaze(Activity activity)
		{
			// maze data is stored in the assets folder as level1.txt, level2.txt
	        // etc....
			int height,width;
			synchronized (LOCK) {
				width=this.pixelWidth;
				height=this.pixelHeight;
				
			}
			
			totalElapsedMs=System.currentTimeMillis();
			
			Random rand=new Random();
			Coin coin;
			int x,y;
			int nivo;
			float lExitTileX,lExitTileY;
	       
	        InputStream is = null;
	        AssetFileDescriptor afd;
	        AudioManager audioManager = (AudioManager) activity.getSystemService(activity.AUDIO_SERVICE);
	        actualVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
	        maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
	        volume = actualVolume / maxVolume;
	        
	        
	        appState=((Robomac)activity.getApplicationContext());
	        nivo=appState.getNivo();

	      
	        robotStripes=BitmapFactory.decodeResource(activity.getResources(), R.drawable.robot_stripes);
	        robotStripeWidth=robotStripes.getWidth();
	        robotStripeHeight=robotStripes.getHeight();
	        
	        rectRobotStripeS=new Rect[16];
	        
	        for (int i=0;i<16;i++){
	        	rectRobotStripeS[i]=getRobotRect(i);
	        }
	        
	        pozadinaIgraBitMap=BitmapFactory.decodeResource(activity.getResources(), R.drawable.pozadina_igra_faded);
	        
	        rectPozadinaS=new Rect(0, 0, pozadinaIgraBitMap.getWidth(), pozadinaIgraBitMap.getHeight());
	        rectPozadinaD=new Rect(0, 0, width, height);
	        
	        coinHomeBitMap=BitmapFactory.decodeResource(activity.getResources(), R.drawable.coin_home_stripes);
	        coinHomeStripeHeight=coinHomeBitMap.getHeight();
	        coinHomeStripeWidth=coinHomeBitMap.getWidth();
	        
	        rectParickaS=new Rect(0, 0, coinHomeStripeWidth, coinHomeStripeHeight/3);
	        rectHomeRedS=new Rect(0, 2*coinHomeStripeHeight/3, coinHomeStripeWidth, coinHomeStripeHeight);
	        rectHomeGreenS=new Rect(0, coinHomeStripeHeight/3, coinHomeStripeWidth, 2*coinHomeStripeHeight/3);
	        
	     
	        backgroundPaint = new Paint();
			backgroundPaint.setColor(Color.WHITE);
			
			mazePaint=new Paint();
			mazePaint.setColor(Color.argb(180, 0, 0, 0));
			

			ballPaint = new Paint();
			ballPaint.setColor(Color.BLUE);
			ballPaint.setAntiAlias(true);
	        
	        

	        try {
	            // construct our maze data array.
	            mMazeData = new int[MAZE_ROWS * MAZE_COLS];
	            // attempt to load maze data.
	            is = activity.getAssets().open("level"+Integer.toString(nivo)+".txt");
	            Log.d("Nivo", Integer.toString(nivo));
	            // we need to loop through the input stream and load each tile for
	            // the current maze.
	            for (int i = 0; i < mMazeData.length; i++) {
	                // data is stored in unicode so we need to convert it.
	                mMazeData[i] = Character.getNumericValue(is.read());
	                //Log.d("Stream",Integer.toString(mMazeData[i]));
	                if (mMazeData[i]==2){
	                	 // calculate the row and column of the current tile.
	    	            mRow = i / MAZE_COLS;
	    	            mCol = i % MAZE_COLS;

	    	            // convert the row and column into actual x,y co-ordinates so we can
	    	            // draw it on screen.
	    	            mX = mCol * TILE_WIDTH;
	    	            mY = mRow * TILE_HEIGHT;
	    	            
	    	            synchronized (LOCK) {
	    					this.exitTileX = mX;
	    					this.exitTileY = mY;
	    				}
	    	            
	                }else if (mMazeData[i]==3){
	                	mRow = i / MAZE_COLS;
	    	            mCol = i % MAZE_COLS;

	    	            // convert the row and column into actual x,y co-ordinates so we can
	    	            // draw it on screen.
	    	            mX = mCol * TILE_WIDTH;
	    	            mY = mRow * TILE_HEIGHT;
	                	
	                	synchronized (LOCK) {
	        	            this.ballPixelX = mX;
	        	            this.ballPixelY = mY;
	        	            velocityX = 0;
	        	            velocityY = 0;
	        	        }
	                }
	                
	                
	                is.read();
	                is.read();
	            }
	        } catch (Exception e) {
	            Log.i("Maze", "load exception: " + e);
	        } finally {
	            closeStream(is);
	        }
	        
	        synchronized (LOCK) {
				lExitTileX=this.exitTileX;
				lExitTileY=this.exitTileY;
			}
	        
	        
	        rectHomeD.left=(int) lExitTileX-ballRadius;
	        rectHomeD.top=(int) lExitTileY-ballRadius;
	        rectHomeD.right= 2*ballRadius+(int) lExitTileX-ballRadius;
	        rectHomeD.bottom=2*ballRadius+(int) lExitTileY-ballRadius;
	        
	        
	        
	        while (coinList.size()<brParicki){
	        	x=rand.nextInt(width);
	        	y=rand.nextInt(height);
	        	
	        	if(checkCoin(x,y)){
	        		coin=new Coin(x, y);
	        		coinList.add(coin);
	        	}
	        
	        }
	        
	        //zvuk za paricka
	        
	        activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
	        
	        soundPool=new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
	        soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
				
				@Override
				public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
					if (sampleId==coinSoundID)	
						coinSoundLoaded=true;
					else finishSoundLoaded=true;
				}
			});
	        
	        try {
				afd=activity.getAssets().openFd("Coin.mp3");
				coinSoundID=soundPool.load(afd, 1);
				afd=activity.getAssets().openFd("Success.mp3");
				finishSoundID=soundPool.load(afd, 2);
			} catch (IOException e) {
				Log.d("Load sound asset", e.getMessage());
			}
	        
	       
	        

		}
		
		
		/**
		 * Funkcija sto vraka source rect za soodvetniot
		 * robot od stripe za robotot
		 * @param i
		 * @return
		 */
		private Rect getRobotRect(int i) {
			Rect rect=new Rect();
			
			
			if (i<8){
				rect.left=0;
				rect.top=i*robotStripeHeight/8;
			}
				
			else
			{
				rect.left=robotStripeWidth/2;
				rect.top=i*robotStripeHeight/8-robotStripeHeight;
			}
			rect.bottom=rect.top+robotStripeHeight/8;
			rect.right=rect.left+robotStripeWidth/2;
			return rect;
		}

		/**
		 * Proverka dali parickata e vo sudir so zidovite,
		 * drugite paricki ili izlezot
		 * @param x
		 * @param y
		 * @return
		 */
		private boolean checkCoin(int x, int y) {
			//zidovi
			if ((getCellType(x+coinRadius, y)==MAZE_TILE)||
				(getCellType(x-coinRadius, y)==MAZE_TILE)||
				(getCellType(x, y-coinRadius)==MAZE_TILE)||
				(getCellType(x, y+coinRadius)==MAZE_TILE))
				return false;
			//paricki
			for (int i=0;i<coinList.size();i++){
				if (circleCollision(x, y, coinRadius, coinList.get(i).x, coinList.get(i).y, coinRadius))
					return false;
			}
			//izlez
			if (circleCollision(x, y, coinRadius, (int) exitTileX, (int) exitTileY, ballRadius))
				return false;
			
			return true;
		}

		/**
		 * Proverka za kolizija pomegu dva kruga
		 * @param x1
		 * @param y1
		 * @param r1
		 * @param x2
		 * @param y2
		 * @param r2
		 * @return
		 */
		private boolean circleCollision(int x1, int y1, int r1, int x2, int y2, int r2){
			if (((x2-x1)*(x2-x1)+(y2-y1)*(y2-y1))<=(r1+r2)*(r1+r2))
					return true;
			
			return false;
		}
		
		/**
		 * Iscrtuvanje na site objekti na platnoto
		 * @param canvas
		 * @param paint
		 * @param activity
		 */
		public void drawMaze(Canvas canvas, Paint paint, Activity activity) {
			
			float lBallX,lBallY;
			synchronized (LOCK) {
	            lBallX = ballPixelX;
	            lBallY = ballPixelY;
	        }
			
			int width = canvas.getWidth();
			int height = canvas.getHeight();
			int nivo=appState.getNivo();
			
			Coin coin;
			
			rectRobotD.left=(int) lBallX-ballRadius;
			rectRobotD.top=(int) lBallY-ballRadius;
			rectRobotD.right= 2*ballRadius+(int) lBallX-ballRadius;
			rectRobotD.bottom=2*ballRadius+(int) lBallY-ballRadius;
			
			
			//pozadina
			canvas.drawBitmap(pozadinaIgraBitMap,rectPozadinaS,rectPozadinaD,paint);
			
			//robot
			canvas.drawBitmap(robotStripes, rectRobotStripeS[currentRobotBitMap], rectRobotD, paint);
			
			
			//rotacija na robotot
			skipFrameCounter++;
			if (skipFrameCounter==3)
			{
				if (rotationDirection==0){
					currentRobotBitMap++;
					if (currentRobotBitMap==16)
						currentRobotBitMap=0;
					skipFrameCounter=0;
				}
				else{
					currentRobotBitMap--;
					if (currentRobotBitMap==-1)
						currentRobotBitMap=15;
					skipFrameCounter=0;
				}
			}
			
			
			//crtanje na lavirintot i kukata
	        for (int i = 0; i < mMazeData.length; i++) {
	           
	            mRow = i / MAZE_COLS;
	            mCol = i % MAZE_COLS;

	           
	            mX = mCol * TILE_WIDTH;
	            mY = mRow * TILE_HEIGHT;

	          
	            if (mMazeData[i] == MAZE_TILE)
	            {
	            
	                
	                mRect.left = mX;
	                mRect.top = mY;
	                mRect.right = mX + TILE_WIDTH;
	                mRect.bottom = mY + TILE_HEIGHT;

	              
	                canvas.drawRect(mRect, mazePaint);
	            
	            }
	            else if(mMazeData[i] == EXIT_TILE){
	            	
	            	if (sobrani){
	            		canvas.drawBitmap(coinHomeBitMap, rectHomeGreenS, rectHomeD, paint);
	            	}
	            	else{
	            		canvas.drawBitmap(coinHomeBitMap, rectHomeRedS, rectHomeD, paint);
	            	}
	            }
	        }

	        //proverka za kolizija so robotot
	        for(int i=0;i<coinList.size();i++){
	        	coin=coinList.get(i);
	        	if (circleCollision((int) lBallX, (int) lBallY, ballRadius, coin.x, coin.y, coinRadius)){
	        		coinList.remove(coin);
	        		if (coinSoundLoaded){
	        			soundPool.play(coinSoundID, volume, volume, 1, 0, 1f);
	        		}
	        	}
	        }
	        
	        if (coinList.isEmpty())
	        	sobrani=true;
	        
	        //kraj na nivoto
	        if (sobrani && circleCollision((int) lBallX, (int) lBallY, ballRadius, (int) exitTileX, (int) exitTileY, coinRadius))
	        {
	        	if (finishSoundLoaded){
        			soundPool.play(finishSoundID, volume, volume, 1, 0, 1f);
        		}
	        	totalElapsedMs=System.currentTimeMillis()-totalElapsedMs;
	        	appState.setTimes(nivo-1, new Time(totalElapsedMs/1000));
	        	Intent intent=new Intent(activity, LevelCompleteActivity.class);
	        	activity.startActivity(intent);
	        }
	        
	        //crtanje na parickite
	        
	        for(int i=0;i<coinList.size();i++){
	        
	        	coin=coinList.get(i);
	        	
	        	rectParickaD.left=(int) coin.x-coinRadius;
	        	rectParickaD.top=(int) coin.y-coinRadius;
	        	rectParickaD.right=2*coinRadius + (int) coin.x-coinRadius;
	        	rectParickaD.bottom=2*coinRadius + (int) coin.y-coinRadius;
	        	
	        	
	        	canvas.drawBitmap(coinHomeBitMap, rectParickaS, rectParickaD, paint);
	        }
	        
	    }

		public void setSize(int width, int height) {
			synchronized (LOCK) {
				this.pixelWidth = width;
				this.pixelHeight = height;
				
			}
			//postavi ja goleminata na plockite
			TILE_HEIGHT=height/MAZE_ROWS;
			TILE_WIDTH=width/MAZE_COLS;

		}
		
	    public int getBallRadius() {
	        return ballRadius;
	    }

	    /**
	     * Pomesti go robotot na dadenata lokacija
	     * @param ballX
	     * @param ballY
	     */
	    public void moveBall(int ballX, int ballY) {
	    	synchronized (LOCK) {
	            this.ballPixelX = ballX;
	            this.ballPixelY = ballY;
	            velocityX = 0;
	            velocityY = 0;
	        }
	    }
	    
	    /**
	     * Azuriranje na fizikata, proverka za kolizija so zidovi
	     * i soodvetno pomestuvanje na robotot
	     */
	    public void updatePhysics() {
	     
	        float lWidth, lHeight, lBallX, lBallY, lAx, lAy, lVx, lVy, gore, dole, levo, desno;
	        float glX, glY, gdX, gdY, dlX, dlY, ddX, ddY, kateta;
	        float gl1X,gl1Y,gl2X,gl2Y,gd1X,gd1Y,gd2X,gd2Y;
	        float dl1X,dl1Y,dl2X,dl2Y,dd1X,dd1Y,dd2X,dd2Y;
	        float mala= (float) (ballRadius*sin22),golema=(float) (ballRadius*cos22);
	        
	        synchronized (LOCK) {
	            lWidth = pixelWidth;
	            lHeight = pixelHeight;
	            lBallX = ballPixelX;
	            lBallY = ballPixelY;
	            lVx = velocityX;            
	            lVy = velocityY;
	            lAx = accelX;
	            lAy = -accelY;
	        }


	        if (lWidth <= 0 || lHeight <= 0) {
	           
	            return;
	        }

	        
	        long curTime = System.currentTimeMillis();
	        if (lastTimeMs < 0) {
	            lastTimeMs = curTime;
	            return;
	        }

	        long elapsedMs = curTime - lastTimeMs;
	        lastTimeMs = curTime;
	        
	        
	        //azuriranje na brzinata
	        lVx += ((elapsedMs * lAx) / 1000) * pixelsPerMeter;
	        lVy += ((elapsedMs * lAy) / 1000) * pixelsPerMeter;

	        //azuriranje na pozicijata
	        lBallX += ((lVx * elapsedMs) / 1000) * pixelsPerMeter;
	        lBallY += ((lVy * elapsedMs) / 1000) * pixelsPerMeter;
	        
	        boolean bouncedX = false;
	        boolean bouncedY = false;

	        //nasoka na rotacija na robotot
	        if (lVy>0)
	        	rotationDirection=0;
	        else
	        	rotationDirection=1;
	        
	        
	        //tocki za proverka za kolizija na robotot
	        
	        kateta=(float) (ballRadius*0.7);
	        
	        gore=lBallY-ballRadius;
	        dole=lBallY+ballRadius;
	        levo=lBallX-ballRadius;
	        desno=lBallX+ballRadius;
	        
	        //gore levo
	        glX=lBallX-kateta;
	        glY=lBallY-kateta;
	        
	        //gore levo 1
	        gl1X=lBallX-golema;
	        gl1Y=lBallY-mala;
	        
	        //gore levo 2
	        gl2X=lBallX-mala;
	        gl2Y=lBallY-golema;
	        
	        //gore desno
	        gdX=lBallX+kateta;
	        gdY=lBallY-kateta;
	        
	        //gore desno 1
	        gd1X=lBallX+mala;
	        gd1Y=lBallY-golema;
	        
	        //gore desno 2
	        gd2X=lBallX+golema;
	        gd2Y=lBallY-mala;
	        
	        //dole levo
	        dlX=lBallX-kateta;
	        dlY=lBallY+kateta;
	        
	        //dole levo 1
	        dl1X=lBallX-golema;
	        dl1Y=lBallY+mala;
	        
	        //dole levo 2
	        dl2X=lBallX-mala;
	        dl2Y=lBallY+golema;
	        
	        //dole desno
	        ddX=lBallX+kateta;
	        ddY=lBallY+kateta;
	        
	        //dole desno 1
	        dd1X=lBallX+mala;
	        dd1Y=lBallY+golema;
	        
	        //dole desno 2
	        dd2X=lBallX+golema;
	        dd2Y=lBallY+mala;

	        
	        //proveri dali ima kolizija so lavirintot
	        if (lVy>0)
	        {
	        	if ((getCellType((int) lBallX, (int) dole)==MAZE_TILE)||
	        		(getCellType((int) dlX, (int) dlY)==MAZE_TILE)||
	        		(getCellType((int) dl1X, (int) dl1Y)==MAZE_TILE)||
	        		(getCellType((int) dl2X, (int) dl2Y)==MAZE_TILE)||
	        		(getCellType((int) dd1X, (int) dd1Y)==MAZE_TILE)||
	        		(getCellType((int) dd2X, (int) dd2Y)==MAZE_TILE)||
	        		(getCellType((int) ddX, (int) ddY)==MAZE_TILE))
	        	{
	        		lBallY-=1;
		            lVy = -lVy * rebound;
		            bouncedY = true;
	        	}
	        }
	        else
	        {
	        	if ((getCellType((int) lBallX, (int) gore)==MAZE_TILE)||
	        		(getCellType((int) glX, (int) glY)==MAZE_TILE)||
	        		(getCellType((int) gl1X, (int) gl1Y)==MAZE_TILE)||
	        		(getCellType((int) gl2X, (int) gl2Y)==MAZE_TILE)||
	        		(getCellType((int) gd1X, (int) gd1Y)==MAZE_TILE)||
	        		(getCellType((int) gd2X, (int) gd2Y)==MAZE_TILE)||
	        		(getCellType((int) gdX, (int) gdY)==MAZE_TILE))
	        	{
	        		lBallY+=1;
	        		lVy = -lVy * rebound;
			        bouncedY = true;
	        	}
	        }
	        
	        if (bouncedY && Math.abs(lVy) < STOP_BOUNCING_VELOCITY) {
	            lVy = 0;  
	            bouncedY = false;
	        }
	        
	        if (lVx>0)
	        {
	        	if ((getCellType((int) desno, (int) lBallY)==MAZE_TILE)||
	        		(getCellType((int) gdX, (int) gdY)==MAZE_TILE)||
	        		(getCellType((int) gd1X, (int) gd1Y)==MAZE_TILE)||
	        		(getCellType((int) gd2X, (int) gd2Y)==MAZE_TILE)||
	        		(getCellType((int) dd1X, (int) dd1Y)==MAZE_TILE)||
	        		(getCellType((int) dd2X, (int) dd2Y)==MAZE_TILE)||
	        		(getCellType((int) ddX, (int) ddY)==MAZE_TILE))
	        	{
	        		lBallX-=1;
	        		lVx = -lVx * rebound;
		        	bouncedX = true;
	        	}
	        }
	        else
	        {
	        	if ((getCellType((int) levo, (int) lBallY)==MAZE_TILE)||
	        		(getCellType((int) glX, (int) glY)==MAZE_TILE)||
	        		(getCellType((int) gl1X, (int) gl1Y)==MAZE_TILE)||
	        		(getCellType((int) gl2X, (int) gl2Y)==MAZE_TILE)||
	        		(getCellType((int) dl1X, (int) dl1Y)==MAZE_TILE)||
	        		(getCellType((int) dl2X, (int) dl2Y)==MAZE_TILE)||
	        		(getCellType((int) dlX, (int) dlY)==MAZE_TILE))
	        	{
	        		lBallX+=1;
	        		lVx = -lVx * rebound;
		        	bouncedX = true;
	        	}
	        }

	        if (bouncedX && Math.abs(lVx) < STOP_BOUNCING_VELOCITY) {
	        	lVx = 0;
	        	bouncedX = false;
	        } 


	        synchronized (LOCK) {
	            ballPixelX = lBallX;
	            ballPixelY = lBallY;
	            
	            velocityX = lVx;
	            velocityY = lVy;
	        }
	        
	        if (bouncedX || bouncedY) {
	        	Vibrator v = vibratorRef.get();
	        	if (v != null) {
	        		v.vibrate(20L);
	        	}
	        }
	    }
	    
	    
	    public int getCellType(int x, int y) {
	    	//konverzija na koordinatite vo redica i kolona na mapata
	        int mCellCol = x / TILE_WIDTH;
	        int mCellRow = y / TILE_HEIGHT;

	        //promenliva za redot
	        int mLocation = 0;

	        //najdi go redot
	        if (mCellRow > 0)
	            mLocation = mCellRow * MAZE_COLS;

	        // dodadi ja kolonata
	        mLocation += mCellCol;
	        if (mLocation<MAZE_COLS*MAZE_ROWS)
	        	return mMazeData[mLocation];
	        return 1;
	    }
	    
	    
	    public void setVibrator(Vibrator v) {
	    	vibratorRef.set(v);
	    }
	
	    private static void closeStream(Closeable stream) {
	        if (stream != null) {
	            try {
	                stream.close();
	            } catch (IOException e) {
	            }
	        }
	    }
	    
	    /**
	     * Osloboduvanje na bit mapite
	     */
	    public void recycleBitMaps(){
	    	robotStripes.recycle();
	    	pozadinaIgraBitMap.recycle();
	    	coinHomeBitMap.recycle();
	    	
	    }
	    public int getBallX(){
	    	synchronized (LOCK) {
				return (int) ballPixelX;
			}
	    }
	    public int getBallY(){
	    	synchronized (LOCK) {
				return (int) ballPixelY;
			}
	    }
}
