package edu.cmu.smartphone.telemedicine.DBLayout;

import java.util.ArrayList;
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
import edu.cmu.smartphone.telemedicine.entities.ChatRecord;
import edu.cmu.smartphone.telemedicine.entities.Contact;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

// reference: http://www.androidhive.info/2013/09/android-sqlite-database-with-multiple-tables/
public class Dao_Sqlite extends SQLiteOpenHelper {
    // Logcat tag
    private static final String LOG = "DatabaseHelper";
 
    // Database Version
    private static final int DATABASE_VERSION = 1;
 
    // Database name
    private static final String TABLE_CONTACT = "TableContact";
    private static final String TABLE_CHATRECORD = "TableChatRecord";
    private static final String TABLE_PATIENT = "TablePatient";
    private static final String TABLE_DOCTOR = "TableDoctor";
    private static final String TABLE_RECENTCHAT = "TableRecentChat";
    
    // contact Table - column names
    private static final String KEY_TYPE = "type";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_NAME = "name";
    private static final String KEY_USERID = "userid";
    private static final String KEY_INTRO = "intro";
    private static final String KEY_HEADPORTRAIT = "headportrait";
    
    // Chat record Table - column names
    private static final String KEY_ID = "id";
    private static final String KEY_MESSAGE = "Message";
    private static final String KEY_STATUS = "Status";
    private static final String KEY_RECORD_TIME = "time";
    private static final String KEY_DIRECTION = "direction";
    private static final String KEY_MESSAGETYPE = "message_type_iD";
    
    // when show by page, the size of every page.
    static final int PageSize = 10;
    
    // parse.com database.
    public static final String KEY_FULLNAME = "fullname";
    public static final String KEY_FRIEND_USER_NAME_CLOUD = "friend_username";
    
    public static final String KEY_USERTABLE = "User"; // this table stored all the users.
    public static final String KEY_USERNAME_CLOUD = "username"; // the keyword of "username"
    public static final String KEY_FULLNAME_CLOUD = "fullname"; // the keyword of "username"
    
     
    
    private Context context;
    
    SQLiteDatabase myDB;
    
    public Dao_Sqlite(Context context, String name, CursorFactory factory,
            int version) {
        super(context, name, factory, version);
        this.context = context;
        
        myDB = this.getWritableDatabase();
        onCreate(myDB);
        this.context = context;
    }
    
    public Dao_Sqlite(Context context) {
        super(context, Contact.getCurrentUserID(), null, DATABASE_VERSION);
        this.context = context;
        myDB = this.getWritableDatabase();
    }
    
    public LinkedList<Contact> getContactList() {
        LinkedList<Contact> contactList = new LinkedList<Contact>();
        
        String sql = "SELECT * FROM " + TABLE_CONTACT;
        Log.e(LOG, sql);
        
        Cursor c = myDB.rawQuery(sql, null);
        
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Contact contact = new Contact(c.getString(c.getColumnIndex("name")), 
                        c.getString(c.getColumnIndex("userid")));
                
                // adding to contact list.
                contactList.add(contact);
            } while (c.moveToNext());
        }
        
        return contactList;
    }
    
    public Cursor getContactCursor() {
        String sql = "SELECT " + KEY_NAME + "," + KEY_USERID +
                " FROM " + TABLE_CONTACT + " ORDER BY " + KEY_NAME;
        Log.e(LOG, sql);
        
        Cursor c = myDB.rawQuery(sql, null);
        return c;
    }
    
    public long getContactNumber() {
        String sql = "SELECT COUNT(*) FROM " + TABLE_CONTACT;
        SQLiteStatement statement = myDB.compileStatement(sql);
        long count = statement.simpleQueryForLong();
        return count;
    }
    
    public Cursor addContactToArrayList(List<Contact> contacts) {
        Cursor c = getContactCursor();
        
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                String name = c.getString(c.getColumnIndex(KEY_NAME));
                String userid = c.getString(c.getColumnIndex(KEY_USERID));
                
                Contact contact = new Contact(name, userid);
                
                // set the first character to be key.
                String sortKey = ContactActivity.getSortKey(name);
                contact.setSortKey(sortKey);
                
                // adding to contact list.
                contacts.add(contact);
            } while (c.moveToNext());
        }
        
        return c;
    }
    
    public void close() {
        myDB.close();
    }
    
    // load a user's data to the database.
    public void loadDataFromCloud(String userName, final DataLoadCallback callback) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(userName);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> contactList, ParseException e) {
                if (e == null) {
                    Log.d("contacts", "Retrieved " + contactList.size() + " contacts");
                    
                    // delete the local database;
                    onUpgrade(myDB, 0, 0);
                    
                    for (ParseObject o: contactList) {
                        String name = o.getString(KEY_FULLNAME);
                        String userid = o.getString(KEY_FRIEND_USER_NAME_CLOUD);
                        
                        // because we need to check another table, just keep it to be userid now.
                        // but later we need to modify it.
                        // later we should use "relation" to realize the feature.
                        Contact contact = new Contact(userid, userid);
                        contact.setSortKey(userid);
                        
                        addContact(contact);
                    }
                    
                    if (callback != null) {
                        callback.dataloadCallback();
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
                        toast.setGravity(Gravity.CENTER|Gravity.CENTER, 0, 0);
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
    
    public void cleanTable() {
        try {
            // drop the contact table.
            String sql = "DROP TABLE " + TABLE_CONTACT + ";";
            myDB.execSQL(sql);
 
        } catch (Exception e) {
            Log.e("Error", "Error", e);
        }
    }
    
    /*
     *      
     // create chatRecord table.
     myDB.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_CHATRECORD
                    + " (Id INTEGER not NULL AUTO_INCREMENT, "
                    + "Message varchar(max), Status bit, Time datetime, userid integer,"
                    + "Direction bit,"
                    + "MessageTypeID integer, "
                    + "PRIMARY KEY ( Id )), FOREIGN KEY (userid) REFERENCES " 
                    + TABLE_CONTACT + "(id) ON DELETE CASCADE;");
     * 
     * */
    public void addChatRecord(String message, Boolean status, String userID, Boolean direction,
            int messageType) {
        
        
        
    }
    
    public void getChatRecord(String userID, ArrayList<ChatRecord> chatRecordArray, int pageID) {
        String sql= "select * from " + TABLE_CHATRECORD +     
                " Limit "+String.valueOf(PageSize)+ " Offset " +String.valueOf(pageID*PageSize);    
                Cursor rec = myDB.rawQuery(sql, null);    
            
                //setTitle("当前分页的数据总数:"+String.valueOf(rec.getCount()));    
                    
                // 取得字段名称    
                String title = "";    
                int colCount = rec.getColumnCount();    
                for (int i = 0; i < colCount; i++)    
                    title = title + rec.getColumnName(i) + "     ";    
            
                    
                // 列举出所有数据    
                String content="";    
                int recCount=rec.getCount();    
                for (int i = 0; i < recCount; i++) {//定位到一条数据    
                    rec.moveToPosition(i);    
                    for(int ii=0;ii<colCount;ii++)//定位到一条数据中的每个字段    
                    {    
                        content=content+rec.getString(ii)+"     ";    
                    }    
                    content=content+"/r/n";    
                }    
                    
                //edtSQL.setText(title+"/r/n"+content);//显示出来    
                rec.close();    
    }
    
    public void addContact(Contact contact) { 
        String tableContact = TABLE_CONTACT;
 
        try {
            // Get the database if database is not exists create new database 
            // Database name is " test " 
            
            StringBuilder sb = new StringBuilder();
            
            // Send all output to the Appendable object sb
            Formatter formatter = new Formatter(sb, Locale.US);
            
            // Explicit argument indices may be used to re-order output.
//            formatter.format("REPLACE INTO %s (Type, email, phone, name, userid, intro,"
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
            
            formatter.format("REPLACE INTO %s (name, userid) VALUES ('%s', '%s');",
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
    
    public void deleteContact(String userid) { 
        myDB.delete(TABLE_CONTACT, KEY_USERID + " = ?",
                new String[] { userid });                
    }

    @Override
    public void onCreate(SQLiteDatabase DB) {
        try {
            // Create contact table
            /*
            myDB.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_CONTACT
                    + " (userid VARCHAR(50), Type VARCHAR(50), email VARCHAR(255), phone VARCHAR(30),"
                    + "Nation VARCHAR(255), Province VARCHAR(255),"
                    + "City VARCHAR(255), name VARCHAR(255), "
                    + "intro VARCHAR(300), HeadPortrait VARCHAR(255), "
                    + "Age integer, Password VARCHAR(255), PRIMARY KEY ( userid ));");
                    */
            String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_CONTACT
                    + " (" + KEY_USERID + " VARCHAR PRIMARY KEY, name VARCHAR);";
            DB.execSQL(sql);
            
            // TODO
            
            // create chatRecord table.
            myDB.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_CHATRECORD
                    + " (" 
                    + KEY_ID + " INTEGER not NULL AUTO_INCREMENT, "
                    + KEY_MESSAGE + " varchar(max), "
                    + KEY_STATUS + " bit, "
                    + KEY_RECORD_TIME + " datetime, "
                    + KEY_USERID + " integer,"
                    + KEY_DIRECTION + " bit,"
                    + KEY_MESSAGETYPE + " integer, "
                    + "PRIMARY KEY ( " + KEY_ID + " )), FOREIGN KEY (" + KEY_USERID + ") REFERENCES " 
                    + TABLE_CONTACT + "(" + KEY_USERID + ") ON DELETE CASCADE;");
            
            // create recentChat table.
            myDB.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_RECENTCHAT
                    + " (Id INTEGER not NULL AUTO_INCREMENT, "
                    + "userid integer, Time datetime"
                    + "PRIMARY KEY ( Id )), FOREIGN KEY (userid) REFERENCES " 
                    + TABLE_CONTACT + "(id) ON DELETE CASCADE;");
//            
//            // create patient table.
//            myDB.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_PATIENT
//                    + " (Id INTEGER not NULL AUTO_INCREMENT, "
//                    + "userid integer, Symptom varchar(300),"
//                    + "PRIMARY KEY ( Id ));");
//            
//            // create doctor table.
//            myDB.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_DOCTOR
//                    + " (Id INTEGER not NULL AUTO_INCREMENT, "
//                    + "userid integer, Department varchar(255), Title varchar(255)"
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
