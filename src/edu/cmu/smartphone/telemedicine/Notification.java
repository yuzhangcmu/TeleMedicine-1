package edu.cmu.smartphone.telemedicine;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.parse.ParsePush;

import edu.cmu.smartphone.telemedicine.entities.Contact;

public class Notification {
    private Context contest;
    
    public Notification (Context contest) {
        this.contest = contest;
    }
    
    public void sendNotification(String userID, String mess, int intMessType) {
        // send a notification to add the contact.
        JSONObject obj=new JSONObject();
        try {
            obj.put("action","edu.cmu.smartphone.telemedicine.UPDATE_STATUS");
            
            // tell friend your user id.
            obj.put("username",Contact.getCurrentUserID());
            obj.put("alert", mess);
            obj.put("messType", intMessType);
            
            if (intMessType == 0) {
                obj.put("title", "Friend request.");
            } else if (intMessType == 1) {
                obj.put("title", "Friend request confirm.");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            
            Toast toast = Toast.makeText(contest,
                    "Internal error!", Toast.LENGTH_LONG);
                  toast.setGravity(Gravity.CENTER, 0, 0);
                  LinearLayout toastView = (LinearLayout) toast.getView();
                  ImageView imageCodeProject = new ImageView(contest);
                  imageCodeProject.setImageResource(R.drawable.ic_action_accept);
                  toastView.addView(imageCodeProject, 0);
                  toast.show();
            return;      
        }

        ParsePush push = new ParsePush();
        push.setChannel(userID);
        push.setData(obj);
        push.sendInBackground();
    }

}
