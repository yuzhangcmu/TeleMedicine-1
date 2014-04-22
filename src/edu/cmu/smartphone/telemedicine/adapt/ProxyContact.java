package edu.cmu.smartphone.telemedicine.adapt;

import java.util.LinkedHashMap;

import edu.cmu.smartphone.telemedicine.entities.Contact;

public class ProxyContact {
    private static LinkedHashMap<String, Contact> contactHash = 
            new LinkedHashMap<String, Contact>();
    
    public void addContact(String name, String loginID) {
        Contact contact = new Contact(name, loginID);
        
        // add a new contact into the hash map list.
        contactHash.put(loginID, contact);
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
