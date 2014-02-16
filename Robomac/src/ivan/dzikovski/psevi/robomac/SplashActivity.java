package ivan.dzikovski.psevi.robomac;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class SplashActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_layout);
		
		ViewGroup container=(ViewGroup) findViewById(R.id.container);
		Display display = getWindowManager().getDefaultDisplay();
		int width = display.getWidth();
		int height =200;
		
		AutoResizeTextView robomac=new AutoResizeTextView(this);

		Typeface type=Typefaces.get(this, "ropa_sans.otf");
		
		robomac.setLayoutParams(new FrameLayout.LayoutParams(width, height));
		robomac.setMaxLines(1);
		robomac.setTextSize(2000);
		robomac.enableSizeCache(false);
		robomac.setTypeface(type);
		robomac.setTextColor(Color.argb(255, 30, 30, 30));
		robomac.setText("Robomac");
		
		container.addView(robomac);
		
		new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                
                Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(mainIntent);
                finish();
            }
        }, 2000);
	}
	
}
