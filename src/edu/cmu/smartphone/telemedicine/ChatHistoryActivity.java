package edu.cmu.smartphone.telemedicine;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class ChatHistoryActivity extends Activity {

    EditText chatInputEditText;
    Button chatSendButton;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_history_view);
        
    }
}
