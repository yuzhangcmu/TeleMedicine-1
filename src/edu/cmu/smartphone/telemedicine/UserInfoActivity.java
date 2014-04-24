package edu.cmu.smartphone.telemedicine;

import org.json.JSONException;
import org.json.JSONObject;

import com.parse.ParsePush;

import android.app.Activity;
import android.content.Intent;
import android.view.ViewGroup;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class UserInfoActivity extends Activity {

	ImageButton userinfoAddFriendButton;
	private String username;
	private String fullname;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.userinfoview);
		
	    username = getIntent().getStringExtra("username");
	    fullname = getIntent().getStringExtra("fullname");
		
		
	    String[] mStrings = {"FullName: " + fullname, "User Name: " + username};
		
		ListView listview = (ListView)findViewById(R.id.userinfoListView);  
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,  
                android.R.layout.simple_list_item_1, mStrings){

                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View view =super.getView(position, convertView, parent);

                        TextView textView=(TextView) view.findViewById(android.R.id.text1);

                        /*YOUR CHOICE OF COLOR*/
                        textView.setTextColor(Color.BLACK);

                        return view;
                    }
        };
                
        listview.setAdapter(adapter);  
		
		userinfoAddFriendButton = (ImageButton)findViewById(R.id.userinfoAddFriendButton);
		userinfoAddFriendButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
			    String mess = LoginActivity.getCurrentUserID() + " request to add you as a friend.";
			    Notification notify = new Notification(UserInfoActivity.this);
			    
			    // send a notification to add the contact.
			    JSONObject obj=new JSONObject();
			    try {
                    obj.put("action","edu.cmu.smartphone.telemedicine.UPDATE_STATUS");
                    
                    // tell friend your user id.
                    obj.put("username",LoginActivity.getCurrentUserID());
                    obj.put("alert", mess);
                    obj.put("title", "Friend request.");
                } catch (JSONException e) {
                    e.printStackTrace();
                    
                    Toast toast = Toast.makeText(UserInfoActivity.this,
                            "Internal error!", Toast.LENGTH_LONG);
                          toast.setGravity(Gravity.CENTER, 0, 0);
                          LinearLayout toastView = (LinearLayout) toast.getView();
                          ImageView imageCodeProject = new ImageView(UserInfoActivity.this);
                          imageCodeProject.setImageResource(R.drawable.ic_action_accept);
                          toastView.addView(imageCodeProject, 0);
                          toast.show();
                    return;      
                }

                ParsePush push = new ParsePush();
                push.setChannel(username);
                push.setData(obj);
                push.sendInBackground();
			    
			    Toast toast = Toast.makeText(UserInfoActivity.this,
			            "Request sent success", Toast.LENGTH_LONG);
			          toast.setGravity(Gravity.CENTER, 0, 0);
			          LinearLayout toastView = (LinearLayout) toast.getView();
			          ImageView imageCodeProject = new ImageView(UserInfoActivity.this);
			          imageCodeProject.setImageResource(R.drawable.ic_action_accept);
			          toastView.addView(imageCodeProject, 0);
			          toast.show();
			          
			    Intent intent = new Intent(UserInfoActivity.this, AddActivity.class);
			    UserInfoActivity.this.startActivity(intent);      
			}
		});
		
	}

}
