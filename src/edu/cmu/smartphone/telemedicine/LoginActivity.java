package edu.cmu.smartphone.telemedicine;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import edu.cmu.smartphone.telemedicine.DBLayout.Dao_Sqlite;

public class LoginActivity extends Activity {

	EditText loginUsernameEditText;
	EditText loginPasswordEditText;
	Button loginButton;
	
	// added by yu zhang for register login.
	TextView registerTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// fix bug: should set to loginView, not register view.
		setContentView(R.layout.loginview);

		
		loginUsernameEditText = (EditText) findViewById(R.id.loginUsername);
		loginPasswordEditText = (EditText) findViewById(R.id.loginPassword);

		loginButton = (Button) findViewById(R.id.login_loginBtn);
		
		// added by yuzhang 
		registerTextView = (TextView)findViewById(R.id.login_link_to_register);
		
		// added by yu zhang for set the on clike of the button "Go to register."
		registerTextView.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,
                        RegisterActivity.class);
                startActivity(intent);
            }
        });

		loginButton.setOnClickListener(new OnClickListener() {

			String username = loginUsernameEditText.getEditableText()
					.toString();
			String password = loginPasswordEditText.getEditableText()
					.toString();

			@Override
			public void onClick(View v) {
			    // First should contact the server to check if the user exit.
			    // ...
			    
			    // if the user exit, LOGIN and CREATE A DATABASE.
			    // if the database exit, ignore.
			    Dao_Sqlite dao = new Dao_Sqlite(LoginActivity.this);
				// TODO Auto-generated method stub

				ParseUser.logInInBackground(username, password,
						new LogInCallback() {
							public void done(ParseUser user, ParseException e) {
								if (user != null) {
									// Hooray! The user is logged in.
									Intent intent = new Intent(
											LoginActivity.this,
											ContactActivity.class);
									startActivity(intent);
								} else {
									// Signup failed. Look at the ParseException
									// to see what happened.
									Toast.makeText(getApplicationContext(),
											e.getMessage(), Toast.LENGTH_LONG)
											.show();
								}
							}
						});
			}
		});

	}

}