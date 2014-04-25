package edu.cmu.smartphone.telemedicine.adapt;

import java.util.LinkedHashMap;

import android.content.Context;
import edu.cmu.smartphone.telemedicine.DBLayout.Dao_Sqlite;
import edu.cmu.smartphone.telemedicine.DBLayout.Dao_parse;
import edu.cmu.smartphone.telemedicine.entities.Contact;

public class ProxyContact {
    private static LinkedHashMap<String, Contact> contactHash = 
            new LinkedHashMap<String, Contact>();
    
    public void addContact(String name, String loginID) {
        Contact contact = new Contact(name, loginID);
        
        // add a new contact into the hash map list.
        contactHash.put(loginID, contact);
    }
    
    public Contact addContact(Context context, String userID) {
        // add a new friend.
        Contact contact = new Contact(userID, userID);
        contact.setSortKey(userID);
        
        // add the new friend to local database. 
        Dao_Sqlite dao = new Dao_Sqlite(context);
        dao.addContact(contact);
        
        // also add the new friend to the cloud service.
        Dao_parse daoparse = new Dao_parse(context);
        daoparse.addContactCloud(contact);
        
        return contact;
    }
    
    public void delContact(String loginID) {
        contactHash.remove(loginID);
    }
    
    public Contact getContact(String loginID) {
        return contactHash.get(loginID);
    }
    
    
    public String getContactName(String loginID) {
        return contactHash.get(loginID).getName();
    }
    
    public void updateContact(String loginID, String intro) {
        Contact contact = getContact(loginID);
        contact.setIntro(intro);
    }
}
