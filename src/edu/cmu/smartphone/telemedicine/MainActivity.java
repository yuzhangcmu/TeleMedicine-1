package edu.cmu.smartphone.telemedicine;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

// Loading Splash View
public class MainActivity extends Activity {

	Button welcomeButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splashview);
		
		welcomeButton = (Button)findViewById(R.id.welcomeButton);
		
		welcomeButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				ParseUser currentUser = ParseUser.getCurrentUser();
//				if (currentUser != null) {
//				  // do stuff with the user
//					Intent intent = new Intent(MainActivity.this,
//			    			ContactActivity.class);
//					startActivity(intent);
//				} else {
//				  // show the signup or login screen
//					Intent intent = new Intent(MainActivity.this,
//							RegisterActivity.class);
//					startActivity(intent);
//				}
				
				Intent intent = new Intent(MainActivity.this,
						RegisterActivity.class);
				startActivity(intent);
			}
		});
		
//		ParseObject testObject = new ParseObject("TestObject");
//		testObject.put("foo2", "bar2");
//		testObject.saveInBackground();

		
	}

}
