package edu.cmu.smartphone.telemedicine;

import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import edu.cmu.smartphone.telemedicine.DBLayout.Dao_Sqlite;
import edu.cmu.smartphone.telemedicine.DBLayout.Dao_parse;
import edu.cmu.smartphone.telemedicine.entities.Contact;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.app.AlertDialog;  
import android.content.DialogInterface;  


public class MyCustomReceiver extends BroadcastReceiver {
    private static final String TAG = "MyCustomReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        //showAlert(context);
        
        try {
            String action = intent.getAction();
            String channel = intent.getExtras().getString("com.parse.Channel");
            JSONObject json = new JSONObject(intent.getExtras().getString(
                    "com.parse.Data"));

            Log.d(TAG, "got action " + action + " on channel " + channel
                    + " with:");
            Iterator itr = json.keys();
            while (itr.hasNext()) {
                String key = (String) itr.next();
                Log.d(TAG, "..." + key + " => " + json.getString(key));
                
                if (key.equals("username")) {
                    String fromUsername = json.getString(key);
                    saveRequestNotification(fromUsername);
                    
                    addContactToLocalAndCloud(context, json.getString(key));
                }
                
            }
        } catch (JSONException e) {
            Log.d(TAG, "JSONException: " + e.getMessage());
        }
    }
    
    // save the notification message.
    private void saveRequestNotification(String fromUsername) {
        // save the notification to the local database.
    }
    
    private void addContactToLocalAndCloud(Context context, String userID) {
        // add a new friend.
        Contact contact = new Contact(userID, userID);
        contact.setSortKey(userID);
        
        // add the new friend to local database. 
        Dao_Sqlite dao = new Dao_Sqlite(context);
        dao.addContact(contact);
        
        // also add the new friend to the cloud service.
        Dao_parse daoparse = new Dao_parse(context);
        daoparse.addContactCloud(contact);
    }

    // we can not show alert here. need to move to "history"
    private void showAlert(Context context) {
        new AlertDialog.Builder(new LoginActivity())
                .setIcon(R.drawable.ic_launcher)
                .// the icon
                setTitle("Delete?")
                .// title
                setMessage("this is a AlertDialog!")
                .// info
                setPositiveButton("Yes", new DialogInterface.OnClickListener() {// ok
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                // yes to do
                            }
                        })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {// cancel
                            @Override
                            public void onClick(DialogInterface arg1, int witch) {
                                // no to do
                            }
                        })
                .setNeutralButton("More",
                        new DialogInterface.OnClickListener() {// normal button
                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which) {
                                // see more
                            }
                        }).show();

    }

}