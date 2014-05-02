/***
 * Package Name  :com.example.ovxexample
 * Version Name  :1.2.3
 * Date          :20140319 
 * Description   :Activity used to show cast the OVX Features.
  
 *****/

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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.openclove.ovx.OVXCallListener;
import com.openclove.ovx.OVXException;
import com.openclove.ovx.OVXView;
import com.parse.ParseUser;

import edu.cmu.smartphone.telemedicine.ws.remote.Notification;

public class VideoActivity extends Activity {

	private static final String tag = "VideoActivity";
	private OVXView ovxView;
//	protected RelativeLayout media_control;
	private Dialog dialog;
	private EditText ovx_text;
	protected TextView chat_box;
	private String groupid;
	private TextView text_gid;
//	private OVXChat currentActivity;
//	private String gid;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.videoview);
		
		final String currentUserName = ParseUser.getCurrentUser().getUsername();
		final String caller_username = getIntent().getStringExtra("caller_username");
		final String callee_username = getIntent().getStringExtra("callee_username");
		final String callee_fullname = getIntent().getStringExtra("fullname");
		final String callee_email = getIntent().getStringExtra("email");
		
		Log.d(tag, "caller_username:" + caller_username);
		Log.d(tag, "callee_username:" + callee_username);
		
		// Comments provided for ovx sdk code
		Log.d("INDUS", "onCreate");

//		currentActivity = this;

		// Access the Shared Instance of the OVXView
		ovxView = OVXView.getOVXContext(this);

		try {
			// To get UID(User ID) for the device.
			String ovxuserId = Secure.getString(this.getContentResolver(), Secure.ANDROID_ID);

			/*We set the OVX User ID as the device id since it is unique. You can use any logic to set 
			*the userId as long as it distinguishes your device from the other devices involved in the conference.    
			*/
			ovxView.setOvxUserId(ovxuserId);

			/*Here we set the OVX Group Id as a unique identifier. Users who initiate call with 
			 * the same group id will end up in a video conference. You can use own logic to 
			 * share this group id, for example via links as invite to another user to join conference.    
			 */
//			ovxView.setOvxGroupId(UUID.randomUUID().toString().replaceAll("-", ""));
			
			if(currentUserName.equals(callee_username)) {
				ovxView.setOvxGroupId(caller_username + "-" + callee_username);
			} else {
				ovxView.setOvxGroupId(currentUserName + "-" + callee_username);
			}
			
			

			/* This refers to the Theme of video frames, background, etc. of
			* the video room.
			* Contact support@openclove.com for more details on the Themes
			* available.
			*/
			ovxView.setOvxMood("1");

			/* Here you can set whether to show the OVX menu when the user taps
			* the video view. OVX menu contains call control features,like audio mute,video mute etc;
			* it also allows you to minimize or maximize the video view.  
			*/
			ovxView.setShowOVXMenuOnTap(true);
			
			/*Remote gesture api is true by default, setting it to false 
			 * will disable the pinch/zoom and drag event of the video view 
			 * and also prevents you from maximizing the video on double tap, 
			 * hence setting it to false will lock the video view in a fixed 
			 * position on the screen.  
			 */
			ovxView.enableRemoteGesture(true);
			
			/* You can set the x and y position of the video view which is relative to 
			*  the top,left position of the screen.
			*/ 
			ovxView.setRemoteViewX(100);
			ovxView.setRemoteViewY(100);

			/*We use a TextView for displaying the current GroupId set through the sdk.
			 */
			text_gid = (TextView) findViewById(R.id.app);
			text_gid.setText("Current Group ID : " + ovxView.getOvxGroupId());

			/* We can launch the OVX menu through the sdk by 
			 * binding it to a buttons click event. We had earlier disabled 
			 * the api to launch OVX menu on click of the video view to show case this feature.  
			 */
			Button ovx_call_menu = (Button) findViewById(R.id.call_menu);
			ovx_call_menu.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					ovxView.showOVXMenu(); // Launches the OVX menu on click of the call_menu button
				}
			});

			
			// Bind the the call event to the click of start_call button  
			Button ovx_call_button = (Button) findViewById(R.id.start_call);
			ovx_call_button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (!ovxView.isCallOn()) //Checks if the call is on 
						try {
							ovxView.call(); // Initiates call and starts a session with the specified OVX group id and other parameters set earlier.
							// Send push notification to the other user to receive the call
							 Notification notify = new Notification(VideoActivity.this);
							 notify.sendInComingCallPush(currentUserName, callee_username, "msg1");
						} catch (OVXException e) {
							e.printStackTrace();
						}
					else { // If call is already started
						CharSequence[] ch = { "Call is already on" };
						showDialog("Warning", ch);
					}
				}
			});

			// Bind the end call event to the click of end_call button
			Button ovx_end_call = (Button) findViewById(R.id.end_call);
			ovx_end_call.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (ovxView.isCallOn()) { // 
						ovxView.exitCall(); // ends the existing call and removes the user from the live conference.
					} 
				}
			});

			/* On click of switch_layer button a dialog is launched with an option to focus on a particular 
			 * participant in the conference. 
			 */
			Button ovx_switch = (Button) findViewById(R.id.switch_layer);
			ovx_switch.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (ovxView.isCallOn()) {
						loadSwitchDialog(); // launches the dialog with switch layer options.  
						
					} else // shows a warning dialog if user tries to call
					{
						CharSequence[] ch = { "Start or join a call first" };
						showDialog("Warning", ch);
					}
				}
			});

			// Dialog for Setting Group Id..
			Button setgrpid = (Button) findViewById(R.id.setgrpid);
			setgrpid.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					sharelayout();  //Enter the GroupId which u want to set in this dialog..
				}
			});

			// Setting Audio Stream to other parties in the conference based on flag..
			Button audio_mute = (Button) findViewById(R.id.audio_mute);
			audio_mute.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (ovxView.isCallOn())
						ovxView.setAudioMute(!ovxView.isAudioMuteOn()); // flag based on if muted ornot.
					else {
						CharSequence[] ch = { "Start or join a call first" };
						showDialog("Warning", ch);
					}
				}
			});


			Button switchCamera = (Button) findViewById(R.id.switch_camera);
			switchCamera.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					/*
					 * This allows you to switch between the back and front camera and send video to the other
					 *  participants in the video conference captured by either your back or front camera. 
					 *  Switch is disabled in case device has single camera 
					 */
					if (ovxView.isCallOn())
						ovxView.switchCamera();
					else {
						CharSequence[] ch = { "Start or join a call first" };
						showDialog("Warning", ch);
					}
				}
			});

			

			Button video_mute = (Button) findViewById(R.id.video_mute);
			video_mute.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					/* checks if the video is muted and will mute if not muted or will undo mute if muted */
					if (ovxView.isCallOn())
						ovxView.setVideoMute(!ovxView.isVideoMuteOn());
					else {
						CharSequence[] ch = { "Start or join a call first" };
						showDialog("Warning", ch);
					}
				}
			});

			
			
			
			Button update_orient = (Button) findViewById(R.id.orientation);
			update_orient.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					/*
					 * Should be called to update the dimensions and position
					 * of the video view that had been changed after the call was started or 
					 * to resume the video stream if it had been paused while launching another activity.  
					 */
					if (ovxView.isCallOn())
						ovxView.updateVideoOrientation();
					else {
						CharSequence[] ch = { "Start or join a call first" };
						showDialog("Warning", ch);
					}
				}
			});

			 
			/*
			 * This feature allows you to dial an msisdn and 
			 * add it to the existing conference.
			 * 
			 */
			Button addFriends = (Button) findViewById(R.id.add_friends);
			addFriends.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (ovxView.isCallOn())
						/*Launches dialog to enter the msisdn.
						 *  Look at method definition for further description  
					     */ 
						dialout_dialog();		
					else {
						CharSequence[] ch = { "Start or join a call first" };
						showDialog("Warning", ch);
					}
				}
			});

			// Binding send message events to send_image button
			Button send_image = (Button) findViewById(R.id.send_image);
			send_image.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (ovxView.isCallOn()) {
						/*
						 * we can use this to send data to other parties in the conference. 
						 * The first parameter is the message type which in this case is an image,
						 * the second parameter contains the data.
						 * It is up to the developer to 
						 * render the link as an image on a view.   
						 */
						ovxView.sendData("image", "http://zkychat.com/img/openclove-color.png");
						chat_box.append("\n" + ovxView.getOvxUserName() + " : "
								+ "http://zkychat.com/img/openclove-color.png");
						// Function for auto scrolling of text in TextView..
						focusOnText();
					} else {
						CharSequence[] ch = { "Start or join a call first" };
						showDialog("Warning", ch);
					}
				}
			});

			// For sharing URL
			Button send_link = (Button) findViewById(R.id.send_link);
			send_link.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (ovxView.isCallOn()) {
						/*
						 * We can use this to send data to other parties in the conference. 
						 * The first parameter is the message type which in this case is a link,
						 * the second parameter contains the data.
						 * It is up to the developer to 
						 * display the link any way he wants.   
						 */
						ovxView.sendData("link", "http://www.google.com");
						// Appending link to the chat_box textView..
						chat_box.append("\n" + ovxView.getOvxUserName() + " : " + "http://www.google.com");
						focusOnText();
					} else {
						CharSequence[] ch = { "Start or join a call first" };
						showDialog("Warning", ch);
					}
				}
			});

			// When performing action on EditText..
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

			
			/* This method contains implementation of 
			 * OVX call listeners. Look into the method
			 * definition for further description
			 * 
			 */
			callListener();

			
			/*Once call is started the scroll event will not take place when user tries to scroll through the 
			*text box since it has its own custom scroll. Users will have to scroll through the sides(regions other than the chat text box)
			*/
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
			e.printStackTrace();
		}

	}

	// Dialog for input of msisdn 
	protected void dialout_dialog() {
//		LayoutInflater factory = LayoutInflater.from(this);
//		final View entry = factory.inflate(R.layout.shr, null);
//		final EditText edit = (EditText) entry.findViewById(R.id.edt);
//
//		edit.setText("", TextView.BufferType.EDITABLE);
//
//		AlertDialog.Builder lmenu = new AlertDialog.Builder(this);
//
//		lmenu.setTitle("Telephone Number:").setView(entry);
//
//		lmenu.setPositiveButton("Dial-Out",
//				new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog, int whichButton) {
//						String msisdn = edit.getText().toString().trim();
//
//						if (msisdn != null && !msisdn.equals("")) {
//							try {
//								// Look at method definition for further description
//								dialout(msisdn);
//								Log.d("INDUS", "msisdn number::" + msisdn);
//							} catch (Exception e) {
//								e.printStackTrace();
//							}
//						} else {
//							CharSequence[] ch = { "Please Enter the correct number to make call" };
//							showDialog("Warning", ch);
//							Log.d("INDUS", "msisdn number::" + msisdn);
//						}
//					}
//				}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog, int whichButton) {
//						dialog.cancel();
//					}
//				});
//		lmenu.show();
	}

	protected void dialout(String msisdn) {
		Log.d("INDUS", "Calling  addOtherUserToGroupChat :" + msisdn);
		try {
			//checks if input string contains numbers else warning is displayed 
			 Double.parseDouble(msisdn); 
			// dial an msisdn and add to the existing video conference session.
			 ovxView.addOtherUserToGroupChat("voice", "TEL", msisdn);
		} 
		 catch (NumberFormatException e) {
			  // not an integer!
			CharSequence[] ch = { "Please Enter the correct number to make call" };
			showDialog("Warning", ch);
			Log.d("INDUS", "msisdn number::" + msisdn);
		}
		catch (OVXException e) {
			e.printStackTrace();
		}
	}

	// An input dialog that allows you to input string to set OVX group id of your choice. 
	protected void sharelayout() {
//		LayoutInflater factory = LayoutInflater.from(this);
//		final View entry = factory.inflate(R.layout.shr, null);
//		final EditText edit = (EditText) entry.findViewById(R.id.edt);
//
//		edit.setText("", TextView.BufferType.EDITABLE);
//
//		AlertDialog.Builder lmenu = new AlertDialog.Builder(this);
//
//		lmenu.setTitle("Enter the GroupId :").setView(entry);
//		lmenu.setPositiveButton("Set", new DialogInterface.OnClickListener() {
//
//			public void onClick(DialogInterface dialog, int whichButton) {
//				groupid = edit.getText().toString().trim();
//				if (groupid != null || !groupid.trim().equals("")) {
//					try {
//						// Sets the user input string as OVX group id. 
//						ovxView.setOvxGroupId(groupid); 
//					} catch (OVXException e) {
//						e.printStackTrace();
//					}
//					text_gid.setText("Current Group ID : " + ovxView.getOvxGroupId());
//				}
//			}
//		}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//			public void onClick(DialogInterface dialog, int whichButton) {
//				dialog.cancel();
//			}
//		});
//		lmenu.show();
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

		// Don't kill process when onDestroy
//		android.os.Process.killProcess(android.os.Process.myPid());
	}

	// generic dialog used to display messages

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

	/* Opens the dialog with buttons containing composite layer and layers numbered 1-9. These correspond 
	*to the users in the conference, click of the layered buttons will focus on a particular participant in the conference.
	*Click of the composite layer button will show all the participants in the conference.   
	*/
	private void loadSwitchDialog() {
//		dialog = new Dialog(this);
//		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//		dialog.setContentView(R.layout.switch_dialog);
//		dialog.setCancelable(false);
//		dialog.setCanceledOnTouchOutside(true);
//
//		Button composite = (Button) dialog.findViewById(R.id.composite_layer);
//		composite.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				if (ovxView.isCallOn())
//					try {
//						/* Switches to composite Layer which contains layers of the all participants in the conference,
//						 * after switching the the video view will stream the video feed of all participants.
//						 */
//						ovxView.switchLayer("0"); 
//					} catch (OVXException e) {
//						e.printStackTrace();
//					}
//				dialog.dismiss();
//			}
//		});
//
//		Button layer1 = (Button) dialog.findViewById(R.id.layer1);
//		layer1.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				if (ovxView.isCallOn())
//					try {
//						/*Switches to the first participant of the video conference and 
//						after switching the video view will stream the video feed of only first participant.  
//						*/
//						ovxView.switchLayer("1");
//					} catch (OVXException e) {
//						e.printStackTrace();
//					}
//				dialog.dismiss();
//			}
//		});
//
//		Button layer2 = (Button) dialog.findViewById(R.id.layer2);
//		layer2.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				if (ovxView.isCallOn())
//					try {
//						/*Switches to the second participant of the video conference and 
//						after switching the video view will stream the video feed of only the second participant.  
//						*/
//						ovxView.switchLayer("2");
//					} catch (OVXException e) {
//						e.printStackTrace();
//					}
//				dialog.dismiss();
//			}
//		});
//
//		Button layer3 = (Button) dialog.findViewById(R.id.layer3);
//		layer3.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				if (ovxView.isCallOn())
//					try {
//						/*Switches to the third participant of the video conference and 
//						after switching the video view will stream the video feed of only the third participant.  
//						*/
//						ovxView.switchLayer("3");
//					} catch (OVXException e) {
//						e.printStackTrace();
//					}
//				dialog.dismiss();
//			}
//		});
//
//		Button layer4 = (Button) dialog.findViewById(R.id.layer4);
//		layer4.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				if (ovxView.isCallOn())
//					try {
//						/*Switches to the fourth participant of the video conference and 
//						after switching the video view will stream the video feed of only the fourth participant.  
//						*/
//						ovxView.switchLayer("4");
//					} catch (OVXException e) {
//						e.printStackTrace();
//					}
//				dialog.dismiss();
//			}
//		});
//
//		Button layer5 = (Button) dialog.findViewById(R.id.layer5);
//		layer5.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				if (ovxView.isCallOn())
//					try {
//						/*Switches to the fifth participant of the video conference and 
//						after switching the video view will stream the video feed of only the fifth participant.  
//						*/
//						ovxView.switchLayer("5");
//					} catch (OVXException e) {
//						e.printStackTrace();
//					}
//				dialog.dismiss();
//			}
//		});
//
//		Button layer6 = (Button) dialog.findViewById(R.id.layer6);
//		layer6.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				if (ovxView.isCallOn())
//					try {
//						/*Switches to the sixth participant of the video conference and 
//						after switching the video view will stream the video feed of only the sixth participant.  
//						*/
//						ovxView.switchLayer("6");
//					} catch (OVXException e) {
//						e.printStackTrace();
//					}
//				dialog.dismiss();
//			}
//		});
//
//		Button layer7 = (Button) dialog.findViewById(R.id.layer7);
//		layer7.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				if (ovxView.isCallOn())
//					try {
//						/*Switches to the seventh participant of the video conference and 
//						after switching the video view will stream the video feed of only the seventh participant.  
//						*/
//						ovxView.switchLayer("7");
//					} catch (OVXException e) {
//						e.printStackTrace();
//					}
//				dialog.dismiss();
//			}
//		});
//
//		Button layer8 = (Button) dialog.findViewById(R.id.layer8);
//		layer8.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				if (ovxView.isCallOn())
//					try {
//						/*Switches to the eigth participant of the video conference and 
//						after switching the video view will stream the video feed of only the eigth participant.  
//						*/
//						ovxView.switchLayer("8");
//					} catch (OVXException e) {
//						e.printStackTrace();
//					}
//				dialog.dismiss();
//			}
//		});
//
//		Button layer9 = (Button) dialog.findViewById(R.id.layer9);
//		layer9.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				if (ovxView.isCallOn())
//					try {
//						/*Switches to the ninth participant of the video conference and 
//						after switching the video view will stream the video feed of only the ninth participant.  
//						*/
//						ovxView.switchLayer("9");
//					} catch (OVXException e) {
//						e.printStackTrace();
//					}
//				dialog.dismiss();
//			}
//		});
//
//		// To Cancel the switch Dialog..
//		View cancelBtn = dialog.findViewById(R.id.cancel);
//		cancelBtn.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				dialog.dismiss();
//			}
//		});
//
//		dialog.show();
	}

	
	 /* 
	 * We can listen to call related events like call started , call ended , call failed and perform appropriate actions 
	 * in these callback functions, we can also receive messages sent from the other parties in the conference using the call listener. 
	 */
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
