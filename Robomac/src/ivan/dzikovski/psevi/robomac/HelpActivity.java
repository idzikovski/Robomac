package ivan.dzikovski.psevi.robomac;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Display;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

public class HelpActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help_layout);
		
		Typeface type=Typefaces.get(this, "ropa_sans.otf");
		
		
		
		ViewGroup container=(ViewGroup) findViewById(R.id.container);
		Display display = getWindowManager().getDefaultDisplay();
		int width = display.getWidth()-40;
		int height =300;
		
		AutoResizeTextView helpText=new AutoResizeTextView(this);

		
		helpText.setLayoutParams(new FrameLayout.LayoutParams(width, height));
		helpText.setMaxLines(7);
		helpText.setTextSize(500);
		helpText.enableSizeCache(false);
		helpText.setTypeface(type);
		helpText.setTextColor(Color.argb(255, 30, 30, 30));
		helpText.setText(R.string.help_text);
		
		container.addView(helpText);
		

	}
	
}
