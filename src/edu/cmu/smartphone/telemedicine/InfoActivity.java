package edu.cmu.smartphone.telemedicine;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class InfoActivity extends Activity {

	Button textInfoButton;
	Button audioInfoButton;
	Button videoInfoButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.infoview);
		
		
		textInfoButton = (Button)findViewById(R.id.textInfoButton);
		audioInfoButton = (Button)findViewById(R.id.audioInfoButton);
		videoInfoButton = (Button)findViewById(R.id.videoInfoButton);
		
		textInfoButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		
		audioInfoButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		
		videoInfoButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});

		
		
	}

}
