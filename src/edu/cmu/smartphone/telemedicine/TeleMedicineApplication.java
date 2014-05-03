package edu.cmu.smartphone.telemedicine;

import android.database.sqlite.SQLiteDatabase;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.PushService;

import edu.cmu.smartphone.telemedicine.DBLayout.Dao_Sqlite;
import edu.cmu.smartphone.telemedicine.DBLayout.DatabaseManager;
import edu.cmu.smartphone.telemedicine.constants.Constant;

public class TeleMedicineApplication extends com.openclove.ovx.OVX {

	@Override
	public void onCreate() {
		super.onCreate();
		
		// Add your initialization code here
		Parse.initialize(this, Constant.PARSE_ID, Constant.PARSE_KEY);

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

		 DatabaseManager.initializeInstance(new Dao_Sqlite(getApplicationContext()));
	}

}
