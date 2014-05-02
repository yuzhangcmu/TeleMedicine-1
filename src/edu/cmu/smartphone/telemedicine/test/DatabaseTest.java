package edu.cmu.smartphone.telemedicine.test;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import edu.cmu.smartphone.telemedicine.DBLayout.Dao_Sqlite;
import edu.cmu.smartphone.telemedicine.entities.ChatRecord;
import edu.cmu.smartphone.telemedicine.entities.Contact;

public class DatabaseTest {
    public static void databaseChatRecordTest(Context context) {
        Dao_Sqlite dao = new Dao_Sqlite(context, "y", null, 1);
        ChatRecord record = new ChatRecord();
        
        
        String date_s = "2011-01-18 00:00:00.0";

        // *** note that it's "yyyy-MM-dd hh:mm:ss" not "yyyy-mm-dd hh:mm:ss"  
//        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//        Date date = null;
//        try {
//            date = (Date) dt.parse(date_s);
//        } catch (java.text.ParseException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }

//        // *** same for the format String below
//        SimpleDateFormat dt1 = new SimpleDateFormat("yyyy-MM-dd");
//        System.out.println(dt1.format(date));
        
        //Date date = "1985-10-29";
        record.setDate(null);
        record.setMessage("I love you");
        dao.addChatRecord(record);
        
        ArrayList<ChatRecord> list = new ArrayList<ChatRecord>();
        dao.getChatRecord("yuzhang", list, 0);
        
        
    }

}
