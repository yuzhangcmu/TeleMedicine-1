package edu.cmu.smartphone.telemedicine;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class InfoActivity extends Activity {

	ImageButton textInfoButton;
	ImageButton audioInfoButton;
	ImageButton videoInfoButton;
	
	ImageButton menuInfoButton;
	
	public void contactMenu(View v) {
	    
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.infoview);
		
		textInfoButton = (ImageButton)findViewById(R.id.textInfoButton);
		audioInfoButton = (ImageButton)findViewById(R.id.audioInfoButton);
		videoInfoButton = (ImageButton)findViewById(R.id.videoInfoButton);
		
		menuInfoButton = (ImageButton)findViewById(R.id.menuInfoButton);
		
		String username = getIntent().getStringExtra("username");
        String fullname = getIntent().getStringExtra("fullname");
        String email = getIntent().getStringExtra("email");
        
        String[] mStrings = {"FullName: " + fullname, "User Name: " + username,
                "Email: " + email};
        
        ListView listview = (ListView)findViewById(R.id.infoListView);  
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
		
		// added by yu zhang , for deleting a contact.
		menuInfoButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                
            }
        });

		
		
	}

}
