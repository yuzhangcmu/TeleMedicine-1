package edu.cmu.smartphone.telemedicine;

import java.util.UUID;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;

import com.openclove.ovx.OVXCallListener;
import com.openclove.ovx.OVXException;
import com.openclove.ovx.OVXView;

public class VideoActivity extends Activity {

	private OVXView ovxView;
	private Dialog dialog;
	private EditText ovx_text;
	protected TextView chat_box;
	private String groupid;
	private TextView text_gid;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.videoview);
		
		ovxView = OVXView.getOVXContext(this);
		
		try {
			String ovxuserId = Secure.getString(this.getContentResolver(), Secure.ANDROID_ID);
			ovxView.setOvxUserId(ovxuserId);
			ovxView.setOvxGroupId(UUID.randomUUID().toString().replaceAll("-", ""));
			ovxView.setOvxMood("1");
			ovxView.setShowOVXMenuOnTap(true);
			ovxView.enableRemoteGesture(true);
			ovxView.setRemoteViewX(100);
			ovxView.setRemoteViewY(100);
			
			
			Button ovx_call_button = (Button) findViewById(R.id.start_call);
			ovx_call_button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (!ovxView.isCallOn()) //Checks if the call is on 
						try {
							ovxView.call(); // Initiates call and starts a session with the specified OVX group id and other parameters set earlier. 
						} catch (OVXException e) {
							e.printStackTrace();
						}
					else { // If call is already started
						CharSequence[] ch = { "Call is already on" };
						showDialog("Warning", ch);
					}

				}
			});
			
			Button ovx_end_call = (Button) findViewById(R.id.end_call);
			ovx_end_call.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (ovxView.isCallOn()) { // 
						ovxView.exitCall(); // ends the existing call and removes the user from the live conference.
					} 
				}
			});
			
			ovx_text = (EditText) findViewById(R.id.chat_text);
			ovx_text.setTextColor(Color.BLACK);
			ovx_text.setOnEditorActionListener(new OnEditorActionListener() {
				@Override
				public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
					if (!ovx_text.getText().toString().trim().equals("") && ovxView.isCallOn()) {
						/*
						 * We can use this to send data to other parties in the conference. 
						 * The first parameter is the message type which in this case is a link, 
						 * the second parameter contains the data.
						 * It is up to the developer to 
						 * display the data any way he wants.   
						 */
						ovxView.sendData("chat", ovx_text.getText().toString());
						chat_box.append("\n" + ovxView.getOvxUserName() + " : "
								+ ovx_text.getText().toString());
						ovx_text.setText("");
						focusOnText();
					}
					return true;
				}
			});
			
			callListener();
			
			ScrollView scroll_layout = (ScrollView) findViewById(R.id.scroll_layout);

			scroll_layout.setOnTouchListener(new View.OnTouchListener() {
				public boolean onTouch(View v, MotionEvent event) {
					// Log.d("INDUS", "PARENT TOUCH");
					findViewById(R.id.chat_text_box).getParent().requestDisallowInterceptTouchEvent(false);
					return false;
				}
			});

			
			chat_box.setOnTouchListener(new View.OnTouchListener() {
				public boolean onTouch(View v, MotionEvent event) {
					// Log.v("INDUS", "CHILD TOUCH");
					// Disallow the touch request for parent scroll on touch of child view
					v.getParent().requestDisallowInterceptTouchEvent(true);
					return false;
				}
			});
		} catch (OVXException e) {
			Toast toast = Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
		    
		    // change the position to show the message.
		    toast.setGravity(Gravity.CENTER|Gravity.CENTER, 0, 0);
		    toast.show();
		}
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// don't reload the current page when the orientation is changed
		Log.d("INDUS", "onConfigurationChanged() Called");
		super.onConfigurationChanged(newConfig);
		try {
			ovxView.setRemoteViewX(50);
			ovxView.setRemoteViewY(50);
		} catch (OVXException e) {
			e.printStackTrace();
		}
		
		/*
		 * Should be called to update the dimensions and position
		 * of the video view that had been changed after the call was started or 
		 * to resume the video stream if it had been paused while launching another activity.  
		 */
		ovxView.updateVideoOrientation();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		
		//exit the call before destroying the activity
		ovxView.exitCall();
		
		//and free the resources used by OVX context
		ovxView.unregister();		

		android.os.Process.killProcess(android.os.Process.myPid());
	}
	
	public void showDialog(String title, CharSequence[] items) {

		final CharSequence[] fitems = items;

		AlertDialog.Builder lmenu = new AlertDialog.Builder(this);

		final AlertDialog ad = lmenu.create();
		lmenu.setTitle(title);
		lmenu.setMessage(fitems[0]);
		lmenu.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				ad.dismiss();
			}
		});
		lmenu.show();
	}
	
	
	public void callListener() {
		chat_box = (TextView) findViewById(R.id.chat_text_box);

		/** call back listener to listen to call events  */
		ovxView.setCallListener(new OVXCallListener() {
			//invoked when the call has been disconnected by the user.
			@Override
			public void callEnded() {
				Log.d("INDUS", "Call Ended ");
				chat_box.setText("");
				chat_box.setHint("Welcome to openclove");
			}

			// invoked when the call fails due to some reasons.
			@Override
			public void callFailed() {
				chat_box.clearComposingText();
				chat_box.setHint("Welcome to openclove");
			}

			// invoked when the call has been established.
			@Override
			public void callStarted() {
				Log.d("INDUS", "Call Started");
			}

			/* Invoked when messages are sent from other parties in the conference and 
			*the server as notifications.
			*/
			@Override
			public void ovxReceivedData(String arg0) {
				Uri uri = Uri.parse("http://dummyserver.com?" + arg0);
				String type = uri.getQueryParameter("type");
				String data = uri.getQueryParameter("data");
				String sender = uri.getQueryParameter("sender");
				Log.d("INDUS", "Received message from ac_server:" + arg0);
				chat_box.setMovementMethod(new ScrollingMovementMethod());

				if (chat_box.getText().toString().equals("Welcome to openclove"))
					chat_box.setText(sender + " : " + data);
				else
					chat_box.append("\n" + sender + " : " + data);

				chat_box.setTextColor(Color.BLACK);
				focusOnText();
			}

			//invoked when the call has been terminated due to n/w issues.
			@Override
			public void callTerminated(String arg0) {
				chat_box.clearComposingText();
				chat_box.setHint("Welcome to openclove");
			}
			
			@Override 
			public void onNotificationEvent(String eventType,String data)
			{
				if(eventType.equals("broadcastUrl"))
				{
					Log.d("INDUS","notification data:"+data);
				}
			}

		});
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d("INDUS", "OnResume:" + this);
		/*
		 * Should be called to update the dimensions and position
		 * of the video view that had been changed after the call was started or 
		 * to resume the video stream if it had been paused while launching another activity.  
		 */
		if (ovxView.isCallOn())
			ovxView.updateVideoOrientation();
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.d("INDUS", "On Pause");

	}
	
	private void focusOnText() {
		// append the new string

		// find the amount we need to scroll. This works by
		// asking the TextView's internal layout for the position
		// of the final line and then subtracting the TextView's height
		final int scrollAmount = chat_box.getLayout().getLineTop(
				chat_box.getLineCount())
				- chat_box.getHeight();
		// if there is no need to scroll, scrollAmount will be <=0
		if (scrollAmount > 0)
			chat_box.scrollTo(0, scrollAmount);
		else
			chat_box.scrollTo(0, 0);
	}
}
