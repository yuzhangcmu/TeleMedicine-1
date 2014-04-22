package edu.cmu.smartphone.telemedicine;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends Activity {

	EditText loginUsernameEditText;
	EditText loginPasswordEditText;
	Button loginButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loginview);

		loginUsernameEditText = (EditText) findViewById(R.id.loginUsername);
		loginPasswordEditText = (EditText) findViewById(R.id.loginPassword);

		loginButton = (Button) findViewById(R.id.login_loginBtn);

		loginButton.setOnClickListener(new OnClickListener() {

			String username = loginUsernameEditText.getEditableText()
					.toString();
			String password = loginPasswordEditText.getEditableText()
					.toString();

			@Override
			public void onClick(View v) {
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