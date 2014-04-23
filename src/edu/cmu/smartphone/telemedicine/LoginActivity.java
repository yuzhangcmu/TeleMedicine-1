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
	
	// added by yu zhang. for userID manage.
	private static String userID;
	
	public static String getUserID() {
	    return userID;
	}
	
	public static void setUserID(String userID1) {
	    userID = userID1;
	}
	// added by yu zhang. for userID manage.

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// fix bug: should set to loginView, not register view.
		setContentView(R.layout.loginview);

		
		loginUsernameEditText = (EditText) findViewById(R.id.loginUsername);
		loginPasswordEditText = (EditText) findViewById(R.id.loginPassword);

		loginButton = (Button) findViewById(R.id.login_loginBtn);
		
		// added by yu zhang. 
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
				ParseUser.logInInBackground(username, password,
						new LogInCallback() {
							public void done(ParseUser user, ParseException e) {
								if (user != null) {
									// Hooray! The user is logged in.
									Intent intent = new Intent(
											LoginActivity.this,
											ContactActivity.class);
									startActivity(intent);
									
									// added by yu zhang. setup the userid.
									userID = username;
									
									// added by yu zhang:
									// create a database, the name is the username.
					                Dao_Sqlite dao = new Dao_Sqlite(LoginActivity.this, username, null, 1);
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