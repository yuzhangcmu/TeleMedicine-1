package edu.cmu.smartphone.telemedicine.DBLayout;

import java.util.Formatter;
import java.util.LinkedList;
import java.util.Locale;

import edu.cmu.smartphone.telemedicine.entities.Contact;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

// reference: http://www.androidhive.info/2013/09/android-sqlite-database-with-multiple-tables/
public class Dao_Sqlite   extends SQLiteOpenHelper {
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
    
    public Dao_Sqlite(Context context, String name, CursorFactory factory,
            int version) {
        super(context, name, factory, version);
    }
    
    public Dao_Sqlite(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
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
        String tableContact = "TableContact";
 
        try {
            // Get the database if database is not exists create new database 
            // Database name is " test " 
            SQLiteDatabase myDB = this.getWritableDatabase();
            
            StringBuilder sb = new StringBuilder();
            
            // Send all output to the Appendable object sb
            Formatter formatter = new Formatter(sb, Locale.US);
            
            // Create contact table
            myDB.execSQL("CREATE TABLE IF NOT EXISTS " + tableContact
                    + " (Id INTEGER not NULL AUTO_INCREMENT, "
                    + "Type VARCHAR(50), Email VARCHAR(255), Phone VARCHAR(30),"
                    + "Name VARCHAR(255), UserID VARCHAR(255)"
                    + "intro varchar(300), HeadPortrait VARCHAR(255), "
                    + "Age integer, Password VARCHAR(255), PRIMARY KEY ( Id ));");

            // Explicit argument indices may be used to re-order output.
            formatter.format("REPLACE INTO %s (Type, Email, Phone, Name, UserID, Intro,"
                    + "HeadPortrait, Age, Password) "
                    + "VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%d', '%s');",
                    tableContact,
                    contact.getType(),
                    contact.getEmail(),
                    contact.getPhone(),
                    contact.getName(),
                    contact.getUserID(),
                    contact.getIntro(),
                    contact.getHeadPortrait(),
                    contact.getAge(),
                    contact.getPassword()
                    );
            formatter.close();
     
            myDB.execSQL(sb.toString());
 
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
            myDB.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_CONTACT
                    + " (Id INTEGER not NULL AUTO_INCREMENT, "
                    + "UserID integer, Type VARCHAR(50), Email VARCHAR(255), Phone VARCHAR(30),"
                    + "Nation VARCHAR(255), Province VARCHAR(255),"
                    + "City VARCHAR(255), Name VARCHAR(255), LoginID VARCHAR(255), "
                    + "Intro VARCHAR(300), HeadPortrait VARCHAR(255), "
                    + "Age integer, Password VARCHAR(255), PRIMARY KEY ( Id ));");
            
            // create chatRecord table.
            myDB.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_CHATRECORD
                    + " (Id INTEGER not NULL AUTO_INCREMENT, "
                    + "Message varchar(max), Status bit, Time datetime, UserID integer,"
                    + "Direction bit,"
                    + "MessageTypeID integer, "
                    + "PRIMARY KEY ( Id )), FOREIGN KEY (UserID) REFERENCES " 
                    + TABLE_CONTACT + "(id) ON DELETE CASCADE;");
            
            // create recentChat table.
            myDB.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_RECENTCHAT
                    + " (Id INTEGER not NULL AUTO_INCREMENT, "
                    + "userID integer, Time datetime"
                    + "PRIMARY KEY ( Id )), FOREIGN KEY (userID) REFERENCES " 
                    + TABLE_CONTACT + "(id) ON DELETE CASCADE;");
            
            // create patient table.
            myDB.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_PATIENT
                    + " (Id INTEGER not NULL AUTO_INCREMENT, "
                    + "UserID integer, Symptom varchar(300),"
                    + "PRIMARY KEY ( Id ));");
            
            // create doctor table.
            myDB.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_DOCTOR
                    + " (Id INTEGER not NULL AUTO_INCREMENT, "
                    + "UserID integer, Department varchar(255), Title varchar(255)"
                    + "PRIMARY KEY ( Id ));");
            
 
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
