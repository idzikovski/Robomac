package ivan.dzikovski.psevi.robomac;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ChallengeCompletedActivity extends Activity {
	
	private Robomac appState;
	private int totalSeconds;
	private Time totalTime;
	private String tTime;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.challenge_complete);
		
        
		
		appState=(Robomac) getApplicationContext();
		for (int i=0;i<5;i++)
			totalSeconds+=appState.getTimes(i).getTotalSeconds();
		totalTime=new Time(totalSeconds);
		
		
		tTime="";
		if (totalTime.getMinutes()>0)
			tTime+=Integer.toString(totalTime.getMinutes())+"m ";
		tTime+=Integer.toString(totalTime.getSeconds())+"s";
		
		Typeface type=Typefaces.get(this, "ropa_sans.otf");
		
		ViewGroup container=(ViewGroup) findViewById(R.id.container);
		Display display = getWindowManager().getDefaultDisplay();
		int width = display.getWidth();
		int height =80;
		
		AutoResizeTextView congrat=new AutoResizeTextView(this);

		
		congrat.setLayoutParams(new FrameLayout.LayoutParams(width, height));
		congrat.setMaxLines(1);
		congrat.setTextSize(500);
		congrat.enableSizeCache(false);
		congrat.setTypeface(type);
		congrat.setTextColor(Color.argb(255, 30, 30, 30));
		congrat.setText("Congratulations");
		
		container.addView(congrat);
		
		TextView congratText=(TextView) findViewById(R.id.congratText);
		TextView totalTime=(TextView) findViewById(R.id.totalTime);
		
		totalTime.setTextColor(Color.argb(255, 30, 30, 30));
		totalTime.setTypeface(type);
		totalTime.setTextSize(26 * getResources().getDisplayMetrics().density);
		totalTime.setText("Total time: "+tTime);
		
		congratText.setTextColor(Color.argb(255, 30, 30, 30));
		congratText.setTypeface(type);
		congratText.setTextSize(25 * getResources().getDisplayMetrics().density);

	}
	
	public void shareButtonClicked(View view){
		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_TEXT, "I completed the Robomac challenge in "+tTime+" http://robomac.eestec-sk.org.mk/");
		sendIntent.setType("text/plain");
		startActivityForResult(Intent.createChooser(sendIntent, "Share to"), 1);
	}
	
}
