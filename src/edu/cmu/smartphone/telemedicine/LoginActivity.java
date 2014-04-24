package edu.cmu.smartphone.telemedicine;

import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.PushService;

import edu.cmu.smartphone.telemedicine.DBLayout.Dao_Sqlite;

public class LoginActivity extends Activity {

	EditText loginUsernameEditText;
	EditText loginPasswordEditText;
	Button loginButton;
	
	// added by yu zhang for register login.
	TextView registerTextView;
	
	// added by yu zhang. for userID manage.
	private static String userID;
	
	// get the current userid.
	public static String getCurrentUserID() {
	    return userID;
	}
	
	public static void setUserID(String userID1) {
	    userID = userID1;
	}
	// added by yu zhang. for userID manage.
	
	public static void login(Context context, String username) {
	    // remove from other channels
	    Set<String> setOfAllSubscriptions = PushService.getSubscriptions(context);
	    for (String s: setOfAllSubscriptions) {
	        if (!s.equals(username)) {
	            PushService.unsubscribe(context, s);
	        }
	    }
	    
	    // When users indicate they are Giants fans, we subscribe them to that channel.
	    PushService.subscribe(context, username, ContactActivity.class);
	    
	    // added by yu zhang. setup the userid.
        userID = username;
        
        // added by yu zhang:
        // create a database, the name is the username.
        Dao_Sqlite dao = new Dao_Sqlite(context, username, null, 1);
	}

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
		    String username = null;
		    String password = null;
		    
			@Override
			public void onClick(View v) {
			    username = loginUsernameEditText.getEditableText()
	                    .toString();
	            password = loginPasswordEditText.getEditableText()
	                    .toString();
	            
	            // hide the keyboard.
                InputMethodManager imm = (InputMethodManager)getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(loginUsernameEditText.getWindowToken(), 0);
			    
				ParseUser.logInInBackground(username, password,
						new LogInCallback() {
							public void done(ParseUser user, ParseException e) {
								if (user != null) {
									// Hooray! The user is logged in.
									Intent intent = new Intent(
											LoginActivity.this,
											ContactActivity.class);
									startActivity(intent);
									
									// login and prepare data.
									login(LoginActivity.this, username);
									
								} else {
									// Signup failed. Look at the ParseException
									// to see what happened.
								    Toast toast = Toast.makeText(getApplicationContext(),
                                            e.getMessage(), Toast.LENGTH_LONG);
								    
								    // change the position to show the message.
								    toast.setGravity(Gravity.CENTER|Gravity.CENTER, 0, 0);
								    toast.show();
								}
							}
						});
			}
		});

	}

}