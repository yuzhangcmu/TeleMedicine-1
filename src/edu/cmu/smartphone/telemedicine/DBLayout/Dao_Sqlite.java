package edu.cmu.smartphone.telemedicine.DBLayout;

import java.util.Formatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import edu.cmu.smartphone.telemedicine.ContactActivity;
import edu.cmu.smartphone.telemedicine.InfoActivity;
import edu.cmu.smartphone.telemedicine.LoginActivity;
import edu.cmu.smartphone.telemedicine.UserInfoActivity;
import edu.cmu.smartphone.telemedicine.entities.Contact;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

// reference: http://www.androidhive.info/2013/09/android-sqlite-database-with-multiple-tables/
public class Dao_Sqlite extends SQLiteOpenHelper {
    // Logcat tag
    private static final String LOG = "DatabaseHelper";
 
    // Database Version
    private static final int DATABASE_VERSION = 1;
 
    // Database Name
    private static final String DATABASE_NAME = "dbTeleMedicine";

    private static final String TABLE_CONTACT = "TableContact";
    private static final String TABLE_CHATRECORD = "TableChatRecord";
    private static final String TABLE_PATIENT = "TablePatient";
    private static final String TABLE_DOCTOR = "TableDoctor";
    private static final String TABLE_RECENTCHAT = "TableRecentChat";
    
    // contact Table - column names
    private static final String KEY_TYPE = "Type";
    private static final String KEY_EMAIL = "Email";
    private static final String KEY_PHONE = "Phone";
    private static final String KEY_NAME = "Name";
    private static final String KEY_USERID = "UserID";
    private static final String KEY_INTRO = "Intro";
    private static final String KEY_HEADPORTRAIT = "headportrait";
    
    // parse.com database.
    public static final String KEY_FULLNAME = "fullname";
    public static final String KEY_FRIEND_USER_NAME_CLOUD = "friend_username";
    
    public static final String KEY_USERTABLE = "User"; // this table stored all the users.
    public static final String KEY_USERNAME_CLOUD = "username"; // the keyword of "username"
    public static final String KEY_FULLNAME_CLOUD = "fullname"; // the keyword of "username"
    
    private Context context;
    
    public Dao_Sqlite(Context context, String name, CursorFactory factory,
            int version) {
        super(context, name, factory, version);
        this.context = context;
        
        SQLiteDatabase db = this.getWritableDatabase();
        onCreate(db);
    }
    
    public Dao_Sqlite(Context context) {
        super(context, LoginActivity.getCurrentUserID(), null, DATABASE_VERSION);
        this.context = context;
    }
    
    public LinkedList<Contact> getContactList() {
        LinkedList<Contact> contactList = new LinkedList<Contact>();
        
        SQLiteDatabase myDB = this.getWritableDatabase();
        
        String sql = "SELECT * FROM " + TABLE_CONTACT;
        Log.e(LOG, sql);
        
        Cursor c = myDB.rawQuery(sql, null);
        
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Contact contact = new Contact(c.getString(c.getColumnIndex("Name")), 
                        c.getString(c.getColumnIndex("UserID")));
                
                // adding to contact list.
                contactList.add(contact);
            } while (c.moveToNext());
        }
        
        return contactList;
    }
    
    public Cursor addContactToArrayList(List<Contact> contacts) {
        SQLiteDatabase myDB = this.getWritableDatabase();
        
        String sql = "SELECT * FROM " + TABLE_CONTACT;
        Log.e(LOG, sql);
        
        Cursor c = myDB.rawQuery(sql, null);
        
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                String name = c.getString(c.getColumnIndex("Name"));
                String userID = c.getString(c.getColumnIndex("UserID"));
                
                Contact contact = new Contact(name, userID);
                
                contact.setName(name);
                contact.setSortKey(name);
                
                // adding to contact list.
                contacts.add(contact);
            } while (c.moveToNext());
        }
        
        return c;
    }
    
    // load a user's data to the database.
    public void loadDataFromCloud(String userName) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(userName);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> contactList, ParseException e) {
                if (e == null) {
                    Log.d("contacts", "Retrieved " + contactList.size() + " contacts");
                    
                    for (ParseObject o: contactList) {
                        //String name = o.getString(KEY_FULLNAME);
                        String userID = o.getString(KEY_FRIEND_USER_NAME_CLOUD);
                        
                        // need to change to name.
                        Contact contact = new Contact(userID, userID);
                        contact.setSortKey(userID);
                        
                        addContact(contact);
                    }
                } else {
                    Log.d("contacts", "Error: " + e.getMessage());
                }
            }
            
        });
        
    }
    
    // search the cloud to get if the contact exit.    
    public int searchContactCloud(String key) {
        // open the user table.
        ParseQuery<ParseUser> friendQuery = ParseUser.getQuery();
        
        friendQuery.whereEqualTo(KEY_USERNAME_CLOUD, key);
        
        friendQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> contactList, ParseException e) {
                if (e == null) {
                    Log.d("contacts", "Retrieved " + contactList.size() + " contacts");
                    
                    if (contactList == null || contactList.size() == 0) {
                        if (context == null) {
                            return;
                        }
                        
                        // show error.
                        Toast toast = Toast.makeText(context, "No such user!", Toast.LENGTH_LONG);
                         
                        // change the position to show the message.
                        toast.setGravity(Gravity.CENTER|Gravity.LEFT, 0, 0);
                        toast.show();
                        return;
                    }
                    
                    String username = contactList.get(0).getUsername();
                    String fullname = contactList.get(0).getString(KEY_FULLNAME_CLOUD);
                    
                    
                    Intent intent = new Intent(context, UserInfoActivity.class);
                    intent.putExtra("username", username);
                    intent.putExtra("fullname", fullname);
                    
                    context.startActivity(intent);
                    
                } else {
                    Log.d("contacts", "Error: " + e.getMessage());
                    
                    // show error.
                    Toast toast = Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG);
                     
                    // change the position to show the message.
                    toast.setGravity(Gravity.CENTER|Gravity.LEFT, 0, 0);
                    toast.show();
                    
                }
            }
            
        });
        
        return -1;
    }
    
    public int updateContact(Contact contact) {
        SQLiteDatabase myDB = this.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        
        values.put(KEY_TYPE, contact.getType());
        values.put(KEY_EMAIL, contact.getEmail());
        values.put(KEY_PHONE, contact.getPhone());
        values.put(KEY_NAME, contact.getName());
        values.put(KEY_INTRO, contact.getIntro());
        values.put(KEY_HEADPORTRAIT, contact.getHeadPortrait());
     
        // updating row
        return myDB.update(TABLE_CONTACT, values, KEY_USERID + " = ?",
                new String[] { contact.getUserID() });
    }
    
    
    
    public void addContact(Contact contact) { 
        String tableContact = TABLE_CONTACT;
 
        try {
            // Get the database if database is not exists create new database 
            // Database name is " test " 
            SQLiteDatabase myDB = this.getWritableDatabase();
            
            StringBuilder sb = new StringBuilder();
            
            // Send all output to the Appendable object sb
            Formatter formatter = new Formatter(sb, Locale.US);
            
            // Explicit argument indices may be used to re-order output.
//            formatter.format("REPLACE INTO %s (Type, Email, Phone, Name, UserID, Intro,"
//                    + "HeadPortrait, Age, Password) "
//                    + "VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%d', '%s');",
//                    tableContact,
//                    contact.getType(),
//                    contact.getEmail(),
//                    contact.getPhone(),
//                    contact.getName(),
//                    contact.getUserID(),
//                    contact.getIntro(),
//                    contact.getHeadPortrait(),
//                    contact.getAge(),
//                    contact.getPassword()
//                    );
            
            formatter.format("REPLACE INTO %s (Name, UserID) VALUES ('%s', '%s');",
                    tableContact,
                    contact.getName(),
                    contact.getUserID()
                    );
            
            formatter.close();
            String sql = sb.toString();
            myDB.execSQL(sql);
            Log.d(LOG, sql);
 
        } catch (Exception e) {
            Log.e("Error", "Error", e);
 
        }
    }
    
    public void deleteContact(String UserID) { 
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACT, KEY_USERID + " = ?",
                new String[] { UserID });                
    }

    @Override
    public void onCreate(SQLiteDatabase myDB) {
        try {
            // Create contact table
            /*
            myDB.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_CONTACT
                    + " (UserID VARCHAR(50), Type VARCHAR(50), Email VARCHAR(255), Phone VARCHAR(30),"
                    + "Nation VARCHAR(255), Province VARCHAR(255),"
                    + "City VARCHAR(255), Name VARCHAR(255), "
                    + "Intro VARCHAR(300), HeadPortrait VARCHAR(255), "
                    + "Age integer, Password VARCHAR(255), PRIMARY KEY ( UserID ));");
                    */
            String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_CONTACT
                    + " (" + KEY_USERID + " VARCHAR PRIMARY KEY, Name VARCHAR);";
            myDB.execSQL(sql);
            
//            // create chatRecord table.
//            myDB.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_CHATRECORD
//                    + " (Id INTEGER not NULL AUTO_INCREMENT, "
//                    + "Message varchar(max), Status bit, Time datetime, UserID integer,"
//                    + "Direction bit,"
//                    + "MessageTypeID integer, "
//                    + "PRIMARY KEY ( Id )), FOREIGN KEY (UserID) REFERENCES " 
//                    + TABLE_CONTACT + "(id) ON DELETE CASCADE;");
//            
//            // create recentChat table.
//            myDB.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_RECENTCHAT
//                    + " (Id INTEGER not NULL AUTO_INCREMENT, "
//                    + "userID integer, Time datetime"
//                    + "PRIMARY KEY ( Id )), FOREIGN KEY (userID) REFERENCES " 
//                    + TABLE_CONTACT + "(id) ON DELETE CASCADE;");
//            
//            // create patient table.
//            myDB.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_PATIENT
//                    + " (Id INTEGER not NULL AUTO_INCREMENT, "
//                    + "UserID integer, Symptom varchar(300),"
//                    + "PRIMARY KEY ( Id ));");
//            
//            // create doctor table.
//            myDB.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_DOCTOR
//                    + " (Id INTEGER not NULL AUTO_INCREMENT, "
//                    + "UserID integer, Department varchar(255), Title varchar(255)"
//                    + "PRIMARY KEY ( Id ));");
            
 
        } catch (Exception e) {
            Log.e("Error", "Error", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACT);
        
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHATRECORD);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECENTCHAT);
        
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PATIENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DOCTOR);
 
        // create new tables
        onCreate(db);
    }
}
