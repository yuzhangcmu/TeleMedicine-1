package edu.cmu.smartphone.telemedicine;

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
			    
			    // send a notification to add the contact.
			    ParsePush push = new ParsePush();
			    push.setChannel(username);
			    push.setMessage("User " + LoginActivity.getCurrentUserID() + " request to add you as friend.");
			    push.sendInBackground();
			    
			    Toast toast = Toast.makeText(getApplicationContext(),
			            "Request sent success", Toast.LENGTH_LONG);
			          toast.setGravity(Gravity.CENTER, 0, 0);
			          LinearLayout toastView = (LinearLayout) toast.getView();
			          ImageView imageCodeProject = new ImageView(getApplicationContext());
			          imageCodeProject.setImageResource(R.drawable.ic_action_accept);
			          toastView.addView(imageCodeProject, 0);
			          toast.show();
			          
			    Intent intent = new Intent(UserInfoActivity.this, AddActivity.class);
			    UserInfoActivity.this.startActivity(intent);      
			}
		});
		
	}

}
