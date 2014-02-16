package ivan.dzikovski.psevi.robomac;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

public class LevelCompleteActivity extends Activity {

	private Robomac appState;
	private int nivo;
	private Time time;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.level_complete);
		
		appState=(Robomac) getApplicationContext();
		nivo=appState.getNivo();
		time=appState.getTimes(nivo-1);
		
		Typeface type=Typefaces.get(this, "ropa_sans.otf");
		
		ViewGroup container=(ViewGroup) findViewById(R.id.container);
		Display display = getWindowManager().getDefaultDisplay();
		int width = display.getWidth();
		int height =80;
		
		AutoResizeTextView levelComplete=new AutoResizeTextView(this);
		
		
		levelComplete.setLayoutParams(new FrameLayout.LayoutParams(width, height));
		levelComplete.setMaxLines(1);
		levelComplete.setTextSize(500);
		levelComplete.enableSizeCache(false);
		levelComplete.setTypeface(type);
		levelComplete.setTextColor(Color.argb(255, 30, 30, 30));
		levelComplete.setText("Level complete");
		
		container.addView(levelComplete);
		
		
		String yTime="Your time: ";
		if (time.getMinutes()>0)
			yTime+=Integer.toString(time.getMinutes())+"m ";
		yTime+=Integer.toString(time.getSeconds())+"s";
		
		TextView yourTime=(TextView) findViewById(R.id.yourTime);
		
		yourTime.setTextColor(Color.argb(255, 30, 30, 30));
		yourTime.setTypeface(type);
		yourTime.setTextSize(25 * getResources().getDisplayMetrics().density);
		yourTime.setText(yTime);
		
	}
	
	public void nextLevelClicked(View view){
		if (nivo==5){
			appState.setNivo(1);
			Intent intent=new Intent(this, ChallengeCompletedActivity.class);
	    	startActivity(intent);
		}	
		else{
			appState.setNivo(nivo+1);
			Intent intent=new Intent(this, GameActivity.class);
			startActivity(intent);
		}
	}
	public void replayLevelClicked(View view){
		Intent intent=new Intent(this, GameActivity.class);
    	startActivity(intent);
	}
	public void homeButtonClicked(View view){
		if (nivo<5){
			appState.setNivo(nivo+1);
		}

		Intent intent=new Intent(this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    	startActivity(intent);
	}

}
