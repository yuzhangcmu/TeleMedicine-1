package edu.cmu.smartphone.telemedicine;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.PushService;

import android.app.Application;

public class TeleMedicineApplication extends com.openclove.ovx.OVX {

	@Override
	public void onCreate() {
		super.onCreate();
		
		// Add your initialization code here
		Parse.initialize(this, "L8Xc3uTHMYHbwGyhwVyi5L9nUJtRtLvYIHGOJa0V", "KlrarH9V0OWEkCr0FjIBpqiKaVfs6o9supWTt2wc");

		ParseUser.enableAutomaticUser();
		ParseACL defaultACL = new ParseACL();
	    
		// If you would like all objects to be private by default, remove this line.
		defaultACL.setPublicReadAccess(true);
		
		ParseACL.setDefaultACL(defaultACL, true);
		
		// added by yu zhang begin for notification function. begin.
		// assign the contact activity to deal with all the notifications.
		PushService.setDefaultPushCallback(this, ContactActivity.class);
		ParseInstallation.getCurrentInstallation().saveInBackground();
		// added by yu zhang begin for notification function. end
	}

}
