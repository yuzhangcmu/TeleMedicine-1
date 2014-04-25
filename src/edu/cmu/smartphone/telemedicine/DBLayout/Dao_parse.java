package edu.cmu.smartphone.telemedicine.DBLayout;

import android.content.Context;

import com.parse.ParseObject;

import edu.cmu.smartphone.telemedicine.LoginActivity;
import edu.cmu.smartphone.telemedicine.entities.Contact;

public class Dao_parse {
    private Context context;
    
    public Dao_parse(Context context) {
        this.context = context;
    }
    
    // add a friend to cloud service.
    public void addContactCloud(Contact contact) { 
        // save a new friend to the cloud.
        ParseObject newFriend = new ParseObject(Contact.getCurrentUserID());
        newFriend.put(Dao_Sqlite.KEY_FRIEND_USER_NAME_CLOUD, contact.getUserID());   
        newFriend.saveInBackground();
        
        // also add current to that user's contact list.
        newFriend = new ParseObject(contact.getUserID());
        newFriend.put(Dao_Sqlite.KEY_FRIEND_USER_NAME_CLOUD, Contact.getCurrentUserID());   
        newFriend.saveInBackground();
    }
    
    public String getFullNameFromUserID(String userID) {
        return null;
    }
}
